package de.tum.pssif.core.metamodel.impl;

import java.util.Collection;
import java.util.Map;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import de.tum.pssif.core.common.PSSIFConstants;
import de.tum.pssif.core.common.PSSIFOption;
import de.tum.pssif.core.common.PSSIFUtil;
import de.tum.pssif.core.exception.PSSIFStructuralIntegrityException;
import de.tum.pssif.core.metamodel.Attribute;
import de.tum.pssif.core.metamodel.AttributeCategory;
import de.tum.pssif.core.metamodel.AttributeGroup;
import de.tum.pssif.core.metamodel.DataType;
import de.tum.pssif.core.metamodel.Enumeration;
import de.tum.pssif.core.metamodel.PrimitiveDataType;
import de.tum.pssif.core.metamodel.Unit;
import de.tum.pssif.core.metamodel.Units;
import de.tum.pssif.core.metamodel.mutable.MutableAttributeGroup;
import de.tum.pssif.core.metamodel.mutable.MutableElementType;


public abstract class ElementTypeImpl extends NamedImpl implements MutableElementType {
  private Map<String, MutableAttributeGroup> attributeGroups = Maps.newHashMap();

  public ElementTypeImpl(String name) {
    super(name);
  }

  @Override
  public Collection<AttributeGroup> getAttributeGroups() {
    return ImmutableSet.<AttributeGroup> copyOf(attributeGroups.values());
  }

  @Override
  public AttributeGroup getDefaultAttributeGroup() {
    PSSIFOption<AttributeGroup> result = getAttributeGroup(PSSIFConstants.DEFAULT_ATTRIBUTE_GROUP_NAME);
    Preconditions.checkState(result.isOne());
    return result.getOne();
  }

  @Override
  public PSSIFOption<AttributeGroup> getAttributeGroup(String name) {
    return PSSIFOption.<AttributeGroup> one(attributeGroups.get(PSSIFUtil.normalize(name)));
  }

  @Override
  public void removeAttributeGroup(AttributeGroup group) {
    PSSIFOption<MutableAttributeGroup> actualGroup = getMutableAttributeGroup(group.getName());
    for (MutableAttributeGroup g : actualGroup.getMany()) {
      attributeGroups.remove(g.getName());
    }
  }

  @Override
  public Attribute createAttribute(AttributeGroup group, String name, DataType dataType, Unit unit, boolean visible, AttributeCategory category) {
    PSSIFUtil.checkNameValidity(name);
    if (!getAttribute(name).isNone()) {
      throw new PSSIFStructuralIntegrityException("an attribute with name " + name + " already exists");
    }
    if (dataType.getMetaType().equals(Enumeration.class) && unit != Units.NONE) {
      throw new PSSIFStructuralIntegrityException("cannot create an enumeration attribute with a unit");
    }
    if (!(PrimitiveDataType.DECIMAL.equals(dataType) || PrimitiveDataType.INTEGER.equals(dataType)) && !Units.NONE.equals(unit)) {
      throw new PSSIFStructuralIntegrityException("Only numeric attributes can have units!");
    }
    PSSIFOption<MutableAttributeGroup> actualGroup = getMutableAttributeGroup(group.getName());
    Preconditions.checkArgument(actualGroup.isOne());
    Attribute result = new AttributeImpl(name, dataType, unit, visible, category);
    actualGroup.getOne().addAttribute(result);
    return result;
  }

  @Override
  public Attribute createAttribute(AttributeGroup group, String name, DataType dataType, boolean visible, AttributeCategory category) {
    return createAttribute(group, name, dataType, Units.NONE, visible, category);
  }

  @Override
  public Collection<Attribute> getAttributes() {
    Collection<Attribute> result = Sets.newHashSet();

    for (AttributeGroup group : getAttributeGroups()) {
      result.addAll(group.getAttributes());
    }

    return ImmutableSet.copyOf(result);
  }

  @Override
  public PSSIFOption<Attribute> getAttribute(String name) {
    PSSIFOption<Attribute> result = PSSIFOption.none();

    for (AttributeGroup group : getAttributeGroups()) {
      result = PSSIFOption.merge(result, group.getAttribute(name));
    }

    return result;
  }

  @Override
  public Collection<Attribute> getDirectAttributes() {
    Collection<Attribute> result = Sets.newHashSet();

    for (AttributeGroup group : getAttributeGroups()) {
      result.addAll(group.getDirectAttributes());
    }

    return ImmutableSet.copyOf(result);
  }

  @Override
  public PSSIFOption<Attribute> getDirectAttribute(String name) {
    PSSIFOption<Attribute> result = PSSIFOption.none();

    for (AttributeGroup group : getAttributeGroups()) {
      result = PSSIFOption.merge(result, group.getDirectAttribute(name));
    }

    return result;
  }

  @Override
  public void removeAttribute(Attribute attribute) {
    for (MutableAttributeGroup group : attributeGroups.values()) {
      PSSIFOption<Attribute> actualAttribute = group.getAttribute(attribute.getName());
      if (actualAttribute.isOne()) {
        group.removeAttribute(actualAttribute.getOne());
      }
    }
  }

  protected final void addAttributeGroup(MutableAttributeGroup group) {
    attributeGroups.put(PSSIFUtil.normalize(group.getName()), group);
  }

  @Override
  public PSSIFOption<MutableAttributeGroup> getMutableAttributeGroup(String name) {
    return PSSIFOption.one(attributeGroups.get(PSSIFUtil.normalize(name)));
  }

  @Override
  public MutableAttributeGroup getMutableDefaultAttributeGroup() {
    PSSIFOption<MutableAttributeGroup> result = getMutableAttributeGroup(PSSIFConstants.DEFAULT_ATTRIBUTE_GROUP_NAME);
    Preconditions.checkState(result.isOne());
    return result.getOne();
  }

  @Override
  public Collection<MutableAttributeGroup> getMutableAttributeGroups() {
    return ImmutableSet.copyOf(attributeGroups.values());
  }
}
