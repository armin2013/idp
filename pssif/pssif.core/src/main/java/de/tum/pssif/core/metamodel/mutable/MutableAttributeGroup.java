package de.tum.pssif.core.metamodel.mutable;

import de.tum.pssif.core.metamodel.Attribute;
import de.tum.pssif.core.metamodel.AttributeGroup;


public interface MutableAttributeGroup extends AttributeGroup {
  void addAttribute(Attribute attribute);

  void removeAttribute(Attribute attribute);
}