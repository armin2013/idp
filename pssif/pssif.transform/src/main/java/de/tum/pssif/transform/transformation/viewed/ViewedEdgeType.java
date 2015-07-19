package de.tum.pssif.transform.transformation.viewed;

import de.tum.pssif.core.common.PSSIFOption;
import de.tum.pssif.core.exception.PSSIFStructuralIntegrityException;
import de.tum.pssif.core.metamodel.AttributeGroup;
import de.tum.pssif.core.metamodel.ConnectionMapping;
import de.tum.pssif.core.metamodel.EdgeType;
import de.tum.pssif.core.metamodel.JunctionNodeType;
import de.tum.pssif.core.metamodel.NodeTypeBase;
import de.tum.pssif.core.metamodel.impl.EdgeTypeImpl;
import de.tum.pssif.core.metamodel.mutable.MutableConnectionMapping;


public class ViewedEdgeType extends EdgeTypeImpl {
  private final EdgeType baseType;

  public ViewedEdgeType(EdgeType baseType) {
    this(baseType, baseType.getName());
  }

  public ViewedEdgeType(EdgeType baseType, String name) {
    super(name);

    this.baseType = baseType;

    for (AttributeGroup g : baseType.getAttributeGroups()) {
      addAttributeGroup(new ViewedInheritingAttributeGroup<EdgeType>(g, g.getName(), this));
    }
  }

  @Override
  public ConnectionMapping createMapping(NodeTypeBase from, NodeTypeBase to, JunctionNodeType... junctions) {
    return createMapping(from, to, PSSIFOption.many(junctions));
  }

  @Override
  public ConnectionMapping createMapping(NodeTypeBase from, NodeTypeBase to, PSSIFOption<JunctionNodeType> junctions) {
    for (JunctionNodeType junction : junctions.getMany()) {
      PSSIFOption<ConnectionMapping> cm = baseType.getMapping(junction, junction);
      if (cm.isOne()) {
        add(new ViewedConnectionMapping(cm.getOne(), this, junction, junction));
      }
      else {
        throw new PSSIFStructuralIntegrityException("unknown mapping");
      }
      for (JunctionNodeType other : junctions.getMany()) {
        if (other != junction) {
          cm = baseType.getMapping(junction, other);
          if (cm.isOne()) {
            add(new ViewedConnectionMapping(cm.getOne(), this, junction, other));
          }
          else {
            throw new PSSIFStructuralIntegrityException("unknown mapping");
          }
        }
      }
      cm = baseType.getMapping(from, junction);
      if (cm.isOne()) {
        add(new ViewedConnectionMapping(cm.getOne(), this, from, junction));
      }
      else {
        throw new PSSIFStructuralIntegrityException("unknown mapping");
      }
      cm = baseType.getMapping(junction, to);
      if (cm.isOne()) {
        add(new ViewedConnectionMapping(cm.getOne(), this, junction, to));
      }
      else {
        throw new PSSIFStructuralIntegrityException("unknown mapping");
      }
    }

    PSSIFOption<ConnectionMapping> baseMapping = baseType.getMapping(from, to);
    if (baseMapping.isOne()) {
      ViewedConnectionMapping viewed = new ViewedConnectionMapping(baseMapping.getOne(), this, from, to);
      addMapping(viewed);
      return viewed;
    }
    throw new PSSIFStructuralIntegrityException("unknown mapping " + getName() + ": " + from.getName() + "-" + to.getName());
  }

  public void add(MutableConnectionMapping mapping) {
    addMapping(mapping);
  }
}
