package de.tum.pssif.transform.transformation.viewed;

import de.tum.pssif.core.common.PSSIFOption;
import de.tum.pssif.core.common.PSSIFValue;
import de.tum.pssif.core.metamodel.Attribute;
import de.tum.pssif.core.metamodel.AttributeCategory;
import de.tum.pssif.core.metamodel.DataType;
import de.tum.pssif.core.metamodel.Unit;
import de.tum.pssif.core.metamodel.impl.AttributeImpl;
import de.tum.pssif.core.model.Element;


public class ViewedAttribute extends AttributeImpl {
  private final Attribute baseAttribute;

  public ViewedAttribute(Attribute baseAttribute, String name, DataType type, Unit unit, boolean visible, AttributeCategory category) {
    super(name, type, unit, visible, category);
    this.baseAttribute = baseAttribute;
  }

  @Override
  public void set(Element element, PSSIFOption<PSSIFValue> value) {
    baseAttribute.set(element, value);
  }

  @Override
  public PSSIFOption<PSSIFValue> get(Element element) {
    return baseAttribute.get(element);
  }

}
