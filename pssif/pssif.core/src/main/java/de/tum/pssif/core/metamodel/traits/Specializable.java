package de.tum.pssif.core.metamodel.traits;

import java.util.Collection;

import de.tum.pssif.core.common.PSSIFOption;
import de.tum.pssif.core.metamodel.ElementType;


public interface Specializable<T extends ElementType> {
  /**
   * @return
   *    The generalization of the context element type. May be <b>null</b>.
   */
  PSSIFOption<T> getGeneral();

  /**
   * @return
   *    The specializations of the context element type. May be empty, but not <b>null</b>.
   */
  Collection<T> getSpecials();

  /**
   * Sets the context element as a specialization of the provided general element.
   * @param general
   *    The general element, of which the context element becomes a specialization.
   */
  void inherit(T general);

  /**
   * Registers a specialization with its generalizaton.
   * <b>USED INTERNALLY ONLY!</b> TODO get rid of it.
   * @param special
   */
  void registerSpecialization(T special);

  /**
   * Un-registers a specialization from its generalizaton.
   * <b>USED INTERNALLY ONLY!</b> TODO get rid of it.
   * @param special
   */
  void unregisterSpecialization(T special);
}
