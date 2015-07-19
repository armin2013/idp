package de.tum.pssif.transform.transformation;

import java.util.Collection;

import com.google.common.collect.Sets;

import de.tum.pssif.core.metamodel.NodeType;
import de.tum.pssif.core.metamodel.mutable.MutableConnectionMapping;
import de.tum.pssif.core.metamodel.mutable.MutableEdgeType;
import de.tum.pssif.core.metamodel.mutable.MutableNodeType;
import de.tum.pssif.transform.transformation.viewed.ViewedNodeType;


public class RenameNodeTypeTransformation extends RenameTransformation<NodeType> {

  public RenameNodeTypeTransformation(NodeType target, String name) {
    super(target, name);
  }

  @Override
  public void apply(Viewpoint view) {
    MutableNodeType actualTarget = view.getMutableNodeType(getTarget()).getOne();
    view.removeNodeType(actualTarget);
    ViewedNodeType renamed = new ViewedNodeType(actualTarget, getName());
    view.add(renamed);

    for (NodeType general : actualTarget.getGeneral().getMany()) {
      renamed.inherit(general);
    }

    Collection<NodeType> specials = Sets.newHashSet(actualTarget.getSpecials());
    for (NodeType special : specials) {
      special.inherit(renamed);
    }

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
