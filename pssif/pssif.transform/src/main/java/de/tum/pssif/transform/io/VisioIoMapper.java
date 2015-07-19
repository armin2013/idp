package de.tum.pssif.transform.io;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import de.tum.pssif.core.common.PSSIFConstants;
import de.tum.pssif.transform.IoMapper;
import de.tum.pssif.transform.graph.AElement;
import de.tum.pssif.transform.graph.Edge;
import de.tum.pssif.transform.graph.Graph;
import de.tum.pssif.transform.graph.Node;
import de.tum.pssif.vsdx.VsdxConnector;
import de.tum.pssif.vsdx.VsdxDocument;
import de.tum.pssif.vsdx.VsdxDocumentLoader;
import de.tum.pssif.vsdx.VsdxMaster;
import de.tum.pssif.vsdx.VsdxShape;
import de.tum.pssif.vsdx.VsdxShapeContainer;
import de.tum.pssif.vsdx.exception.VsdxException;
import de.tum.pssif.vsdx.impl.VsdxDocumentLoaderFactory;


public class VisioIoMapper implements IoMapper {

  private static final String VISIO_MASTER_SUFFIX_REGEX = "(\\.(\\d)*+)?";
  private static final String VISIO_MASTER_SPLIT_REGEX  = "\\.";

  private final String        templateFile;
  private final Set<String>   nodeMasters;
  private final Set<String>   edgeMasters;

  public VisioIoMapper(String templateFile, Set<String> nodeMasters, Set<String> edgeMasters) {
    this.templateFile = templateFile;
    this.nodeMasters = Sets.newHashSet(nodeMasters);
    this.edgeMasters = Sets.newHashSet(edgeMasters);
  }

  @Override
  public Graph read(InputStream in) {
    VsdxDocument document = null;
    try {
      VsdxDocumentLoader loader = VsdxDocumentLoaderFactory.INSTANCE.create();
      document = loader.loadDocument(in);
    } catch (VsdxException e) {
      throw new PSSIFIoException("Failed to load VSDX document.", e);
    }
    Graph graph = new Graph();
    readNodes(document, graph);
    readEdges(document, graph);

    return graph;
  }

  @Override
  public void write(Graph graph, OutputStream out) {
    VsdxDocument document = null;
    try {
      VsdxDocumentLoader loader = VsdxDocumentLoaderFactory.INSTANCE.create();
      document = loader.loadDocument(getClass().getResourceAsStream(templateFile));
    } catch (VsdxException e) {
      throw new PSSIFIoException("Failed to load VSDX document.", e);
    }
    Map<Node, VsdxShape> shapes = writeShapes(graph.getNodes(), document);
    writeConnectors(graph, document, shapes);
    document.getDocumentWriter().write(out);
  }

  private Map<Node, VsdxShape> writeShapes(Set<Node> nodes, VsdxDocument document) {
    Map<Node, VsdxShape> shapes = Maps.newHashMap();
    for (Node node : nodes) {
      if (document.hasMaster(node.getType())) {
        VsdxShape shape = writeShape(node, document.getMaster(node.getType()), document.getPage());
        shapes.put(node, shape);
        if (node.getInnerNodes().size() > 0) {
          Map<Node, VsdxShape> innerShapes = writeShapes(node.getInnerNodes(), document);
          shapes.putAll(innerShapes);
        }
      }

    }
    return shapes;
  }

  private VsdxShape writeShape(Node node, VsdxMaster master, VsdxShapeContainer container) {
    VsdxShape shape = container.createNewShape(master);
    writeAttributes(shape, node);
    return shape;
  }

  private void writeConnectors(Graph graph, VsdxDocument document, Map<Node, VsdxShape> shapes) {
    for (Edge edge : graph.getEdges()) {
      VsdxShape sourceShape = shapes.get(edge.getSource());
      VsdxShape targetShape = shapes.get(edge.getTarget());
      if (document.hasMaster(edge.getType()) && sourceShape != null && targetShape != null) {
        VsdxConnector connector = document.getPage().createNewConnector(document.getMaster(edge.getType()), sourceShape, targetShape);
        writeAttributes(connector, edge);
      }
    }
  }

  private void writeAttributes(VsdxShape inShape, AElement fromElement) {
    inShape.setText(fromElement.getAttributeValue(PSSIFConstants.BUILTIN_ATTRIBUTE_NAME));
    for (String attrName : fromElement.getAttributeNames()) {
      if (fromElement.getAttributeValue(attrName) != null) {
        inShape.setCustomProperty(attrName, fromElement.getAttributeValue(attrName));
      }
    }
  }

  private void readNodes(VsdxDocument document, Graph graph) {
    for (VsdxShape shape : document.getPage().getShapes()) {
      readNode(shape, graph);
    }
  }

  private Node readNode(VsdxShape shape, Graph graph) {
    if (isNodeMasterSupported(shape.getMaster().getName())) {
      Node node = graph.createNode(String.valueOf(shape.getId()));
      node.setType(getValidMasterName(shape.getMaster().getName()));
      readAttributes(node, shape);
      for (VsdxShape inner : shape.getShapes()) {
        Node innerNode = readNode(inner, graph);
        if (innerNode != null) {
          node.addInnerNode(innerNode);
        }
      }
      return node;
    }
    return null;
  }

  private void readEdges(VsdxDocument document, Graph graph) {
    for (VsdxConnector connector : document.getPage().getConnectors()) {
      if (isEdgeMasterSupported(connector.getMaster().getName())) {
        Node source = graph.findNode(String.valueOf(connector.getSourceShape().getId()));
        Node target = graph.findNode(String.valueOf(connector.getTargetShape().getId()));
        if (source != null && target != null) {
          Edge edge = graph.createEdge(String.valueOf(connector.getId()));
          edge.setType(getValidMasterName(connector.getMaster().getName()));
          graph.connect(source, edge, target);
          readAttributes(edge, connector);
        }
      }
    }
  }

  private void readAttributes(AElement intoElement, VsdxShape source) {
    intoElement.setAttribute(PSSIFConstants.BUILTIN_ATTRIBUTE_NAME, source.getText());
    for (String pName : source.getCustomPropertyNames()) {
      if (source.getCustomPropertyValue(pName) != null) {
        intoElement.setAttribute(pName, source.getCustomPropertyValue(pName));
      }
    }
  }

  private boolean isEdgeMasterSupported(String master) {
    return masterIsSupported(master, edgeMasters);
  }

  private boolean isNodeMasterSupported(String master) {
    return masterIsSupported(master, nodeMasters);
  }

  private boolean masterIsSupported(String master, Set<String> masters) {
    if (masters.contains(master)) {
      return true;
    }
    for (String candidate : masters) {
      if (master.matches(candidate + VISIO_MASTER_SUFFIX_REGEX)) {
        return true;
      }
    }
    return false;
  }

  private String getValidMasterName(String masterName) {
    return masterName.split(VISIO_MASTER_SPLIT_REGEX)[0];
  }
}
