package de.tum.pssif.core.metamodel.external;

/**
 * Represents an attribute as it is saved in the metamodel file
 * @author Alex
 *
 */
public class MetamodelAttribute extends MetamodelComponent {

	private String attributeGroup;
	private String attributeDataType;
	private boolean attributeVisiblity;
	private String attributeCategory;
	private String attributeUnit;

	/**
	 * Constructor of an attribute
	 * @param tag Tag of the attribute (unique)
	 * @param name Name of the attribute
	 * @param group Group of the attribute
	 * @param dataType Data type of the attribute
	 * @param visibility Visibility of the attribute
 	 * @param category Category of the attribute
	 * @param unit Unit of the attribute
	 */
	public MetamodelAttribute(String tag, String name, String group,
			String dataType, boolean visibility, String category, String unit) {
		super(tag, name, "ATTRIBUTE");
		this.attributeGroup = group;
		this.attributeDataType = dataType;
		this.attributeVisiblity = visibility;
		this.attributeCategory = category;
		this.attributeUnit = unit;
	}
	
	/**
	 * Constructor for an attributes
	 * Caution! Only use for import
	 * @param tag Tag of the attribute (unique)
	 * @param name Name of the attribute
	 */
	public MetamodelAttribute(String tag, String name) {
		super(tag, name, "ATTRIBUTE");
	}

	/**
	 * Get the group of the current attribute
	 * @return The requested group
	 */
	public String getAttributeGroup() {
		return attributeGroup;
	}

	/**
	 * Set the group of the current attribute
	 * @param attributeGroup New name of the group
	 */
	public void setAttributeGroup(String attributeGroup) {
		this.attributeGroup = attributeGroup;
	}

	/**
	 * Get the data type of the current attribute
	 * @return The requested data type
	 */
	public String getAttributeDataType() {
		return attributeDataType;
	}

	/**
	 * Set the data type of the current attribute
	 * @param attributeDataType New name of the data type
	 */
	public void setAttributeDataType(String attributeDataType) {
		this.attributeDataType = attributeDataType;
	}

	/**
	 * Get the visibility of the current attribute
	 * @return The requested visibility
	 */
	public boolean getAttributeVisiblity() {
		return attributeVisiblity;
	}

	/**
	 * Set the visiblity of the current attribute
	 * @param attributeVisiblity The new visibility
	 */
	public void setAttributeVisiblity(boolean attributeVisiblity) {
		this.attributeVisiblity = attributeVisiblity;
	}

	/**
	 * Get the category of the current attribute
	 * @return The requested category
	 */
	public String getAttributeCategory() {
		return attributeCategory;
	}

	/**
	 * Set the category of the current attribute
	 * @param attributeCategory New name of the category
	 */
	public void setAttributeCategory(String attributeCategory) {
		this.attributeCategory = attributeCategory;
	}

	/**
	 * Get the unit of the current attribute
	 * @return The requested unit
	 */
	public String getAttributeUnit() {
		return attributeUnit;
	}

	/**
	 * Set the unit of the current attribute
	 * @param attributeUnit New name of the unit
	 */
	public void setAttributeUnit(String attributeUnit) {
		this.attributeUnit = attributeUnit;
	}

	/**
	 * Get a formatted attribute string
	 */
	public String toString() {
		return "\nTag:\t\t" + super.getTag() + "\nName:\t\t" + super.getName() + "\nGroup\t\t"
				+ attributeGroup + "\nData Type:\t\t" + attributeDataType
				+ "\nVisibility:\t\t" + attributeVisiblity + "\nCategory:\t\t"
				+ attributeCategory + "\nUnit:\t\t" + attributeUnit;
	}
}
