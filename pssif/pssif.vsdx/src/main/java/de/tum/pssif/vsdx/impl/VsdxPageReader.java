package de.tum.pssif.vsdx.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import de.tum.pssif.vsdx.exception.VsdxXmlException;
import de.tum.pssif.vsdx.zip.ZipArchiveEntryWithData;


class VsdxPageReader {

  static VsdxPageReader INSTANCE = new VsdxPageReader();

  private VsdxPageReader() {
    //Nothing
  }

  public VsdxPageImpl create(ZipArchiveEntryWithData pageEntry) {
    Set<VsdxShapeImpl> shapes = Sets.newHashSet();
    Set<VsdxConnectorImpl> connectors = Sets.newHashSet();
    try {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = factory.newDocumentBuilder();
      Document document = builder.parse(new ByteArrayInputStream(pageEntry.getData()));
      document.normalizeDocument();
      Element rootNode = document.getDocumentElement();

      Node shapesNode = locateSuccessorElement(rootNode, VsdxTokens.SHAPES);
      if (shapesNode != null) {
        shapes.addAll(readShapes(shapesNode));
        Node connectsNode = locateSuccessorElement(rootNode, VsdxTokens.CONNECTS);
        if (connectsNode != null) {
          Set<VsdxShapeImpl> disconnectedConnectors = Sets.newHashSet();
          connectors.addAll(readConnectors(connectsNode, shapes, disconnectedConnectors));
          Set<VsdxShapeImpl> shapesToRemove = Sets.newHashSet();
          for (VsdxConnectorImpl connect : connectors) {
            for (VsdxShapeImpl shape : shapes) {
              if (connect.getId() == shape.getId()) {
                shapesToRemove.add(shape);
              }
            }
          }
          shapes.removeAll(shapesToRemove);
          shapes.removeAll(disconnectedConnectors);
        }
      }
    } catch (IOException e) {
      throw new VsdxXmlException("Failed to read masters xml file from visio document.", e);
    } catch (ParserConfigurationException e) {
      throw new VsdxXmlException("Failed to read masters xml file from visio document.", e);
    } catch (SAXException e) {
      throw new VsdxXmlException("Failed to read masters xml file from visio document.", e);
    }
    return new VsdxPageImpl(pageEntry.getZipEntry(), shapes, connectors);
  }

  private Set<VsdxShapeImpl> readShapes(Node shapesElement) {
    Set<VsdxShapeImpl> shapesImpl = Sets.newHashSet();
    NodeList shapes = shapesElement.getChildNodes();
    for (int i = 0; i < shapes.getLength(); i++) {
      Node shape = shapes.item(i);
      if (VsdxTokens.SHAPE.equals(shape.getNodeName())) {
        VsdxShapeImpl shapeImpl = readShape(shape);
        if (shapeImpl != null) {
          shapesImpl.add(shapeImpl);
        }
      }
    }
    return shapesImpl;
  }

  private VsdxShapeImpl readShape(Node shapeNode) {
    String shapeIdString = getAttributeValue(shapeNode, VsdxTokens.ID);
    String masterIdString = getAttributeValue(shapeNode, VsdxTokens.MASTER);

    if (shapeIdString.trim().isEmpty() || masterIdString.trim().isEmpty()) {
      return null;
    }

    int shapeId = Integer.valueOf(shapeIdString).intValue();
    int masterId = Integer.valueOf(masterIdString).intValue();
    VsdxShapeImpl shape = new VsdxShapeImpl(shapeId, masterId);
    Node textNode = locateSuccessorElement(shapeNode, VsdxTokens.TEXT);
    if (textNode != null) {
      shape.setText(seekText(textNode));
    }
    for (Node sectionNode : locateChildElements(shapeNode, VsdxTokens.SECTION)) {
      if (VsdxTokens.PROPERTY.equalsIgnoreCase(getAttributeValue(sectionNode, VsdxTokens.ATTRIBUTE_NAME))) {
        for (Node rowNode : locateChildElements(sectionNode, VsdxTokens.ROW)) {
          String propName = getAttributeValue(rowNode, VsdxTokens.ATTRIBUTE_NAME);
          if (propName != null && !propName.trim().isEmpty()) {
            for (Node cellNode : locateChildElements(rowNode, VsdxTokens.CELL)) {
              if (VsdxTokens.VALUE.equalsIgnoreCase(getAttributeValue(cellNode, VsdxTokens.ATTRIBUTE_NAME))) {
                String propertyValue = getAttributeValue(cellNode, VsdxTokens.ATTRIBUTE_VALUE);
                if (propertyValue != null && !propertyValue.trim().isEmpty()) {
                  shape.setCustomProperty(propName, propertyValue);
                }
              }
            }
          }
        }
      }
    }
    Node innerShapesNode = locateSuccessorElement(shapeNode, VsdxTokens.SHAPES);
    if (innerShapesNode != null) {
      shape.setInnerShapes(readShapes(innerShapesNode));
    }
    return shape;
  }

  private Set<VsdxConnectorImpl> readConnectors(Node connectsNode, Set<VsdxShapeImpl> rootShapes, Set<VsdxShapeImpl> disconnectedConnectors) {
    Set<VsdxConnectorImpl> connects = Sets.newHashSet();
    Map<Integer, VsdxShapeImpl> allShapes = getShapesByIdFlattened(rootShapes);
    Map<Integer, Integer> connectorToSource = Maps.newHashMap();
    Map<Integer, Integer> connectorToTarget = Maps.newHashMap();
    NodeList children = connectsNode.getChildNodes();
    for (int i = 0; i < children.getLength(); i++) {
      Node connectNode = children.item(i);
      String connectorIdString = getAttributeValue(connectNode, VsdxTokens.FROM_SHEET);
      String shapeIdString = getAttributeValue(connectNode, VsdxTokens.TO_SHEET);
      String sourceOrTargetString = getAttributeValue(connectNode, VsdxTokens.FROM_CELL);
      Integer connectorId = connectorIdString.trim().isEmpty() ? -1 : Integer.valueOf(connectorIdString);
      Integer shapeId = shapeIdString.trim().isEmpty() ? -1 : Integer.valueOf(shapeIdString);
      boolean isSource = VsdxTokens.BEGIN_X.equals(sourceOrTargetString);
      if (connectorId != -1 && shapeId != -1) {
        if (isSource) {
          connectorToSource.put(connectorId, shapeId);
        }
        else {
          connectorToTarget.put(connectorId, shapeId);
        }
      }
    }

    for (Entry<Integer, Integer> entry : connectorToSource.entrySet()) {
      if (connectorToTarget.get(entry.getKey()) != null) {
        VsdxShapeImpl connectorShape = allShapes.get(entry.getKey());
        VsdxShapeImpl sourceShape = allShapes.get(entry.getValue());
        VsdxShapeImpl targetShape = allShapes.get(connectorToTarget.get(entry.getKey()));
        VsdxConnectorImpl connector = new VsdxConnectorImpl(connectorShape.getId(), connectorShape.getMasterId());
        connector.setSource(sourceShape);
        connector.setTarget(targetShape);
        connects.add(connector);
      }
      else {
        disconnectedConnectors.add(allShapes.get(entry.getKey()));
      }
    }
    for (Entry<Integer, Integer> entry : connectorToTarget.entrySet()) {
      if (connectorToSource.get(entry.getKey()) == null) {
        disconnectedConnectors.add(allShapes.get(entry.getKey()));
      }
    }
    //Note: filtering the disconnected ones works only partially this way
    //i.e. connectors which have nor source neither target will be skipped
    //but: they can only be caught if explicit knowledge of
    //1D and 2D shape masters is available
    return connects;
  }

  private static String getAttributeValue(Node node, String attributeName) {
    NamedNodeMap map = node.getAttributes();
    Node attrVal = map.getNamedItemNS(null, attributeName);
    if (attrVal != null) {
      return attrVal.getNodeValue();
    }
    return "";
  }

  private static Map<Integer, VsdxShapeImpl> getShapesByIdFlattened(Set<VsdxShapeImpl> shapes) {
    Map<Integer, VsdxShapeImpl> flattened = Maps.newHashMap();
    for (VsdxShapeImpl shape : shapes) {
      flattened.put(Integer.valueOf(shape.getId()), shape);
      flattened.putAll(getShapesByIdFlattened(shape.innerShapesImpl()));
    }
    return flattened;
  }

  private static String seekText(Node node) {
    if (!node.getTextContent().trim().isEmpty()) {
      return node.getTextContent();
    }
    NodeList children = node.getChildNodes();
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < children.getLength(); i++) {
      String childText = seekText(children.item(i));
      if (!childText.trim().isEmpty()) {
        builder.append(childText);
        builder.append(" ");
      }
    }
    return builder.toString();
  }

  private static Node locateSuccessorElement(Node inElement, String localName) {
    //Note: BFS
    NodeList nodeList = inElement.getChildNodes();

    for (int i = 0; i < nodeList.getLength(); i++) {
      Node node = nodeList.item(i);
      if (localName.equals(node.getNodeName())) {
        return node;
      }
    }
    for (int i = 0; i < nodeList.getLength(); i++) {
      Node shapesNode = locateSuccessorElement(nodeList.item(i), localName);
      if (shapesNode != null) {
        return shapesNode;
      }
    }
    return null;
  }

  private static Set<Node> locateChildElements(Node inElement, String localName) {
    NodeList nodeList = inElement.getChildNodes();
    Set<Node> result = Sets.newHashSet();
    for (int i = 0; i < nodeList.getLength(); i++) {
      Node node = nodeList.item(i);
      if (localName.equals(node.getNodeName())) {
        result.add(node);
      }
    }
    return result;
  }
}
