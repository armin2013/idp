package de.tum.pssif.core.metamodel.external;

import java.util.ArrayList;

import de.tum.pssif.core.model.Tupel;

/**
 * Represents an edge as it is represented in the external metamodel file
 * @author Alex
 *
 */
public class MetamodelEdge extends MetamodelComponent {

	private ArrayList<Tupel> mappings;
	private MetamodelEdge parent;
	private String tempParent;

	/**
	 * Constructor for an edge component
	 * Caution! Only use for import
	 * @param tag Tag of the edge
	 * @param name Name of the edge
	 */
	public MetamodelEdge(String tag, String name) {
		super(tag, name, "EDGE");
	}
	
	/**
	 * Constructor for an edge component
	 * @param tag Tag of the edge
	 * @param name Name of the edge
	 * @param parent Parent of the edge
	 */
	public MetamodelEdge(String tag, String name, MetamodelEdge parent) {
		super(tag, name, "EDGE");
		this.parent = parent;
	}

	/**
	 * Get all mappings of the current component
	 * @return List of all mappings of this component
	 */
	public ArrayList<Tupel> getMappings() {
		return mappings;
	}

	/**
	 * Set the mappings of this component
	 * @param mappings List of all mappings for this component
	 */
	public void setMappings(ArrayList<Tupel> mappings) {
		this.mappings = mappings;
	}
	
	/**
	 * As a String formatted edge
	 */
	public String toString() {

		String result = "";

		result += "Type:\t" + super.getType() + "\n";
		result += "Tag:\t" + super.getTag() + "\n";
		result += "Name:\t" + super.getName() + "\n";

		if (parent != null) {
			result += "Parent:\t" + parent.getName() + "\n";
		}

		if (mappings != null) {
			String mapping = "";
			for (Tupel tupel : mappings) {
				mapping += "\tMapping: " + tupel.getFirst() + "\t=> "
						+ tupel.getSecond() + "\n";
			}

			result += mapping;

		}
 
		result += "\n";

		return result;
	}

	/**
	 * Set the parent edge of this component
	 * @param parent The new parent edge
	 */
	public void setParent(MetamodelEdge parent) {
		this.parent = parent;
	}
	
	/**
	 * Get the parent edge of this component
	 * @return The parent edge
	 */
	public MetamodelEdge getParent() {
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
	public void setTempParent(String tempParent) {
		this.tempParent = tempParent;
	}
}
