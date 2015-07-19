package de.tum.pssif.core.metamodel;

/**
 * A convenience enumeration which makes it possible to
 * assign a category to each attribute type.
 * <br><br>
 * Attribute Categories are a shortcut around the
 * concept of specialization introduced for attributes
 * in the PSS-IF metamodel specification (see vdx).
 */
public enum AttributeCategory {

  MONETARY("Monetary"), WEIGHT("Weight"), DENSITY("Density"), TIME("Time"), GEOMETRY("Geometry"), METADATA("MetaData"), MATERIAL("Material");

  private final String name;

  private AttributeCategory(String name) {
    this.name = name;
  }

  public String toString() {
    return this.name;
  }

  public String getName() {
    return this.name;
  }

}
