package de.tum.pssif.core.metamodel.external;

/**
 * Represents a conjunction as it is saved in the metamodel file
 * @author Alex
 *
 */
public class MetamodelConjunction extends MetamodelComponent {

	/**
	 * Constructor for conjunction objects
	 * @param tag Tag of the conjunction
	 * @param name Name of the conjunction
	 */
	public MetamodelConjunction(String tag, String name) {
		super(tag, name, "CONJUNCTION");
	}

	/**
	 * Formatted Conjunction
	 */
	public String toString() {
		return "Type:\t" + super.getType() + "\nTag:\t" + super.getTag()
				+ "\nName:\t" + super.getName() + "\n";
	}

}
