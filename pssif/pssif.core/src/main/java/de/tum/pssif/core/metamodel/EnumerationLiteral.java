package de.tum.pssif.core.metamodel;

import de.tum.pssif.core.metamodel.traits.Named;


/**
 * A literal of an enumeration in a PSS-IF Metamodel.
 */
public interface EnumerationLiteral extends Named {

  /**
   * @return
   *    The enumeration to which the literal belongs.
   */
  Enumeration getOwner();

}
