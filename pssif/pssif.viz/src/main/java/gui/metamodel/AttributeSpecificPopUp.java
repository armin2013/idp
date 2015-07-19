package gui.metamodel;

import java.awt.GridBagConstraints;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Arrays;

import de.tum.pssif.core.metamodel.Enumeration;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import de.tum.pssif.core.metamodel.Unit;
import de.tum.pssif.core.metamodel.Units;
import de.tum.pssif.core.metamodel.AttributeGroup;
import de.tum.pssif.core.metamodel.AttributeCategory;
import de.tum.pssif.core.metamodel.PrimitiveDataType;
import de.tum.pssif.core.metamodel.external.MetamodelAttribute;
import de.tum.pssif.core.metamodel.impl.EdgeTypeImpl;

public class AttributeSpecificPopUp {

	private JPanel attributePanel;
	private JLabel unitLabel;
	private JLabel groupLabel;
	private JLabel datatypeLabel;
	private JLabel visiblityLabel;
	private JLabel categoryLabel;
	private JComboBox<String> groupBox;
	private JComboBox<String> unitBox;
	private JComboBox<String> dataTypeBox;
	private JComboBox<String> visibilityBox;
	private JComboBox<String> categoryBox;
	private MetamodelAttribute currentAttribute;
	private GridBagConstraints cAttLabel;
	private GridBagConstraints cAttField;
	// Defines line shift
	private int attributeCounter = 0;

	/**
	 * Construct for attribute specific views
	 * @param attributePanel Panel to att the attribute related view components to
	 * @param currentAttribute Currently selected attribute containing the data
	 * @param parent MetamodelDetailView to revalidate the save button
	 */
	public AttributeSpecificPopUp(JPanel attributePanel,
			MetamodelAttribute currentAttribute, ComponentDetailPopup parent) {
		this.attributePanel = attributePanel;
		this.currentAttribute = currentAttribute;
	}

	/**
	 * Create and add an attribute the the provide JPanel	
	 */
	public void createAttributeView() {

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

		groupLabel = new JLabel("Group");
		cAttLabel.gridx = 1;
		cAttLabel.gridy = attributeCounter + 2;
		attributePanel.add(groupLabel, cAttLabel);

		// Convert AttributeGroup interface to String
		Object[] attributeGroupObjects = new EdgeTypeImpl("")
				.getAttributeGroups().toArray();
		String[] attributeGroups = new String[attributeGroupObjects.length];
		for (int i = 0; i < attributeGroups.length; i++) {
			attributeGroups[i] = ((AttributeGroup) attributeGroupObjects[i])
					.getName();
		}
		Arrays.sort(attributeGroups);
		groupBox = new JComboBox<String>(attributeGroups);
		groupBox.setSelectedItem(currentAttribute.getAttributeGroup());

		cAttField.gridx = 2;
		cAttField.gridy = attributeCounter + 2;
		attributePanel.add(groupBox, cAttField);

		datatypeLabel = new JLabel("DataType");
		cAttLabel.gridx = 1;
		cAttLabel.gridy = attributeCounter + 3;
		attributePanel.add(datatypeLabel, cAttLabel);

		// Convert Interface DataTypes to String
		PrimitiveDataType[] dataTypeObjects = PrimitiveDataType.TYPES
				.toArray(new PrimitiveDataType[PrimitiveDataType.TYPES.size()]);
		String[] datatypes = new String[dataTypeObjects.length];

		for (int i = 0; i < datatypes.length; i++) {
			datatypes[i] = dataTypeObjects[i].getName();
		}
		Arrays.sort(datatypes);
		dataTypeBox = new JComboBox<String>(datatypes);
		dataTypeBox.setSelectedItem(currentAttribute.getAttributeDataType());
		cAttField.gridx = 2;
		cAttField.gridy = attributeCounter + 3;
		attributePanel.add(dataTypeBox, cAttField);

		visiblityLabel = new JLabel("Visibility");
		cAttLabel.gridx = 1;
		cAttLabel.gridy = attributeCounter + 4;
		attributePanel.add(visiblityLabel, cAttLabel);

		String[] visibilityTypes = { "true", "false" };
		visibilityBox = new JComboBox<String>(visibilityTypes);
		visibilityBox.setSelectedItem(currentAttribute.getAttributeVisiblity() + "");
		cAttField.gridx = 2;
		cAttField.gridy = attributeCounter + 4;
		attributePanel.add(visibilityBox, cAttField);

		categoryLabel = new JLabel("Category");
		cAttLabel.gridx = 1;
		cAttLabel.gridy = attributeCounter + 5;
		attributePanel.add(categoryLabel, cAttLabel);

		// Convert the the ENUM Category Values to a String Array
		AttributeCategory[] categoryENUM = AttributeCategory.values();
		String[] categoryValues = new String[categoryENUM.length];

		for (int i = 0; i < categoryValues.length; i++) {
			categoryValues[i] = categoryENUM[i].getName();
		}
		Arrays.sort(categoryValues);
		categoryBox = new JComboBox<String>(categoryValues);
		categoryBox.setSelectedItem(currentAttribute.getAttributeCategory());
		cAttField.gridx = 2;
		cAttField.gridy = attributeCounter + 5;
		attributePanel.add(categoryBox, cAttField);

		unitLabel = new JLabel("Unit");
		cAttLabel.gridx = 1;
		cAttLabel.gridy = attributeCounter + 6;
		attributePanel.add(unitLabel, cAttLabel);

		// Convert Units to String
		Object[] unitObjects = Units.UNITS.toArray();
		String[] units = new String[unitObjects.length];

		for (int i = 0; i < units.length; i++) {
			units[i] = ((Unit) unitObjects[i]).getName();
		}
		Arrays.sort(units);
		unitBox = new JComboBox<String>(units);
		// If its a new Panel make sure the user can't mess things up
		// (Inappropriate combo of Unit - DataType)
		if (currentAttribute.getAttributeUnit() != "") {
			unitBox.setSelectedItem(currentAttribute.getAttributeUnit());
		} else {
			unitBox.setSelectedItem(Units.NONE.getName());
			unitBox.setEnabled(false);
		}
		cAttField.gridx = 2;
		cAttField.gridy = attributeCounter + 6;
		attributePanel.add(unitBox, cAttField);

		ItemListener itemListener = new ItemListener() {
			public void itemStateChanged(ItemEvent itemEvent) {
				checkDataTypeBoxContent();
			}
		};
		dataTypeBox.addItemListener(itemListener);
		checkDataTypeBoxContent();
		
		attributeCounter += 7;
	}

	/**
	 * Returns an MetamodelAttribute Object with all the current set data
	 * @return The set attribute
	 */
	public MetamodelAttribute getCurrentSetAttribute() {
		
		return new MetamodelAttribute(currentAttribute.getTag(),
				currentAttribute.getName(),
				(String) groupBox.getSelectedItem(),
				(String) dataTypeBox.getSelectedItem(),
				Boolean.parseBoolean((String) visibilityBox.getSelectedItem()),
				(String) categoryBox.getSelectedItem(),
				(String) unitBox.getSelectedItem());
	}

	/**
	 * Revalidate the unit box depending on what datatype object is selected
	 */
	public void checkDataTypeBoxContent() {
		// if true => enable unit = false
		PrimitiveDataType searchType = null;
		for (PrimitiveDataType currentDataType : PrimitiveDataType.TYPES
				.toArray(new PrimitiveDataType[PrimitiveDataType.TYPES.size()])) {
			if (currentDataType.getName().equals(
					(String) dataTypeBox.getSelectedItem())) {
				searchType = currentDataType;
				break;
			}
		}
		if ((searchType.getMetaType().equals(Enumeration.class))
				|| (!(PrimitiveDataType.DECIMAL.equals(searchType) || PrimitiveDataType.INTEGER
						.equals(searchType)))) {
			unitBox.setSelectedItem(Units.NONE.getName());
			unitBox.setEnabled(false);
		} else {
			unitBox.setEnabled(true);
		}
	}
}
