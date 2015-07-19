package de.tum.pssif.core.metamodel.impl;

import java.util.Collection;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import de.tum.pssif.core.common.PSSIFConstants;
import de.tum.pssif.core.common.PSSIFOption;
import de.tum.pssif.core.exception.PSSIFStructuralIntegrityException;
import de.tum.pssif.core.metamodel.AttributeGroup;
import de.tum.pssif.core.metamodel.EdgeType;
import de.tum.pssif.core.metamodel.ElementType;
import de.tum.pssif.core.metamodel.NodeType;
import de.tum.pssif.core.metamodel.NodeTypeBase;
import de.tum.pssif.core.metamodel.mutable.MutableAttributeGroup;
import de.tum.pssif.core.metamodel.mutable.MutableNodeType;
import de.tum.pssif.core.model.Model;
import de.tum.pssif.core.model.Node;


public class NodeTypeImpl extends NodeTypeBaseImpl implements MutableNodeType {
  private NodeType            general         = null;
  private final Set<NodeType> specializations = Sets.newHashSet();

  public NodeTypeImpl(String name) {
    super(name);
    addAttributeGroup(new InheritingAttributeGroup<NodeType>(PSSIFConstants.DEFAULT_ATTRIBUTE_GROUP_NAME, this));
  }

  @Override
  public AttributeGroup createAttributeGroup(String name) {
    if (!getAttributeGroup(name).isNone()) {
      throw new PSSIFStructuralIntegrityException("group with name " + name + " already exists in type " + getName());
    }
    MutableAttributeGroup result = new InheritingAttributeGroup<NodeType>(name, this);
    addAttributeGroup(result);
    return result;
  }

  @Override
  public PSSIFOption<Node> apply(Model model, boolean includeSubtypes) {
    PSSIFOption<Node> result = PSSIFOption.none();
    if (includeSubtypes) {
      for (NodeType special : getSpecials()) {
        result = PSSIFOption.merge(result, special.apply(model, includeSubtypes));
      }
    }
    return PSSIFOption.merge(result, new ReadNodesOperation(this).apply(model));
  }

  @Override
  public PSSIFOption<Node> apply(Model model, String id, boolean includeSubtypes) {
    PSSIFOption<Node> result = PSSIFOption.none();
    if (includeSubtypes) {
      for (NodeType special : getSpecials()) {
        result = PSSIFOption.merge(result, special.apply(model, id, includeSubtypes));
      }
    }
    return PSSIFOption.merge(result, new ReadNodeOperation(this, id).apply(model));
  }

  @Override
  public boolean isAssignableFrom(ElementType type) {
    if (this.equals(type)) {
      return true;
    }
    else {
      for (NodeType special : getSpecials()) {
        if (special.isAssignableFrom(type)) {
          return true;
        }
      }
    }

    return false;
  }

  @Override
  public void inherit(NodeType general) {
    if (general.isAssignableFrom(this)) {
      throw new PSSIFStructuralIntegrityException("inheritance cycle detected");
    }
    if (this.general != null) {
      this.general.unregisterSpecialization(this);
    }
    this.general = general;
    this.general.registerSpecialization(this);
  }

  @Override
  public PSSIFOption<NodeType> getGeneral() {
    return PSSIFOption.one(general);
  }

  @Override
  public Collection<NodeType> getSpecials() {
    return ImmutableSet.copyOf(specializations);
  }

  @Override
  public void registerSpecialization(NodeType special) {
    specializations.add(special);
  }

  @Override
  public void unregisterSpecialization(NodeType special) {
    specializations.remove(special);
  }

  @Override
  public Class<?> getMetaType() {
    return NodeType.class;
  }

  @Override
  public Collection<NodeTypeBase> leftClosure(EdgeType edgeType, Node node) {
    return ImmutableSet.<NodeTypeBase> of(this);
  }

  @Override
  public Collection<NodeTypeBase> rightClosure(EdgeType edgeType, Node node) {
    return ImmutableSet.<NodeTypeBase> of(this);
  }

  @Override
  public int junctionIncomingEdgeCount(EdgeType edgeType, Node node) {
    return 0;
  }

  @Override
  public int junctionOutgoingEdgeCount(EdgeType edgeType, Node node) {
    return 0;
  }
}
