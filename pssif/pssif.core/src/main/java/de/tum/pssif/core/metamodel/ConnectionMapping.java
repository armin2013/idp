package de.tum.pssif.core.metamodel;

import de.tum.pssif.core.common.PSSIFOption;
import de.tum.pssif.core.model.Edge;
import de.tum.pssif.core.model.Model;
import de.tum.pssif.core.model.Node;


public interface ConnectionMapping {
  EdgeType getType();

  NodeTypeBase getTo();

  NodeTypeBase getFrom();

  Edge create(Model model, Node from, Node to);

  Node applyFrom(Edge edge);

  Node applyTo(Edge edge);

  PSSIFOption<Edge> apply(Model model);

  PSSIFOption<Edge> applyOutgoing(Node node);

  PSSIFOption<Edge> applyIncoming(Node node);
}
