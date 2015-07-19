package de.tum.pssif.core.metamodel.traits;

import java.util.Collection;

import de.tum.pssif.core.common.PSSIFOption;
import de.tum.pssif.core.metamodel.Attribute;


public interface Attributable {
  Collection<Attribute> getAttributes();

  PSSIFOption<Attribute> getAttribute(String name);

  Collection<Attribute> getDirectAttributes();

  PSSIFOption<Attribute> getDirectAttribute(String name);
}
