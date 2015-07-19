package de.tum.pssif.transform.mapper.reqif;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class ReqifGraph {
	private Boolean edgeDefaultDirected = Boolean.TRUE;

	private Map<String, ReqifNodeImpl> nodes = Maps.newHashMap();
	private Map<String, ReqifEdgeImpl> edges = Maps.newHashMap(); // TODO a set
	
	private Set<ReqifAttribute> nodeAttributes = Sets.newHashSet();
	private Set<ReqifAttribute> edgeAttributes = Sets.newHashSet();

	private ReqElement current;
	
	private String tagContent = "";
	private String prevStartTag = "";
	
	private boolean source = false;
	private boolean target = false;

	public static ReqifGraph read(InputStream in) {
		ReqifGraph result = new ReqifGraph();

		result.readInternal(in);

		return result;
	}

	public static void write(ReqifGraph graph, OutputStream out) {
		//graph.writeInternal(out);
	}

	public static ReqifGraph create() {
		return new ReqifGraph();
	}

	private ReqifGraph() {
	}

	void addNodeAttributes(Collection<ReqifAttribute> attrs) {
		this.nodeAttributes.addAll(attrs);
	}

	void addEdgeAttributes(Collection<ReqifAttribute> attrs) {
		this.edgeAttributes.addAll(attrs);
	}

	void addNode(ReqifNodeImpl node) {
		this.nodes.put(node.getId(), node);
	}

	void addEdge(ReqifEdgeImpl edge) {
		this.edges.put(edge.getId(), edge);
	}

	public Set<ReqifNode> getNodes() {
		Set<ReqifNode> result = Sets.newHashSet();
		result.addAll(nodes.values());
		return result;
	}

	public ReqifNode getNode(String id) {
		return nodes.get(id);
	}

	public Set<ReqifEdge> getEdges() {
		Set<ReqifEdge> result = Sets.newHashSet();
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
				case XMLEvent.CHARACTERS:
					if (source) {
						((ReqifEdgeImpl)current).setSource(reader.getText());
						source = false;
					}
					if (target) {
						((ReqifEdgeImpl)current).setTarget(reader.getText());
						target = false;
					}
					break;

				case XMLEvent.END_ELEMENT:
					endElement(reader);
					break;
				}
			}

			reader.close();
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void startElement(XMLStreamReader reader) throws XMLStreamException {
	    String elementName = reader.getName().getLocalPart();
	    
	    if (ReqifTokens.NODE.equals(elementName)) {
	    	current = new ReqifNodeImpl(reader.getAttributeValue(null, ReqifTokens.ID));
	    	current.setValue("name", reader.getAttributeValue(null, ReqifTokens.NAME));
	    }
	    else if (ReqifTokens.NODE_TYPE.equals(elementName)) {
	    	current.setValue(elementName, reader.getElementText());
	    }
	    else if (ReqifTokens.EDGE.equals(elementName)) {
			// TODO calc directed
			boolean directed = false;
			current = new ReqifEdgeImpl(reader.getAttributeValue(null, ReqifTokens.ID), directed);
		}
	    else if (ReqifTokens.SRC_TRG_VALUE.equals(elementName)) {
	    	if (prevStartTag.equals(ReqifTokens.SOURCE)) {
	    		source = true;
	    	} else if (prevStartTag.equals(ReqifTokens.TARGET)){
	    		target = true;
	    		//((ReqifEdgeImpl)current).target(tagContent);
	    	}
	    }
	    else if (ReqifTokens.EDGE_TYPE.equals(elementName)) {
	    	current.setValue(elementName, reader.getElementText());
	    } 
	    
	    prevStartTag = elementName;
	}
	
	public void endElement(XMLStreamReader reader) throws XMLStreamException{
		String elementName = reader.getName().getLocalPart();
	    if (ReqifTokens.NODE.equals(elementName)) {
	      if (!(current instanceof ReqifNode)) {
	        throw new IllegalStateException();
	      }
	      nodes.put(current.getId(), (ReqifNodeImpl) current);
	      current = null;
	    }
	    else if (ReqifTokens.EDGE.equals(elementName)) {
	      if (!(current instanceof ReqifEdge)) {
	        throw new IllegalStateException();
	      }
	      edges.put(current.getId(), (ReqifEdgeImpl) current);
	      current = null;
	    }
	}

	static class ReqifNodeImpl extends ReqElement implements ReqifNode {
		ReqifNodeImpl(String id) {
			super(id);
		}
	}

	static class ReqifEdgeImpl extends ReqElement implements ReqifEdge {
		private String source;
		private String target;
		private final Boolean directed;

		ReqifEdgeImpl(String id, boolean directed) {
			super(id);
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
		
		public void setSource(String id) {
			source = id;
		}
		
		public void setTarget(String id) {
			target = id;
		}
	}

	private static class ReqElement implements ReqifElement {
		private final String id;
		private String type;
		private Map<String, String> values = Maps.newHashMap();

		private ReqElement(String id) {
			this.id = id;
		}

		void setValue(String key, String value) {
			if (ReqifTokens.NODE_TYPE.equalsIgnoreCase(key)) {
				type = "Requirement";
				values.put(key, value);
				return;
			}
			if (ReqifTokens.EDGE_TYPE.equalsIgnoreCase(key)) {
				type = value;
				values.put(key, value);
				return;
			}
			values.put(key, value);
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
		public String getType() {
			return type;
		}
	}

	static class ReqifAttrImpl implements ReqifAttribute {
		
		private final String name;
		private final String type;

		ReqifAttrImpl(String name, String type) {
			this.name = name;
			this.type = type;
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

	}

}
