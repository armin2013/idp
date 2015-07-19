package de.tum.pssif.transform.transformation;

import de.tum.pssif.core.common.PSSIFOption;
import de.tum.pssif.core.metamodel.ConnectionMapping;
import de.tum.pssif.core.metamodel.NodeType;
import de.tum.pssif.core.metamodel.mutable.MutableEdgeType;


public class HideNodeTypeTransformation extends HideTypeTransformation<NodeType> {
  public HideNodeTypeTransformation(NodeType type) {
    super(type);
  }

  @Override
  public void apply(Viewpoint view) {
    if (getType() != null) {
      removeType(view, getType());
    }
  }

  private void removeType(Viewpoint view, String type) {
    PSSIFOption<NodeType> actualType = view.getNodeType(type);

    for (NodeType mnt : actualType.getMany()) {
      for (NodeType special : mnt.getSpecials()) {
        removeType(view, special.getName());
      }
    }

    for (MutableEdgeType et : view.getMutableEdgeTypes()) {
      for (ConnectionMapping cm : et.getMappings().getMany()) {
        for (NodeType mnt : actualType.getMany()) {
          if (cm.getFrom().equals(mnt) || cm.getTo().equals(mnt)) {
            et.removeMapping(cm);
          }
        }
      }
    }

    for (NodeType mnt : actualType.getMany()) {
      view.removeNodeType(mnt);
    }
  }
}
