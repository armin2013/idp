package reqtool.graph;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * The Class RequirementFromSpecPopup.
 */
public class RequirementFromSpecPopup {
	
	/** The all panel. */
	private JPanel allPanel;
	
	/** The requirement name text field. */
	private JTextField reqName;
	
	/** The author name text field. */
	private JTextField authorName;
	
	/** The principal name. */
	private JTextField principalName;
	
	/** The abstraction level name text field. */
	private JTextField abstractionLevelName;
	
	/** The has author check box. */
	private JCheckBox hasAuthorCheckBox;
	
	/** The has principal check box. */
	private JCheckBox hasPrincipalCheckBox;
	
	/** The has abs level check box. */
	private JCheckBox hasAbsLevelCheckBox;
	
	
	/**
	 * Show the popup for creating a requirement node from the specification wizard.
	 *
	 * @return true, if successful
	 */
	public boolean showPopup() {
		JPanel allPanel = createPanel();
		
		int dialogResult = JOptionPane.showConfirmDialog(null, allPanel, "Create Requirement from specification artifact", JOptionPane.DEFAULT_OPTION);

		return evalDialog(dialogResult);
	}
	
	/**
	 * Evaluates the user input from the dialog.
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
	 * Creates the main panel.
	 *
	 * @return the main panel
	 */
	private JPanel createPanel() {
		allPanel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		reqName = new JTextField(15);
		authorName = new JTextField(15);
		principalName = new JTextField(15);
		abstractionLevelName = new JTextField(15);
    	//mJCSpecArtifTypes =  new JComboBox<MyNodeType>(getSpecArtifTypes());
    	
    	//JLabel jLnodeTypes = new JLabel("Choose Specification node Type:");
		JLabel jLnodeName = new JLabel("Requirement name:");
		
		hasAuthorCheckBox = new JCheckBox("Add Author");
		hasPrincipalCheckBox = new JCheckBox("Add Principal");
		hasAbsLevelCheckBox = new JCheckBox("Add Abstraction Level");
		
		c.gridx = 0;
		c.gridy = 0;
		allPanel.add(jLnodeName, c);
		
		c.gridx = 1;
		c.gridy = 0;
		c.weightx = 0.5;
		c.insets = new Insets(0,10,0,0);
		allPanel.add(reqName, c);
		
		c.gridx = 0;
		c.gridy = 1;
		allPanel.add(hasAuthorCheckBox, c);
		
		c.gridx = 1;
		c.gridy = 1;
		c.weightx = 0.5;
		c.insets = new Insets(0,10,0,0);
		allPanel.add(authorName, c);
		
		c.gridx = 0;
		c.gridy = 2;
		allPanel.add(hasPrincipalCheckBox, c);
		
		c.gridx = 1;
		c.gridy = 2;
		c.weightx = 0.5;
		c.insets = new Insets(0,10,0,0);
		allPanel.add(principalName, c);
		
		c.gridx = 0;
		c.gridy = 3;
		allPanel.add(hasAbsLevelCheckBox, c);
		
		c.gridx = 1;
		c.gridy = 3;
		c.weightx = 0.5;
		c.insets = new Insets(0,10,0,0);
		allPanel.add(abstractionLevelName, c);
		
		
		return allPanel;
	}
	
	/**
	 * Gets the requirement name.
	 *
	 * @return the requirement name
	 */
	public String getReqName() {
		if (reqName != null) {
			return reqName.getText();
		}
		return "";
	}
	
	/**
	 * Gets the author name.
	 *
	 * @return the author name
	 */
	public String getAuthorName() {
		if (authorName != null) {
			return authorName.getText();
		}
		return "";
	}
	
	/**
	 * Gets the principal name.
	 *
	 * @return the principal name
	 */
	public String getPrincipalName() {
		if (principalName != null) {
			return principalName.getText();
		}
		return "";
	}
	
	/**
	 * Gets the abstraction level name.
	 *
	 * @return the abstraction level name
	 */
	public String getAbstractionLevelName() {
		if (abstractionLevelName != null) {
			return abstractionLevelName.getText();
		}
		return "";
		
	}
	
	/**
	 * Selected author check box.
	 *
	 * @return true, if successful
	 */
	public boolean selectedAuthorCheckBox() {
		if ( hasAuthorCheckBox != null ) {
			return hasAuthorCheckBox.isSelected();
		}
		return false;
	}
	
	/**
	 * Selected principal check box.
	 *
	 * @return true, if successful
	 */
	public boolean selectedPrincipalCheckBox() {
		if ( hasPrincipalCheckBox != null ) {
			return hasPrincipalCheckBox.isSelected();
		}
		return false;
	}
	
	/**
	 * Selected abs level check box.
	 *
	 * @return true, if successful
	 */
	public boolean selectedAbsLevelCheckBox() {
		if ( hasAbsLevelCheckBox != null ) {
			return hasAbsLevelCheckBox.isSelected();
		}
		return false;
	}
}


