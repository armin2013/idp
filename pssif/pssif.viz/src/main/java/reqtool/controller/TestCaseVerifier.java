package reqtool.controller;

import graph.model.MyEdge;
import graph.model.MyEdgeType;
import graph.model.MyNode;
import graph.model.MyNodeType;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import model.ModelBuilder;
import reqtool.graph.TestCaseValidatorPopup;
import de.tum.pssif.core.metamodel.Attribute;
import de.tum.pssif.core.metamodel.DataType;
import de.tum.pssif.core.metamodel.external.PSSIFCanonicMetamodelCreator;

/**
 * The Class TestCaseVerifier.
 */
public class TestCaseVerifier {
	
	/** The selected node. */
	private MyNode myNode;
	
	/**
	 * Instantiates a new test case verifier.
	 *
	 * @param node the node
	 */
	public TestCaseVerifier(MyNode node) {
		this.myNode = node;
	}

	/**
	 * Verify test case.
	 */
	public void verifyTestCase() {
		Map<String, DataType> attributes = getSolArtifactAttributes(getVerifiedSolArtifacts());
		TestCaseValidatorPopup popUp = new TestCaseValidatorPopup(getVerifiedSolArtifacts(), attributes, myNode);
		int result = popUp.showPopup();

		switch (result) {
		case TestCaseValidatorPopup.RESULT_OK:
			System.out.println("Validation successfull");
			myNode.updateAttribute(PSSIFCanonicMetamodelCreator.TAGS.get("A_TEST_CASE_STATUS"), "Succes");
			break;
		case TestCaseValidatorPopup.RESULT_NOK:
			System.out.println("Validation not successfull");
			
			MyNodeType issueNodeType = ModelBuilder.getNodeTypes().getValue(PSSIFCanonicMetamodelCreator.TAGS.get("N_ISSUE"));
			MyEdgeType leadsTo = ModelBuilder.getEdgeTypes().getValue(PSSIFCanonicMetamodelCreator.TAGS.get("E_RELATIONSHIP_CHRONOLOGICAL_LEADS_TO"));
			ModelBuilder.addNewEdgeGUI(myNode, ModelBuilder.addNewNodeFromGUI(myNode.getNode().getId(), issueNodeType), leadsTo, true);
			
			myNode.updateAttribute(PSSIFCanonicMetamodelCreator.TAGS.get("A_TEST_CASE_STATUS"), "Fail");
			
			break;
		case TestCaseValidatorPopup.RESULT_CANCEL:
			System.out.println("Test Case verification error");
			break;

		default:
			break;
		}
	}

	/**
	 * Gets the verified solution artifacts.
	 *
	 * @return the verified solution artifacts
	 */
	public LinkedList<MyNode> getVerifiedSolArtifacts() {
		MyEdgeType verifies = ModelBuilder.getEdgeTypes().getValue(PSSIFCanonicMetamodelCreator.TAGS.get("E_RELATIONSHIP_LOGICAL_VERIFIES"));
		LinkedList<MyNode> verSolArtifacts = new LinkedList<MyNode>();
		for (MyEdge e : ModelBuilder.getAllEdges()) {
			if (e.getSourceNode().equals(myNode)
					&& e.getEdgeType().equals(verifies)) {

				verSolArtifacts.add((MyNode) e.getDestinationNode());
				System.out.println(e.getDestinationNode().getName());
			}
		}

		return verSolArtifacts;
	}

	/**
	 * Gets the solution artifact attributes.
	 *
	 * @param verifiedSolArtifacts the verified solution artifacts
	 * @return the solution artifact attributes
	 */
	public Map<String, DataType> getSolArtifactAttributes(LinkedList<MyNode> verifiedSolArtifacts) {
		Map<String, DataType> attrNames = new TreeMap<String, DataType>();

		for (MyNode n : verifiedSolArtifacts) {
			if (attrNames.size() == 0) {
				for (Entry<String, Attribute> attr : n.getAttributesHashMap().entrySet()) {
					attrNames.put(attr.getKey(), attr.getValue().getType());
				}
			} else {
				List<String> keysToRemove = new ArrayList<String>();
				for (Entry<String, DataType> attrKey : attrNames.entrySet()) {
					if (!n.getAttributesHashMap().containsKey(attrKey.getKey())) {
						keysToRemove.add(attrKey.getKey());
					}
				}
				for (String key : keysToRemove) {
					attrNames.remove(key);
				}
				keysToRemove.clear();
			}
		}

		return attrNames;
	}

}
