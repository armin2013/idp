package de.tum.pssif.core.metamodel;

import de.tum.pssif.core.common.PSSIFOption;
import de.tum.pssif.core.metamodel.traits.Specializable;
import de.tum.pssif.core.model.Edge;
import de.tum.pssif.core.model.Model;
import de.tum.pssif.core.model.Node;


public interface EdgeType extends ElementType, Specializable<EdgeType> {
  PSSIFOption<ConnectionMapping> getMappings();

  PSSIFOption<ConnectionMapping> getMapping(NodeTypeBase from, NodeTypeBase to);

  PSSIFOption<ConnectionMapping> getOutgoingMappings(NodeTypeBase from);

  PSSIFOption<ConnectionMapping> getIncomingMappings(NodeTypeBase to);

  Node applyFrom(Edge edge);

  Node applyTo(Edge edge);

  PSSIFOption<Edge> apply(Model model, boolean includeSubtypes);

  PSSIFOption<Edge> applyOutgoing(Node node, boolean includeSubtypes);

  PSSIFOption<Edge> applyIncoming(Node node, boolean includeSubtypes);
}
