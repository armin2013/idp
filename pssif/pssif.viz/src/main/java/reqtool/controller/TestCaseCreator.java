package reqtool.controller;

import graph.model.MyEdgeType;
import graph.model.MyNode;
import graph.model.MyNodeType;

import java.util.List;

import model.ModelBuilder;
import de.tum.pssif.core.metamodel.external.PSSIFCanonicMetamodelCreator;

/**
 * The Class TestCaseCreator.
 */
public class TestCaseCreator {

	/**
	 * Creates the test case node.
	 *
	 * @param mNode the requirement node for which the test case should be created
	 * @param solutionArtifacts the solution artifacts
	 * @param name the name
	 */
	public static void createTestCase(MyNode mNode, List<MyNode> solutionArtifacts, String name) {
		System.out.println("Creating Test Case for " + mNode.getName());

		MyNodeType testCaseNodeType = ModelBuilder.getNodeTypes().getValue(PSSIFCanonicMetamodelCreator.TAGS.get("N_TEST_CASE"));

		MyEdgeType basedOn = ModelBuilder.getEdgeTypes().getValue(PSSIFCanonicMetamodelCreator.TAGS.get("E_RELATIONSHIP_CHRONOLOGICAL_BASED_ON"));

		MyNode testCaseNode = ModelBuilder.addNewNodeFromGUI(name, testCaseNodeType);

		/*
		 * Create edge "based on" between testcase and requirement
		*/
		ModelBuilder.addNewEdgeGUI(testCaseNode, mNode, basedOn, true);

		/*
		 * Create verifies edge between testcase and solution artifact requirement
		 */
		createVerifyEdges(solutionArtifacts, testCaseNode);
	}

	/**
	 * Creates the verify edges.
	 *
	 * @param solArtifNodes the sol artif nodes
	 * @param testCaseNode the test case node
	 * @return true, if successful
	 */
	private static boolean createVerifyEdges(List<MyNode> solArtifNodes, MyNode testCaseNode) {
		MyEdgeType verifies = ModelBuilder.getEdgeTypes().getValue(PSSIFCanonicMetamodelCreator.TAGS.get("E_RELATIONSHIP_LOGICAL_VERIFIES"));
		for(MyNode solArtfNode:solArtifNodes) {
			ModelBuilder.addNewEdgeGUI(testCaseNode, solArtfNode, verifies, true);
		}
		return true;
	}

}