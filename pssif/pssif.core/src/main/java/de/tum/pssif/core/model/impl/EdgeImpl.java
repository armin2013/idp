package de.tum.pssif.core.model.impl;

import de.tum.pssif.core.metamodel.impl.ReadFromNodesOperation;
import de.tum.pssif.core.metamodel.impl.ReadToNodesOperation;
import de.tum.pssif.core.model.Edge;
import de.tum.pssif.core.model.Model;
import de.tum.pssif.core.model.Node;


public class EdgeImpl extends ElementImpl implements Edge {
  private final Node from;
  private final Node to;

  public EdgeImpl(Model model, Node from, Node to) {
    super(model);
    this.from = from;
    this.to = to;
  }

  @Override
  public Node apply(ReadFromNodesOperation op) {
    return from;
  }

  @Override
  public Node apply(ReadToNodesOperation op) {
    return to;
  }
}
