package de.tum.pssif.transform.transformation.viewed;

import de.tum.pssif.core.common.PSSIFOption;
import de.tum.pssif.core.metamodel.AttributeGroup;
import de.tum.pssif.core.metamodel.NodeType;
import de.tum.pssif.core.metamodel.impl.NodeTypeImpl;
import de.tum.pssif.core.model.Model;
import de.tum.pssif.core.model.Node;


public class ViewedNodeType extends NodeTypeImpl {
  private final NodeType baseType;

  public ViewedNodeType(NodeType baseType) {
    this(baseType, baseType.getName());
  }

  public ViewedNodeType(NodeType baseType, String name) {
    super(name);
    this.baseType = baseType;

    for (AttributeGroup g : baseType.getAttributeGroups()) {
      addAttributeGroup(new ViewedInheritingAttributeGroup<NodeType>(g, g.getName(), this));
    }
  }

  @Override
  public PSSIFOption<Node> apply(Model model, boolean includeSubtypes) {
    return baseType.apply(model, includeSubtypes);
  }

  @Override
  public PSSIFOption<Node> apply(Model model, String id, boolean includeSubtypes) {
    return baseType.apply(model, id, includeSubtypes);
  }

  @Override
  public Node create(Model model) {
    return baseType.create(model);
  }

  protected final NodeType getBaseType() {
    return baseType;
  }
}
