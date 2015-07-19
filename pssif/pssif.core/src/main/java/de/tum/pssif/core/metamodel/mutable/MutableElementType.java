package de.tum.pssif.core.metamodel.mutable;

import java.util.Collection;

import de.tum.pssif.core.common.PSSIFOption;
import de.tum.pssif.core.metamodel.Attribute;
import de.tum.pssif.core.metamodel.AttributeCategory;
import de.tum.pssif.core.metamodel.AttributeGroup;
import de.tum.pssif.core.metamodel.DataType;
import de.tum.pssif.core.metamodel.ElementType;
import de.tum.pssif.core.metamodel.Unit;


public interface MutableElementType extends ElementType {
  AttributeGroup createAttributeGroup(String name);

  MutableAttributeGroup getMutableDefaultAttributeGroup();

  PSSIFOption<MutableAttributeGroup> getMutableAttributeGroup(String name);

  Collection<MutableAttributeGroup> getMutableAttributeGroups();

  void removeAttributeGroup(AttributeGroup group);

  Attribute createAttribute(AttributeGroup group, String name, DataType dataType, boolean visible, AttributeCategory category);

  Attribute createAttribute(AttributeGroup group, String name, DataType dataType, Unit unit, boolean visible, AttributeCategory category);

  void removeAttribute(Attribute attribute);
}
