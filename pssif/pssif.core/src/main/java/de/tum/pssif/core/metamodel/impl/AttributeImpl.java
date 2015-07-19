package de.tum.pssif.core.metamodel.impl;

import de.tum.pssif.core.common.PSSIFOption;
import de.tum.pssif.core.common.PSSIFValue;
import de.tum.pssif.core.metamodel.Attribute;
import de.tum.pssif.core.metamodel.AttributeCategory;
import de.tum.pssif.core.metamodel.DataType;
import de.tum.pssif.core.metamodel.Unit;
import de.tum.pssif.core.model.Element;


public class AttributeImpl extends NamedImpl implements Attribute {
  private final DataType          type;
  private final Unit              unit;
  private final boolean           visible;
  private final AttributeCategory category;

  public AttributeImpl(String name, DataType dataType, boolean visible, AttributeCategory category) {
    this(name, dataType, null, visible, category);
  }

  public AttributeImpl(String name, DataType dataType, Unit unit, boolean visible, AttributeCategory category) {
    super(name);
    type = dataType;
    this.unit = unit;
    this.visible = visible;
    this.category = category;
  }

  @Override
  public Class<?> getMetaType() {
    return Attribute.class;
  }

  @Override
  public PSSIFOption<PSSIFValue> get(Element element) {
    return new GetValueOperation(this).apply(element);
  }

  @Override
  public AttributeCategory getCategory() {
    return category;
  }

  @Override
  public DataType getType() {
    return type;
  }

  @Override
  public Unit getUnit() {
    return unit;
  }

  @Override
  public boolean isVisible() {
    return visible;
  }

  @Override
  public void set(Element element, PSSIFOption<PSSIFValue> value) {
    new SetValueOperation(this, value).apply(element);
  }
}
