package de.tum.pssif.core.model.impl;

import de.tum.pssif.core.metamodel.ConnectionMapping;


/* package */final class ConnectionMappingSignature {
  private final String edgeTypeName;
  private final String fromTypeName;
  private final String toTypeName;

  /*package*/ConnectionMappingSignature(ConnectionMapping mapping) {
    edgeTypeName = mapping.getType().getName();
    fromTypeName = mapping.getFrom().getName();
    toTypeName = mapping.getTo().getName();
  }

  /*package*/boolean isCompatibleWith(ConnectionMapping mapping) {
    return edgeTypeName.equals(mapping.getType().getName()) && fromTypeName.equals(mapping.getFrom().getName())
        && toTypeName.equals(mapping.getTo().getName());
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((edgeTypeName == null) ? 0 : edgeTypeName.hashCode());
    result = prime * result + ((fromTypeName == null) ? 0 : fromTypeName.hashCode());
    result = prime * result + ((toTypeName == null) ? 0 : toTypeName.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    ConnectionMappingSignature other = (ConnectionMappingSignature) obj;
    if (edgeTypeName == null) {
      if (other.edgeTypeName != null) {
        return false;
      }
    }
    else if (!edgeTypeName.equals(other.edgeTypeName)) {
      return false;
    }
    if (fromTypeName == null) {
      if (other.fromTypeName != null) {
        return false;
      }
    }
    else if (!fromTypeName.equals(other.fromTypeName)) {
      return false;
    }
    if (toTypeName == null) {
      if (other.toTypeName != null) {
        return false;
      }
    }
    else if (!toTypeName.equals(other.toTypeName)) {
      return false;
    }
    return true;
  }
}