package de.tum.pssif.transform.transformation;

import java.util.Collection;

import com.google.common.collect.Sets;

import de.tum.pssif.core.metamodel.ConnectionMapping;
import de.tum.pssif.core.metamodel.EdgeType;
import de.tum.pssif.core.metamodel.mutable.MutableEdgeType;
import de.tum.pssif.transform.transformation.viewed.ViewedEdgeType;


public class RenameEdgeTypeTransformation extends RenameTransformation<EdgeType> {

  public RenameEdgeTypeTransformation(EdgeType target, String name) {
    super(target, name);
  }

  @Override
  public void apply(Viewpoint view) {
    MutableEdgeType actualTarget = view.getMutableEdgeType(getTarget()).getOne();
    view.removeEdgeType(actualTarget);
    ViewedEdgeType renamed = new ViewedEdgeType(actualTarget, getName());
    view.add(renamed);

    for (EdgeType general : actualTarget.getGeneral().getMany()) {
      renamed.inherit(general);
    }

    Collection<EdgeType> specials = Sets.newHashSet(actualTarget.getSpecials());
    for (EdgeType special : specials) {
      special.inherit(renamed);
    }

    for (ConnectionMapping mapping : actualTarget.getMappings().getMany()) {
      renamed.createMapping(view.getBaseNodeType(mapping.getFrom().getName()).getOne(), view.getBaseNodeType(mapping.getTo().getName()).getOne());
    }
  }
}
