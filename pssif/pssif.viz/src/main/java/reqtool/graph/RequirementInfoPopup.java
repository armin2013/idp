package reqtool.graph;

import graph.model.MyNode;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import reqtool.controller.RequirementToolbox;
import de.tum.pssif.core.metamodel.external.PSSIFCanonicMetamodelCreator;

/**
 * The Class RequirementInfoPopup.
 */
public class RequirementInfoPopup {
	
	/** The requirement node. */
	private MyNode reqNode;
	
	/** The all panel. */
	private JPanel allPanel;
	
	/** The panel constraints. */
	private GridBagConstraints constr;
	
	/**
	 * Instantiates a new requirement info popup.
	 *
	 * @param reqNode the requirement node
	 */
	public RequirementInfoPopup(MyNode reqNode) {
		this.reqNode = reqNode;
	}
	
	/**
	 * Show the popup for reading the requirement node informations.
	 *
	 * @return true, if successful
	 */
	public boolean showPopup() {
		createPanel();
		
		int dialogResult = JOptionPane.showConfirmDialog(null, allPanel, "Requirement informations", JOptionPane.DEFAULT_OPTION);

		return evalDialog(dialogResult);
	}
	
	
	
	/**
	 * Evaluates the user input from the dialog
	 *
	 * @param dialogResult the dialog result
	 * @return true, if successful
	 */
	private boolean evalDialog(int dialogResult) {
		if (dialogResult == 0) {
			return true;
		}
		return false;
	}
	
	/**
	 * Creates the panel.
	 */
	private void createPanel() {
		getConstraints().gridx = 0;
		getConstraints().gridx = 0;
		addAttributes();
		
		getConstraints().gridx= 0 ;
		getConstraints().gridy = 3;
		addVersionsCount();
		
		getConstraints().gridy = 10;
		getConstraints().insets = new Insets(50, 50, 0, 0);
		getConstraints().gridx = 0 ;
		addSolArtifNodes();
		
		getConstraints().gridy = 10;
		getConstraints().insets = new Insets(50, 50, 0, 0);
		addSpecArtifNodes();
		
		getConstraints().gridy = 10;
		getConstraints().insets = new Insets(50, 50, 0, 0);
		addAuthorNodes();
		
		getConstraints().gridy = 10;
		getConstraints().insets = new Insets(50, 50, 0, 0);
		addPrincNodes();
		
		getConstraints().gridy = 10;
		getConstraints().insets = new Insets(50, 50, 0, 0);
		List<MyNode> tcNodes = addTestCaseNodes();
		
		getConstraints().gridy = 10;
		getConstraints().insets = new Insets(50, 50, 0, 0);
		addIssueNodes(tcNodes);
		
		getConstraints().gridy = 10;
		getConstraints().insets = new Insets(50, 50, 0, 0);
		addChangeEventNodes();
	}
	
	/**
	 * Adds the versions count.
	 */
	private void addVersionsCount() {
		// TODO Auto-generated method stub
	}
	
	/**
	 * Adds the solution artifact nodes.
	 */
	private void addSolArtifNodes() {
		JLabel labelTxt = new JLabel("<html><B> Solution Artifacts </B></html>");
		getConstraints().gridx++;
		getPanel().add(labelTxt, getConstraints());
		int i=0;
		getConstraints().insets = new Insets(5, 50, 0, 0);
		for (MyNode node : RequirementToolbox.getRequirementSourceNodes(reqNode, PSSIFCanonicMetamodelCreator.TAGS.get("E_RELATIONSHIP_LOGICAL_SATISFIES"))) {
			getConstraints().gridy+=i;
			addMultRows(node.getName());
			i++;
		}
	}
	
	/**
	 * Adds the specification artifact nodes.
	 */
	private void addSpecArtifNodes() {
		JLabel labelTxt = new JLabel("<html><B> Specification Artifacts </B></html>");
		getConstraints().gridx++;
		getPanel().add(labelTxt, getConstraints());
		int i=0;
		getConstraints().insets = new Insets(5, 50, 0, 0);
		for (MyNode node : RequirementToolbox.getRequirementSourceNodes(reqNode, PSSIFCanonicMetamodelCreator.TAGS.get("E_RELATIONSHIP_REFERENTIAL_DEFINES"))) {
			getConstraints().gridy+=i;
			addMultRows(node.getName());
			i++;
		}
	}
	
	/**
	 * Adds the author nodes.
	 */
	private void addAuthorNodes() {
		JLabel labelTxt = new JLabel("<html><B> Author </B></html>");
		getConstraints().gridx++;
		getPanel().add(labelTxt, getConstraints());
		getConstraints().insets = new Insets(5, 50, 0, 0);
		int i=0;
		for (MyNode node : RequirementToolbox.getRequirementSourceNodes(reqNode, PSSIFCanonicMetamodelCreator.TAGS.get("E_RELATIONSHIP_CAUSAL_CREATES"), PSSIFCanonicMetamodelCreator.TAGS.get("N_AUTHOR"))) {
			getConstraints().gridy+=i;
			addMultRows(node.getName());
			i++;
		}
	}
	
	/**
	 * Adds the principal nodes.
	 */
	private void addPrincNodes() {
		JLabel labelTxt = new JLabel("<html><B> Principal </B></html>");
		getConstraints().gridx++;
		getPanel().add(labelTxt, getConstraints());
		getConstraints().insets = new Insets(5, 50, 0, 0);
		int i=0;
		for (MyNode node : RequirementToolbox.getRequirementSourceNodes(reqNode, PSSIFCanonicMetamodelCreator.TAGS.get("E_RELATIONSHIP_CAUSAL_REQUESTS"), PSSIFCanonicMetamodelCreator.TAGS.get("N_PRINCIPAL"))) {
			getConstraints().gridy+=i;
			addMultRows(node.getName());
			i++;
		}
	}
	
	/**
	 * Adds the test case nodes.
	 *
	 * @return the list
	 */
	private List<MyNode> addTestCaseNodes() {
		JLabel labelTxt = new JLabel("<html><B> Test case </B></html>");
		getConstraints().gridx++;
		getPanel().add(labelTxt, getConstraints());
		List<MyNode> nodes = new ArrayList<MyNode>();
		getConstraints().insets = new Insets(5, 50, 0, 0);
		int i=0;
		for (MyNode node : RequirementToolbox.getRequirementRelNodes(reqNode, PSSIFCanonicMetamodelCreator.TAGS.get("E_RELATIONSHIP_CHRONOLOGICAL_BASED_ON"), PSSIFCanonicMetamodelCreator.TAGS.get("N_TEST_CASE"))) {
			getConstraints().gridy+=i;
			addMultRows(node.getName());
			nodes.add(node);
			i++;
		}
		return nodes;
	}
	
	/**
	 * Adds the issue nodes.
	 *
	 * @param testCases the test cases
	 */
	private void addIssueNodes(List<MyNode> testCases) {
		JLabel labelTxt = new JLabel("<html><B> Issue </B></html>");
		getConstraints().gridx++;
		getPanel().add(labelTxt, getConstraints());
		getConstraints().insets = new Insets(5, 50, 0, 0);
		int i=0;
		for (MyNode testCaseNode: testCases) {
			for (MyNode node : RequirementToolbox.getRequirementDestNodes(testCaseNode, PSSIFCanonicMetamodelCreator.TAGS.get("E_RELATIONSHIP_CHRONOLOGICAL_LEADS_TO"), PSSIFCanonicMetamodelCreator.TAGS.get("N_ISSUE"))) {
				getConstraints().gridy+=i;
				addMultRows(node.getName());
				i++;
			}
		}
	}
	
	/**
	 * Adds the change event nodes.
	 */
	private void addChangeEventNodes() {
		JLabel labelTxt = new JLabel("<html><B> Change event </B></html>");
		getConstraints().gridx++;
		getPanel().add(labelTxt, getConstraints());
		getConstraints().insets = new Insets(5, 50, 0, 0);
		int i=0;
		for (MyNode node : RequirementToolbox.getRequirementDestNodes(reqNode, PSSIFCanonicMetamodelCreator.TAGS.get("E_RELATIONSHIP_CHRONOLOGICAL_BASED_ON"), PSSIFCanonicMetamodelCreator.TAGS.get("N_CHANGE_EVENT"))) {
			getConstraints().gridy+=i;
			addMultRows(node.getName());
			i++;
		}
	}
	
	/**
	 * Adds an attribute view.
	 *
	 * @param labelText the label text
	 * @param valueText the value text
	 */
	private void addAttrView(String labelText, String valueText) {
		JLabel labelTxt = new JLabel("<html><B>"+labelText+"</B></html>");
		getConstraints().gridx++;
		getConstraints().gridy = 0;
		getConstraints().insets = new Insets(5, 50, 0, 0);
		getPanel().add(labelTxt, getConstraints());
		if (valueText != null) {
			JLabel label = new JLabel(valueText);
			getConstraints().gridy = 1;
			getPanel().add(label, getConstraints());
		}
	}
	
	/**
	 * Adds multiple rows.
	 *
	 * @param valueText the value text
	 */
	private void addMultRows(String valueText) {
		if (valueText != null) {
			getConstraints().gridy++;
			JLabel label = new JLabel(valueText);
			getPanel().add(label, getConstraints());
		}
	}
	
	/**
	 * Adds the attributes.
	 */
	private void addAttributes() {
		String reqId = "";
		try {
			reqId = RequirementToolbox.getAttributeValue(reqNode, "id").getOne().asString();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		addAttrView("Id", reqId);
		
		String reqName = "";
		try {
			reqName  = RequirementToolbox.getAttributeValue(reqNode, "name").getOne().asString();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		addAttrView("Name", reqName);
		
		String reqVersion = "";
		try {
			reqVersion  = RequirementToolbox.getAttributeValue(reqNode, "version").getOne().asString();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		addAttrView("Version", reqVersion);
		
		String reqStatus = "";
		try {
			reqName  = RequirementToolbox.getAttributeValue(reqNode, "status").getOne().asString();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		addAttrView("Status", reqStatus);
		
		String reqAbsLevel = "";
		try {
			reqAbsLevel  = RequirementToolbox.getAttributeValue(reqNode, PSSIFCanonicMetamodelCreator.TAGS.get("A_REQUIREMENT_ABS_LEVEL")).getOne().asString();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		addAttrView("Absraction level", reqAbsLevel);
		
	}

	/**
	 * Gets the main panel.
	 *
	 * @return the panel
	 */
	private JPanel getPanel() {
		if (allPanel == null) {
			allPanel = new JPanel(new GridBagLayout());
		}		
		return allPanel;
	}
	
	/**
	 * Gets the constraints.
	 *
	 * @return the constraints
	 */
	public GridBagConstraints getConstraints() {
		if (constr == null) {
			constr = new GridBagConstraints();
		}
		return constr;
	}
	
	/**
	 * Reset popup.
	 */
	public void resetPopup() {
		reqNode = null;
		constr = null;
		allPanel = null;
	}

}
