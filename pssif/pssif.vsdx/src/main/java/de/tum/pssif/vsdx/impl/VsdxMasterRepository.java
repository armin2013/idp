package de.tum.pssif.vsdx.impl;

import java.util.Set;

import com.google.common.collect.Sets;


class VsdxMasterRepository {

  private final Set<VsdxMasterImpl> masters;

  private VsdxDocumentImpl          document;

  public VsdxMasterRepository(Set<VsdxMasterImpl> masters) {
    this.masters = masters;
  }

  void setDocument(VsdxDocumentImpl document) {
    this.document = document;
  }

  Set<VsdxMasterImpl> getMasters() {
    return Sets.newHashSet(masters);
  }

  VsdxMasterImpl getMaster(String name) {
    for (VsdxMasterImpl master : masters) {
      if (master.getName().equalsIgnoreCase(name)) {
        return master;
      }
    }
    return null;
  }

  VsdxMasterImpl getMaster(int id) {
    for (VsdxMasterImpl master : masters) {
      if (master.getId() == id) {
        return master;
      }
    }
    return null;
  }

  boolean hasMaster(String name) {
    return getMaster(name) != null;
  }

  boolean hasMaster(int id) {
    return getMaster(id) != null;
  }
}
