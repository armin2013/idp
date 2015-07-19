package de.tum.pssif.transform.transformation.viewed;

import de.tum.pssif.core.metamodel.Attribute;
import de.tum.pssif.core.metamodel.AttributeGroup;
import de.tum.pssif.core.metamodel.ElementType;
import de.tum.pssif.core.metamodel.impl.InheritingAttributeGroup;
import de.tum.pssif.core.metamodel.traits.Specializable;


public class ViewedInheritingAttributeGroup<T extends ElementType & Specializable<T>> extends InheritingAttributeGroup<T> {
  public ViewedInheritingAttributeGroup(AttributeGroup baseGroup, String name, T owner) {
    super(name, owner);

    for (Attribute a : baseGroup.getDirectAttributes()) {
      addAttribute(new ViewedAttribute(a, a.getName(), a.getType(), a.getUnit(), a.isVisible(), a.getCategory()));
    }
  }
}
