package de.tum.pssif.transform.transformation.joined;

import java.util.List;

import com.google.common.collect.Lists;

import de.tum.pssif.core.metamodel.ConnectionMapping;
import de.tum.pssif.core.metamodel.EdgeType;
import de.tum.pssif.core.metamodel.Metamodel;
import de.tum.pssif.core.metamodel.NodeTypeBase;
import de.tum.pssif.core.model.Node;


public class JoinPath {
  private static class JoinPathSegment {
    private ConnectionMapping mapping;
    private Direction         direction;

    private enum Direction {
      OUTGOING, INCOMING, BOTH;
    }

    private JoinPathSegment(ConnectionMapping mapping, Direction direction) {
      this.mapping = mapping;
      this.direction = direction;
    }

    private NodeTypeBase getTargetNodeType() {
      NodeTypeBase result = mapping.getTo();
      if (direction.equals(Direction.INCOMING)) {
        result = mapping.getFrom();
      }
      return result;
    }

    private Node join(Node node) {
      Node result = mapping.applyTo(mapping.applyOutgoing(node).getOne());
      if (direction.equals(Direction.INCOMING)) {
        result = mapping.applyFrom(mapping.applyIncoming(node).getOne());
      }
      return result;
    }

    private Node unJoin(Node node) {
      Node result = mapping.applyFrom(mapping.applyIncoming(node).getOne());
      if (direction.equals(Direction.INCOMING)) {
        result = mapping.applyTo(mapping.applyOutgoing(node).getOne());
      }
      return result;
    }

    private JoinPathSegment forMetamodel(Metamodel metamodel) {
      EdgeType type = metamodel.getEdgeType(mapping.getType().getName()).getOne();
      return new JoinPathSegment(type.getMapping(metamodel.getBaseNodeType(mapping.getFrom().getName()).getOne(),
          metamodel.getBaseNodeType(mapping.getTo().getName()).getOne()).getOne(), direction);
    }
  }

  private List<JoinPathSegment> segments = Lists.newArrayList();

  public void joinOutgoing(ConnectionMapping mapping) {
    segments.add(new JoinPathSegment(mapping, JoinPathSegment.Direction.OUTGOING));
  }

  public void joinIncoming(ConnectionMapping mapping) {
    segments.add(new JoinPathSegment(mapping, JoinPathSegment.Direction.OUTGOING));
  }

  public NodeTypeBase getTargetNodeType() {
    return segments.get(segments.size() - 1).getTargetNodeType();
  }

  public Node join(Node node) {
    Node result = node;

    for (JoinPathSegment segment : segments) {
      result = segment.join(result);
    }

    return result;
  }

  public Node unJoin(Node node) {
    Node result = node;

    for (JoinPathSegment segment : Lists.reverse(segments)) {
      result = segment.unJoin(result);
    }

    return result;
  }

  public JoinPath forMetamodel(Metamodel metamodel) {
    JoinPath result = new JoinPath();

    for (JoinPathSegment segment : segments) {
      result.segments.add(segment.forMetamodel(metamodel));
    }

    return result;
  }
}
