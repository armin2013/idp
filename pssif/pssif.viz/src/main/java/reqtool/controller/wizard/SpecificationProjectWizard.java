package reqtool.controller.wizard;

import graph.model.MyEdgeType;
import graph.model.MyNode;

import java.awt.Component;

import model.FileImporter;
import model.ModelBuilder;
import reqtool.graph.SpecificationNodePopup;
import de.tum.pssif.core.common.PSSIFOption;
import de.tum.pssif.core.metamodel.external.PSSIFCanonicMetamodelCreator;
import de.tum.pssif.core.model.Model;
import de.tum.pssif.core.model.Node;
import de.tum.pssif.core.model.impl.ModelImpl;

/**
 * The Class SpecificationProjectWizard.
 */
public class SpecificationProjectWizard {
	
	/** The new specification project wizard popup. */
	private SpecificationNodePopup newSpecPopup;
	
	/** The specification artifact node. */
	private MyNode specificationArtifactNode;
	
	/**
	 * Instantiates a new specification project wizard.
	 */
	public SpecificationProjectWizard() {
		initModel();
	}

	/**
	 * Inits the model with the metamodel.
	 */
	private void initModel() {
		Model model = new ModelImpl();
		if (model!=null) {
	       ModelBuilder.addModel(ModelBuilder.getMetamodel(), model);
        }
	}

	/**
	 * Opens the specification wizard popup.
	 *
	 * @param frame the frame
	 * @return true, if successful
	 */
	public boolean open(Component frame) {
		initModel();
		newSpecPopup = new SpecificationNodePopup();

		if (newSpecPopup.showPopup()) {
			
			if (!newSpecPopup.selectedFileImport()) { 
				createSpecificationNode();
			} else if (newSpecPopup.selectedFileImport()) {
				FileImporter importer = new FileImporter();
				
				if (importer.showPopup(frame)) {
					createSpecificationNode();
					createEdges(importer.getLastImportedModel());
				}
			}
			
			if (newSpecPopup.selectedNewReq()) {
				new RequirementWizard(specificationArtifactNode).open();
			}

			return true;
		}

		return false;
	}

	/**
	 * Creates the edges.
	 *
	 * @param newModel the new model
	 */
	private void createEdges(Model newModel/*LinkedList<MyNode> excludedNodes*/) {
		MyEdgeType contains = ModelBuilder.getEdgeTypes().getValue(PSSIFCanonicMetamodelCreator.TAGS.get("E_RELATIONSHIP_INCLUSION_CONTAINS"));
		for (MyNode node : ModelBuilder.getAllNodes()) {
			PSSIFOption<? extends Node> result = node.getNodeType().getType().apply(newModel, node.getNode().getId(), false);
			if (!result.isNone() && !result.getOne().equals(specificationArtifactNode.getNode())) {
				ModelBuilder.addNewEdgeGUI(specificationArtifactNode, node, contains, true);
			}
		}
	}

	/**
	 * Creates the specification node.
	 */
	private void createSpecificationNode() {
		specificationArtifactNode = ModelBuilder.addNewNodeFromGUI(newSpecPopup.getSpecArtifName(), newSpecPopup.getSelectedSpecArtifType());
	}
	
	/**
	 * Gets the specification artifact node.
	 *
	 * @return the specification artifact node
	 */
	public MyNode getSpecificationArtifactNode() {
		return specificationArtifactNode;
	}

	/**
	 * Sets the specification artifact node.
	 *
	 * @param specificationArtifactNode the new specification artifact node
	 */
	public void setSpecificationArtifactNode(MyNode specificationArtifactNode) {
		this.specificationArtifactNode = specificationArtifactNode;
	}
	
}
