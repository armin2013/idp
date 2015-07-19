package de.tum.pssif.transform.transformation.viewed;

import de.tum.pssif.core.metamodel.Enumeration;
import de.tum.pssif.core.metamodel.EnumerationLiteral;
import de.tum.pssif.core.metamodel.impl.EnumerationImpl;


public class ViewedEnumeration extends EnumerationImpl {
  public ViewedEnumeration(Enumeration enumeration) {
    super(enumeration.getName());
    for (EnumerationLiteral literal : enumeration.getLiterals()) {
      addLiteral(literal);
    }
  }
}
