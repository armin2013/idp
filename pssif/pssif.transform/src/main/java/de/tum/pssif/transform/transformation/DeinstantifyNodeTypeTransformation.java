package de.tum.pssif.transform.transformation;

import de.tum.pssif.core.common.PSSIFOption;
import de.tum.pssif.core.metamodel.NodeType;
import de.tum.pssif.transform.transformation.deinstantified.DeinstantifiedNodeType;


public class DeinstantifyNodeTypeTransformation extends AbstractTransformation {
  private final String target;

  public DeinstantifyNodeTypeTransformation(NodeType target) {
    this.target = target.getName();
  }

  @Override
  public void apply(Viewpoint view) {
    PSSIFOption<NodeType> actualTarget = view.getNodeType(target);

    for (NodeType nt : actualTarget.getMany()) {
      view.removeNodeType(nt);
      DeinstantifiedNodeType deinstantified = new DeinstantifiedNodeType(nt);
      for (NodeType general : nt.getGeneral().getMany()) {
        general.unregisterSpecialization(nt);
        deinstantified.inherit(general);
      }
      for (NodeType special : nt.getSpecials()) {
        special.inherit(deinstantified);
      }
      view.add(deinstantified);
    }
  }
}
