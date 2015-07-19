package de.tum.pssif.transform.transformation;

import de.tum.pssif.core.metamodel.JunctionNodeType;
import de.tum.pssif.core.metamodel.mutable.MutableConnectionMapping;
import de.tum.pssif.core.metamodel.mutable.MutableEdgeType;
import de.tum.pssif.core.metamodel.mutable.MutableJunctionNodeType;
import de.tum.pssif.transform.transformation.viewed.ViewedJunctionNodeType;


public class RenameJunctionNodeTypeTransformation extends RenameTransformation<JunctionNodeType> {

  public RenameJunctionNodeTypeTransformation(JunctionNodeType target, String name) {
    super(target, name);
  }

  @Override
  public void apply(Viewpoint view) {
    MutableJunctionNodeType actualTarget = view.getMutableJunctionNodeType(getTarget()).getOne();
    view.removeJunctionNodeType(actualTarget);
    ViewedJunctionNodeType renamed = new ViewedJunctionNodeType(actualTarget, getName());
    view.add(renamed);

    for (MutableEdgeType met : view.getMutableEdgeTypes()) {
      for (MutableConnectionMapping cm : met.getMutableMappings().getMany()) {
        if (cm.getFrom().equals(actualTarget)) {
          cm.setFrom(renamed);
        }
        if (cm.getTo().equals(actualTarget)) {
          cm.setTo(renamed);
        }
      }
    }
  }
}
