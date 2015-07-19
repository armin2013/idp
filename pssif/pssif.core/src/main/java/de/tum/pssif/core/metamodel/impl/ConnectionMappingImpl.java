package de.tum.pssif.core.metamodel.impl;

import java.util.Collection;

import de.tum.pssif.core.common.PSSIFOption;
import de.tum.pssif.core.exception.PSSIFStructuralIntegrityException;
import de.tum.pssif.core.metamodel.EdgeType;
import de.tum.pssif.core.metamodel.NodeTypeBase;
import de.tum.pssif.core.metamodel.mutable.MutableConnectionMapping;
import de.tum.pssif.core.metamodel.mutable.MutableNodeTypeBase;
import de.tum.pssif.core.model.Edge;
import de.tum.pssif.core.model.Model;
import de.tum.pssif.core.model.Node;


// TODO check equals and hashcode (i.e. EdgeType and NodeType)
public class ConnectionMappingImpl implements MutableConnectionMapping {
  private final EdgeType type;
  private NodeTypeBase   from;
  private NodeTypeBase   to;

  public ConnectionMappingImpl(EdgeType type, NodeTypeBase from, NodeTypeBase to) {
    this.type = type;
    this.from = from;
    this.to = to;
  }

  @Override
  public EdgeType getType() {
    return type;
  }

  @Override
  public NodeTypeBase getTo() {
    return to;
  }

  @Override
  public NodeTypeBase getFrom() {
    return from;
  }

  @Override
  public Edge create(Model model, Node from, Node to) {
    if (!getFrom().apply(model, true).getMany().contains(from) || !getTo().apply(model, true).getMany().contains(to)) {
      throw new PSSIFStructuralIntegrityException("could not find one of the nodes to connect");
    }

    //check edgeType compatibility
    from.isEdgeTypeCompatible(getType());
    to.isEdgeTypeCompatible(getType());

    //check multiplicity on junctions
    int fromIncomingCount = getFrom().junctionIncomingEdgeCount(getType(), from);
    int fromOutgoingCount = getFrom().junctionOutgoingEdgeCount(getType(), from);
    int toOutgoingCount = getTo().junctionOutgoingEdgeCount(getType(), to);
    int toIncomingCount = getTo().junctionIncomingEdgeCount(getType(), to);

    if (fromIncomingCount > 1 && fromOutgoingCount >= 1) {
      throw new PSSIFStructuralIntegrityException("creating the requested edge would violate multiplicities across junctions");
    }
    else if (toOutgoingCount > 1 && toIncomingCount >= 1) {
      throw new PSSIFStructuralIntegrityException("creating the requested edge would violate multiplicities across junctions");
    }

    //check connectionmapping consistency across junctions
    Collection<NodeTypeBase> leftClosure = getFrom().leftClosure(type, from);
    Collection<NodeTypeBase> rightClosure = getTo().rightClosure(type, to);

    for (NodeTypeBase leftType : leftClosure) {
      for (NodeTypeBase rightType : rightClosure) {
        if (getType().getMapping(leftType, rightType).isNone()) {
          throw new PSSIFStructuralIntegrityException("creating the requested edge would violate connectionmappings across junctions");
        }
      }
    }

    Edge result = new CreateEdgeOperation(this, from, to).apply(model);

    getFrom().onOutgoingEdgeCreated(from, this, result);
    getTo().onIncomingEdgeCreated(to, this, result);

    return result;
  }

  @Override
  public Node applyFrom(Edge edge) {
    return new ReadFromNodesOperation().apply(edge);
  }

  @Override
  public Node applyTo(Edge edge) {
    return new ReadToNodesOperation().apply(edge);
  }

  @Override
  public PSSIFOption<Edge> applyOutgoing(Node node) {
    return new ReadOutgoingNodesOperation(this).apply(node);
  }

  @Override
  public PSSIFOption<Edge> applyIncoming(Node node) {
    return new ReadIncomingNodesOperation(this).apply(node);
  }

  @Override
  public PSSIFOption<Edge> apply(Model model) {
    return new ReadEdgesOperation(this).apply(model);
  }

  /* (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((from == null) ? 0 : from.hashCode());
    result = prime * result + ((to == null) ? 0 : to.hashCode());
    result = prime * result + ((type == null) ? 0 : type.hashCode());
    return result;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    ConnectionMappingImpl other = (ConnectionMappingImpl) obj;
    if (from == null) {
      if (other.from != null) {
        return false;
      }
    }
    else if (!from.equals(other.from)) {
      return false;
    }
    if (to == null) {
      if (other.to != null) {
        return false;
      }
    }
    else if (!to.equals(other.to)) {
      return false;
    }
    if (type == null) {
      if (other.type != null) {
        return false;
      }
    }
    else if (!type.equals(other.type)) {
      return false;
    }
    return true;
  }

  @Override
  public void setFrom(MutableNodeTypeBase from) {
    this.from = from;
  }

  @Override
  public void setTo(MutableNodeTypeBase to) {
    this.to = to;
  }
}
