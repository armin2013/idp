package de.tum.pssif.transform.transformation.reversed;

import de.tum.pssif.core.metamodel.ConnectionMapping;
import de.tum.pssif.core.model.Edge;
import de.tum.pssif.core.model.Model;
import de.tum.pssif.core.model.Node;
import de.tum.pssif.transform.transformation.viewed.ViewedConnectionMapping;


public class ReversedConnectionMapping extends ViewedConnectionMapping {
  public ReversedConnectionMapping(ConnectionMapping baseMapping) {
    super(baseMapping, baseMapping.getType(), baseMapping.getTo(), baseMapping.getFrom());
  }

  @Override
  public Edge create(Model model, Node from, Node to) {
    return super.create(model, to, from);
  }
}
