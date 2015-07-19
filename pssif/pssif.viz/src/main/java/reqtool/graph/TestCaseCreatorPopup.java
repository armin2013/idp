package reqtool.graph;

import graph.model.MyNode;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import reqtool.controller.RequirementToolbox;
import reqtool.controller.TestCaseCreator;
import de.tum.pssif.core.metamodel.external.PSSIFCanonicMetamodelCreator;

/**
 * The Class TestCaseCreatorPopup.
 */
public class TestCaseCreatorPopup {
	
	/** The node panel. */
	private JPanel nodePanel;
	
	/** The test case name text field. */
	private JTextField testCaseNameTextField;
	
	/** The solution artifacts. */
	private LinkedList<MyNode> solutionArtifacts;
	
	/** The requirement node. */
	private MyNode requirementNode;

	/**
	 * Instantiates a new test case creator popup.
	 *
	 * @param requirementNode the requirement node
	 */
	public TestCaseCreatorPopup(MyNode requirementNode) {
		this.requirementNode = requirementNode;
		this.solutionArtifacts = RequirementToolbox.getRequirementSourceNodes(requirementNode, PSSIFCanonicMetamodelCreator.TAGS.get("E_RELATIONSHIP_LOGICAL_SATISFIES"));
	}

	/**
	 * Show the Popup to the user.
	 *
	 * @return true, if successful
	 */
	public boolean showPopup() {
		JPanel allPanel = createPanel();

		int dialogResult = JOptionPane.showConfirmDialog(null, allPanel, "Create a new Test Case", JOptionPane.DEFAULT_OPTION);

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
			LinkedList<MyNode> selectedNodes = new LinkedList<MyNode>();
			
			List<Integer> indexes = new ArrayList<Integer>();
			Component[] nodes = nodePanel.getComponents();
			for (int i=0;i<nodes.length;i++) {
				Component tmp = nodes[i];
				if ((tmp instanceof JCheckBox)) {
					JCheckBox a = (JCheckBox) tmp;
					if (a.isSelected()) {
						indexes.add(i);
					}
				}
			}
			
			for(int index:indexes) {
				selectedNodes.add(solutionArtifacts.get(index));
			}
			
			String nodeName = testCaseNameTextField.getText();
			TestCaseCreator.createTestCase(requirementNode, selectedNodes, nodeName);
			
			return true;
		}
		return false;
	}

	/**
	 * Creates the panel.
	 *
	 * @return the j panel
	 */
	private JPanel createPanel() {
		JPanel allPanel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		nodePanel = new JPanel(new GridLayout(0, 1));
		nodePanel.addMouseMotionListener(new MouseMotionListener() {
			
			@Override
			public void mouseMoved(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseDragged(MouseEvent e) {
				Rectangle rect = new Rectangle(e.getX(), e.getY(), 1, 1);
				((JPanel)e.getSource()).scrollRectToVisible(rect);
			}
		});
		for (MyNode node : solutionArtifacts) {
			JCheckBox choice = new JCheckBox(node.getName());

			choice.setSelected(false);
			nodePanel.add(choice);
		}

		JScrollPane scrollNodes = new JScrollPane(nodePanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollNodes.setPreferredSize(new Dimension(200, (solutionArtifacts.size()*35)));
		scrollNodes.setMaximumSize(new Dimension(200, 200));
		scrollNodes.setAutoscrolls(true);

		final JCheckBox selectAllNodes = new JCheckBox("Select all solution artifacts");

		selectAllNodes.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (selectAllNodes.isSelected()) {
					Component[] node = nodePanel.getComponents();
					for (Component tmp : node) {
						if ((tmp instanceof JCheckBox)) {
							JCheckBox a = (JCheckBox) tmp;

							a.setSelected(true);
						}
					}
				} else {
					Component[] attr = nodePanel.getComponents();
					for (Component tmp : attr) {
						if ((tmp instanceof JCheckBox)) {
							JCheckBox a = (JCheckBox) tmp;

							a.setSelected(false);
						}
					}
				}
			}
		});

		selectAllNodes.setSelected(false);

		testCaseNameTextField = new JTextField(10);
		
		JLabel nodeTypesJL = new JLabel("Choose Node Types:");
		JLabel graphNameJL = new JLabel("TestCase name:");

		c.gridx = 0;
		c.gridy = 0;

		if (solutionArtifacts.size() > 0) {
			allPanel.add(nodeTypesJL, c);
			c.gridx = 1;
			c.gridy = 0;
			c.weightx = 0.5;
			allPanel.add(scrollNodes, c);
			
			c.gridx = 1;
			c.gridy = 1;
			c.insets = new Insets(0,10,0,0);
			allPanel.add(selectAllNodes, c);
		}

		c.gridx = 0;
		c.gridy = 4;
		c.insets = new Insets(10,0,0,0);
		allPanel.add(graphNameJL, c);
		c.gridx = 1;
		c.gridy = 4;
		c.gridwidth = 2;
		allPanel.add(testCaseNameTextField, c);

		allPanel.setPreferredSize(null);
		allPanel.setMaximumSize(new Dimension(400, 50));
		allPanel.setMinimumSize(new Dimension(400, 50));

		return allPanel;
	}

}
