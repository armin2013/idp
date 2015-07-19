package de.tum.pssif.transform.transformation.delegating;

import de.tum.pssif.core.metamodel.ConnectionMapping;
import de.tum.pssif.core.metamodel.NodeTypeBase;


/* package */final class ConnectionMappingDelegateDescriptor {
  private final String type;
  private final String from;
  private final String to;

  /*package*/ConnectionMappingDelegateDescriptor(NodeTypeBase from, NodeTypeBase to, ConnectionMapping baseMapping) {
    this.type = baseMapping.getType().getName();
    this.from = from.getName();
    this.to = to.getName();
  }

  /*package*/String getType() {
    return type;
  }

  /*package*/String getFrom() {
    return from;
  }

  /*package*/String getTo() {
    return to;
  }
}