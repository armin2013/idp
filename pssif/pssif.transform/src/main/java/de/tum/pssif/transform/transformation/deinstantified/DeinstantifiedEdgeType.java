package de.tum.pssif.transform.transformation.deinstantified;

import de.tum.pssif.core.metamodel.ConnectionMapping;
import de.tum.pssif.core.metamodel.mutable.MutableEdgeType;
import de.tum.pssif.transform.transformation.viewed.ViewedEdgeType;


public class DeinstantifiedEdgeType extends ViewedEdgeType {
  public DeinstantifiedEdgeType(MutableEdgeType baseType) {
    super(baseType);

    for (ConnectionMapping mapping : baseType.getMutableMappings().getMany()) {
      addMapping(new DeinstantifiedConnectionMapping(mapping, this, mapping.getFrom(), mapping.getTo()));
    }
  }
}