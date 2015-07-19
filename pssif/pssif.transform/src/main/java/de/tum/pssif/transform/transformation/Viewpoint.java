package de.tum.pssif.transform.transformation;

import de.tum.pssif.core.common.PSSIFOption;
import de.tum.pssif.core.metamodel.ConnectionMapping;
import de.tum.pssif.core.metamodel.EdgeType;
import de.tum.pssif.core.metamodel.Enumeration;
import de.tum.pssif.core.metamodel.JunctionNodeType;
import de.tum.pssif.core.metamodel.Metamodel;
import de.tum.pssif.core.metamodel.NodeType;
import de.tum.pssif.core.metamodel.impl.MetamodelImpl;
import de.tum.pssif.core.metamodel.mutable.MutableEdgeType;
import de.tum.pssif.core.metamodel.mutable.MutableJunctionNodeType;
import de.tum.pssif.core.metamodel.mutable.MutableNodeType;
import de.tum.pssif.transform.transformation.viewed.ViewedConnectionMapping;
import de.tum.pssif.transform.transformation.viewed.ViewedEdgeType;
import de.tum.pssif.transform.transformation.viewed.ViewedEnumeration;
import de.tum.pssif.transform.transformation.viewed.ViewedJunctionNodeType;
import de.tum.pssif.transform.transformation.viewed.ViewedNodeType;


public class Viewpoint extends MetamodelImpl {
  public Viewpoint(Metamodel baseMetamodel) {
    super(false);

    for (Enumeration enumeration : baseMetamodel.getEnumerations()) {
      addEnumeration(new ViewedEnumeration(enumeration));
    }

    for (NodeType nt : baseMetamodel.getNodeTypes()) {
      addNodeType(new ViewedNodeType(nt));
    }
    for (NodeType nt : baseMetamodel.getNodeTypes()) {
      PSSIFOption<NodeType> general = nt.getGeneral();
      if (general.isOne()) {
        NodeType owned = getNodeType(nt.getName()).getOne();
        NodeType ownedGeneral = getNodeType(general.getOne().getName()).getOne();
        owned.inherit(ownedGeneral);
      }
    }

    for (JunctionNodeType jnt : baseMetamodel.getJunctionNodeTypes()) {
      addJunctionNodeType(new ViewedJunctionNodeType(jnt));
    }

    for (EdgeType et : baseMetamodel.getEdgeTypes()) {
      ViewedEdgeType vet = new ViewedEdgeType(et);
      for (ConnectionMapping cm : et.getMappings().getMany()) {
        vet.add(new ViewedConnectionMapping(cm, vet, getBaseNodeType(cm.getFrom().getName()).getOne(), getBaseNodeType(cm.getTo().getName()).getOne()));
      }
      addEdgeType(vet);
    }

    for (EdgeType et : baseMetamodel.getEdgeTypes()) {
      PSSIFOption<EdgeType> general = et.getGeneral();
      if (general.isOne()) {
        EdgeType owned = getEdgeType(et.getName()).getOne();
        EdgeType ownedGeneral = getEdgeType(general.getOne().getName()).getOne();
        owned.inherit(ownedGeneral);
      }
    }
  }

  public void add(MutableNodeType nt) {
    addNodeType(nt);
  }

  public void add(MutableJunctionNodeType jnt) {
    addJunctionNodeType(jnt);
  }

  public void add(MutableEdgeType et) {
    addEdgeType(et);
  }

  public Metamodel transform(AbstractTransformation transformation) {
    transformation.apply(this);
    return this;
  }
}
