package de.tum.pssif.core.metamodel.external;

import java.util.ArrayList;

/**
 * The structure of a node in the metamodel file
 * @author Alex
 *
 */
public class MetamodelNode extends MetamodelComponent {

	private ArrayList<String> connectedAttributes;
	private MetamodelNode parent;
	private String tempParent;

	public MetamodelNode(String tag, String name) {
		super(tag, name, "NODE");
	}
	
	public MetamodelNode(String tag, String name, MetamodelNode parent) {
		super(tag, name, "NODE");
		this.parent = parent;
	}
	
	public String toString() {

		String result = "";

		result += "Type:\t" + super.getType() + "\n";
		result += "Tag:\t" + super.getTag() + "\n";
		result += "Name:\t" + super.getName() + "\n";

		if (parent != null) {
			result += "Parent:\t" + parent.getName() + "\n";
		}

		if(connectedAttributes != null) {
			String attributeString = "";
			for (String attribute : connectedAttributes) {
				attributeString += "\tAttributeName: " + attribute + "\n";
			}
			
			result += attributeString;
		}
 
		result += "\n";

		return result;
	}

	/**
	 * Get the attributes names of this component 
	 * @return The attribute names
	 */
	public ArrayList<String> getConnectedAttributes() {
		return connectedAttributes;
	}

	/**
	 * Set the attributes of the current node
	 * @param ConnectedAttributes The attribute names
	 */
	public void setConnectedAttributes(ArrayList<String> connectedAttributes) {
		this.connectedAttributes = connectedAttributes;
	}
	
	/**
	 * Set the parent node of this component
	 * @param parent Node to set as a parent
	 */
	public void setParent(MetamodelNode parent) {
		this.parent = parent;
	}
	
	/**
	 * Get the current parent of this node
	 * @return The parent node
	 */
	public MetamodelNode getParent() {
		return parent;
	}

	/**
	 * Get the name of the current parent
	 * @return Name of parent
	 */
	public String getTempParent() {
		return tempParent;
	}

	/**
	 * Set the name of the current parent
	 * @param tempParent Name of parent
	 */
	public void setParentAsString(String tempParent) {
		this.tempParent = tempParent;
	}
}
	

