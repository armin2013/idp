package de.tum.pssif.core.model;

import de.tum.pssif.core.common.PSSIFOption;
import de.tum.pssif.core.metamodel.ConnectionMapping;
import de.tum.pssif.core.metamodel.EdgeType;
import de.tum.pssif.core.metamodel.impl.ReadIncomingNodesOperation;
import de.tum.pssif.core.metamodel.impl.ReadOutgoingNodesOperation;


public interface Node extends Element {
  PSSIFOption<Edge> apply(ReadOutgoingNodesOperation op);

  PSSIFOption<Edge> apply(ReadIncomingNodesOperation op);

  void registerOutgoingEdge(ConnectionMapping mapping, Edge edge);

  void registerIncomingEdge(ConnectionMapping mapping, Edge edge);

  boolean isEdgeTypeCompatible(EdgeType edgeType);

  void initializeEdgeTypeSignature(EdgeType edgeType);
}
