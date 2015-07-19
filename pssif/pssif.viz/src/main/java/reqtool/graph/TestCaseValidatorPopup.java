package reqtool.graph;

import graph.model.MyNode;
import graph.operations.AttributeFilter;
import graph.operations.AttributeOperations;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import de.tum.pssif.core.common.PSSIFConstants;
import de.tum.pssif.core.common.PSSIFValue;
import de.tum.pssif.core.metamodel.Attribute;
import de.tum.pssif.core.metamodel.DataType;
import de.tum.pssif.core.metamodel.external.PSSIFCanonicMetamodelCreator;
import de.tum.pssif.core.metamodel.PrimitiveDataType;

/**
 * The Class TestCaseValidatorPopup.
 */
public class TestCaseValidatorPopup {
	
	/** The Constant RESULT_OK. */
	public static final int RESULT_OK = 1;
	
	/** The Constant RESULT_NOK. */
	public static final int RESULT_NOK = 0;
	
	/** The Constant RESULT_CANCEL. */
	public static final int RESULT_CANCEL = -1;
	
	/** The all panel. */
	private JPanel allPanel;
	
	/** The attribute list. */
	private JComboBox<String> attributeList;
	
	/** The operation list. */
	private JComboBox<String> operationList;
	
	/** The value text field. */
	private JTextField valueTextField;
	
	/** The attribute names. */
	private Map<String, DataType> attributeNames;
	
	/** The nodes. */
	private LinkedList<MyNode> nodes;
	
	/** The selected node. */
	private MyNode selectedNode;

	/**
	 * Instantiates a new test case validator popup.
	 *
	 * @param verifiedNodes the verified nodes
	 * @param attributes the attributes
	 * @param myNode the my node
	 */
	public TestCaseValidatorPopup(LinkedList<MyNode> verifiedNodes,	Map<String, DataType> attributes, MyNode myNode) {
		this.selectedNode = myNode;
		this.attributeNames = attributes;
		this.nodes = verifiedNodes;
	}

	/**
	 * Show the Popup for validating a test case to the user.
	 *
	 * @return "Edge" if an Edge Condition was added, "Node" of a Node condition
	 *         was added, null otherwise
	 */
	public int showPopup() {
		JPanel panel = createPanel();

		int dialogResult = JOptionPane.showConfirmDialog(null, panel, "Numerical verification", JOptionPane.DEFAULT_OPTION);

		return evalDialog(dialogResult);
	}

	/**
	 * Evaluates the user input from the dialog
	 *
	 * @param dialogResult the dialog result
	 * @return the int
	 */
	private int evalDialog(int dialogResult) {
		if (dialogResult == 0) {

			try {
				String selectedAttribute = (String) attributeList.getSelectedItem();
				String selectedOperation = (String) operationList.getSelectedItem();
				String RefValue = valueTextField.getText();

				if (RefValue.length() == 0) {
					// not enough information
					JPanel errorPanel = new JPanel();

					errorPanel.add(new JLabel("No name entered or no attribute and operations selected"));

					JOptionPane.showMessageDialog(null, errorPanel, "Ups something went wrong", JOptionPane.ERROR_MESSAGE);

					return -1;
				} else {
					// all information is available
					AttributeOperations op = AttributeOperations.getValueOf(selectedOperation);
					for (MyNode node : nodes) {
						HashMap<String, Attribute> attributes = node.getAttributesHashMap();
						Attribute attr = attributes.get(selectedAttribute);
						if (attr != null) {
							boolean result = false;

							if (attr.get(node.getNode()) != null) {
								if (attr.get(node.getNode()).isOne()) {
									PSSIFValue attrValue = attr.get(node.getNode()).getOne();

									PrimitiveDataType currentType = (PrimitiveDataType) attr.getType();

									if (currentType.equals(PrimitiveDataType.BOOLEAN))
										result = AttributeFilter.BooleanEval(attrValue, op, RefValue);
									if (currentType.equals(PrimitiveDataType.DATE))
										result = AttributeFilter.DateEval(attrValue, op, RefValue);
									if (currentType.equals(PrimitiveDataType.DECIMAL))
										result = AttributeFilter.DecimalEval(attrValue, op, RefValue);
									if (currentType.equals(PrimitiveDataType.INTEGER))
										result = AttributeFilter.IntegerEval(attrValue, op, RefValue);
									if (currentType.equals(PrimitiveDataType.STRING))
										result = AttributeFilter.StringEval(attrValue, op, RefValue);
								}
								if (attr.get(node.getNode()).isMany()) {
									throw new NullPointerException("Not allowed");
								}

								if (attr.get(node.getNode()).isNone()) {
									result = false;
								}
							}
							selectedNode.updateAttribute(PSSIFConstants.A_TEST_CASE_CONDITION_ATTRIBUTE, selectedAttribute);
							selectedNode.updateAttribute(PSSIFConstants.A_TEST_CASE_CONDITION_OP, selectedOperation);
							selectedNode.updateAttribute(PSSIFConstants.A_TEST_CASE_CONDITION_VALUE, RefValue);
							if (result == false) {
								
								return 0;
							} else {
								return 1;
							}
						}
					}
				}

			} catch (Exception e) {
				System.out.println("Here");
				JPanel errorPanel = new JPanel();

				errorPanel.add(new JLabel("There was a problem converting the entered value into the attribute data type"));

				JOptionPane.showMessageDialog(null, errorPanel, "Ups something went wrong", JOptionPane.ERROR_MESSAGE);
				return -1;
			}
		}
		return -1;
	}

	/**
	 * Creates the panel.
	 *
	 * @return the j panel
	 */
	private JPanel createPanel() {
		allPanel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		int ypos = 0;

		attributeList = new JComboBox<String>(attributeNames.keySet().toArray(new String[] {}));

		operationList = new JComboBox<String>(new String[] {});

		attributeList.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JComboBox<String> cb = (JComboBox<String>) e.getSource();
				if (cb.getSelectedItem() != null) {
					String attrName = (String) cb.getSelectedItem();

					DataType dataType = attributeNames.get(attrName);

					operationList.removeAllItems();

					for (String s : getPossibleOperations(dataType)) {
						operationList.addItem(s);
					}
				}
			}
		});

		if (attributeList.getItemCount() > 0)
			attributeList.setSelectedIndex(0);
		if (operationList.getItemCount() > 0)
			operationList.setSelectedIndex(0);

		valueTextField = new JTextField(10);

		c.gridx = 0;
		c.gridy = ypos;
		allPanel.add(new JLabel("Choose an Attribute"), c);
		c.gridx = 1;
		c.gridy = ypos;
		allPanel.add(new JLabel("Choose an Operation"), c);
		c.gridx = 2;
		c.gridy = ypos++;
		allPanel.add(new JLabel("Enter a compare Value"), c);

		// c.weighty = 1;
		c.weightx = 1;

		c.gridx = 0;
		c.gridy = ypos;
		allPanel.add(attributeList, c);
		c.gridx = 1;
		c.gridy = ypos;
		allPanel.add(operationList, c);
		c.gridx = 2;
		c.gridy = ypos++;
		allPanel.add(valueTextField, c);

		allPanel.setPreferredSize(new Dimension(400, 200));
		allPanel.setMaximumSize(new Dimension(400, 200));
		allPanel.setMinimumSize(new Dimension(400, 200));

		return allPanel;

	}

	/**
	 * Defines which compare operations are possible for the given Datatype.
	 *
	 * @param type            the datatype for which we have to define the compare
	 *            possibilities
	 * @return a String array with all the possible compare operation names
	 */
	private String[] getPossibleOperations(DataType type) {
		String[] res;

		if (type.equals(PrimitiveDataType.BOOLEAN)
				|| type.equals(PrimitiveDataType.STRING)) {
			res = new String[] { AttributeOperations.EQUAL.toString(),
					AttributeOperations.NOT_EQUAL.toString() };
		} else {
			AttributeOperations[] tmp = AttributeOperations.values();

			res = new String[tmp.length];

			int i = 0;
			for (AttributeOperations ao : tmp) {
				res[i] = ao.getName();
				i++;

			}
		}

		Arrays.sort(res);

		return res;
	}

}
