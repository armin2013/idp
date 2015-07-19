package de.tum.pssif.transform.transformation.viewed;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Sets;

import de.tum.pssif.core.common.PSSIFOption;
import de.tum.pssif.core.metamodel.ConnectionMapping;
import de.tum.pssif.core.metamodel.EdgeType;
import de.tum.pssif.core.metamodel.NodeTypeBase;
import de.tum.pssif.core.metamodel.impl.ConnectionMappingImpl;
import de.tum.pssif.core.model.Edge;
import de.tum.pssif.core.model.Model;
import de.tum.pssif.core.model.Node;


public class ViewedConnectionMapping extends ConnectionMappingImpl {
  private final ConnectionMapping baseMapping;

  public ViewedConnectionMapping(ConnectionMapping baseMapping, EdgeType type, NodeTypeBase from, NodeTypeBase to) {
    super(type, from, to);
    this.baseMapping = baseMapping;
  }

  @Override
  public PSSIFOption<Edge> apply(final Model model) {
    return filter(baseMapping.apply(model));
  }

  @Override
  public Node applyFrom(Edge edge) {
    return baseMapping.applyFrom(edge);
  }

  @Override
  public Node applyTo(Edge edge) {
    return baseMapping.applyTo(edge);
  }

  @Override
  public PSSIFOption<Edge> applyIncoming(Node node) {
    return filter(baseMapping.applyIncoming(node));
  }

  @Override
  public PSSIFOption<Edge> applyOutgoing(Node node) {
    return filter(baseMapping.applyOutgoing(node));
  }

  @Override
  public Edge create(Model model, Node from, Node to) {
    return baseMapping.create(model, from, to);
  }

  protected final ConnectionMapping getBaseMapping() {
    return baseMapping;
  }

  protected PSSIFOption<Edge> filter(PSSIFOption<Edge> edges) {
    return PSSIFOption.many(Sets.newHashSet(Collections2.filter(edges.getMany(), new Predicate<Edge>() {
      @Override
      public boolean apply(Edge input) {
        boolean result = getFrom().apply(input.getModel(), true).getMany().contains(applyFrom(input))
            && getTo().apply(input.getModel(), true).getMany().contains(applyTo(input));
        return result;
      }
    })));
  }
}
