package de.tum.pssif.core.metamodel.impl;

import de.tum.pssif.core.metamodel.traits.Named;


public abstract class NamedImpl implements Named {
  private final String name;

  public NamedImpl(String name) {
    this.name = name;
  }

  @Override
  public String getName() {
    return name;
  }
}
