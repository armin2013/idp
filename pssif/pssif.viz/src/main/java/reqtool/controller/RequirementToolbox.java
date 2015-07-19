package reqtool.controller;

import java.util.LinkedList;
import java.util.Vector;

import model.ModelBuilder;
import graph.model.MyEdge;
import graph.model.MyEdgeType;
import graph.model.MyNode;
import graph.model.MyNodeType;
import de.tum.pssif.core.common.PSSIFOption;
import de.tum.pssif.core.common.PSSIFValue;
import de.tum.pssif.core.metamodel.Attribute;
import de.tum.pssif.core.metamodel.NodeType;
import de.tum.pssif.core.metamodel.external.PSSIFCanonicMetamodelCreator;
import de.tum.pssif.core.model.Node;

/**
 * The Class RequirementToolbox.
 */
public class RequirementToolbox {

	/**
	 * Gets the MyNode for the given Node.
	 *
	 * @param node the node
	 * @return the my node
	 */
	public static MyNode getMyNode(Node node) {
		for (MyNode n : ModelBuilder.getAllNodes()) {
			if (n.getNode().equals(node)) {
				return n;
			}
		}
		return null;
	}
	
	/**
	 * Gets the attribute value of the node.
	 *
	 * @param node the node
	 * @param attrName the attribute name
	 * @return the attribute value
	 * @throws NullPointerException the exception thrown
	 */
	public static PSSIFOption<PSSIFValue> getAttributeValue(MyNode node, String attrName) throws NullPointerException{
		NodeType currentType = node.getNodeType().getType();
		for (Attribute a : currentType.getAttributes()) {
			if (a.getName().equalsIgnoreCase(attrName)) {
				return a.get(node.getNode());
			
			}
		}
		return null;
	}
	
	/**
	 * Gets the requirement source nodes.
	 *
	 * @param requirementNode the requirement node
	 * @param edgeTypeName the edge type name
	 * @return the requirement source nodes
	 */
	public static LinkedList<MyNode> getRequirementSourceNodes(MyNode requirementNode, String edgeTypeName) {
		LinkedList<MyNode> nodes = new LinkedList<MyNode>();
		for (MyEdge myEdge : ModelBuilder.getAllEdges()) {
			if (myEdge.getDestinationNode().equals(requirementNode)	&& myEdge.getEdgeType().getName().equals(edgeTypeName)) {
				nodes.add((MyNode) myEdge.getSourceNode());
			}
		}
		return nodes;
	}
	
	/**
	 * Gets the requirement source nodes for the given node type.
	 *
	 * @param requirementNode the requirement node
	 * @param edgeTypeName the edge type name
	 * @param nodeTypeName the node type name
	 * @return the requirement source nodes
	 */
	public static LinkedList<MyNode> getRequirementSourceNodes(MyNode requirementNode, String edgeTypeName, String nodeTypeName) {
		LinkedList<MyNode> nodes = new LinkedList<MyNode>();
		for (MyEdge myEdge : ModelBuilder.getAllEdges()) {
			if (myEdge.getDestinationNode().equals(requirementNode)	&& 
				myEdge.getEdgeType().getName().equals(edgeTypeName) && 
				( ( (MyNode) myEdge.getSourceNode()).getNodeType().getType().getName().equals(nodeTypeName)) )
				{
				nodes.add((MyNode) myEdge.getSourceNode());
			}
		}
		return nodes;
	}
	
	/**
	 * Gets the requirement destination nodes.
	 *
	 * @param requirementNode the requirement node
	 * @param edgeTypeName the edge type name
	 * @return the requirement destination nodes
	 */
	public static LinkedList<MyNode> getRequirementDestNodes(MyNode requirementNode, String edgeTypeName) {
		LinkedList<MyNode> nodes = new LinkedList<MyNode>();
		for (MyEdge myEdge : ModelBuilder.getAllEdges()) {
			if (myEdge.getSourceNode().equals(requirementNode)	&& myEdge.getEdgeType().getName().equals(edgeTypeName)) {
				nodes.add((MyNode) myEdge.getDestinationNode());
			}
		}
		return nodes;
	}
	
	/**
	 * Gets the requirement destination nodes for the given node type.
	 *
	 * @param requirementNode the requirement node
	 * @param edgeTypeName the edge type name
	 * @param nodeTypeName the node type name
	 * @return the requirement destination nodes
	 */
	public static LinkedList<MyNode> getRequirementDestNodes(MyNode requirementNode, String edgeTypeName, String nodeTypeName) {
		LinkedList<MyNode> nodes = new LinkedList<MyNode>();
		for (MyEdge myEdge : ModelBuilder.getAllEdges()) {
			if (myEdge.getSourceNode().equals(requirementNode)	&& 
				myEdge.getEdgeType().getName().equals(edgeTypeName) && 
				( (MyNode) myEdge.getDestinationNode()).getNodeType().getName().equals(nodeTypeName)) {
				nodes.add((MyNode) myEdge.getDestinationNode());
			}
		}
		return nodes;
	}
	
	/**
	 * Gets the requirement relation nodes.
	 *
	 * @param requirementNode the requirement node
	 * @param edgeTypeName the edge type name
	 * @param nodeTypeName the node type name
	 * @return the requirement relation nodes
	 */
	public static LinkedList<MyNode> getRequirementRelNodes(MyNode requirementNode, String edgeTypeName, String nodeTypeName) {
		LinkedList<MyNode> nodes = new LinkedList<MyNode>();
		for (MyEdge myEdge : ModelBuilder.getAllEdges()) {
			if ((myEdge.getDestinationNode().equals(requirementNode)	&& 
				myEdge.getEdgeType().getName().equals(edgeTypeName) && 
				( (MyNode) myEdge.getSourceNode()).getNodeType().getName().equals(nodeTypeName)) ||
				
				(myEdge.getSourceNode().equals(requirementNode)	&& 
						myEdge.getEdgeType().getName().equals(edgeTypeName) && 
						( (MyNode) myEdge.getDestinationNode()).getNodeType().getName().equals(nodeTypeName))
				) {
				nodes.add((MyNode) myEdge.getSourceNode());
			}
		}
		return nodes;
	}
	
	/**
	 * Show the nodes from the specification node contained file.
	 *
	 * @param specNode the specification node
	 */
	public static void showContainment(MyNode specNode){
		MyEdgeType contains = ModelBuilder.getEdgeTypes().getValue(PSSIFCanonicMetamodelCreator.TAGS.get("E_RELATIONSHIP_INCLUSION_CONTAINS"));
		for (MyEdge e : ModelBuilder.getAllEdges()){
			if (e.getSourceNode().equals(specNode)&&e.getEdgeType().equals(contains)) {
				getMyNode(e.getDestinationNode().getNode()).setVisible(true);
			}
		}	
	}
	
	/**
	 * Hide the nodes from the specification node contained file.
	 *
	 * @param specNode the specification node
	 */
	public static void hideContainment(MyNode specNode){
		MyEdgeType contains = ModelBuilder.getEdgeTypes().getValue(PSSIFCanonicMetamodelCreator.TAGS.get("E_RELATIONSHIP_INCLUSION_CONTAINS"));
		for (MyEdge e : ModelBuilder.getAllEdges()) {
			if (e.getSourceNode().equals(specNode)&&e.getEdgeType().equals(contains)&&!e.getDestinationNode().equals(specNode)) {
				getMyNode(e.getDestinationNode().getNode()).setVisible(false);
			}
		}	
	}
	
	/**
	 * Checks if a specification node contains any file.
	 *
	 * @param specNode the specification node
	 * @return true, if successful
	 */
	public static boolean hasContainment(MyNode specNode){
		MyEdgeType contains = ModelBuilder.getEdgeTypes().getValue(PSSIFCanonicMetamodelCreator.TAGS.get("E_RELATIONSHIP_INCLUSION_CONTAINS"));
		for (MyEdge e : ModelBuilder.getAllEdges()){
			if (e.getSourceNode().equals(specNode)&&e.getEdgeType().equals(contains)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks if the contained nodes are visible.
	 *
	 * @param specNode the specification node
	 * @return true, if successful
	 */
	public static boolean containmentIsVisible(MyNode specNode) {
		MyEdgeType contains = ModelBuilder.getEdgeTypes().getValue(PSSIFCanonicMetamodelCreator.TAGS.get("E_RELATIONSHIP_INCLUSION_CONTAINS"));
		for (MyEdge e : ModelBuilder.getAllEdges()){
			if (e.getSourceNode().equals(specNode)&&e.getEdgeType().equals(contains)) {
				return e.getDestinationNode().isVisible();
			}
		
		}
		return false;
	}
	
	/**
	 * Gets the specification artifact types.
	 *
	 * @return the specification artifact types
	 */
	public static Vector<MyNodeType> getSpecArtifTypes() {
		Vector<MyNodeType> specificationTypes = new Vector<MyNodeType>();

		MyNodeType specType = ModelBuilder.getNodeTypes().getValue(PSSIFCanonicMetamodelCreator.TAGS.get("N_SPEC_ARTIFACT"));
		specificationTypes.add(specType);

		for (NodeType nodeType : specType.getType().getSpecials()) {
			specificationTypes.add(new MyNodeType(nodeType));
		}
		return specificationTypes;
	}
	
}
