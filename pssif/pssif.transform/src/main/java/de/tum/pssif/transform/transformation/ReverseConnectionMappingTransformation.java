package de.tum.pssif.transform.transformation;

import de.tum.pssif.core.metamodel.ConnectionMapping;
import de.tum.pssif.core.metamodel.NodeTypeBase;
import de.tum.pssif.core.metamodel.mutable.MutableEdgeType;
import de.tum.pssif.transform.transformation.reversed.ReversedConnectionMapping;


public class ReverseConnectionMappingTransformation extends AbstractTransformation {
  private final String type;
  private final String from;
  private final String to;

  public ReverseConnectionMappingTransformation(ConnectionMapping baseMapping) {
    type = baseMapping.getType().getName();
    from = baseMapping.getFrom().getName();
    to = baseMapping.getTo().getName();
  }

  @Override
  public void apply(Viewpoint view) {
    MutableEdgeType baseType = view.getMutableEdgeType(type).getOne();
    NodeTypeBase from = view.getBaseNodeType(this.from).getOne();
    NodeTypeBase to = view.getBaseNodeType(this.to).getOne();
    baseType.addMapping(new ReversedConnectionMapping(baseType.getMapping(from, to).getOne()));
  }
}
