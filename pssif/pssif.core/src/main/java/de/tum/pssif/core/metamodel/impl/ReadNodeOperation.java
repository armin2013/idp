package de.tum.pssif.core.metamodel.impl;

import de.tum.pssif.core.common.PSSIFOption;
import de.tum.pssif.core.metamodel.NodeTypeBase;
import de.tum.pssif.core.model.Model;
import de.tum.pssif.core.model.Node;


public final class ReadNodeOperation {
  private final NodeTypeBase type;
  private final String   id;

  /*package*/ ReadNodeOperation(NodeTypeBase type, String id) {
    this.type = type;
    this.id = id;
  }

  public NodeTypeBase getType() {
    return type;
  }

  public String getId() {
    return id;
  }

  public PSSIFOption<Node> apply(Model model) {
    return model.apply(this);
  }

}
