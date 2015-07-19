package de.tum.pssif.transform.transformation;

import de.tum.pssif.core.metamodel.traits.Named;


public abstract class RenameTransformation<T extends Named> extends AbstractTransformation {
  private final String target;
  private final String name;

  public RenameTransformation(T target, String name) {
    this.target = target.getName();
    this.name = name;
  }

  protected final String getTarget() {
    return target;
  }

  protected final String getName() {
    return name;
  }
}
