package de.tum.pssif.core.metamodel.external;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.tum.pssif.core.model.Tupel;

/**
 * Responsible for exporting the Metamodel to XML
 * 
 * @author Alex
 * 
 */
public class MetamodelExport {

	private HashMap<String, MetamodelConjunction> conjunctions;
	private HashMap<String, MetamodelNode> nodes;
	private HashMap<String, MetamodelEdge> edges;
	private HashMap<String, MetamodelAttribute> attributes;
	private ArrayList<MetamodelNode> byDeletedParentAffectedNodes = new ArrayList<MetamodelNode>();
	private ArrayList<MetamodelEdge> byDeletedParentAffectedEdges = new ArrayList<MetamodelEdge>();

	/**
	 * Constructor for the exporter
	 */
	public MetamodelExport() {
		resetData();
	}

	/**
	 * Call if a single components content is changed to save the changes
	 * 
	 * @param originalName
	 *            The original name of the component (before it was potentially
	 *            changed)
	 * @param component
	 *            The changed component
	 */
	public void saveChangedComponent(String originalName,
			MetamodelComponent component) {

		if (component.getType().equals("CONJUNCTION")) {
			if (conjunctions.containsKey(originalName)) {
				conjunctions.remove(originalName);
			}
			conjunctions.put(component.getName(),
					(MetamodelConjunction) component);
			writeMetaModelToXML();

		} else if (component.getType().equals("NODE")) {

			if (nodes.containsKey(originalName)) {
				nodes.remove(originalName);
			}
			nodes.put(component.getName(), (MetamodelNode) component);
			writeMetaModelToXML();

		} else if (component.getType().equals("EDGE")) {

			if (edges.containsKey(originalName)) {
				edges.remove(originalName);
			}
			edges.put(component.getName(), (MetamodelEdge) component);
			writeMetaModelToXML();

		} else {
			if (attributes.containsKey(originalName)) {
				attributes.remove(originalName);
			}
			attributes.put(component.getName(), (MetamodelAttribute) component);
			writeMetaModelToXML();
		}
	}

	/**
	 * Call if a single component is supposed to be removed for the metamodel
	 * 
	 * @param componentToDelete
	 *            The component to delete
	 * @param selectedItem
	 *            The decision what is supposed to happen with 'parent => child'
	 *            relationships if any
	 */
	public void removeComponent(MetamodelComponent componentToDelete,
			int selectedItem) {
		switch (componentToDelete.getType()) {
		// Set affected components
		case "NODE":
			resetByDeletedParentAffectedNodes(componentToDelete);
			// resetAffectedParentNode((MetamodelNode) componentToDelete);
			removeMappingsInvolvingThisNode(componentToDelete);
			break;
		case "EDGE":
			resetByDeletedParentAffectedEdges(componentToDelete);
			// resetAffectedParentEdge((MetamodelEdge) componentToDelete);
			break;
		case "ATTRIBUTE":
			removeAttributeReferencesInvolvingThisAttribute((MetamodelAttribute) componentToDelete);
			break;
		}

		switch (selectedItem) {
		case -1:
			// do nothing at all
			break;
		case 0: // Set parent of children to parent of deleted object
			if (componentToDelete.getType().equals("NODE")) {
				for (MetamodelNode node : byDeletedParentAffectedNodes) {
					node.setParent(((MetamodelNode) componentToDelete)
							.getParent());
				}
			} else {
				for (MetamodelEdge edge : byDeletedParentAffectedEdges) {
					edge.setParent(((MetamodelEdge) componentToDelete)
							.getParent());
				}
			}
			removeComponentFromList(componentToDelete);
			writeMetaModelToXML();
			break;
		case 1: // Set parent of children to null
			if (componentToDelete.getType().equals("NODE")) {
				for (MetamodelNode node : byDeletedParentAffectedNodes) {
					node.setParent(null);
				}
			} else {
				for (MetamodelEdge edge : byDeletedParentAffectedEdges) {
					edge.setParent(null);
				}
			}
			removeComponentFromList(componentToDelete);
			writeMetaModelToXML();
			break;
		case 2:
			// do nothing at all
			break;
		case 3: // just remove the component (for components without any 'parent
				// => child' relations)
			removeComponentFromList(componentToDelete);
			writeMetaModelToXML();
		}
	}

	/**
	 * Write the entire metamodel to XML
	 */
	public void writeMetaModelToXML() {

		try {

			DocumentBuilderFactory docFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			// root element
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("COMPONENTS");
			doc.appendChild(rootElement);

			addConjunctions(rootElement, doc);
			addNodes(rootElement, doc);
			addEdges(rootElement, doc);
			addAttributes(rootElement, doc);

			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory
					.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);

			// Path to file
			// String path = System.getProperty("user.dir");
			// path = path.substring(0, path.length() - 9) + "Meta-Modell.xml";
			String path = "C:\\Users\\Meta-Model.xml";

			StreamResult result = new StreamResult(new File(path));

			transformer.transform(source, result);

			System.out.println("File saved!");

		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (TransformerException tfe) {
			tfe.printStackTrace();
		}
	}

	private void addConjunctions(Element rootElement, Document doc) {

		// Conjunctions elements
		Element conjunctionsTag = doc.createElement("CONJUNCTIONS");
		rootElement.appendChild(conjunctionsTag);

		for (MetamodelConjunction component : conjunctions.values()) {
			Element conjunction = doc.createElement("CONJUNCTION");

			conjunction.setAttribute("Tag", component.getTag());
			conjunction.setAttribute("Name", component.getName());

			conjunctionsTag.appendChild(conjunction);
		}

	}

	private void addNodes(Element rootElement, Document doc) {

		// Nodes elements
		Element nodesTag = doc.createElement("NODES");
		rootElement.appendChild(nodesTag);

		for (MetamodelNode currentNode : nodes.values()) {

			Element nodeElement = doc.createElement("NODE");
			nodeElement.setAttribute("Tag", currentNode.getTag());
			nodeElement.setAttribute("Name", currentNode.getName());

			if (currentNode.getParent() != null) {
				nodeElement.setAttribute("Parent", currentNode.getParent()
						.getName());
			}

			if (currentNode.getConnectedAttributes() != null) {
				Element attributes = doc.createElement("SELECTEDATTRIBUTES");

				for (String connectedAttribute : currentNode
						.getConnectedAttributes()) {
					Element attribute = doc.createElement("SELECTEDATTRIBUTE");
					attribute.setAttribute("Name", connectedAttribute);
					attributes.appendChild(attribute);
				}
				nodeElement.appendChild(attributes);
			}
			nodesTag.appendChild(nodeElement);
		}

	}

	private void addEdges(Element rootElement, Document doc) {

		// Edges elements
		Element edgesTag = doc.createElement("EDGES");
		rootElement.appendChild(edgesTag);

		for (MetamodelEdge currentEdge : edges.values()) {

			Element edgeElement = doc.createElement("EDGE");
			edgeElement.setAttribute("Tag", currentEdge.getTag());
			edgeElement.setAttribute("Name", currentEdge.getName());

			if (currentEdge.getParent() != null) {
				edgeElement.setAttribute("Parent", currentEdge.getParent()
						.getName());
			}

			if (currentEdge.getMappings() != null) {

				Element mappings = doc.createElement("MAPPINGS");
				edgeElement.appendChild(mappings);

				for (Tupel currentMapping : currentEdge.getMappings()) {

					Element mapping = doc.createElement("MAPPING");

					mapping.setAttribute("From", currentMapping.getFirst());
					mapping.setAttribute("To", currentMapping.getSecond());

					mappings.appendChild(mapping);
				}
			}

			edgesTag.appendChild(edgeElement);
		}
	}

	private void addAttributes(Element rootElement, Document doc) {

		Element attributeElement = doc.createElement("ATTRIBUTES");
		rootElement.appendChild(attributeElement);

		for (MetamodelAttribute currentAttribute : attributes.values()) {

			Element attribute = doc.createElement("ATTRIBUTE");

			attribute.setAttribute("Tag", currentAttribute.getTag());
			attribute.setAttribute("Name", currentAttribute.getName());

			Element group = doc.createElement("GROUP");
			group.setAttribute("Value", currentAttribute.getAttributeGroup());
			attribute.appendChild(group);

			Element dataType = doc.createElement("DATATYPE");
			dataType.setAttribute("Value",
					currentAttribute.getAttributeDataType());
			attribute.appendChild(dataType);

			Element visible = doc.createElement("VISIBLE");
			visible.setAttribute("Value",
					currentAttribute.getAttributeVisiblity() + "");
			attribute.appendChild(visible);

			Element category = doc.createElement("CATEGORY");
			category.setAttribute("Value",
					currentAttribute.getAttributeCategory());
			attribute.appendChild(category);

			Element unit = doc.createElement("UNIT");
			unit.setAttribute("Value", currentAttribute.getAttributeUnit());
			attribute.appendChild(unit);

			attributeElement.appendChild(attribute);
		}

	}

	public void resetData() {
		conjunctions = PSSIFCanonicMetamodelCreator.conjunctions;
		nodes = PSSIFCanonicMetamodelCreator.nodes;
		edges = PSSIFCanonicMetamodelCreator.edges;
		attributes = PSSIFCanonicMetamodelCreator.attributes;
	}

	public void resetByDeletedParentAffectedNodes(
			MetamodelComponent componentToDelete) {

		// Check for interdependencies
		// Get all involved Components
		for (MetamodelNode node : nodes.values()) {
			// Get every component with the deleted component as a
			// parent
			if (node.getParent() != null
					&& node.getParent().equals(componentToDelete)) {
				byDeletedParentAffectedNodes.add(node);
			}
		}
	}

	public void resetByDeletedParentAffectedEdges(
			MetamodelComponent componentToDelete) {

		// Check for interdependencies
		// Get all involved Components
		for (MetamodelEdge edge : edges.values()) {
			// Get every component with the deleted component as a
			// parent
			if (edge.getParent() != null
					&& edge.getParent().equals(componentToDelete)) {
				byDeletedParentAffectedEdges.add(edge);
			}
		}
	}

	/**
	 * Removes every mapping for edges where the deleted node is involved
	 * 
	 * @param nodeToDelete
	 *            The node to delete
	 * @return
	 */
	private void removeMappingsInvolvingThisNode(MetamodelComponent nodeToDelete) {

		for (MetamodelEdge currentEdge : edges.values()) {
			if (currentEdge.getMappings() != null) {
				ArrayList<Tupel> affectedMappings = new ArrayList<Tupel>();
				for (Tupel mapping : currentEdge.getMappings()) {
					if (mapping.contains(nodeToDelete.getName())) {
						affectedMappings.add(mapping);
					}
				}

				for (Tupel tupel : affectedMappings) {
					currentEdge.getMappings().remove(tupel);
				}
			}
		}
	}

	/**
	 * Removes this attribute from every node having it as an attribute
	 * 
	 * @param attributeToDelete
	 *            The attribute to delete
	 * @return
	 */
	private void removeAttributeReferencesInvolvingThisAttribute(
			MetamodelAttribute attributeToDelete) {

		for (MetamodelNode currentNode : nodes.values()) {

			if (currentNode.getConnectedAttributes() != null
					&& currentNode.getConnectedAttributes().contains(
							attributeToDelete.getName())) {

				currentNode.getConnectedAttributes().remove(
						attributeToDelete.getName());
			}
		}
	}

	public void removeComponentFromList(MetamodelComponent componentToDelete) {
		if (componentToDelete.getType().equals("CONJUNCTION")) {
			conjunctions.remove(componentToDelete.getName());
		} else if (componentToDelete.getType().equals("NODE")) {
			nodes.remove(componentToDelete.getName());
		} else if (componentToDelete.getType().equals("EDGE")) {
			edges.remove(componentToDelete.getName());
		} else {
			attributes.remove(componentToDelete.getName());
		}
	}
}
