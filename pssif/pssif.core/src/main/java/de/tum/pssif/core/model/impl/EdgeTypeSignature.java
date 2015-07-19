package de.tum.pssif.core.model.impl;

import de.tum.pssif.core.metamodel.EdgeType;


/* package */final class EdgeTypeSignature {
  private final String edgeTypeName;

  public EdgeTypeSignature(EdgeType edgeType) {
    edgeTypeName = edgeType.getName();
  }

  public boolean isCompatibleWith(EdgeType edgeType) {
    return edgeTypeName.equals(edgeType.getName());
  }
}
