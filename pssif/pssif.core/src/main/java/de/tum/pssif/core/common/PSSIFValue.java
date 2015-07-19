package de.tum.pssif.core.common;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import de.tum.pssif.core.metamodel.EnumerationLiteral;
import de.tum.pssif.core.metamodel.Unit;
import de.tum.pssif.core.metamodel.Units;


/**
 * The value of an Attribute Type in a PSS-IF Model.
 */
public class PSSIFValue {

  private final Object val;
  private final Unit   unit;

  private PSSIFValue(Object val, Unit unit) {
    this.val = val;
    this.unit = unit;
  }

  /**
   * Creates a new PSS-IF value for the provided object with the provided unit.
   * @param obj
   *    The object of this PSS-IF value.
   * @param unit
   *    The unit of this PSS-IF value.
   * @return
   *    The immutable PSS-IF value.
   */
  public static PSSIFValue create(Object obj, Unit unit) {
    if (obj == null || unit == null) {
      throw new IllegalArgumentException("Can not create a PSSIFValue with null content, or null unit!");
    }
    return new PSSIFValue(obj, unit);
  }

  /**
   * Creates a new PSS-IF value for the provided object.
   * @param obj
   *    The object of this PSS-IF value.
   * @return
   *    The immutable PSS-IF value.
   */
  public static PSSIFValue create(Object obj) {
    return create(obj, Units.NONE);
  }

  /**
   * @return
   *    The object of this PSS-IF value.
   */
  public Object getValue() {
    return val;
  }

  /**
   * @return
   *    The unit of this PSS-IF value. Will be NONE, if the value was created without
   *    unit.
   */
  public Unit getUnit() {
    return this.unit;
  }

  /**
   * @return
   *    Whether the object contained in this PSS-IF value is an instance of {@link java.lang.String}.
   */
  public boolean isString() {
    return val instanceof String;
  }

  /**
   * @return
   *    Whether the object contained in this PSS-IF value is an instance of {@link java.lang.Boolean} 
   */
  public boolean isBoolean() {
    return val instanceof Boolean;
  }

  /**
   * @return
   *    Whether the object contained in this PSS-IF value is an instance of {@link java.util.Date}
   */
  public boolean isDate() {
    return val instanceof Date;
  }

  /**
   * @return
   *    Whether the object contained in this PSS-IF value is an instance of {@link java.lang.Integer},
   *    {@link java.lang.Long} or {@link java.math.BigInteger}
   */
  public boolean isInteger() {
    return val instanceof Integer || val instanceof BigInteger || val instanceof Long;
  }

  /**
   * @return
   *    Whether the object contained in this PSS-IF value is an instance of {@link java.lang.Float},
   *    {@link java.lang.Double} or {@link java.math.BigDecimal}
   */
  public boolean isDecimal() {
    return val instanceof Double || val instanceof BigDecimal || val instanceof Float || isInteger();
  }

  /**
   * @return
   *    Whether the object contained in this PSS-IF value is an instance of {@link EnumerationLiteral}
   */
  public boolean isEnumeration() {
    return val instanceof EnumerationLiteral;
  }

  /**
   * @return
   *    The value of this PSS-IF value as String. May throw a {@link ClassCastException} if used inproperty..
   */
  public String asString() {
    return (String) val;
  }

  /**
   * @return
   *    The value of this PSS-IF value as Boolean. May throw a {@link ClassCastException} if used inproperty..
   */
  public Boolean asBoolean() {
    return (Boolean) val;
  }

  /**
   * @return
   *    The value of this PSS-IF value as Date. May throw a {@link ClassCastException} if used inproperty..
   */
  public Date asDate() {
    return (Date) val;
  }

  /**
   * @return
   *    The value of this PSS-IF value as BigInteger. May throw a {@link ClassCastException} if used inproperty..
   */
  public BigInteger asInteger() {
    if (val instanceof Integer) {
      return BigInteger.valueOf(((Integer) val).longValue());
    }
    else if (val instanceof Long) {
      return BigInteger.valueOf(((Long) val).longValue());
    }
    return (BigInteger) val;
  }

  /**
   * @return
   *    The value of this PSS-IF value as BigDecimal. May throw a {@link ClassCastException} if used inproperty..
   */
  public BigDecimal asDecimal() {
    if (isInteger()) {
      return BigDecimal.valueOf(asInteger().longValue());
    }
    else if (val instanceof Double) {
      return BigDecimal.valueOf(((Double) val).longValue());
    }
    else if (val instanceof Float) {
      return BigDecimal.valueOf(((Float) val).longValue());
    }
    return (BigDecimal) val;
  }

  /**
   * @return
   *    The value of this PSS-IF value as EnumerationLiteral. May throw a {@link ClassCastException} if used inproperty..
   */
  public EnumerationLiteral asEnumeration() {
    return (EnumerationLiteral) val;
  }

}
