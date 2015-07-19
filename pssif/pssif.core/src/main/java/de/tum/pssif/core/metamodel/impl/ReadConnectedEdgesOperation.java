package de.tum.pssif.core.metamodel.impl;

import de.tum.pssif.core.common.PSSIFOption;
import de.tum.pssif.core.metamodel.ConnectionMapping;
import de.tum.pssif.core.model.Edge;
import de.tum.pssif.core.model.Node;


/* package */abstract class ReadConnectedEdgesOperation {
  private final ConnectionMapping mapping;

  public ReadConnectedEdgesOperation(ConnectionMapping mapping) {
    this.mapping = mapping;
  }

  public abstract PSSIFOption<Edge> apply(Node node);

  public final ConnectionMapping getMapping() {
    return mapping;
  }
}
