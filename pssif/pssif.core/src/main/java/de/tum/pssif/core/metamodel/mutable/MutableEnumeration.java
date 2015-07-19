package de.tum.pssif.core.metamodel.mutable;

import de.tum.pssif.core.metamodel.Enumeration;
import de.tum.pssif.core.metamodel.EnumerationLiteral;


public interface MutableEnumeration extends Enumeration {
  /**
   * Creates a literal in this enumeration.
   * @param name
   *    The name of the literal.
   * @return
   *    The created literal.
   */
  EnumerationLiteral createLiteral(String name);

  /**
   * Removes a literal from this enumeration.
   * @param literal
   *    The literal to remove.
   */
  void removeLiteral(EnumerationLiteral literal);
}
