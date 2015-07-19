package de.tum.pssif.transform.transformation.deinstantified;

import de.tum.pssif.core.common.PSSIFOption;
import de.tum.pssif.core.exception.PSSIFIllegalAccessException;
import de.tum.pssif.core.metamodel.ConnectionMapping;
import de.tum.pssif.core.metamodel.EdgeType;
import de.tum.pssif.core.metamodel.NodeTypeBase;
import de.tum.pssif.core.model.Edge;
import de.tum.pssif.core.model.Model;
import de.tum.pssif.core.model.Node;
import de.tum.pssif.transform.transformation.viewed.ViewedConnectionMapping;


public class DeinstantifiedConnectionMapping extends ViewedConnectionMapping {
  public DeinstantifiedConnectionMapping(ConnectionMapping baseMapping, EdgeType type, NodeTypeBase from, NodeTypeBase to) {
    super(baseMapping, type, from, to);
  }

  @Override
  public PSSIFOption<Edge> apply(Model model) {
    return PSSIFOption.none();
  }

  @Override
  public Node applyFrom(Edge edge) {
    throw new PSSIFIllegalAccessException("cannot apply on deinstantified connection mapping");
  }

  @Override
  public Node applyTo(Edge edge) {
    throw new PSSIFIllegalAccessException("cannot apply on deinstantified connection mapping");
  }

  @Override
  public PSSIFOption<Edge> applyIncoming(Node node) {
    return PSSIFOption.none();
  }

  @Override
  public PSSIFOption<Edge> applyOutgoing(Node node) {
    return PSSIFOption.none();
  }
}
