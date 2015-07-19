package de.tum.pssif.core.model;

import de.tum.pssif.core.metamodel.impl.ReadFromNodesOperation;
import de.tum.pssif.core.metamodel.impl.ReadToNodesOperation;


public interface Edge extends Element {
  Node apply(ReadFromNodesOperation op);

  Node apply(ReadToNodesOperation op);
}
