package de.tum.pssif.transform.transformation;

import de.tum.pssif.core.metamodel.ElementType;


public abstract class HideTypeTransformation<T extends ElementType> extends AbstractTransformation {
  private String type;

  protected String getType() {
    return type;
  }

  public HideTypeTransformation(T type) {
    this.type = type.getName();
  }
}
