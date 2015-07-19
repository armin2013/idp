package de.tum.pssif.transform.transformation.artificial;

import java.util.Collection;

import com.google.common.collect.Sets;

import de.tum.pssif.core.common.PSSIFOption;
import de.tum.pssif.core.metamodel.ConnectionMapping;
import de.tum.pssif.core.metamodel.EdgeType;
import de.tum.pssif.core.metamodel.NodeTypeBase;
import de.tum.pssif.core.model.Edge;
import de.tum.pssif.core.model.Model;
import de.tum.pssif.core.model.Node;
import de.tum.pssif.transform.transformation.viewed.ViewedConnectionMapping;


public class ArtificializedConnectionMapping extends ViewedConnectionMapping {
  private final ConnectionMapping sourceMapping;

  public ArtificializedConnectionMapping(ConnectionMapping baseMapping, EdgeType type, NodeTypeBase from, NodeTypeBase to, EdgeType sourceType) {
    super(baseMapping, type, from, to);
    sourceMapping = sourceType.getMapping(from, to).getOne();
  }

  @Override
  public PSSIFOption<Edge> apply(Model model) {
    Collection<Edge> result = Sets.newHashSet();
    candidate: for (Edge candidate : getBaseMapping().apply(model).getMany()) {
      Node from = getBaseMapping().applyFrom(candidate);
      Node to = getBaseMapping().applyTo(candidate);
      for (Edge edge : sourceMapping.applyOutgoing(from).getMany()) {
        if (sourceMapping.applyTo(edge).equals(to)) {
          continue candidate;
        }
      }
      result.add(candidate);
    }
    return filter(PSSIFOption.many(result));
  }

  @Override
  public PSSIFOption<Edge> applyIncoming(Node node) {
    Collection<Edge> result = Sets.newHashSet();
    candidate: for (Edge candidate : getBaseMapping().applyIncoming(node).getMany()) {
      Node from = getBaseMapping().applyFrom(candidate);
      for (Edge edge : sourceMapping.applyOutgoing(from).getMany()) {
        if (sourceMapping.applyTo(edge).equals(node)) {
          continue candidate;
        }
      }
      result.add(candidate);
    }
    return filter(PSSIFOption.many(result));
  }

  @Override
  public PSSIFOption<Edge> applyOutgoing(Node node) {
    Collection<Edge> result = Sets.newHashSet();
    candidate: for (Edge candidate : getBaseMapping().applyOutgoing(node).getMany()) {
      Node to = getBaseMapping().applyTo(candidate);
      for (Edge edge : sourceMapping.applyIncoming(to).getMany()) {
        if (sourceMapping.applyFrom(edge).equals(node)) {
          continue candidate;
        }
      }
      result.add(candidate);
    }
    return filter(PSSIFOption.many(result));
  }
}
