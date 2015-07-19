package de.tum.pssif.core.metamodel.impl;

import de.tum.pssif.core.common.PSSIFOption;
import de.tum.pssif.core.metamodel.NodeTypeBase;
import de.tum.pssif.core.model.Model;
import de.tum.pssif.core.model.Node;


public final class ReadNodesOperation {
  private final NodeTypeBase type;

  /*package*/ReadNodesOperation(NodeTypeBase type) {
    this.type = type;
  }

  public NodeTypeBase getType() {
    return type;
  }

  public PSSIFOption<Node> apply(Model model) {
    return model.apply(this);
  }
}
