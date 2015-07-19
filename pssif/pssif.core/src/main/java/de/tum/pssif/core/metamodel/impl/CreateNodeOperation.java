package de.tum.pssif.core.metamodel.impl;

import de.tum.pssif.core.metamodel.NodeTypeBase;
import de.tum.pssif.core.model.Model;
import de.tum.pssif.core.model.Node;


public final class CreateNodeOperation {
  private final NodeTypeBase type;

  /*package*/CreateNodeOperation(NodeTypeBase type) {
    this.type = type;
  }

  public NodeTypeBase getType() {
    return type;
  }

  public Node apply(Model model) {
    return model.apply(this);
  }
}
