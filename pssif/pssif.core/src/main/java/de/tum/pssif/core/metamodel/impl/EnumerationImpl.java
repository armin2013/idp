package de.tum.pssif.core.metamodel.impl;

import java.util.Collection;
import java.util.Map;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

import de.tum.pssif.core.common.PSSIFOption;
import de.tum.pssif.core.common.PSSIFUtil;
import de.tum.pssif.core.common.PSSIFValue;
import de.tum.pssif.core.exception.PSSIFStructuralIntegrityException;
import de.tum.pssif.core.metamodel.Enumeration;
import de.tum.pssif.core.metamodel.EnumerationLiteral;
import de.tum.pssif.core.metamodel.mutable.MutableEnumeration;


public class EnumerationImpl extends NamedImpl implements MutableEnumeration {
  private Map<String, EnumerationLiteral> literals = Maps.newHashMap();

  public EnumerationImpl(String name) {
    super(name);
  }

  @Override
  public PSSIFValue fromObject(Object object) {
    if (object instanceof PSSIFValue) {
      if (((PSSIFValue) object).isEnumeration()) {
        return PSSIFValue.create(((PSSIFValue) object).getValue());
      }
    }
    else if (object instanceof EnumerationLiteral) {
      if (getLiteral(((EnumerationLiteral) object).getName()).isOne()) {
        return PSSIFValue.create(object);
      }
      else {
        throw new IllegalArgumentException();
      }
    }
    else if (object == null) {
      throw new IllegalArgumentException();
    }
    else if (getLiteral(object.toString()).isOne()) {
      return PSSIFValue.create(getLiteral(object.toString()).getOne());
    }
    throw new IllegalArgumentException();
  }

  @Override
  public String toString(PSSIFValue val) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Class<?> getMetaType() {
    return Enumeration.class;
  }

  @Override
  public EnumerationLiteral createLiteral(String name) {
    PSSIFUtil.checkNameValidity(name);
    if (!getLiteral(name).isNone()) {
      throw new PSSIFStructuralIntegrityException("a literal with name " + name + " already exists");
    }
    EnumerationLiteral result = new EnumerationLiteralImpl(name, this);
    addLiteral(result);
    return result;
  }

  protected final void addLiteral(EnumerationLiteral result) {
    literals.put(PSSIFUtil.normalize(result.getName()), result);
  }

  @Override
  public void removeLiteral(EnumerationLiteral literal) {
    literals.remove(literal.getName());
  }

  @Override
  public Collection<EnumerationLiteral> getLiterals() {
    return ImmutableSet.copyOf(literals.values());
  }

  @Override
  public PSSIFOption<EnumerationLiteral> getLiteral(String name) {
    return PSSIFOption.one(literals.get(PSSIFUtil.normalize(name)));
  }
}
