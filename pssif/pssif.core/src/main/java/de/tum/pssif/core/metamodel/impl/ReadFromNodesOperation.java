package de.tum.pssif.core.metamodel.impl;

import de.tum.pssif.core.model.Edge;
import de.tum.pssif.core.model.Node;


public final class ReadFromNodesOperation extends ReadConnectedNodesOperation {
  @Override
  public Node apply(Edge edge) {
    return edge.apply(this);
  }
}
