package de.tum.pssif.core.metamodel.impl;

import java.util.Collection;

import com.google.common.collect.Sets;

import de.tum.pssif.core.common.PSSIFConstants;
import de.tum.pssif.core.common.PSSIFOption;
import de.tum.pssif.core.exception.PSSIFStructuralIntegrityException;
import de.tum.pssif.core.metamodel.AttributeGroup;
import de.tum.pssif.core.metamodel.ConnectionMapping;
import de.tum.pssif.core.metamodel.EdgeType;
import de.tum.pssif.core.metamodel.ElementType;
import de.tum.pssif.core.metamodel.JunctionNodeType;
import de.tum.pssif.core.metamodel.NodeTypeBase;
import de.tum.pssif.core.metamodel.mutable.MutableAttributeGroup;
import de.tum.pssif.core.metamodel.mutable.MutableJunctionNodeType;
import de.tum.pssif.core.model.Edge;
import de.tum.pssif.core.model.Model;
import de.tum.pssif.core.model.Node;


public class JunctionNodeTypeImpl extends NodeTypeBaseImpl implements MutableJunctionNodeType {
  public JunctionNodeTypeImpl(String name) {
    super(name);
    addAttributeGroup(new NonInheritingAttributeGroup(PSSIFConstants.DEFAULT_ATTRIBUTE_GROUP_NAME, this));
  }

  @Override
  public AttributeGroup createAttributeGroup(String name) {
    if (!getAttributeGroup(name).isNone()) {
      throw new PSSIFStructuralIntegrityException("group with name " + name + " already exists in type " + getName());
    }
    MutableAttributeGroup result = new NonInheritingAttributeGroup(name, this);
    addAttributeGroup(result);
    return result;
  }

  @Override
  public Node create(Model model) {
    return new CreateJunctionNodeOperation(this).apply(model);
  }

  @Override
  public PSSIFOption<Node> apply(Model model, boolean includeSubtypes) {
    return new ReadNodesOperation(this).apply(model);
  }

  @Override
  public PSSIFOption<Node> apply(Model model, String id, boolean includeSubtypes) {
    return new ReadNodeOperation(this, id).apply(model);
  }

  @Override
  public Collection<NodeTypeBase> leftClosure(EdgeType edgeType, Node node) {
    Collection<NodeTypeBase> result = Sets.<NodeTypeBase> newHashSet(this);
    for (ConnectionMapping incomingMapping : edgeType.getIncomingMappings(this).getMany()) {
      for (Edge incomingEdge : incomingMapping.applyIncoming(node).getMany()) {
        Node fromConnected = incomingMapping.applyFrom(incomingEdge);
        result.addAll(incomingMapping.getFrom().leftClosure(edgeType, fromConnected));
      }
    }
    return result;
  }

  @Override
  public int junctionIncomingEdgeCount(EdgeType edgeType, Node node) {
    int result = 0;

    for (ConnectionMapping incomingMapping : edgeType.getIncomingMappings(this).getMany()) {
      result += incomingMapping.applyIncoming(node).size();
    }

    return result;
  }

  @Override
  public Collection<NodeTypeBase> rightClosure(EdgeType edgeType, Node node) {
    Collection<NodeTypeBase> result = Sets.<NodeTypeBase> newHashSet(this);
    for (ConnectionMapping outgoingMapping : edgeType.getOutgoingMappings(this).getMany()) {
      for (Edge outgoingEdge : outgoingMapping.applyOutgoing(node).getMany()) {
        Node toConnected = outgoingMapping.applyTo(outgoingEdge);
        result.addAll(outgoingMapping.getTo().rightClosure(edgeType, toConnected));
      }
    }
    return result;
  }

  @Override
  public int junctionOutgoingEdgeCount(EdgeType edgeType, Node node) {
    int result = 0;

    for (ConnectionMapping outgoingMapping : edgeType.getOutgoingMappings(this).getMany()) {
      result += outgoingMapping.applyOutgoing(node).size();
    }

    return result;
  }

  @Override
  public void onIncomingEdgeCreated(Node targetNode, ConnectionMapping mapping, Edge edge) {
    super.onIncomingEdgeCreated(targetNode, mapping, edge);
    if (!targetNode.isEdgeTypeCompatible(mapping.getType())) {
      throw new PSSIFStructuralIntegrityException("edge types incompatible");
    }
    targetNode.initializeEdgeTypeSignature(mapping.getType());
  }

  @Override
  public void onOutgoingEdgeCreated(Node sourceNode, ConnectionMapping mapping, Edge edge) {
    super.onOutgoingEdgeCreated(sourceNode, mapping, edge);
    if (!sourceNode.isEdgeTypeCompatible(mapping.getType())) {
      throw new PSSIFStructuralIntegrityException("edge types incompatible");
    }
    sourceNode.initializeEdgeTypeSignature(mapping.getType());
  }

  @Override
  public boolean isAssignableFrom(ElementType type) {
    return getMetaType().equals(type.getMetaType()) && getName().equals(type.getName());
  }

  @Override
  public Class<?> getMetaType() {
    return JunctionNodeType.class;
  }
}
