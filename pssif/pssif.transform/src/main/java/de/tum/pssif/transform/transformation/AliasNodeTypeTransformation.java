package de.tum.pssif.transform.transformation;

import de.tum.pssif.core.metamodel.ConnectionMapping;
import de.tum.pssif.core.metamodel.NodeType;
import de.tum.pssif.core.metamodel.NodeTypeBase;
import de.tum.pssif.core.metamodel.mutable.MutableEdgeType;
import de.tum.pssif.core.metamodel.mutable.MutableNodeType;
import de.tum.pssif.transform.transformation.viewed.AliasedNodeType;
import de.tum.pssif.transform.transformation.viewed.ViewedConnectionMapping;
import de.tum.pssif.transform.transformation.viewed.ViewedNodeType;


public class AliasNodeTypeTransformation extends RenameTransformation<NodeType> {
  private final String annotation;

  public AliasNodeTypeTransformation(NodeType target, String name, String annotation) {
    super(target, name);
    this.annotation = annotation;
  }

  @Override
  public void apply(Viewpoint view) {
    MutableNodeType actualTarget = view.getMutableNodeType(getTarget()).getOne();
    ViewedNodeType aliased = new AliasedNodeType(actualTarget, getName(), annotation);

    for (NodeType general : actualTarget.getGeneral().getMany()) {
      aliased.inherit(general);
    }

    view.add(aliased);

    for (MutableEdgeType met : view.getMutableEdgeTypes()) {
      for (ConnectionMapping cm : met.getMappings().getMany()) {
        NodeTypeBase from = cm.getFrom();
        NodeTypeBase to = cm.getTo();
        boolean create = false;
        if (from.equals(actualTarget)) {
          from = aliased;
          create = true;
        }
        if (to.equals(actualTarget)) {
          to = aliased;
          create = true;
        }
        if (create) {
          met.addMapping(new ViewedConnectionMapping(cm, met, from, to));
        }
      }
    }
  }

}
