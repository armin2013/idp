package de.tum.pssif.core.metamodel.mutable;

import de.tum.pssif.core.metamodel.ConnectionMapping;


public interface MutableConnectionMapping extends ConnectionMapping {
  void setFrom(MutableNodeTypeBase from);

  void setTo(MutableNodeTypeBase to);
}
