package reqtool.controller.wizard;

import model.ModelBuilder;
import de.tum.pssif.core.common.PSSIFConstants;
import de.tum.pssif.core.common.PSSIFOption;
import de.tum.pssif.core.common.PSSIFValue;
import de.tum.pssif.core.metamodel.external.PSSIFCanonicMetamodelCreator;
import reqtool.graph.RequirementFromSpecPopup;
import graph.model.MyEdgeType;
import graph.model.MyNode;
import graph.model.MyNodeType;

/**
 * The Class RequirementWizard.
 */
public class RequirementWizard {
	
	/** The requirement node. */
	private MyNode requirementNode;
	
	/** The specification node. */
	private MyNode specificationNode;
	
	/** The new requirement popup. */
	private RequirementFromSpecPopup newReqPopup;
	
	/**
	 * Instantiates a new requirement wizard.
	 *
	 * @param specNode the specification node
	 */
	public RequirementWizard(MyNode specNode) {
		specificationNode = specNode;
	}
	
	/**
	 * Opens the new project wizard popup.
	 */
	public void open() {
		newReqPopup = new RequirementFromSpecPopup();
		if (newReqPopup.showPopup()) {
			createRequirementNode();
			
			if (newReqPopup.selectedAuthorCheckBox()){
				createAuthor();
			}
			
			if (newReqPopup.selectedPrincipalCheckBox()){
				createPrincipalNode();
			}
			
			if (true){
				createAbsLevelNode();
			}
		}
	}

	/**
	 * Creates the abstraction level node.
	 */
	private void createAbsLevelNode() {
		requirementNode.getNodeType().getType().getAttribute(PSSIFConstants.BUILTIN_ATTRIBUTE_ABS_LEVEL).getOne().set(requirementNode.getNode(), PSSIFOption.one(PSSIFValue.create(newReqPopup.getAbstractionLevelName())));
	}

	/**
	 * Creates the principal node.
	 */
	private void createPrincipalNode() {
		MyNodeType principal = ModelBuilder.getNodeTypes().getValue(PSSIFCanonicMetamodelCreator.TAGS.get("N_PRINCIPAL"));
		MyEdgeType requests = ModelBuilder.getEdgeTypes().getValue(PSSIFCanonicMetamodelCreator.TAGS.get("E_RELATIONSHIP_CAUSAL_REQUESTS"));
		MyNode principalNode = ModelBuilder.addNewNodeFromGUI(newReqPopup.getPrincipalName(), principal);
		ModelBuilder.addNewEdgeGUI(principalNode, requirementNode, requests, true);
	}

	/**
	 * Creates the author node.
	 */
	private void createAuthor() {
		MyNodeType author = ModelBuilder.getNodeTypes().getValue(PSSIFCanonicMetamodelCreator.TAGS.get("N_AUTHOR"));
		MyEdgeType creates = ModelBuilder.getEdgeTypes().getValue(PSSIFCanonicMetamodelCreator.TAGS.get("E_RELATIONSHIP_CAUSAL_CREATES"));
		MyNode authorNode = ModelBuilder.addNewNodeFromGUI(newReqPopup.getAuthorName(), author);
		ModelBuilder.addNewEdgeGUI(authorNode, requirementNode, creates, true);
		
	}

	/**
	 * Creates the requirement node.
	 */
	private void createRequirementNode() {
		MyNodeType requirement = ModelBuilder.getNodeTypes().getValue(PSSIFCanonicMetamodelCreator.TAGS.get("N_REQUIREMENT"));
		MyEdgeType defines = ModelBuilder.getEdgeTypes().getValue(PSSIFCanonicMetamodelCreator.TAGS.get("E_RELATIONSHIP_REFERENTIAL_DEFINES"));
		requirementNode = ModelBuilder.addNewNodeFromGUI(newReqPopup.getReqName(), requirement);
		ModelBuilder.addNewEdgeGUI(specificationNode, requirementNode, defines, true);
	}

}
