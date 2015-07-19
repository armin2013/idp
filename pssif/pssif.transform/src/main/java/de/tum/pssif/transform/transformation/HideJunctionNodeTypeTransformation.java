package de.tum.pssif.transform.transformation;

import de.tum.pssif.core.common.PSSIFOption;
import de.tum.pssif.core.metamodel.ConnectionMapping;
import de.tum.pssif.core.metamodel.JunctionNodeType;
import de.tum.pssif.core.metamodel.mutable.MutableEdgeType;


public class HideJunctionNodeTypeTransformation extends HideTypeTransformation<JunctionNodeType> {
  public HideJunctionNodeTypeTransformation(JunctionNodeType type) {
    super(type);
  }

  @Override
  public void apply(Viewpoint view) {
    if (getType() != null) {
      removeType(view, getType());
    }
  }

  private void removeType(Viewpoint view, String type) {
    PSSIFOption<JunctionNodeType> actualType = view.getJunctionNodeType(type);

    for (MutableEdgeType et : view.getMutableEdgeTypes()) {
      for (ConnectionMapping cm : et.getMappings().getMany()) {
        for (JunctionNodeType mnt : actualType.getMany()) {
          if (cm.getFrom().equals(mnt) || cm.getTo().equals(mnt)) {
            et.removeMapping(cm);
          }
        }
      }
    }

    for (JunctionNodeType mnt : actualType.getMany()) {
      view.removeJunctionNodeType(mnt);
    }
  }
}
