package de.tum.pssif.core.metamodel.impl;

import de.tum.pssif.core.metamodel.Enumeration;
import de.tum.pssif.core.metamodel.EnumerationLiteral;


public class EnumerationLiteralImpl extends NamedImpl implements EnumerationLiteral {
  private final Enumeration owner;

  public EnumerationLiteralImpl(String name, Enumeration owner) {
    super(name);
    this.owner = owner;
  }

  @Override
  public Class<?> getMetaType() {
    return EnumerationLiteral.class;
  }

  @Override
  public Enumeration getOwner() {
    return owner;
  }
}
