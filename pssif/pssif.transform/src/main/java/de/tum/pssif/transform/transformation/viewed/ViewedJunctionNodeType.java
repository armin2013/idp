package de.tum.pssif.transform.transformation.viewed;

import de.tum.pssif.core.common.PSSIFOption;
import de.tum.pssif.core.metamodel.AttributeGroup;
import de.tum.pssif.core.metamodel.JunctionNodeType;
import de.tum.pssif.core.metamodel.impl.JunctionNodeTypeImpl;
import de.tum.pssif.core.model.Model;
import de.tum.pssif.core.model.Node;


public class ViewedJunctionNodeType extends JunctionNodeTypeImpl {
  private final JunctionNodeType baseType;

  public ViewedJunctionNodeType(JunctionNodeType baseType) {
    this(baseType, baseType.getName());
  }

  public ViewedJunctionNodeType(JunctionNodeType baseType, String name) {
    super(name);
    this.baseType = baseType;

    for (AttributeGroup g : baseType.getAttributeGroups()) {
      addAttributeGroup(new ViewedNonInheritingAttributeGroup(g, g.getName(), this));
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
}
