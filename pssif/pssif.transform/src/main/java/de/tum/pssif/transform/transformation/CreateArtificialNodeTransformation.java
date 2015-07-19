package de.tum.pssif.transform.transformation;

import de.tum.pssif.core.metamodel.ConnectionMapping;
import de.tum.pssif.core.metamodel.EdgeType;
import de.tum.pssif.core.metamodel.NodeType;
import de.tum.pssif.core.metamodel.mutable.MutableEdgeType;
import de.tum.pssif.transform.transformation.artificial.ArtificializedNodeType;
import de.tum.pssif.transform.transformation.artificial.ArtificializingNodeType;
import de.tum.pssif.transform.transformation.artificial.ToArtificializedNodeTypeConnectionMapping;


public class CreateArtificialNodeTransformation extends AbstractTransformation {
  private String sourceType;
  private String edgeType;
  private String targetType;

  public CreateArtificialNodeTransformation(NodeType sourceType, EdgeType edgeType, NodeType targetType) {
    this.sourceType = sourceType.getName();
    this.edgeType = edgeType.getName();
    this.targetType = targetType.getName();
  }

  @Override
  public void apply(Viewpoint view) {
    NodeType actualSourceType = view.getNodeType(sourceType).getOne();
    NodeType actualTargetType = view.getNodeType(targetType).getOne();
    MutableEdgeType actualEdgeType = view.getMutableEdgeType(edgeType).getOne();

    ArtificializingNodeType artificializing = new ArtificializingNodeType(actualSourceType, actualEdgeType, actualTargetType);
    for (NodeType general : actualSourceType.getGeneral().getMany()) {
      artificializing.inherit(general);
    }
    for (NodeType special : actualSourceType.getSpecials()) {
      special.inherit(artificializing);
    }

    ArtificializedNodeType artificialized = new ArtificializedNodeType(actualSourceType, actualEdgeType, actualTargetType);
    for (NodeType general : actualTargetType.getGeneral().getMany()) {
      artificialized.inherit(general);
    }
    for (NodeType special : actualTargetType.getSpecials()) {
      special.inherit(artificialized);
    }

    ConnectionMapping mapping = actualEdgeType.getMapping(actualSourceType, actualTargetType).getOne();
    if (mapping.getFrom().equals(actualSourceType) && mapping.getTo().equals(actualTargetType)) {
      //replace the mapping
      actualEdgeType.removeMapping(mapping);
    }
    actualEdgeType.addMapping(new ToArtificializedNodeTypeConnectionMapping(mapping, actualEdgeType, artificializing, artificialized));

    view.removeNodeType(actualTargetType);
    view.add(artificialized);
    view.removeNodeType(actualSourceType);
    view.add(artificializing);
  }
}
