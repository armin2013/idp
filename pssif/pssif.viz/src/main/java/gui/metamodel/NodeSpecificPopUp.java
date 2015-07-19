package gui.metamodel;

import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import de.tum.pssif.core.metamodel.external.MetamodelAttribute;
import de.tum.pssif.core.metamodel.external.MetamodelNode;

public class NodeSpecificPopUp {

	private JPanel nodePanel;
	private JPanel attributePanel;
	private JLabel attributeLabel;
	private JButton addAttributesButton;
	private JScrollPane attributeScroller;
	private GridBagConstraints attributeTagCons;
	private GridBagConstraints cAttLabel;
	private GridBagConstraints cAttField;
	private GridBagConstraints cLabel;
	private GridBagConstraints cButton;
	private GridBagConstraints cScrollPane;
	private String originalSelectedAttribute;
	// Defines line shift
	private int attributeCounter = 0;
	private MetamodelNode node;
	private HashMap<String, MetamodelAttribute> potentialAttributes;
	private ArrayList<String> currentAttributeNames;
	private ComponentDetailPopup parent;

	/**
	 * Constructor for the node specific view
	 * 
	 * @param nodePanel
	 *            Panel to add the view components to
	 * @param node
	 *            The node containing the components data
	 * @param potentialAttributes
	 *            Every attribute which is potentially chosen as connected to
	 *            the node
	 * @param parent
	 *            The component details view to revalidate the save button
	 */
	public NodeSpecificPopUp(JPanel nodePanel, MetamodelNode node,
			HashMap<String, MetamodelAttribute> potentialAttributes,
			ComponentDetailPopup parent) {
		this.nodePanel = nodePanel;
		this.node = node;
		this.potentialAttributes = potentialAttributes;
		this.parent = parent;
	}

	/**
	 * Create the node related view components
	 */
	public void createNodeView() {

		// Constraint for every Button
		cButton = new GridBagConstraints();
		cButton.ipadx = 15;
		cButton.ipady = 5;

		// Constraint for every Scroll Panel for Attributes
		cScrollPane = new GridBagConstraints();
		cScrollPane.anchor = GridBagConstraints.NORTHWEST;
		cScrollPane.gridx = 0;
		cScrollPane.gridy = 6;
		cScrollPane.fill = GridBagConstraints.HORIZONTAL;
		cScrollPane.ipady = 300;
		cScrollPane.ipadx = 320;
		cScrollPane.weightx = 0;
		cScrollPane.weighty = 0;
		cScrollPane.gridwidth = 4;

		// Constraint for every Label
		cLabel = new GridBagConstraints();
		cLabel.ipadx = 50;
		cLabel.gridwidth = 1;
		cLabel.anchor = GridBagConstraints.WEST;

		// Constraint for every Label for Attributes
		cAttLabel = new GridBagConstraints();
		cAttLabel.ipadx = 7;
		cAttLabel.ipady = 5;
		cAttLabel.gridwidth = 1;
		cAttLabel.anchor = GridBagConstraints.WEST;

		// Constraint for every TextField for Attributes
		cAttField = new GridBagConstraints();
		cAttField.ipadx = 120;
		cAttField.gridwidth = 1;
		cAttField.fill = GridBagConstraints.HORIZONTAL;
		cAttField.anchor = GridBagConstraints.WEST;

		attributeLabel = new JLabel("Attributes");
		cLabel.gridx = 0;
		cLabel.gridy = 5;
		cLabel.ipadx = 0;
		nodePanel.add(attributeLabel, cLabel);

		attributePanel = new JPanel(new GridBagLayout());
		attributeScroller = new JScrollPane(attributePanel);
		attributeScroller
				.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

		if (node.getConnectedAttributes() != null) {
			for (MetamodelAttribute attribute : potentialAttributes.values()) {
				if (node.getConnectedAttributes().contains(attribute.getName())) {
					createAttribute(attributePanel, attribute);
				}
			}
		}

		addAttributesButton = new JButton("+");
		addAttributesButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				createAttribute(
						attributePanel,
						potentialAttributes.get(potentialAttributes.keySet()
								.iterator().next()));
				attributeLabel.revalidate();
				attributePanel.repaint();
			}
		});
		addAttributesButton.setPreferredSize(new Dimension(350, 20));
		cButton.gridx = 0;
		cButton.gridy = 7;
		cButton.gridwidth = 3;
		cButton.fill = GridBagConstraints.HORIZONTAL;
		nodePanel.add(addAttributesButton, cButton);

		attributeScroller.setPreferredSize(new Dimension(500, 250));
		nodePanel.add(attributeScroller, cScrollPane);

	}

	/**
	 * Create and add an attribute the the provide JPanel
	 * 
	 * @param attributePanel
	 *            Panel to add the attributes components to
	 * @param attribute
	 *            The attribute in question
	 */
	private void createAttribute(final JPanel attributePanel,
			final MetamodelAttribute attribute) {

		if(currentAttributeNames == null) {
			currentAttributeNames = new ArrayList<String>();
		}
		
		final JLabel attributeTag;
		final JLabel attributeTagValueLabel;
		final JLabel attributeName;
		final JComboBox<String> attributeNameComboBox;
		final JLabel groupLabel;
		final JLabel groupValueLabel;
		final JLabel datatypeLabel;
		final JLabel datatypeValueLabel;
		final JLabel visibilityLabel;
		final JLabel visibilityValueLabel;
		final JLabel categoryLabel;
		final JLabel categoryValueLabel;
		final JLabel unitLabel;
		final JLabel unitValueLabel;
		final JButton deleteAttributeButton;

		attributeTag = new JLabel("Tag");
		cAttLabel.gridx = 0;
		cAttLabel.gridy = attributeCounter + 0;
		attributePanel.add(attributeTag, cAttLabel);

		attributeTagCons = new GridBagConstraints();

		attributeTagValueLabel = new JLabel(attribute.getTag());
		attributeTagCons.gridx = 1;
		attributeTagCons.gridy = attributeCounter + 0;
		attributeTagCons.ipadx = 50;
		attributeTagCons.gridwidth = 2;
		attributeTagCons.fill = GridBagConstraints.HORIZONTAL;
		attributeTagCons.anchor = GridBagConstraints.WEST;
		attributePanel.add(attributeTagValueLabel, attributeTagCons);

		attributeName = new JLabel("Name");
		cAttLabel.gridx = 1;
		cAttLabel.gridy = attributeCounter + 1;
		attributePanel.add(attributeName, cAttLabel);

		MetamodelAttribute[] attributeArray = potentialAttributes.values()
				.toArray(new MetamodelAttribute[potentialAttributes.size()]);
		String[] attributeNames = new String[attributeArray.length];
		for (int i = 0; i < attributeNames.length; i++) {
			attributeNames[i] = attributeArray[i].getName();
		}
		Arrays.sort(attributeNames);
		attributeNameComboBox = new JComboBox<String>(attributeNames);
		attributeNameComboBox.setSelectedItem(attribute.getName());
		currentAttributeNames.add(attribute.getName());
		cAttField.gridx = 2;
		cAttField.gridy = attributeCounter + 1;
		attributePanel.add(attributeNameComboBox, cAttField);
		parent.revalidateSaveButton();

		groupLabel = new JLabel("Group");
		cAttLabel.gridx = 1;
		cAttLabel.gridy = attributeCounter + 2;
		attributePanel.add(groupLabel, cAttLabel);

		groupValueLabel = new JLabel(attribute.getAttributeGroup());
		cAttField.gridx = 2;
		cAttField.gridy = attributeCounter + 2;
		attributePanel.add(groupValueLabel, cAttField);

		datatypeLabel = new JLabel("DataType");
		cAttLabel.gridx = 1;
		cAttLabel.gridy = attributeCounter + 3;
		attributePanel.add(datatypeLabel, cAttLabel);

		datatypeValueLabel = new JLabel(attribute.getAttributeDataType());
		cAttField.gridx = 2;
		cAttField.gridy = attributeCounter + 3;
		attributePanel.add(datatypeValueLabel, cAttField);

		visibilityLabel = new JLabel("Visibility");
		cAttLabel.gridx = 1;
		cAttLabel.gridy = attributeCounter + 4;
		attributePanel.add(visibilityLabel, cAttLabel);

		visibilityValueLabel = new JLabel(attribute.getAttributeVisiblity() + "");
		cAttField.gridx = 2;
		cAttField.gridy = attributeCounter + 4;
		attributePanel.add(visibilityValueLabel, cAttField);

		categoryLabel = new JLabel("Category");
		cAttLabel.gridx = 1;
		cAttLabel.gridy = attributeCounter + 5;
		attributePanel.add(categoryLabel, cAttLabel);

		categoryValueLabel = new JLabel(attribute.getAttributeCategory());
		cAttField.gridx = 2;
		cAttField.gridy = attributeCounter + 5;
		attributePanel.add(categoryValueLabel, cAttField);

		unitLabel = new JLabel("Unit");
		cAttLabel.gridx = 1;
		cAttLabel.gridy = attributeCounter + 6;
		attributePanel.add(unitLabel, cAttLabel);

		unitValueLabel = new JLabel(attribute.getAttributeUnit());
		cAttField.gridx = 2;
		cAttField.gridy = attributeCounter + 6;
		attributePanel.add(unitValueLabel, cAttField);

		// Change the attributes info if another attribute is chosen
		attributeNameComboBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent itemEvent) {
				// Has to be checked, because itemStateChanged is called for
				// select & deselect
				if (itemEvent.getStateChange() == ItemEvent.SELECTED) {

					MetamodelAttribute attributeInQuestion = potentialAttributes
							.get(attributeNameComboBox.getSelectedItem());

					attributeTagValueLabel.setText(attributeInQuestion.getTag());
					groupValueLabel.setText(attributeInQuestion
							.getAttributeGroup());
					datatypeValueLabel.setText(attributeInQuestion
							.getAttributeDataType());
					visibilityValueLabel.setText(attributeInQuestion
							.getAttributeVisiblity() + "");
					categoryValueLabel.setText(attributeInQuestion
							.getAttributeCategory());
					unitValueLabel.setText(attributeInQuestion
							.getAttributeUnit());

					currentAttributeNames.remove(originalSelectedAttribute);
					currentAttributeNames.add(attributeInQuestion.getName());
					parent.revalidateSaveButton();
					originalSelectedAttribute = attributeInQuestion.getName();
				} else {
					originalSelectedAttribute = attribute.getName();
				}
			}
		});

		// / Delete Attribute Button
		// Path to Icon
		String path = System.getProperty("user.dir");
		String delete = path.substring(0, path.length() - 9)
				+ "button\\deletebutton.png";

		Icon icon = new javax.swing.ImageIcon(delete);
		((ImageIcon) icon).setImage(((ImageIcon) icon).getImage()
				.getScaledInstance(15, 15, Image.SCALE_DEFAULT));
		deleteAttributeButton = new JButton(icon);

		GridBagConstraints deleteButtonConst = new GridBagConstraints();
		deleteAttributeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				currentAttributeNames.remove((String) attributeNameComboBox
						.getSelectedItem());
				parent.revalidateSaveButton();

				attributePanel.remove(attributeTag);
				attributePanel.remove(attributeTagValueLabel);
				attributePanel.remove(attributeName);
				attributePanel.remove(attributeNameComboBox);
				attributePanel.remove(datatypeLabel);
				attributePanel.remove(datatypeValueLabel);
				attributePanel.remove(categoryLabel);
				attributePanel.remove(categoryValueLabel);
				attributePanel.remove(unitLabel);
				attributePanel.remove(unitValueLabel);
				attributePanel.remove(visibilityLabel);
				attributePanel.remove(visibilityValueLabel);
				attributePanel.remove(groupLabel);
				attributePanel.remove(groupValueLabel);
				attributePanel.remove(deleteAttributeButton);
				attributePanel.revalidate();
				attributePanel.repaint();
			}
		});
		deleteAttributeButton.setPreferredSize(new Dimension(20, 20));
		deleteAttributeButton.setFont(new Font("serif", Font.PLAIN, 6));
		deleteButtonConst.gridheight = 7;
		deleteButtonConst.gridx = 3;
		deleteButtonConst.gridy = 0 + attributeCounter;
		deleteButtonConst.gridwidth = 3;
		deleteButtonConst.fill = GridBagConstraints.VERTICAL;
		deleteButtonConst.insets = new Insets(5, 5, 5, 5);
		attributePanel.add(deleteAttributeButton, deleteButtonConst);

		attributeCounter += 7;
	}

	/**
	 * Return every currently set attribute
	 * @return Names of every set attribute
	 */
	public ArrayList<String> getCurrentAttributeNames() {
		return currentAttributeNames;
	}

}
