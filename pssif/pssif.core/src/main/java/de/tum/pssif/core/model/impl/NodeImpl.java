package de.tum.pssif.core.model.impl;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import de.tum.pssif.core.common.PSSIFOption;
import de.tum.pssif.core.metamodel.ConnectionMapping;
import de.tum.pssif.core.metamodel.EdgeType;
import de.tum.pssif.core.metamodel.impl.ReadIncomingNodesOperation;
import de.tum.pssif.core.metamodel.impl.ReadOutgoingNodesOperation;
import de.tum.pssif.core.model.Edge;
import de.tum.pssif.core.model.Model;
import de.tum.pssif.core.model.Node;


public class NodeImpl extends ElementImpl implements Node {
  private Multimap<ConnectionMappingSignature, Edge> outgoingEdges = HashMultimap.create();
  private Multimap<ConnectionMappingSignature, Edge> incomingEdges = HashMultimap.create();

  public NodeImpl(Model model) {
    super(model);
  }

  @Override
  public PSSIFOption<Edge> apply(ReadOutgoingNodesOperation op) {
    for (ConnectionMappingSignature candidate : outgoingEdges.keySet()) {
      if (candidate.isCompatibleWith(op.getMapping())) {
        return PSSIFOption.many(outgoingEdges.get(candidate));
      }
    }
    return PSSIFOption.none();
  }

  @Override
  public PSSIFOption<Edge> apply(ReadIncomingNodesOperation op) {
    for (ConnectionMappingSignature candidate : incomingEdges.keySet()) {
      if (candidate.isCompatibleWith(op.getMapping())) {
        return PSSIFOption.many(incomingEdges.get(candidate));
      }
    }
    return PSSIFOption.none();
  }

  @Override
  public void registerOutgoingEdge(ConnectionMapping mapping, Edge edge) {
    outgoingEdges.put(new ConnectionMappingSignature(mapping), edge);
  }

  @Override
  public void registerIncomingEdge(ConnectionMapping mapping, Edge edge) {
    incomingEdges.put(new ConnectionMappingSignature(mapping), edge);
  }

  @Override
  public boolean isEdgeTypeCompatible(EdgeType edgeType) {
    return true;
  }

  @Override
  public void initializeEdgeTypeSignature(EdgeType edgeType) {
    //nothing to do here
  }
}
