package de.tum.pssif.core.metamodel.impl;

import de.tum.pssif.core.model.Edge;
import de.tum.pssif.core.model.Node;


/* package */abstract class ReadConnectedNodesOperation {
  public abstract Node apply(Edge edge);
}
