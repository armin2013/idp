package de.tum.pssif.core.metamodel;

import de.tum.pssif.core.common.PSSIFOption;
import de.tum.pssif.core.common.PSSIFValue;
import de.tum.pssif.core.metamodel.traits.Named;
import de.tum.pssif.core.model.Element;


public interface Attribute extends Named {
  /**
   * @return
   *    The data type of this attribute type.
   */
  DataType getType();

  /**
   * @return
   *    The units of this attribute type. Can be {@link de.tum.pssif.core.metamodel.Units.NONE}.
   */
  Unit getUnit();

  /**
   * @return
   *    Whether the attribute type is visible. This kind of visibility
   *    is concerned with UI and visualizations and does <b>not</b> 
   *    have anyting to do with permissions. 
   */
  boolean isVisible();

  /**
   * @return
   *    The category of this attribute type. See {@link AttributeCategory}.
   */
  AttributeCategory getCategory();

  /**
   * Set the specified value as value of this {@link Attribute} for the specified {@link Element}
   * 
   * @param element the {@link Element}
   * @param value the value to set
   */
  void set(Element element, PSSIFOption<PSSIFValue> value);

  /**
   * Get the value for this {@link Attribute} for the specified {@link Element}
   * 
   * @param element the {@link Element}
   * @return the value of this {@link Attribute} for the specified {@link Element}
   */
  PSSIFOption<PSSIFValue> get(Element element);
}
