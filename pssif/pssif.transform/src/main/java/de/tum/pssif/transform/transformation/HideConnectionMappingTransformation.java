package de.tum.pssif.transform.transformation;

import de.tum.pssif.core.common.PSSIFOption;
import de.tum.pssif.core.metamodel.ConnectionMapping;
import de.tum.pssif.core.metamodel.EdgeType;
import de.tum.pssif.core.metamodel.NodeTypeBase;
import de.tum.pssif.core.metamodel.mutable.MutableEdgeType;


public class HideConnectionMappingTransformation extends AbstractTransformation {
  private final String type;
  private final String from;
  private final String to;

  public HideConnectionMappingTransformation(EdgeType type, ConnectionMapping mapping) {
    this.type = type.getName();
    from = mapping.getFrom().getName();
    to = mapping.getTo().getName();
  }

  @Override
  public void apply(Viewpoint view) {
    MutableEdgeType actualType = view.getMutableEdgeType(type).getOne();
    NodeTypeBase fromType = view.getBaseNodeType(from).getOne();
    NodeTypeBase toType = view.getBaseNodeType(to).getOne();
    PSSIFOption<ConnectionMapping> actualMapping = actualType.getMapping(fromType, toType);
    for (ConnectionMapping cm : actualMapping.getMany()) {
      actualType.removeMapping(cm);
    }
  }
}
