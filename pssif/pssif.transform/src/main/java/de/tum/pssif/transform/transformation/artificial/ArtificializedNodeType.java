package de.tum.pssif.transform.transformation.artificial;

import java.util.Collection;

import com.google.common.collect.Sets;

import de.tum.pssif.core.common.PSSIFOption;
import de.tum.pssif.core.metamodel.ConnectionMapping;
import de.tum.pssif.core.metamodel.EdgeType;
import de.tum.pssif.core.metamodel.NodeType;
import de.tum.pssif.core.model.Edge;
import de.tum.pssif.core.model.Model;
import de.tum.pssif.core.model.Node;
import de.tum.pssif.transform.transformation.viewed.ViewedNodeType;


public class ArtificializedNodeType extends ViewedNodeType {
  private final ConnectionMapping mapping;
  private final NodeType          sourceType;

  public ArtificializedNodeType(NodeType sourceType, EdgeType edgeType, NodeType targetType) {
    super(targetType);
    mapping = edgeType.getMapping(sourceType, targetType).getOne();
    this.sourceType = sourceType;
  }

  @Override
  public PSSIFOption<Node> apply(Model model, boolean includeSubtypes) {
    return filter(model, getBaseType().apply(model, includeSubtypes));
  }

  @Override
  public PSSIFOption<Node> apply(Model model, String id, boolean includeSubtypes) {
    return filter(model, getBaseType().apply(model, id, includeSubtypes));
  }

  protected PSSIFOption<Node> filter(Model model, PSSIFOption<Node> candidates) {
    Collection<Node> sources = sourceType.apply(model, true).getMany();
    Collection<Node> result = Sets.newHashSet();
    candidate: for (Node candidate : candidates.getMany()) {
      for (Edge e : mapping.applyIncoming(candidate).getMany()) {
        if (sources.contains(mapping.applyFrom(e))) {
          continue candidate;
        }
      }
      result.add(candidate);
    }
    return PSSIFOption.many(result);
  }
}
