package reqtool.controller;

import java.util.LinkedList;

import model.ModelBuilder;
import graph.model.MyEdge;
import graph.model.MyEdgeType;
import graph.model.MyNode;
import graph.model.MyNodeType;
import gui.graph.GraphVisualization;
import de.tum.pssif.core.common.PSSIFConstants;
import de.tum.pssif.core.common.PSSIFOption;
import de.tum.pssif.core.common.PSSIFValue;
import de.tum.pssif.core.metamodel.NodeType;
import de.tum.pssif.core.metamodel.external.PSSIFCanonicMetamodelCreator;

/**
 * The Class RequirementVersionManager.
 */
public class RequirementVersionManager {

	/**
	 * Creates the new version node.
	 *
	 * @param gViz the graph visualization
	 * @param node the old version node
	 * @param newVersion the new version
	 * @return true, if successful
	 */
	public static boolean createNewVersion(GraphVisualization gViz, MyNode node, String newVersion) {
		MyNode mNode = node;
		MyNodeType requirementNodeType = ModelBuilder.getNodeTypes().getValue(PSSIFCanonicMetamodelCreator.TAGS.get("N_REQUIREMENT"));
		MyEdgeType evolvesTo = ModelBuilder.getEdgeTypes().getValue(PSSIFCanonicMetamodelCreator.TAGS.get("E_RELATIONSHIP_CHRONOLOGICAL_EVOLVES_TO"));
	
		NodeType nodeType = ModelBuilder.getMetamodel().getNodeType(requirementNodeType.getName()).getOne();
	
		PSSIFOption<PSSIFValue> oldVersion = nodeType.getAttribute(PSSIFConstants.BUILTIN_ATTRIBUTE_VERSION).getOne().get(node.getNode());
		
		if (oldVersion.isNone()) {
			nodeType.getAttribute(PSSIFConstants.BUILTIN_ATTRIBUTE_VERSION).getOne().set(node.getNode(), PSSIFOption.one(PSSIFValue.create("0.001")));
		oldVersion = nodeType.getAttribute(PSSIFConstants.BUILTIN_ATTRIBUTE_VERSION).getOne().get(node.getNode());
	}
	
	String oVersion = oldVersion.getOne().asString();
	
	String[] idNV = mNode.getNode().getId().toString().split("_");
	
	if (!hasNextVersions(mNode) && !hasPreviousVersions(mNode)){
		/*TODO
		 * When Requirement has no other versions.
		 */
		System.out.println("This Requirement has no previous or next versions");
		
		ModelBuilder.addNewNodeFromGUI(mNode.getNode().getId() + "_", requirementNodeType);
		
		for (MyNode newNode : ModelBuilder.getAllNodes()) {
			String[] idNVNew = newNode.getNode().getId().toString().split("_");
			if (idNVNew.length > 0 && idNVNew[0].equals(idNV[0])) {
				PSSIFOption<PSSIFValue> version2 = nodeType.getAttribute(PSSIFConstants.BUILTIN_ATTRIBUTE_VERSION).getOne().get(newNode.getNode());
				if (version2.isNone()) {
					ModelBuilder.addNewEdgeGUI(newNode, mNode, evolvesTo, true);
					newNode.getNodeType().getType().getAttribute(PSSIFConstants.BUILTIN_ATTRIBUTE_VERSION).getOne().set(newNode.getNode(), PSSIFOption.one(PSSIFValue.create(String.valueOf(oVersion))));
					newNode.getNodeType().getType().getAttribute(PSSIFConstants.BUILTIN_ATTRIBUTE_ID).getOne().set(newNode.getNode(), PSSIFOption.one(PSSIFValue.create(mNode.getNode().getId()
							+ "_" + oVersion)));
					newNode.getNodeType().getType().getAttribute(PSSIFConstants.BUILTIN_ATTRIBUTE_NAME).getOne().set(newNode.getNode(), PSSIFOption.one(PSSIFValue.create(mNode.getName())));
					mNode.getNodeType().getType().getAttribute(PSSIFConstants.BUILTIN_ATTRIBUTE_VERSION).getOne().set(mNode.getNode(), PSSIFOption.one(PSSIFValue.create(String.valueOf(newVersion))));
					return true;
				}
			}
		}
	}
	
	if (!hasNextVersions(mNode)) {
		System.out.println("Creating new version for " + mNode.getName());
		if (!newVersion.equals(oVersion)) {
			ModelBuilder.addNewNodeFromGUI(mNode.getNode().getId() + "_", requirementNodeType);
	
			MyNode newVersionNode = null;
			MyNode minVersionNode = null;
			MyNode maxVersionNode = null;
			double minVersion = Integer.MAX_VALUE;
			double maxVersion = Integer.MIN_VALUE;
			
			for (MyNode newNode : ModelBuilder.getAllNodes()) {
				String[] idNVNew = newNode.getNode().getId().toString().split("_");
				if (idNVNew.length > 0 && idNVNew[0].equals(idNV[0])) {
					PSSIFOption<PSSIFValue> version2 = nodeType.getAttribute(PSSIFConstants.BUILTIN_ATTRIBUTE_VERSION).getOne().get(newNode.getNode());
					if (version2.isNone()) {
						newVersionNode = newNode;
					} else {
						double version2Dbl = Double.parseDouble(version2.getOne().asString());
						if (version2Dbl < minVersion) {
							minVersion = version2Dbl;
							minVersionNode = newNode;
						}
						if (version2Dbl > maxVersion) {
							maxVersion = version2Dbl;
							maxVersionNode = newNode;
						}
					}
				}
			}
	
			MyEdge edge = null;
			MyNode sourceNode = null;
			for (MyEdge myEdge : ModelBuilder.getAllEdges()) {
				if (myEdge.getDestinationNode().equals(mNode)
						&& myEdge.getEdgeType().equals(evolvesTo)) {
					edge = myEdge;
					sourceNode = (MyNode) myEdge.getSourceNode();
				}
			}
			ModelBuilder.getAllEdges().remove(edge);
			ModelBuilder.addNewEdgeGUI(sourceNode, newVersionNode, evolvesTo, true);
			ModelBuilder.addNewEdgeGUI(newVersionNode, mNode, evolvesTo, true);
	
			newVersionNode.getNodeType().getType().getAttribute(PSSIFConstants.BUILTIN_ATTRIBUTE_VERSION).getOne().set(newVersionNode.getNode(), PSSIFOption.one(PSSIFValue.create(String.valueOf(maxVersion))));
			newVersionNode.getNodeType().getType().getAttribute(PSSIFConstants.BUILTIN_ATTRIBUTE_ID).getOne().set(newVersionNode.getNode(), PSSIFOption.one(PSSIFValue.create(mNode.getNode().getId()
					+ "_" + maxVersion)));
			newVersionNode.getNodeType().getType().getAttribute(PSSIFConstants.BUILTIN_ATTRIBUTE_NAME).getOne().set(newVersionNode.getNode(), PSSIFOption.one(PSSIFValue.create(mNode.getName())));
			mNode.getNodeType().getType().getAttribute(PSSIFConstants.BUILTIN_ATTRIBUTE_VERSION).getOne().set(mNode.getNode(), PSSIFOption.one(PSSIFValue.create(String.valueOf(newVersion))));
			return true;
				
		} else {
			System.out.println("Cannot create new requirement with same version!");
			return false;
		}
		
	
		/*
		 * TODO
		 * 
		 * Create new node with same attributes, except version. Take new
		 * edges from old node for the new one, and connect the old one
		 * through directed "Evolves-To" to new node.
		 */
		} else {
			createNewVersion(gViz, getMaxVersion(mNode), newVersion);
			return true;
		}
	}
	
	/**
	 * Checks for previous node versions.
	 *
	 * @param myNode the old version node
	 * @return true, if successful
	 */
	public static boolean hasPreviousVersions(MyNode myNode) {
		for (MyEdge e : ModelBuilder.getAllEdges()) {
			if (e.getEdgeType().getName().equals(PSSIFCanonicMetamodelCreator.TAGS.get("E_RELATIONSHIP_CHRONOLOGICAL_EVOLVES_TO"))
					&& e.getDestinationNode().getNode().equals(myNode.getNode())) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Checks for next versions.
	 *
	 * @param myNode the my node
	 * @return true, if successful
	 */
	public static boolean hasNextVersions(MyNode myNode) {
		for (MyEdge e : ModelBuilder.getAllEdges()) {
			if (e.getEdgeType().getName().equals(PSSIFCanonicMetamodelCreator.TAGS.get("E_RELATIONSHIP_CHRONOLOGICAL_EVOLVES_TO"))
					&& e.getSourceNode().getNode().equals(myNode.getNode())) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Gets the max version.
	 *
	 * @param myNode the my node
	 * @return the max version
	 */
	public static MyNode getMaxVersion(MyNode myNode) {
		MyNodeType requirementNodeType = ModelBuilder.getNodeTypes().getValue(PSSIFCanonicMetamodelCreator.TAGS.get("N_REQUIREMENT"));
		NodeType nodeType = ModelBuilder.getMetamodel().getNodeType(requirementNodeType.getName()).getOne();
		MyNode maxVersionNode = null;
		double maxVersion = Integer.MIN_VALUE;
		String[] idNV = myNode.getNode().getId().toString().split("_");
		for (MyNode newNode : ModelBuilder.getAllNodes()) {
			String[] idNVNew = newNode.getNode().getId().toString().split("_");
			if (idNVNew.length > 0 && idNVNew[0].equals(idNV[0])) {
				PSSIFOption<PSSIFValue> version2 = nodeType.getAttribute(PSSIFConstants.BUILTIN_ATTRIBUTE_VERSION).getOne().get(newNode.getNode());
				if (!version2.isNone()) {

					double version2Dbl = Double.parseDouble(version2.getOne().asString());
					if (version2Dbl > maxVersion) {
						maxVersion = version2Dbl;
						maxVersionNode = newNode;
					}
				}
			}
		}
		return maxVersionNode;
	}
	
		/**
	 * Hide versions.
	 *
	 * @param myNode the my node
	 */
	public static void hideVersions(MyNode myNode) {
		MyNode parentNode = getMaxVersion(myNode);
		LinkedList<MyNode> toBeHidden = new LinkedList<MyNode>();
		String[] idNV = myNode.getNode().getId().toString().split("_");
		
		for (MyNode mN : ModelBuilder.getAllNodes()) {
			String[] idNVNew = mN.getNode().getId().toString().split("_");
				if (idNVNew.length > 0 && idNVNew[0].equals(idNV[0])
						&& !mN.equals(parentNode)) {
					toBeHidden.add(mN);
				}
		}
		
		for (MyEdge e: ModelBuilder.getAllEdges()){
			if ( toBeHidden.contains((MyNode)e.getDestinationNode()) && ((MyNode)e.getSourceNode()).getNodeType().toString().equals(PSSIFCanonicMetamodelCreator.TAGS.get("N_TEST_CASE")) ){
				toBeHidden.add((MyNode)e.getSourceNode());
			}
		}
		
		for (MyNode n : toBeHidden){
			n.setVisible(false);
		}
	}
	
	/**
	 * Show versions.
	 *
	 * @param myNode the my node
	 */
	public static void showVersions(MyNode myNode) {
		MyNode parentNode = getMaxVersion(myNode);
		String[] idNV = myNode.getNode().getId().toString().split("_");
		LinkedList<MyNode> toBeShown = new LinkedList<MyNode>();
		for (MyNode mN : ModelBuilder.getAllNodes()) {
			String[] idNVNew = mN.getNode().getId().toString().split("_");
				if (idNVNew.length > 0 && idNVNew[0].equals(idNV[0])
						&& !mN.equals(parentNode)) {
					toBeShown.add(mN);
				}
		}
	
		for (MyEdge e: ModelBuilder.getAllEdges()){
			if ( toBeShown.contains((MyNode)e.getDestinationNode()) && ((MyNode)e.getSourceNode()).getNodeType().toString().equals(PSSIFCanonicMetamodelCreator.TAGS.get("N_TEST_CASE")) ){
				toBeShown.add((MyNode)e.getSourceNode());
			}
		}
		
		for (MyNode n : toBeShown){
			n.setVisible(true);
		}
	
	}
	
	/**
	 * Gets the min version.
	 *
	 * @param myNode the node
	 * @return the min version
	 */
	public static MyNode getMinVersion(MyNode myNode) {
		MyNodeType requirementNodeType = ModelBuilder.getNodeTypes().getValue(PSSIFCanonicMetamodelCreator.TAGS.get("N_REQUIREMENT"));
		NodeType nodeType = ModelBuilder.getMetamodel().getNodeType(requirementNodeType.getName()).getOne();
		
		MyNode minVersionNode = myNode;
		double minVersion = Integer.MAX_VALUE;
		String[] idNV = myNode.getNode().getId().toString().split("_");
		for (MyNode newNode : ModelBuilder.getAllNodes()) {
		String[] idNVNew = newNode.getNode().getId().toString().split("_");
				if (idNVNew.length > 0 && idNVNew[0].equals(idNV[0])) {
					PSSIFOption<PSSIFValue> version2 = nodeType.getAttribute(PSSIFConstants.BUILTIN_ATTRIBUTE_VERSION).getOne().get(newNode.getNode());
					if (!version2.isNone()){	
					double version2Dbl = Double.parseDouble(version2.getOne().asString());
						if (version2Dbl < minVersion) {
							minVersion = version2Dbl;
							minVersionNode = newNode;
							}
						}
					}
				}
				
		return minVersionNode;
	}

}
