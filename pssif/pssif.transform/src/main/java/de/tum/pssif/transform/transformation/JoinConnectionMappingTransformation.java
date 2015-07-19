package de.tum.pssif.transform.transformation;

import de.tum.pssif.core.metamodel.ConnectionMapping;
import de.tum.pssif.core.metamodel.EdgeType;
import de.tum.pssif.core.metamodel.NodeTypeBase;
import de.tum.pssif.core.metamodel.mutable.MutableEdgeType;
import de.tum.pssif.transform.transformation.joined.JoinPath;
import de.tum.pssif.transform.transformation.joined.JoinedConnectionMapping;


public class JoinConnectionMappingTransformation extends AbstractTransformation {
  private ConnectionMapping baseMapping;
  private EdgeType          type;
  private NodeTypeBase      from;
  private NodeTypeBase      to;
  private JoinPath          leftPath;
  private JoinPath          rightPath;
  private NodeTypeBase      targetFrom;
  private NodeTypeBase      targetTo;

  public JoinConnectionMappingTransformation(ConnectionMapping baseMapping, EdgeType type, NodeTypeBase from, NodeTypeBase to, JoinPath leftPath,
      JoinPath rightPath, NodeTypeBase targetFrom, NodeTypeBase targetTo) {
    this.baseMapping = baseMapping;
    this.type = type;
    this.from = from;
    this.to = to;
    this.leftPath = leftPath;
    this.rightPath = rightPath;
    this.targetFrom = targetFrom;
    this.targetTo = targetTo;
  }

  @Override
  public void apply(Viewpoint view) {
    ConnectionMapping actualBaseMapping = view.getEdgeType(baseMapping.getType().getName()).getOne()
        .getMapping(view.getBaseNodeType(from.getName()).getOne(), view.getBaseNodeType(to.getName()).getOne()).getOne();
    MutableEdgeType actualType = view.getMutableEdgeType(type.getName()).getOne();
    NodeTypeBase actualFrom = view.getBaseNodeType(from.getName()).getOne();
    NodeTypeBase actualTo = view.getBaseNodeType(to.getName()).getOne();
    NodeTypeBase actualTargetFrom = view.getBaseNodeType(targetFrom.getName()).getOne();
    NodeTypeBase actualTargetTo = view.getBaseNodeType(targetTo.getName()).getOne();
    actualType.addMapping(new JoinedConnectionMapping(actualBaseMapping, actualType, actualFrom, actualTo, leftPath.forMetamodel(view), rightPath
        .forMetamodel(view), actualTargetFrom, actualTargetTo));
  }
}
