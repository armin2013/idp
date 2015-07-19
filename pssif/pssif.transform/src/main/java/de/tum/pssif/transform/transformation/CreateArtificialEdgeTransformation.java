package de.tum.pssif.transform.transformation;

import de.tum.pssif.core.metamodel.ConnectionMapping;
import de.tum.pssif.core.metamodel.EdgeType;
import de.tum.pssif.core.metamodel.NodeType;
import de.tum.pssif.core.metamodel.mutable.MutableEdgeType;
import de.tum.pssif.transform.transformation.artificial.ArtificializedConnectionMapping;
import de.tum.pssif.transform.transformation.artificial.ArtificializingConnectionMapping;


public class CreateArtificialEdgeTransformation extends AbstractTransformation {
  private final String  from;
  private final String  to;
  private final String  base;
  private final String  target;
  private final Boolean directed;

  public CreateArtificialEdgeTransformation(NodeType from, NodeType to, EdgeType base, EdgeType target, Boolean directed) {
    this.from = from.getName();
    this.to = to.getName();
    this.base = base.getName();
    this.target = target.getName();
    this.directed = directed;
  }

  @Override
  public void apply(Viewpoint view) {
    NodeType actualFrom = view.getNodeType(from).getOne();
    NodeType actualTo = view.getNodeType(to).getOne();
    MutableEdgeType actualBase = view.getMutableEdgeType(base).getOne();
    MutableEdgeType actualTarget = view.getMutableEdgeType(target).getOne();

    ConnectionMapping baseMapping = actualBase.getMapping(actualFrom, actualTo).getOne();
    ConnectionMapping targetMapping = actualTarget.getMapping(actualFrom, actualTo).getOne();

    if (baseMapping.getFrom().equals(actualFrom) && baseMapping.getTo().equals(actualTo)) {
      //replace the mapping
      actualBase.removeMapping(baseMapping);
    }
    actualBase.addMapping(new ArtificializingConnectionMapping(baseMapping, baseMapping.getType(), actualFrom, actualTo, actualTarget, directed));

    if (targetMapping.getFrom().equals(actualFrom) && targetMapping.getTo().equals(actualTo)) {
      //replace the mapping
      actualTarget.removeMapping(targetMapping);
    }
    ArtificializedConnectionMapping artificializing = new ArtificializedConnectionMapping(targetMapping, targetMapping.getType(), actualFrom,
        actualTo, actualBase);
    actualTarget.addMapping(artificializing);
  }
}
