package de.tum.pssif.core.metamodel.impl;

import de.tum.pssif.core.common.PSSIFOption;
import de.tum.pssif.core.metamodel.ConnectionMapping;
import de.tum.pssif.core.model.Edge;
import de.tum.pssif.core.model.Node;


public final class ReadIncomingNodesOperation extends ReadConnectedEdgesOperation {
  public ReadIncomingNodesOperation(ConnectionMapping mapping) {
    super(mapping);
  }

  @Override
  public PSSIFOption<Edge> apply(Node node) {
    return node.apply(this);
  }
}
