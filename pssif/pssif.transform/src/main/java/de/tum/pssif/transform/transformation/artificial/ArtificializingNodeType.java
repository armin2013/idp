package de.tum.pssif.transform.transformation.artificial;

import de.tum.pssif.core.common.PSSIFConstants;
import de.tum.pssif.core.common.PSSIFOption;
import de.tum.pssif.core.common.PSSIFValue;
import de.tum.pssif.core.metamodel.Attribute;
import de.tum.pssif.core.metamodel.ConnectionMapping;
import de.tum.pssif.core.metamodel.EdgeType;
import de.tum.pssif.core.metamodel.NodeType;
import de.tum.pssif.core.model.Edge;
import de.tum.pssif.core.model.Model;
import de.tum.pssif.core.model.Node;
import de.tum.pssif.transform.transformation.viewed.ViewedNodeType;


public class ArtificializingNodeType extends ViewedNodeType {
  private final ConnectionMapping mapping;
  private final NodeType          targetType;
  private final Attribute         targetTypeIdAttribute;
  private final Attribute         edgeTypeIdAttribute;

  public ArtificializingNodeType(NodeType sourceType, EdgeType edgeType, NodeType targetType) {
    super(sourceType);
    this.mapping = edgeType.getMapping(getBaseType(), targetType).getOne();
    this.targetType = targetType;

    targetTypeIdAttribute = targetType.getAttribute(PSSIFConstants.BUILTIN_ATTRIBUTE_ID).getOne();
    edgeTypeIdAttribute = edgeType.getAttribute(PSSIFConstants.BUILTIN_ATTRIBUTE_ID).getOne();
  }

  @Override
  public Node create(Model model) {
    Node result = getBaseType().create(model);

    Node target = targetType.create(model);
    targetTypeIdAttribute.set(target, PSSIFOption.one(PSSIFValue.create(model.generateId())));

    Edge edge = mapping.create(model, result, target);
    edgeTypeIdAttribute.set(edge, PSSIFOption.one(PSSIFValue.create(model.generateId())));

    return result;
  }
}
