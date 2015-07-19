package de.tum.pssif.core.metamodel;

import java.util.Collection;

import de.tum.pssif.core.common.PSSIFOption;
import de.tum.pssif.core.metamodel.traits.Attributable;
import de.tum.pssif.core.metamodel.traits.Named;


public interface ElementType extends Named, Attributable {
  boolean isAssignableFrom(ElementType type);

  Collection<AttributeGroup> getAttributeGroups();

  AttributeGroup getDefaultAttributeGroup();

  PSSIFOption<AttributeGroup> getAttributeGroup(String name);
}
