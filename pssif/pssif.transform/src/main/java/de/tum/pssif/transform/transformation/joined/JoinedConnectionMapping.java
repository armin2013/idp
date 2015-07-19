package de.tum.pssif.transform.transformation.joined;

import java.util.Collection;

import com.google.common.collect.Sets;

import de.tum.pssif.core.common.PSSIFOption;
import de.tum.pssif.core.exception.PSSIFStructuralIntegrityException;
import de.tum.pssif.core.metamodel.ConnectionMapping;
import de.tum.pssif.core.metamodel.EdgeType;
import de.tum.pssif.core.metamodel.NodeTypeBase;
import de.tum.pssif.core.model.Edge;
import de.tum.pssif.core.model.Model;
import de.tum.pssif.core.model.Node;
import de.tum.pssif.transform.transformation.viewed.ViewedConnectionMapping;


public class JoinedConnectionMapping extends ViewedConnectionMapping {
  private JoinPath          leftPath;
  private JoinPath          rightPath;
  private ConnectionMapping targetMapping;

  public JoinedConnectionMapping(ConnectionMapping baseMapping, EdgeType type, NodeTypeBase from, NodeTypeBase to, JoinPath leftPath,
      JoinPath rightPath, NodeTypeBase targetFrom, NodeTypeBase targetTo) {
    super(baseMapping, type, from, to);
    this.leftPath = leftPath;
    this.rightPath = rightPath;
    PSSIFOption<ConnectionMapping> mapping = type.getMapping(targetFrom, targetTo);
    if (!mapping.isOne()) {
      throw new PSSIFStructuralIntegrityException("could not resolve target mapping for joined mapping");
    }
    targetMapping = mapping.getOne();
  }

  @Override
  public Edge create(Model model, Node from, Node to) {
    return targetMapping.create(model, leftPath.join(from), rightPath.join(to));
  }

  @Override
  public PSSIFOption<Edge> apply(Model model) {
    Collection<Edge> result = Sets.newHashSet();
    for (Edge e : targetMapping.apply(model).getMany()) {
      result.add(new UnjoinedEdge(e, leftPath.unJoin(applyFrom(e)), rightPath.unJoin(applyTo(e))));
    }
    return filter(PSSIFOption.many(result));
  }

  @Override
  public Node applyFrom(Edge edge) {
    return getBaseMapping().applyFrom(edge);
  }

  @Override
  public Node applyTo(Edge edge) {
    return getBaseMapping().applyTo(edge);
  }

  @Override
  public PSSIFOption<Edge> applyOutgoing(Node node) {
    return filter(targetMapping.applyOutgoing(leftPath.join(node)));
  }

  @Override
  public PSSIFOption<Edge> applyIncoming(Node node) {
    return filter(targetMapping.applyIncoming(rightPath.join(node)));
  }
}
