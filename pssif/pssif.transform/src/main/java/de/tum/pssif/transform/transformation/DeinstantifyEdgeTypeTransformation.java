package de.tum.pssif.transform.transformation;

import de.tum.pssif.core.common.PSSIFOption;
import de.tum.pssif.core.metamodel.EdgeType;
import de.tum.pssif.core.metamodel.mutable.MutableEdgeType;
import de.tum.pssif.transform.transformation.deinstantified.DeinstantifiedEdgeType;


public class DeinstantifyEdgeTypeTransformation extends AbstractTransformation {
  private final String target;

  public DeinstantifyEdgeTypeTransformation(EdgeType target) {
    this.target = target.getName();
  }

  @Override
  public void apply(Viewpoint view) {
    PSSIFOption<MutableEdgeType> actualTarget = view.getMutableEdgeType(target);

    for (MutableEdgeType et : actualTarget.getMany()) {
      view.removeEdgeType(et);
      DeinstantifiedEdgeType deinstantified = new DeinstantifiedEdgeType(et);
      for (EdgeType general : et.getGeneral().getMany()) {
        general.unregisterSpecialization(et);
        deinstantified.inherit(general);
      }
      for (EdgeType special : et.getSpecials()) {
        special.inherit(deinstantified);
      }
      view.add(deinstantified);
    }
  }

}
