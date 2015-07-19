package de.tum.pssif.transform.transformation.delegating;

import de.tum.pssif.core.metamodel.ConnectionMapping;
import de.tum.pssif.core.metamodel.NodeTypeBase;
import de.tum.pssif.core.metamodel.impl.EdgeTypeImpl;
import de.tum.pssif.transform.transformation.viewed.ViewedConnectionMapping;


public class DelegatingEdgeType extends EdgeTypeImpl {
  public DelegatingEdgeType(String name) {
    super(name);
  }

  /*package*/void delegate(ConnectionMapping baseMapping, NodeTypeBase from, NodeTypeBase to) {
    addMapping(new ViewedConnectionMapping(baseMapping, this, from, to));
  }
}
