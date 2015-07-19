package de.tum.pssif.core.common;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import com.google.common.collect.Sets;

import de.tum.pssif.core.exception.PSSIFIllegalAccessException;


/**
 * An immutable result which is mull, a single element, or a number of elements.
 *
 * @param <P>
 *  The contained type.
 */
public final class PSSIFOption<P> implements Iterable<P> {

  private final Set<P> elements;

  /**
   * Merges two options into a single option.
   * @param option1
   *    The first option.
   * @param option2
   *    The second option.
   * @return
   *    The new merged option.
   */
  public static <T> PSSIFOption<T> merge(PSSIFOption<? extends T> option1, PSSIFOption<? extends T> option2) {
    Set<T> all = Sets.newHashSet();
    all.addAll(option1.getMany());
    all.addAll(option2.getMany());
    return many(all);
  }

  /**
   * Creates a new option containing a single element.
   * If the element is null, the resulting option will be none.
   * @param element
   *    The element for which to create the option.
   * @return
   *    The resulting option - either one or none.
   */
  public static <T> PSSIFOption<T> one(T element) {
    Set<T> elements = Sets.newHashSet();
    if (element != null) {
      elements.add(element);
    }
    return new PSSIFOption<T>(elements);
  }

  /**
   * Creates a new none (empty) option.
   * @return
   *    The resulting none option.
   */
  public static <T> PSSIFOption<T> none() {
    return new PSSIFOption<T>(null);
  }

  /**
   * Creates an option for a collection of elements.
   * If the collection is empty, a none option will be created.
   * If the collection has only one element, a one option will be created.
   * @param elements
   *    The elements for which to create the option.
   * @return
   *    The resulting option, can be many, one or none, depending on the
   *    contents of the collection.
   */
  public static <T> PSSIFOption<T> many(Collection<? extends T> elements) {
    return new PSSIFOption<T>(elements);
  }

  /**
   * Creates an option for a collection of elements.
   * If the collection is empty, a none option will be created.
   * If the collection has only one element, a one option will be created.
   * @param elements
   *    The elements for which to create the option.
   * @return
   *    The resulting option, can be many, one or none, depending on the
   *    contents of the collection.
   */
  public static <T> PSSIFOption<T> many(T... elements) {
    return new PSSIFOption<T>(Sets.newHashSet(elements));
  }

  private PSSIFOption(Collection<? extends P> elements) {
    this.elements = Sets.newHashSet();
    if (elements != null) {
      for (P elem : elements) {
        if (elem != null) {
          this.elements.add(elem);
        }
      }
    }
  }

  /**
   * @return
   *    Whether this option is one, i.e. contains a single element.
   */
  public boolean isOne() {
    return elements.size() == 1;
  }

  /**
   * @return
   *    Whether this option is many, i.e. contains more than one element.
   */
  public boolean isMany() {
    return elements.size() > 1;
  }

  /**
   * @return
   *    Whether this option is none, i.e. contains no elements.
   */
  public boolean isNone() {
    return elements.isEmpty();
  }

  /**
   * @return
   *    The one element contained in this option.
   * @throws PSSIFIllegalAccessException
   *    If this element is none or many.
   */
  public P getOne() throws PSSIFIllegalAccessException {
    if (!isOne()) {
      throw new PSSIFIllegalAccessException("many or none can not be accessed as one");
    }
    return elements.iterator().next();
  }

  /**
   * @return
   *    Retrieves the contents of this option as a set.
   */
  public Set<P> getMany() {
    return Sets.newHashSet(elements);
  }

  /**
   * @return
   *    The numer of elements in this option.
   */
  public int size() {
    return elements.size();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof PSSIFOption) {
      return elements.equals(((PSSIFOption<?>) obj).getMany());
    }
    return false;
  }

  @Override
  public int hashCode() {
    return elements.hashCode();
  }

  @Override
  public Iterator<P> iterator() {
    return this.elements.iterator();
  }
}
