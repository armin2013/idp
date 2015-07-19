package de.tum.pssif.transform.mapper.graphml;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.XMLEvent;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import de.tum.pssif.transform.io.PSSIFIoException;


/**
 * TODO edgedefault
 */
public class GraphMLGraph {
  private Boolean                      edgeDefaultDirected = Boolean.TRUE;

  private Map<String, GraphMlNodeImpl> nodes               = Maps.newHashMap();
  private Map<String, GraphMlEdgeImpl> edges               = Maps.newHashMap(); //TODO a set might be sufficient here

  private Set<GraphMlAttribute>        nodeAttributes      = Sets.newHashSet();
  private Set<GraphMlAttribute>        edgeAttributes      = Sets.newHashSet();

  private Element                      current;

  public static GraphMLGraph read(InputStream in) {
    GraphMLGraph result = new GraphMLGraph();

    result.readInternal(in);

    return result;
  }

  public static void write(GraphMLGraph graph, OutputStream out) {
    graph.writeInternal(out);
  }

  public static GraphMLGraph create() {
    return new GraphMLGraph();
  }

  private GraphMLGraph() {
  }

  void addNodeAttributes(Collection<GraphMlAttribute> attrs) {
    this.nodeAttributes.addAll(attrs);
  }

  void addEdgeAttributes(Collection<GraphMlAttribute> attrs) {
    this.edgeAttributes.addAll(attrs);
  }

  void addNode(GraphMlNodeImpl node) {
    this.nodes.put(node.getId(), node);
  }

  void addEdge(GraphMlEdgeImpl edge) {
    this.edges.put(edge.getId(), edge);
  }

  public Set<GraphMLNode> getNodes() {
    Set<GraphMLNode> result = Sets.newHashSet();
    result.addAll(nodes.values());
    return result;
  }

  public GraphMLNode getNode(String id) {
    return nodes.get(id);
  }

  public Set<GraphMLEdge> getEdges() {
    Set<GraphMLEdge> result = Sets.newHashSet();
    result.addAll(edges.values());
    return result;
  }

  private void readInternal(InputStream in) {
    XMLInputFactory factory = XMLInputFactory.newInstance();

    try {
      XMLStreamReader reader = factory.createXMLStreamReader(in);

      while (reader.hasNext()) {
        int event = reader.next();

        switch (event) {
          case XMLEvent.START_ELEMENT:
            startElement(reader);
            break;
          case XMLEvent.END_ELEMENT:
            endElement(reader);
            break;
        }
      }

      reader.close();
    } catch (XMLStreamException e) {
      throw new PSSIFIoException("Failed to load graphml document.", e);
    }
  }

  private void startElement(XMLStreamReader reader) throws XMLStreamException {
    String elementName = reader.getName().getLocalPart();
    if (GraphMLTokens.GRAPH.equals(elementName)) {
      if (GraphMLTokens.UNDIRECTED.equals(reader.getAttributeValue(null, GraphMLTokens.EDGEDEFAULT))) {
        edgeDefaultDirected = Boolean.FALSE;
      }
    }
    else if (GraphMLTokens.KEY.equals(elementName)) {
      //TODO what to do here? nothing?
    }
    else if (GraphMLTokens.NODE.equals(elementName)) {
      current = new GraphMlNodeImpl(reader.getAttributeValue(null, GraphMLTokens.ID));
    }
    else if (GraphMLTokens.EDGE.equals(elementName)) {
      boolean directed = edgeDefaultDirected.booleanValue() || Boolean.valueOf(reader.getAttributeValue(null, GraphMLTokens.DIRECTED)).booleanValue();
      current = new GraphMlEdgeImpl(reader.getAttributeValue(null, GraphMLTokens.ID), reader.getAttributeValue(null, GraphMLTokens.SOURCE),
          reader.getAttributeValue(null, GraphMLTokens.TARGET), directed);
    }
    else if (GraphMLTokens.DATA.equals(elementName)) {
      String key = reader.getAttributeValue(null, GraphMLTokens.KEY);
      if (GraphMLTokens.ID.equals(key)) {
        key = "__" + key + "__";
      }
      if (reader.getAttributeValue(null, GraphMLTokens.ANNOTATION) == null) {
        current.setValue(key, reader.getElementText());
      }
      else {
        current.setAnnotations(key, reader.getElementText());
      }
    }
  }

  private void endElement(XMLStreamReader reader) {
    String elementName = reader.getName().getLocalPart();
    if (GraphMLTokens.NODE.equals(elementName)) {
      if (!(current instanceof GraphMLNode)) {
        throw new IllegalStateException();
      }
      nodes.put(current.getId(), (GraphMlNodeImpl) current);
      current = null;
    }
    else if (GraphMLTokens.EDGE.equals(elementName)) {
      if (!(current instanceof GraphMLEdge)) {
        throw new IllegalStateException();
      }
      edges.put(current.getId(), (GraphMlEdgeImpl) current);
      current = null;
    }
  }

  private void writeInternal(OutputStream out) {
    XMLOutputFactory factory = XMLOutputFactory.newInstance();

    try {
      XMLStreamWriter writer = factory.createXMLStreamWriter(out, "UTF-8");
      writeDocumentHeader(writer);
      writeNodes(writer);
      writeEdges(writer);
      writeDocumentFooter(writer);
      writer.flush();
    } catch (XMLStreamException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  private void writeDocumentHeader(XMLStreamWriter writer) throws XMLStreamException {
    writer.writeStartDocument("UTF-8", "1.0");
    writer.writeStartElement(GraphMLTokens.GRAPHML);
    writer.writeAttribute(GraphMLTokens.XMLNS, GraphMLTokens.GRAPHML_XMLNS);
    //write attribute keys
    writeKeyElement(writer, GraphMLTokens.ELEMENT_TYPE, GraphMLTokens.STRING, GraphMLTokens.NODE, GraphMLTokens.ELEMENT_TYPE);
    writeKeyElement(writer, GraphMLTokens.ELEMENT_TYPE, GraphMLTokens.STRING, GraphMLTokens.EDGE, GraphMLTokens.ELEMENT_TYPE);
    for (GraphMlAttribute attr : nodeAttributes) {
      writeKeyElement(writer, attr.getName(), attr.getType(), GraphMLTokens.NODE, attr.getId());
    }
    for (GraphMlAttribute attr : edgeAttributes) {
      writeKeyElement(writer, attr.getName(), attr.getType(), GraphMLTokens.EDGE, attr.getId());
    }
    writer.writeStartElement(GraphMLTokens.GRAPH);
    if (!edges.isEmpty()) {
    	writer.writeAttribute(GraphMLTokens.EDGEDEFAULT, GraphMLTokens.DIRECTED);
    }
  }

  private void writeKeyElement(XMLStreamWriter writer, String attrName, String attrType, String forAttr, String id) throws XMLStreamException {
    writer.writeEmptyElement(GraphMLTokens.KEY);
    writer.writeAttribute(GraphMLTokens.ATTR_NAME, attrName);
    writer.writeAttribute(GraphMLTokens.ATTR_TYPE, attrType);
    writer.writeAttribute(GraphMLTokens.FOR, forAttr);
    writer.writeAttribute(GraphMLTokens.ID, id);
  }

  private void writeDataElemnent(XMLStreamWriter writer, String key, String text) throws XMLStreamException {
    writer.writeStartElement(GraphMLTokens.DATA);
    writer.writeAttribute(GraphMLTokens.KEY, key);
    writer.writeCharacters(text);
    writer.writeEndElement();
  }

  private void writeAnnotationElemnent(XMLStreamWriter writer, String key, String text) throws XMLStreamException {
    writer.writeStartElement(GraphMLTokens.DATA);
    writer.writeAttribute(GraphMLTokens.KEY, key);
    writer.writeAttribute(GraphMLTokens.ANNOTATION, GraphMLTokens.ANNOTATION);
    writer.writeCharacters(text);
    writer.writeEndElement();
  }

  private void writeDataElements(XMLStreamWriter writer, Map<String, String> elements) throws XMLStreamException {
    for (Entry<String, String> attrVal : elements.entrySet()) {
      writeDataElemnent(writer, attrVal.getKey(), attrVal.getValue());
    }
  }

  private void writeAnnotationElements(XMLStreamWriter writer, Map<String, String> elements) throws XMLStreamException {
    for (Entry<String, String> attrVal : elements.entrySet()) {
      writeAnnotationElemnent(writer, attrVal.getKey(), attrVal.getValue());
    }
  }

  private void writeNodes(XMLStreamWriter writer) throws XMLStreamException {
    for (Entry<String, GraphMlNodeImpl> entry : nodes.entrySet()) {
      writer.writeStartElement(GraphMLTokens.NODE);
      writer.writeAttribute(GraphMLTokens.ID, entry.getValue().getId());
      writeDataElemnent(writer, GraphMLTokens.ELEMENT_TYPE, entry.getValue().getType());
      writeDataElements(writer, entry.getValue().getValues());
      writeAnnotationElements(writer, entry.getValue().getAnnotations());
      writer.writeEndElement();
    }
  }

  private void writeEdges(XMLStreamWriter writer) throws XMLStreamException {
    for (Entry<String, GraphMlEdgeImpl> entry : edges.entrySet()) {
      writer.writeStartElement(GraphMLTokens.EDGE);
      writer.writeAttribute(GraphMLTokens.ID, entry.getValue().getId());
      writer.writeAttribute(GraphMLTokens.SOURCE, entry.getValue().getSourceId());
      writer.writeAttribute(GraphMLTokens.TARGET, entry.getValue().getTargetId());
      writeDataElemnent(writer, GraphMLTokens.ELEMENT_TYPE, entry.getValue().getType());
      writeDataElements(writer, entry.getValue().getValues());
      writeAnnotationElements(writer, entry.getValue().getAnnotations());
      writer.writeEndElement();
    }
  }

  private void writeDocumentFooter(XMLStreamWriter writer) throws XMLStreamException {
    writer.writeEndElement();
    writer.writeEndElement();
    writer.writeEndDocument();
  }

  static class GraphMlNodeImpl extends Element implements GraphMLNode {
    GraphMlNodeImpl(String id) {
      super(id);
    }
  }

  static class GraphMlEdgeImpl extends Element implements GraphMLEdge {
    private final String  source;
    private final String  target;
    private final Boolean directed;

    GraphMlEdgeImpl(String id, String source, String target, boolean directed) {
      super(id);
      this.source = source;
      this.target = target;
      this.directed = Boolean.valueOf(directed);
    }

    @Override
    public String getSourceId() {
      return source;
    }

    @Override
    public String getTargetId() {
      return target;
    }

    @Override
    public Boolean isDirected() {
      return directed;
    }
  }

  private static class Element implements GraphMLElement {
    private final String        id;
    private String              type;
    private Map<String, String> values      = Maps.newHashMap();
    private Map<String, String> annotations = Maps.newHashMap();

    private Element(String id) {
      this.id = id;
    }

    void setValue(String key, String value) {
      if (GraphMLTokens.ELEMENT_TYPE.equals(key)) {
        type = value;
      }
      else {
        values.put(key, value);
      }
    }

    void setAnnotations(String key, String value) {
      if (GraphMLTokens.ELEMENT_TYPE.equals(key)) {
        type = value;
      }
      else {
        annotations.put(key, value);
      }
    }

    @Override
    public String getId() {
      return id;
    }

    @Override
    public Map<String, String> getValues() {
      return ImmutableMap.copyOf(values);
    }

    @Override
    public Map<String, String> getAnnotations() {
      return ImmutableMap.copyOf(annotations);
    }

    @Override
    public String getType() {
      return type;
    }
  }

  static class GraphMlAttrImpl implements GraphMlAttribute {
    private final String name;
    private final String type;
    private final String annotation;

    GraphMlAttrImpl(String name, String type) {
      this(name, type, null);
    }

    GraphMlAttrImpl(String name, String type, String annotation) {
      this.name = name;
      this.type = type;
      this.annotation = annotation;
    }

    @Override
    public String getName() {
      return name;
    }

    @Override
    public String getType() {
      return type;
    }

    @Override
    public String getId() {
      return name;
    }

    @Override
    public boolean isAnnotation() {
      return annotation != null;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((annotation == null) ? 0 : annotation.hashCode());
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }
      if (obj == null) {
        return false;
      }
      if (getClass() != obj.getClass()) {
        return false;
      }
      GraphMlAttrImpl other = (GraphMlAttrImpl) obj;
      if (annotation == null) {
        if (other.annotation != null) {
          return false;
        }
      }
      else if (!annotation.equals(other.annotation)) {
        return false;
      }
      if (name == null) {
        if (other.name != null) {
          return false;
        }
      }
      else if (!name.equals(other.name)) {
        return false;
      }
      return true;
    }
  }
}
