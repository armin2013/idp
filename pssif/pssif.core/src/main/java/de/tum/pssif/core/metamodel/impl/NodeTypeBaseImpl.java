package de.tum.pssif.core.metamodel.impl;

import de.tum.pssif.core.metamodel.ConnectionMapping;
import de.tum.pssif.core.metamodel.mutable.MutableNodeTypeBase;
import de.tum.pssif.core.model.Edge;
import de.tum.pssif.core.model.Model;
import de.tum.pssif.core.model.Node;


public abstract class NodeTypeBaseImpl extends ElementTypeImpl implements MutableNodeTypeBase {
  public NodeTypeBaseImpl(String name) {
    super(name);
  }

  @Override
  public Node create(Model model) {
    return new CreateNodeOperation(this).apply(model);
  }

  @Override
  public void onOutgoingEdgeCreated(Node sourceNode, ConnectionMapping mapping, Edge edge) {
    sourceNode.registerOutgoingEdge(mapping, edge);
  }

  @Override
  public void onIncomingEdgeCreated(Node targetNode, ConnectionMapping mapping, Edge edge) {
    targetNode.registerIncomingEdge(mapping, edge);
  }
}
