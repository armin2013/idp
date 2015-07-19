package de.tum.pssif.transform.transformation;

import de.tum.pssif.core.common.PSSIFOption;
import de.tum.pssif.core.metamodel.Attribute;
import de.tum.pssif.core.metamodel.ConnectionMapping;
import de.tum.pssif.core.metamodel.EdgeType;
import de.tum.pssif.core.metamodel.NodeTypeBase;
import de.tum.pssif.core.metamodel.mutable.MutableAttributeGroup;
import de.tum.pssif.core.metamodel.mutable.MutableEdgeType;
import de.tum.pssif.core.metamodel.mutable.MutableNodeTypeBase;
import de.tum.pssif.transform.transformation.moved.CopiedAttribute;
import de.tum.pssif.transform.transformation.moved.MovedAttribute;


public class MoveAttributeTransformation extends AbstractTransformation {
  private final String sourceType;
  private final String attributeName;
  private final String targetType;
  private final String targetAttribute;
  private final String edgeType;

  public MoveAttributeTransformation(NodeTypeBase sourceType, String name, NodeTypeBase targetType, Attribute targetAttribute, EdgeType edgeType) {
    this.sourceType = sourceType.getName();
    attributeName = name;
    this.targetType = targetType.getName();
    this.targetAttribute = targetAttribute.getName();
    this.edgeType = edgeType.getName();
  }

  @Override
  public void apply(Viewpoint view) {
    MutableNodeTypeBase actualSourceType = view.getMutableBaseNodeType(sourceType).getOne();
    MutableNodeTypeBase actualTargetType = view.getMutableBaseNodeType(targetType).getOne();
    MutableEdgeType actualEdgeType = view.getMutableEdgeType(edgeType).getOne();
    Attribute actualTargetAttribute = actualTargetType.getAttribute(targetAttribute).getOne();

    MutableAttributeGroup defaultGroup = actualSourceType.getMutableDefaultAttributeGroup();
    ConnectionMapping mapping = actualEdgeType.getMapping(actualSourceType, actualTargetType).getOne();

    PSSIFOption<Attribute> sourceAttribute = actualSourceType.getAttribute(attributeName);
    if (sourceAttribute.isOne()) {
      defaultGroup.removeAttribute(sourceAttribute.getOne());
      defaultGroup.addAttribute(new CopiedAttribute(attributeName, sourceAttribute.getOne(), actualTargetAttribute, mapping));
    }
    else {
      defaultGroup.addAttribute(new MovedAttribute(attributeName, actualTargetAttribute, mapping));
    }
  }
}
