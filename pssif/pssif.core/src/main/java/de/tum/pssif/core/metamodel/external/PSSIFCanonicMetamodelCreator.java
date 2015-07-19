package de.tum.pssif.core.metamodel.external;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import de.tum.pssif.core.common.PSSIFConstants;
import de.tum.pssif.core.common.PSSIFOption;
import de.tum.pssif.core.metamodel.AttributeCategory;
import de.tum.pssif.core.metamodel.AttributeGroup;
import de.tum.pssif.core.metamodel.DataType;
import de.tum.pssif.core.metamodel.EdgeType;
import de.tum.pssif.core.metamodel.JunctionNodeType;
import de.tum.pssif.core.metamodel.Metamodel;
import de.tum.pssif.core.metamodel.NodeType;
import de.tum.pssif.core.metamodel.PrimitiveDataType;
import de.tum.pssif.core.metamodel.Unit;
import de.tum.pssif.core.metamodel.Units;
import de.tum.pssif.core.metamodel.impl.MetamodelImpl;
import de.tum.pssif.core.metamodel.mutable.MutableEdgeType;
import de.tum.pssif.core.metamodel.mutable.MutableElementType;
import de.tum.pssif.core.metamodel.mutable.MutableEnumeration;
import de.tum.pssif.core.metamodel.mutable.MutableJunctionNodeType;
import de.tum.pssif.core.metamodel.mutable.MutableNodeType;
import de.tum.pssif.core.model.Tupel;

/**
 * Creates the metamodel out of imported data
 * 
 * @author Alex
 * 
 */
public final class PSSIFCanonicMetamodelCreator {

	public static HashMap<String, String> TAGS = new HashMap<String, String>();
	private static MetamodelImportRDF importer;
	public static HashMap<String, MetamodelConjunction> conjunctions;
	public static HashMap<String, MetamodelNode> nodes;
	public static HashMap<String, MetamodelEdge> edges;
	public static HashMap<String, MetamodelAttribute> attributes;
	
	/**
	 * Initiate the creation of the metamodel
	 * 
	 * @return The requested metamodel
	 */
	public static Metamodel create() {
		MetamodelImpl metamodel = new MetamodelImpl();
		
		loadXMLData();
		createEnumerations(metamodel);
		createNodeTypes(metamodel);
		createEdgeTypes(metamodel);
		createInternalInheritanceRelations(metamodel);

		return metamodel;
	}

	/**
	 * Get Node from Metamodel
	 * 
	 * @param name
	 *            name of Nodn
	 * @param metamodel
	 *            metamodel to extract the node from
	 * @return Requested node
	 */
	private static NodeType node(String name, Metamodel metamodel) {
		return metamodel.getNodeType(name).getOne();
	}

	/**
	 * Get Edge from Metamodel
	 * 
	 * @param name
	 *            name of edge
	 * @param metamodel
	 *            metamodel to extract the edge from
	 * @return Requested edge
	 */
	private static EdgeType edge(String name, Metamodel metamodel) {
		return metamodel.getEdgeType(name).getOne();
	}

	/**
	 * Create the conjunctions in the metamodel as they are described in the
	 * imported components
	 * 
	 * @param metamodel
	 *            metamodel to create the conjunctions in
	 * @param importedConjunctions
	 *            conjunctions imported via xml
	 */
	private static void createEnumerations(MetamodelImpl metamodel) {

		// Case: Every Conjunction was deleted
		if (conjunctions.size() == 0) {
			return;
		}
		
//		MutableEnumeration conjunction = metamodel
//				.createEnumeration(conjunctions.get("Conjunction").getName());
		MutableEnumeration conjunction = metamodel
				.createEnumeration("Conjunction");
		
		for(String key : conjunctions.keySet()) {
			if(!key.equals("Conjunction")) {
				conjunction.createLiteral(conjunctions.get(key).getName());
			}
		}
	}

	/**
	 * Create all nodes
	 * 
	 * @param metamodel
	 *            model to add the nodes to
	 * @param importedNodes
	 *            nodes imported via the xml file
	 */
	private static void createNodeTypes(MetamodelImpl metamodel) {

		// Case: Every Node was deleted
		if (nodes.size() == 0) {
			return;
		}

		// Add new node and eventually attributes (MutableNodeType)
		for (MetamodelNode node : nodes.values()) {

			if (node.getConnectedAttributes() != null) {

				if (!node.getName().equals("Conjunction")) {
					MutableNodeType newNode = metamodel.createNodeType(node
							.getName());

					for (String attribute : node.getConnectedAttributes()) {

						setAttributes(attribute, newNode);
					}
				} else {
					MutableJunctionNodeType junction = metamodel
							.createJunctionNodeType(node.getName());

					for (String attribute : node.getConnectedAttributes()) {

						setAttributes(attribute, junction);
					}
				}
			} else {
				metamodel.createNodeType(node.getName());
			}
		}
	}

	/**
	 * Set an attribute of a node
	 * 
	 * @param attribute
	 *            components of each attribute
	 * @param newNode
	 *            the node to add the attribute to
	 */
	private static void setAttributes(String attributeName,
			MutableElementType newNode) {

		// Get the matching attribute
		MetamodelAttribute matchingAttribute = attributes.get(attributeName);

		// Set name
		String name = matchingAttribute.getName();

		// Set group
		AttributeGroup group = null;
		Collection<AttributeGroup> attributeGroups = newNode
				.getAttributeGroups(); // zurzeit nur default?
		for (AttributeGroup attributeGroup : attributeGroups) {
			if (attributeGroup.getName().equals(
					matchingAttribute.getAttributeGroup())) {
				group = attributeGroup;
				break;
			}
		}

		// Set dataType
		DataType dataType = null;
		for (DataType type : PrimitiveDataType.TYPES) {
			if (type.getName().equals(matchingAttribute.getAttributeDataType())) {
				dataType = type;
				break;
			}
		}

		// Set visibility
		boolean visible = true;
		if (matchingAttribute.getAttributeVisiblity() == false) {
			visible = false;
		}

		// Set Category
		AttributeCategory category = null;
		for (AttributeCategory attCat : AttributeCategory.values()) {
			if (attCat.getName().equals(
					matchingAttribute.getAttributeCategory())) {
				category = attCat;
				break;
			}
		}

		// Set Unit
		Unit unit = null;

		for (Unit type : Units.UNITS) {
			if (type.getName().equals(matchingAttribute.getAttributeUnit())) {
				unit = type;
				break;
			}
		}
		// Create Attribute with unit
		newNode.createAttribute(group, name, dataType, unit, visible, category);
	}

	/**
	 * Create all edges
	 * 
	 * @param metamodel
	 *            model to add the edges to
	 * @param importedEdges
	 *            edges imported via the xml file
	 */
	private static void createEdgeTypes(MetamodelImpl metamodel) {

		// Case: Every Edge was deleted
		if (edges.size() == 0) {
			return;
		}

		// Create all Edges and eventually set mappings (MutableEdgeType)
		for (MetamodelEdge edge : edges.values()) {

			if (edge.getMappings() != null) {
				MutableEdgeType newEdge = metamodel.createEdgeType(edge
						.getName());

				for (Tupel mapping : edge.getMappings()) {

					String from = mapping.getFirst();
					String to = mapping.getSecond();

					// Every child of flow has an addition "junction" property
					// when created
					if (edge.getTempParent() != null
							&& edge.getTempParent().equals("Flow")) {
						PSSIFOption<JunctionNodeType> junction = metamodel
								.getJunctionNodeType("Conjunction");
						newEdge.createMapping(node(from, metamodel),
								node(from, metamodel), junction);
					} else {
						newEdge.createMapping(node(from, metamodel),
								node(to, metamodel));
					}
				}
			} else {
				metamodel.createEdgeType(edge.getName());
			}
		}
	}

	/**
	 * Iterate through the components and connect them if they inherit from each
	 * other
	 * 
	 * @param metamodel
	 *            model in which the connections are created in
	 * @param importedNodes
	 *            nodes imported via xml
	 * @param importedEdges
	 *            edges imported via xml
	 */
	private static void createInternalInheritanceRelations(
			MetamodelImpl metamodel) {

		for (MetamodelEdge edge : edges.values()) {
			if (edge.getTempParent() != null) {
				edge(edge.getName(), metamodel).inherit(
						edge(edge.getTempParent(), metamodel));
			}
		}

		for (MetamodelNode node : nodes.values()) {
			if (node.getTempParent() != null) {
				node(node.getName(), metamodel).inherit(
						node(node.getTempParent(), metamodel));
			}
		}
	}

	/**
	 * Set the components within the metamodel in relation to each other
	 */
	private static void createExternalInheritanceRelations() {
		for (MetamodelNode node : nodes.values()) {
			if (node.getTempParent() != null) {
				node.setParent(nodes.get(node.getTempParent()));
			}
		}

		for (MetamodelEdge edge : edges.values()) {
			if (edge.getTempParent() != null) {
				edge.setParent(edges.get(edge.getTempParent()));
			}
		}
	}

	/**
	 * Generate all data used in this class
	 */
	public static void loadXMLData() {
		// Import current version of the canonic Metamodel as stated in the .xml
		importer = new MetamodelImportRDF();
		importer.runParser();
		TAGS = importer.getTagMap();

		conjunctions = importer.getConjunctions();
		nodes = importer.getNodes();
		edges = importer.getEdges();
		attributes = importer.getAttributes();

		createExternalInheritanceRelations();
	}
}