package de.tum.pssif.vsdx.impl;

import de.tum.pssif.vsdx.VsdxMaster;


public class VsdxMasterImpl implements VsdxMaster {

  private final int    id;
  private final String name;

  VsdxMasterImpl(String name, int id) {
    this.id = id;
    this.name = name;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public int getId() {
    return id;
  }

  public String toString() {
    return "VsdxMaster(" + name + ", " + id + ")";
  }

}
