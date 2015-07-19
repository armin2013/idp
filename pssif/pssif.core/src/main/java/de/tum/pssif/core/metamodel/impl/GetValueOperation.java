package de.tum.pssif.core.metamodel.impl;

import de.tum.pssif.core.common.PSSIFOption;
import de.tum.pssif.core.common.PSSIFValue;
import de.tum.pssif.core.metamodel.Attribute;
import de.tum.pssif.core.model.Element;


public class GetValueOperation {
  private final Attribute type;

  /*package*/public GetValueOperation(Attribute type) {
    this.type = type;
  }

  public Attribute getAttribute() {
    return type;
  }

  public PSSIFOption<PSSIFValue> apply(Element element) {
    return element.apply(this);
  }
}
