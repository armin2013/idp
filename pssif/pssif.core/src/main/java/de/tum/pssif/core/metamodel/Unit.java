package de.tum.pssif.core.metamodel;

import de.tum.pssif.core.metamodel.traits.Named;

/**
 * A unit of measurement used as a feature of numeric
 * Attribute Types throughout the PSS-IF metamodel. For convenience,
 * a unit NONE also exists, and can be used with any Attribute Type.
 *
 */
public interface Unit extends Named {

  /**
   * @return
   *    The abbreviation of this unut.
   */
  String getAbbreviation();

  /**
   * @return
   *    Whether the unit is in the SI standard.
   */
  boolean isSi();

}
