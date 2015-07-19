package de.tum.pssif.transform.transformation.artificial;

import java.util.Collection;

import com.google.common.collect.Sets;

import de.tum.pssif.core.common.PSSIFOption;
import de.tum.pssif.core.metamodel.ConnectionMapping;
import de.tum.pssif.core.metamodel.EdgeType;
import de.tum.pssif.core.model.Edge;
import de.tum.pssif.core.model.Model;
import de.tum.pssif.core.model.Node;
import de.tum.pssif.transform.transformation.viewed.ViewedConnectionMapping;


public class ToArtificializedNodeTypeConnectionMapping extends ViewedConnectionMapping {
  public ToArtificializedNodeTypeConnectionMapping(ConnectionMapping baseMapping, EdgeType type, ArtificializingNodeType from,
      ArtificializedNodeType to) {
    super(baseMapping, type, from, to);
  }

  @Override
  public Edge create(Model model, Node from, Node to) {
    return getBaseMapping().create(model, from, to);
  }

  @Override
  public PSSIFOption<Edge> apply(Model model) {
    Collection<Edge> result = Sets.newHashSet();
    Collection<Node> sources = getBaseMapping().getFrom().apply(model, true).getMany();
    Collection<Node> targets = getBaseMapping().getTo().apply(model, true).getMany();

    for (Edge e : getBaseMapping().apply(model).getMany()) {
      Node from = getBaseMapping().applyFrom(e);
      Node to = getBaseMapping().applyTo(e);

      if (!(sources.contains(from) && targets.contains(to))) {
        result.add(e);
      }
    }

    return filter(PSSIFOption.many(result));
  }

  @Override
  public PSSIFOption<Edge> applyIncoming(Node node) {
    Collection<Edge> result = Sets.newHashSet();
    Collection<Node> sources = getBaseMapping().getFrom().apply(node.getModel(), true).getMany();

    for (Edge e : getBaseMapping().applyIncoming(node).getMany()) {
      if (!sources.contains(getBaseMapping().applyFrom(e))) {
        result.add(e);
      }
    }

    return filter(PSSIFOption.many(result));
  }

  @Override
  public PSSIFOption<Edge> applyOutgoing(Node node) {
    Collection<Edge> result = Sets.newHashSet();
    Collection<Node> targets = getBaseMapping().getTo().apply(node.getModel(), true).getMany();

    for (Edge e : getBaseMapping().applyOutgoing(node).getMany()) {
      if (!targets.contains(getBaseMapping().applyTo(e))) {
        result.add(e);
      }
    }

    return filter(PSSIFOption.many(result));
  }
}
