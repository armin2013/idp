package de.tum.pssif.transform.transformation.viewed;

import de.tum.pssif.core.metamodel.Attribute;
import de.tum.pssif.core.metamodel.AttributeGroup;
import de.tum.pssif.core.metamodel.JunctionNodeType;
import de.tum.pssif.core.metamodel.impl.NonInheritingAttributeGroup;


public class ViewedNonInheritingAttributeGroup extends NonInheritingAttributeGroup {
  public ViewedNonInheritingAttributeGroup(AttributeGroup baseGroup, String name, JunctionNodeType owner) {
    super(name, owner);

    for (Attribute a : baseGroup.getDirectAttributes()) {
      addAttribute(new ViewedAttribute(a, a.getName(), a.getType(), a.getUnit(), a.isVisible(), a.getCategory()));
    }
  }
}
