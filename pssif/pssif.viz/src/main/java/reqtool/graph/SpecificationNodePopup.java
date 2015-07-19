package reqtool.graph;

import graph.model.MyNodeType;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Vector;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import model.ModelBuilder;

import org.apache.poi.hssf.util.HSSFColor.YELLOW;

import de.tum.pssif.core.metamodel.NodeType;
import de.tum.pssif.core.metamodel.external.PSSIFCanonicMetamodelCreator;

/**
 * The Class SpecificationNodePopup.
 */
public class SpecificationNodePopup {
	
	/** The specification types. */
	private Vector<MyNodeType> specificationTypes;
	
	/** The all panel. */
	private JPanel allPanel;
	
	/** The specification artifact name text field. */
	private JTextField mJTSpecArtifName;
	
	/** The specification artifact types combo box. */
	private JComboBox<MyNodeType> mJCSpecArtifTypes;
	
	/** The check box for importing a file. */
	private JCheckBox mJCheckBoxImportFile;
	
	/** The new requirement check box. */
	private JCheckBox newReqCheckBox;
	
	/**
	 * Show popup.
	 *
	 * @return true, if successful
	 */
	public boolean showPopup() {
		createPanel();
		
		int dialogResult = JOptionPane.showConfirmDialog(null, allPanel, "Create specification artifact", JOptionPane.DEFAULT_OPTION);

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
		allPanel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		mJTSpecArtifName = new JTextField(15);
    	mJCSpecArtifTypes =  new JComboBox<MyNodeType>(getSpecArtifTypes());
    	
    	JLabel jLnodeTypes = new JLabel("Choose Specification node Type:");
		JLabel jLnodeName = new JLabel("Specification Node name:");
		
		mJCheckBoxImportFile = new JCheckBox("Contains file");
		
		newReqCheckBox = new JCheckBox("Create Requirement from SpecArtifact");
		
		c.gridx = 0;
		c.gridy = 0;
		allPanel.add(jLnodeName, c);
		
		c.gridx = 1;
		c.gridy = 0;
		c.weightx = 0.5;
		c.insets = new Insets(0,10,0,0);
		allPanel.add(mJTSpecArtifName, c);
		
		c.gridx = 0;
		c.gridy = 1;
		allPanel.add(jLnodeTypes, c);
		
		c.gridx = 1;
		c.gridy = 1;
		c.weightx = 0.5;
		c.insets = new Insets(0,10,0,0);
		allPanel.add(mJCSpecArtifTypes, c);
		
		c.gridx = 0;
		c.gridy = 2;
		allPanel.add(mJCheckBoxImportFile, c);
		
		c.gridx = 0;
		c.gridy = 3;
		allPanel.add(newReqCheckBox, c);
		
	}

	/**
	 * Get the selected Specification Artifact Type Format.
	 *
	 * @return the String which the Mapper Factory needs, to create the correct Mapper
	 */
	public MyNodeType getSelectedSpecArtifType() {
		if (mJCSpecArtifTypes != null) {
			return (MyNodeType) mJCSpecArtifTypes.getSelectedItem();
		}
		return null;
	}

	/**
	 * Selected file import.
	 *
	 * @return true, if successful
	 */
	public boolean selectedFileImport() {
		if ( mJCheckBoxImportFile != null ) {
			return mJCheckBoxImportFile.isSelected();
		}
		return false;
	}
	
	/**
	 * Selected new requirement.
	 *
	 * @return true, if successful
	 */
	public boolean selectedNewReq() {
		if ( newReqCheckBox != null ) {
			return newReqCheckBox.isSelected();
		}
		return false;
	}
	
	/**
	 * Gets the specification artifact name.
	 *
	 * @return the specification artifact name
	 */
	public String getSpecArtifName() {
		if (mJTSpecArtifName != null) {
			return mJTSpecArtifName.getText();
		}
		return "";
	}
	
	/**
	 * Gets the specification artifact types.
	 *
	 * @return the specification artifact types.
	 */
	private Vector<MyNodeType> getSpecArtifTypes() {
		if (specificationTypes == null) {
			specificationTypes = new Vector<MyNodeType>();
			
			MyNodeType specType = ModelBuilder.getNodeTypes().getValue(PSSIFCanonicMetamodelCreator.TAGS.get("N_SPEC_ARTIFACT")); 
			specificationTypes.add(specType);
			
			for(NodeType nodeType : specType.getType().getSpecials()) {
				specificationTypes.add(new MyNodeType(nodeType));
			}
		}
		return specificationTypes;
	}

}
