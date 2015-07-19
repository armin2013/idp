package de.tum.pssif.core.metamodel;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Collection;
import java.util.Date;

import com.google.common.collect.Sets;

import de.tum.pssif.core.common.PSSIFValue;


/**
 * A primitive data type in the PSS-IF Metamodel.
 */
public interface PrimitiveDataType extends DataType {

  PrimitiveDataType             STRING  = new PrimitiveTypeImpl(String.class.getSimpleName(), String.class);
  PrimitiveDataType             BOOLEAN = new PrimitiveTypeImpl(Boolean.class.getSimpleName(), Boolean.class);
  PrimitiveDataType             INTEGER = new PrimitiveTypeImpl(BigInteger.class.getSimpleName(), BigInteger.class);
  PrimitiveDataType             DECIMAL = new PrimitiveTypeImpl(BigDecimal.class.getSimpleName(), BigDecimal.class);
  PrimitiveDataType             DATE    = new PrimitiveTypeImpl(Date.class.getSimpleName(), Date.class);

  Collection<PrimitiveDataType> TYPES   = Sets.newHashSet(STRING, BOOLEAN, INTEGER, DECIMAL, DATE);

  /**
   * @return
   *    The class which is represented by this data type.
   */
  Class<?> getType();

  static class PrimitiveTypeImpl implements PrimitiveDataType {

    private final String   name;
    private final Class<?> clazz;

    private PrimitiveTypeImpl(String name, Class<?> clazz) {
      this.name = name;
      this.clazz = clazz;
    }

    @Override
    public String getName() {
      return name;
    }

    @Override
    public Class<?> getType() {
      return clazz;
    }

    @Override
    public Class<?> getMetaType() {
      return PrimitiveDataType.class;
    }

    @Override
    public PSSIFValue fromObject(Object object) {
      if (this.equals(STRING)) {
        if (object instanceof String) {
          return PSSIFValue.create(object);
        }
        else {
          return PSSIFValue.create(object.toString());
        }
      }
      else if (this.equals(BOOLEAN)) {
        if (object instanceof Boolean) {
          return PSSIFValue.create(object);
        }
        else if (object instanceof String) {
          return PSSIFValue.create(Boolean.parseBoolean((String) object));
        }
        else {
          throw new IllegalArgumentException();
        }
      }
      else if (this.equals(INTEGER)) {
        if (object instanceof Integer) {
          return PSSIFValue.create(BigInteger.valueOf(((Integer) object).longValue()));
        }
        else if (object instanceof Long) {
          return PSSIFValue.create(BigInteger.valueOf(((Long) object).longValue()));
        }
        else if (object instanceof BigInteger) {
          return PSSIFValue.create(object);
        }
        else if (object instanceof String) {
          try {
            return PSSIFValue.create(BigDecimal.valueOf(NumberFormat.getInstance().parse((String) object).longValue()));
          } catch (ParseException e) {
            throw new IllegalArgumentException(e);
          }
        }
        else {
          throw new IllegalArgumentException();
        }
      }
      else if (this.equals(DECIMAL)) {
        if (object instanceof Integer) {
          return PSSIFValue.create(BigDecimal.valueOf(((Integer) object).longValue()));
        }
        else if (object instanceof Long) {
          return PSSIFValue.create(BigDecimal.valueOf(((Long) object).longValue()));
        }
        else if (object instanceof BigInteger) {
          return PSSIFValue.create(BigDecimal.valueOf(((BigInteger) object).longValue()));
        }
        else if (object instanceof Double) {
          return PSSIFValue.create(BigDecimal.valueOf(((Double) object).longValue()));
        }
        else if (object instanceof Float) {
          return PSSIFValue.create(BigDecimal.valueOf(((Float) object).longValue()));
        }
        else if (object instanceof BigDecimal) {
          return PSSIFValue.create(object);
        }
        else if (object instanceof String) {
          try {
            return PSSIFValue.create(BigDecimal.valueOf(NumberFormat.getInstance().parse((String) object).doubleValue()));
          } catch (ParseException e) {
            throw new IllegalArgumentException(e);
          }
        }
        else {
          throw new IllegalArgumentException();
        }
      }
      else if (this.equals(DATE)) {
        if (object instanceof Date) {
          return PSSIFValue.create(object);
        }
        else if (object instanceof String) {
          try {
            return PSSIFValue.create(DateFormat.getInstance().parseObject((String) object));
          } catch (ParseException e) {
            throw new IllegalArgumentException(e);
          }
        }
        else {
          throw new IllegalArgumentException();
        }
      }
      else {
        throw new IllegalArgumentException();
      }
    }

    @Override
    public String toString(PSSIFValue val) {
      if (this.equals(STRING) && val.isString()) {
        return val.asString();
      }
      else if (this.equals(BOOLEAN) && val.isBoolean()) {
        return val.asBoolean().toString();
      }
      else if (this.equals(INTEGER) && val.isInteger()) {
        return NumberFormat.getInstance().format(val.asInteger());
      }
      else if (this.equals(DECIMAL) && val.isDecimal()) {
        return NumberFormat.getInstance().format(val.asDecimal());
      }
      else if (this.equals(DATE) && val.isDate()) {
        return DateFormat.getInstance().format(val.asDate());
      }
      else {
        throw new IllegalArgumentException();
      }
    }
  }

}
