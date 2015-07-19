package de.tum.pssif.core.common;

import java.util.Collection;

import de.tum.pssif.core.exception.PSSIFStructuralIntegrityException;
import de.tum.pssif.core.metamodel.traits.Named;


/**
 * Utility class for common procedured used throughout the PSS-IF core.
 */
public class PSSIFUtil {

  /**
   * A null-safe case-insensitive trimming comparison of two Strings.
   * @param name1
   *    The first String.
   * @param name2
   *    The second String.
   * @return
   *    true if the two strings are the same after normalization (see {@link de.tum.pssif.core.util.PSSIFUtil.normalize()}).
   */
  public static boolean areSame(String s1, String s2) {
    return normalize(s1).equals(normalize(s2));
  }

  /**
   * Normalizes a String. If the string is null, an empty string
   * is returned, otherwise the string is trimmed and converted to
   * lower case.
   * @param in
   *    The string to normalize.
   * @return
   *    The normalized string.
   */
  public static String normalize(String in) {
    return in == null ? "" : in.trim().toLowerCase();
  }

  public static boolean isValidName(String name) {
    return !normalize(name).isEmpty();
  }

  public static void checkNameValidity(String name) {
    if (!PSSIFUtil.isValidName(name)) {
      throw new PSSIFStructuralIntegrityException("invalid name '" + name + "'");
    }
  }

  /**
   * Locates a named element by name in a collection of named elements.
   * TODO use throughout the metamodel impl.
   * @param name
   *    The name of the element to locate.
   * @param collection
   *    The collection to look into.
   * @return
   *    The named element, or null, it not found in the collection.
   */
  public static <T extends Named> T find(String name, Collection<T> collection) {
    for (T candidate : collection) {
      if (PSSIFUtil.areSame(candidate.getName(), name)) {
        return candidate;
      }
    }
    return null;
  }
}
