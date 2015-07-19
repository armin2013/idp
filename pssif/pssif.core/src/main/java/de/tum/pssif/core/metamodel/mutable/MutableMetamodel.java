package de.tum.pssif.core.metamodel.mutable;

import java.util.Collection;

import de.tum.pssif.core.common.PSSIFOption;
import de.tum.pssif.core.metamodel.EdgeType;
import de.tum.pssif.core.metamodel.Enumeration;
import de.tum.pssif.core.metamodel.JunctionNodeType;
import de.tum.pssif.core.metamodel.Metamodel;
import de.tum.pssif.core.metamodel.NodeType;


public interface MutableMetamodel extends Metamodel {
  MutableEnumeration createEnumeration(String name);

  Collection<MutableEnumeration> getMutableEnumerations();

  PSSIFOption<MutableEnumeration> getMutableEnumeration(String name);

  void removeEnumeration(Enumeration enumeration);

  MutableNodeType createNodeType(String name);

  MutableJunctionNodeType createJunctionNodeType(String name);

  MutableEdgeType createEdgeType(String name);

  Collection<MutableNodeTypeBase> getMutableBaseNodeTypes();

  Collection<MutableNodeType> getMutableNodeTypes();

  Collection<MutableJunctionNodeType> getMutableJunctionNodeTypes();

  Collection<MutableEdgeType> getMutableEdgeTypes();

  PSSIFOption<MutableNodeTypeBase> getMutableBaseNodeType(String name);

  PSSIFOption<MutableNodeType> getMutableNodeType(String name);

  PSSIFOption<MutableJunctionNodeType> getMutableJunctionNodeType(String name);

  PSSIFOption<MutableEdgeType> getMutableEdgeType(String name);

  void removeNodeType(NodeType nodeType);

  void removeJunctionNodeType(JunctionNodeType junctionNodeType);

  void removeEdgeType(EdgeType edgeType);
}
