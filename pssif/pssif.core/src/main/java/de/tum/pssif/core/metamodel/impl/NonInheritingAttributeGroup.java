package de.tum.pssif.core.metamodel.impl;

import java.util.Collection;

import de.tum.pssif.core.common.PSSIFOption;
import de.tum.pssif.core.metamodel.Attribute;
import de.tum.pssif.core.metamodel.AttributeGroup;
import de.tum.pssif.core.metamodel.JunctionNodeType;


public class NonInheritingAttributeGroup extends AbstractAttributeGroup<JunctionNodeType> {
  public NonInheritingAttributeGroup(String name, JunctionNodeType owner) {
    super(name, owner);
  }

  @Override
  public Collection<Attribute> getAttributes() {
    return getDirectAttributes();
  }

  @Override
  public PSSIFOption<Attribute> getAttribute(String name) {
    return getDirectAttribute(name);
  }

  @Override
  public Class<?> getMetaType() {
    return AttributeGroup.class;
  }
}
