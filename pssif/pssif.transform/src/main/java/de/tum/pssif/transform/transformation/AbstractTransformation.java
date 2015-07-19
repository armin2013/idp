package de.tum.pssif.transform.transformation;

import de.tum.pssif.core.metamodel.Metamodel;


public abstract class AbstractTransformation implements Transformation {
  @Override
  public Metamodel apply(Metamodel input) {
    return new Viewpoint(input).transform(this);
  }

  public abstract void apply(Viewpoint view);
}
