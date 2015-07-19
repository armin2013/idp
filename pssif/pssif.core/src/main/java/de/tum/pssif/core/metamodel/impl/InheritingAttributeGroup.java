package de.tum.pssif.core.metamodel.impl;

import java.util.Collection;

import com.google.common.collect.Sets;

import de.tum.pssif.core.common.PSSIFOption;
import de.tum.pssif.core.metamodel.Attribute;
import de.tum.pssif.core.metamodel.AttributeGroup;
import de.tum.pssif.core.metamodel.ElementType;
import de.tum.pssif.core.metamodel.traits.Specializable;


public class InheritingAttributeGroup<T extends ElementType & Specializable<T>> extends AbstractAttributeGroup<T> {
  public InheritingAttributeGroup(String name, T owner) {
    super(name, owner);
  }

  @Override
  public Collection<Attribute> getAttributes() {
    Collection<Attribute> result = Sets.newHashSet(getDirectAttributes());

    for (T general : getOwner().getGeneral().getMany()) {
      for (AttributeGroup g : general.getAttributeGroup(getName()).getMany()) {
        result.addAll(g.getAttributes());
      }
    }

    return result;
  }

  @Override
  public PSSIFOption<Attribute> getAttribute(String name) {
    PSSIFOption<Attribute> result = getDirectAttribute(name);

    for (T general : getOwner().getGeneral().getMany()) {
      result = PSSIFOption.merge(result, general.getAttribute(name));
    }

    return result;
  }
}
