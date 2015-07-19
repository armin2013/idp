package de.tum.pssif.core.metamodel;

import java.util.Collection;

import de.tum.pssif.core.common.PSSIFOption;


/**
 * An erumeration data type within a PSS-IF Metamodel.
 * Can be used as the Data Type of many Attribute Types.
 */
public interface Enumeration extends DataType {
  /**
   * @return
   *    The collection of all literals of this enumeration.
   */
  Collection<EnumerationLiteral> getLiterals();

  /**
   * Finds a literal by its name.
   * @param name
   *    The name of the literal.
   * @return
   *    The literal with this name, or <b>null</b> if it can not be found in this enumeration.
   */
  PSSIFOption<EnumerationLiteral> getLiteral(String name);
}
