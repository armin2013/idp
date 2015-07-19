package de.tum.pssif.core.metamodel.impl;

import de.tum.pssif.core.metamodel.NodeTypeBase;
import de.tum.pssif.core.model.Model;
import de.tum.pssif.core.model.Node;

public final class RemoveNodeOperation {
	private NodeTypeBase nodeTypeBase;
	private final Node node;

	  RemoveNodeOperation(NodeTypeBase nodeTypeBase, Node node) {
	    this.node = node;
	    this.nodeTypeBase = nodeTypeBase;
	  }

	  public Node getNode() {
	    return node;
	  }
	  
	  public NodeTypeBase getType() {
		return nodeTypeBase;
	  }

	  public boolean apply(Model model) {
	    return model.apply(this);
	  }
}
