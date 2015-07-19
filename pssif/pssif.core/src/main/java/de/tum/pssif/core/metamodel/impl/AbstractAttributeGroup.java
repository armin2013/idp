package de.tum.pssif.core.metamodel.impl;

import java.util.Collection;
import java.util.Map;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

import de.tum.pssif.core.common.PSSIFOption;
import de.tum.pssif.core.common.PSSIFUtil;
import de.tum.pssif.core.metamodel.Attribute;
import de.tum.pssif.core.metamodel.AttributeGroup;
import de.tum.pssif.core.metamodel.ElementType;
import de.tum.pssif.core.metamodel.mutable.MutableAttributeGroup;


public abstract class AbstractAttributeGroup<T extends ElementType> extends NamedImpl implements MutableAttributeGroup {
  private Map<String, Attribute> attributes = Maps.newHashMap();
  private final T                owner;

  public AbstractAttributeGroup(String name, T owner) {
    super(name);
    this.owner = owner;
  }

  @Override
  public Class<?> getMetaType() {
    return AttributeGroup.class;
  }

  @Override
  public void addAttribute(Attribute attribute) {
    attributes.put(PSSIFUtil.normalize(attribute.getName()), attribute);
  }

  @Override
  public Collection<Attribute> getDirectAttributes() {
    return ImmutableSet.copyOf(attributes.values());
  }

  @Override
  public PSSIFOption<Attribute> getDirectAttribute(String name) {
    return PSSIFOption.one(attributes.get(PSSIFUtil.normalize(name)));
  }

  @Override
  public void removeAttribute(Attribute attribute) {
    attributes.remove(PSSIFUtil.normalize(attribute.getName()));
  }

  protected final T getOwner() {
    return owner;
  }
}
