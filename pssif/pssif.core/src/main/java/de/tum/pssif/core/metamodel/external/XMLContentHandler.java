package de.tum.pssif.core.metamodel.external;

import java.util.ArrayList;
import java.util.HashMap;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import de.tum.pssif.core.model.Tupel;

/**
 * Runs the parser importing the .xml data
 * 
 * @author Alex
 * 
 */
public class XMLContentHandler implements ContentHandler {

	private MetamodelConjunction currentConjunction;
	private MetamodelNode currentNode;
	private MetamodelEdge currentEdge;
	private MetamodelAttribute currentAttribute;

	private HashMap<String, MetamodelConjunction> conjunctions = new HashMap<String, MetamodelConjunction>();
	private HashMap<String, MetamodelNode> nodes = new HashMap<String, MetamodelNode>();
	private HashMap<String, MetamodelEdge> edges = new HashMap<String, MetamodelEdge>();
	private HashMap<String, MetamodelAttribute> attributes = new HashMap<String, MetamodelAttribute>();

	private HashMap<String, String> tagMap = new HashMap<String, String>();

	/**
	 * Defines the format in a line (e.q. tabs)
	 */
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		new String(ch, start, length);
	}

	// Method is called if the parser detects a "start"-tag
	public void startElement(String uri, String localName, String qName,
			Attributes atts) throws SAXException {

		// Case: New Component
		if (localName.equals("CONJUNCTION")) {
			currentConjunction = new MetamodelConjunction(atts.getValue("Tag"),
					atts.getValue("Name"));
			conjunctions.put(atts.getValue("Name"), currentConjunction);
			tagMap.put(atts.getValue("Tag"), atts.getValue("Name"));
		} else if (localName.equals("NODE")) {
			currentNode = new MetamodelNode(atts.getValue("Tag"),
					atts.getValue("Name"));
			nodes.put(atts.getValue("Name"), currentNode);
			String parent = atts.getValue("Parent");
			if (parent != null) {
				currentNode.setParentAsString(parent);
			}
			tagMap.put(atts.getValue("Tag"), atts.getValue("Name"));
		} else if (localName.equals("EDGE")) {
			currentEdge = new MetamodelEdge(atts.getValue("Tag"),
					atts.getValue("Name"));
			edges.put(atts.getValue("Name"), currentEdge);
			String parent = atts.getValue("Parent");
			if (parent != null) {
				currentEdge.setTempParent(parent);
			}
			tagMap.put(atts.getValue("Tag"), atts.getValue("Name"));
		} else if (localName.equals("ATTRIBUTE")) {
			currentAttribute = new MetamodelAttribute(atts.getValue("Tag"),
					atts.getValue("Name"));
			attributes.put(atts.getValue("Name"), currentAttribute);
			tagMap.put(atts.getValue("Tag"), atts.getValue("Name"));
		}

		else if (localName.equals("GROUP")) {
			currentAttribute.setAttributeGroup(atts.getValue(0));
		} else if (localName.equals("DATATYPE")) {
			currentAttribute.setAttributeDataType(atts.getValue(0));
		} else if (localName.equals("VISIBLE")) {
			currentAttribute.setAttributeVisiblity(Boolean.parseBoolean(atts.getValue(0)));
		} else if (localName.equals("CATEGORY")) {
			currentAttribute.setAttributeCategory(atts.getValue(0));
		} else if (localName.equals("UNIT")) {
			currentAttribute.setAttributeUnit(atts.getValue(0));
		}

		// Case: Mapping
		else if (localName.equals("MAPPINGS")) {
			currentEdge.setMappings(new ArrayList<Tupel>());
		} else if (localName.equals("MAPPING")) {
			currentEdge.getMappings().add(new Tupel(atts.getValue("From"), atts.getValue("To")));
		}

		// Case: A new node connected attribute is read
		else if (localName.equals("SELECTEDATTRIBUTES")) {
			currentNode.setConnectedAttributes(new ArrayList<String>());
		} else if (localName.equals("SELECTEDATTRIBUTE")) {
			currentNode.getConnectedAttributes().add(atts.getValue(0));
		}
	}

	public void endElement(String uri, String localName, String qName)
			throws SAXException {
	}

	public void endDocument() throws SAXException {
	}

	public void endPrefixMapping(String prefix) throws SAXException {
	}

	public void ignorableWhitespace(char[] ch, int start, int length)
			throws SAXException {
	}

	public void processingInstruction(String target, String data)
			throws SAXException {
	}

	public void setDocumentLocator(Locator locator) {
	}

	public void skippedEntity(String name) throws SAXException {
	}

	public void startDocument() throws SAXException {
	}

	public void startPrefixMapping(String prefix, String uri)
			throws SAXException {
	}

	/**
	 * Get a HashMap containing every conjunction
	 * @return The conjunctions
	 */
	public HashMap<String, MetamodelConjunction> getConjunctions() {
		return conjunctions;
	}

	/**
	 * Get a HashMap containing every node
	 * @return The nodes
	 */
	public HashMap<String, MetamodelNode> getNodes() {
		return nodes;
	}
	
	/**
	 * Get a HashMap containing every edge
	 * @return The edges
	 */
	public HashMap<String, MetamodelEdge> getEdges() {
		return edges;
	}

	/**
	 * Get a HashMap containing every attribute
	 * @return The attributes
	 */
	public HashMap<String, MetamodelAttribute> getAttributes() {
		return attributes;
	}

	/**
	 * Get a HashMap containing every tag of the imported components
	 * @return The tags
	 */
	public HashMap<String, String> getTagMap() {
		return tagMap;
	}
}