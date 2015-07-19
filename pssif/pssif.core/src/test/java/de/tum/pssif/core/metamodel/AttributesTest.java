package de.tum.pssif.core.metamodel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import de.tum.pssif.core.common.PSSIFConstants;
import de.tum.pssif.core.common.PSSIFOption;
import de.tum.pssif.core.exception.PSSIFStructuralIntegrityException;
import de.tum.pssif.core.metamodel.impl.MetamodelImpl;
import de.tum.pssif.core.metamodel.mutable.MutableEdgeType;
import de.tum.pssif.core.metamodel.mutable.MutableEnumeration;
import de.tum.pssif.core.metamodel.mutable.MutableMetamodel;
import de.tum.pssif.core.metamodel.mutable.MutableNodeType;
import de.tum.pssif.core.metamodel.mutable.MutableNodeTypeBase;


public class AttributesTest {

  private MutableMetamodel metamodel;

  @Before
  public void createMetamodel() {
    this.metamodel = new MetamodelImpl();
    metamodel.createNodeType("A");
    metamodel.createNodeType("B");
  }

  @Test
  public void testCreatePrimitiveNodeAttribute() {
    nodeA().createAttribute(nodeA().getDefaultAttributeGroup(), "attr", PrimitiveDataType.INTEGER, Units.INCH, true, AttributeCategory.METADATA);
    PSSIFOption<Attribute> attr = nodeA().getAttribute("attr");
    assertTrue(attr.isOne());
    assertEquals(PrimitiveDataType.INTEGER, attr.getOne().getType());
    assertEquals(Units.INCH, attr.getOne().getUnit());
  }

  @Test
  public void testCreatePrimitiveEdgeAttribute() {
    edge().createAttribute(nodeA().getDefaultAttributeGroup(), "attr", PrimitiveDataType.INTEGER, Units.INCH, true, AttributeCategory.METADATA);
    PSSIFOption<Attribute> attr = edge().getAttribute("attr");
    assertTrue(attr.isOne());
    assertEquals(PrimitiveDataType.INTEGER, attr.getOne().getType());
    assertEquals(Units.INCH, attr.getOne().getUnit());
  }

  @Test
  public void testRemoveNodeAttribute() {
    nodeA().createAttribute(nodeA().getDefaultAttributeGroup(), "attr", PrimitiveDataType.INTEGER, Units.INCH, true, AttributeCategory.METADATA);
    PSSIFOption<Attribute> attr = nodeA().getAttribute("attr");
    assertTrue(attr.isOne());
    assertEquals(PrimitiveDataType.INTEGER, attr.getOne().getType());
    assertEquals(Units.INCH, attr.getOne().getUnit());

    //    nodeA().getDefaultAttributeGroup().removeAttribute(attr);
    //    assertNull(nodeA().findAttribute("attr"));
  }

  @Test
  public void testRemoveEdgeAttribute() {
    edge().createAttribute(nodeA().getDefaultAttributeGroup(), "attr", PrimitiveDataType.INTEGER, Units.INCH, true, AttributeCategory.METADATA);
    PSSIFOption<Attribute> attr = edge().getAttribute("attr");
    assertTrue(attr.isOne());
    assertEquals(PrimitiveDataType.INTEGER, attr.getOne().getType());
    assertEquals(Units.INCH, attr.getOne().getUnit());

    //    edge().getDefaultAttributeGroup().removeAttribute(attr);
    //    assertNull(edge().findAttribute("attr"));
  }

  @Test
  public void testCreateEnumeration() {
    metamodel.createEnumeration("enum");
    PSSIFOption<Enumeration> enumeration = metamodel.getEnumeration("enum");
    assertTrue(enumeration.isOne());
    assertEquals(0, enumeration.getOne().getLiterals().size());
    assertEquals("enum", enumeration.getOne().getName());
  }

  @Test
  public void testRemoveEnumeration() {
    metamodel.createEnumeration("enum");
    PSSIFOption<Enumeration> enumeration = metamodel.getEnumeration("enum");
    assertTrue(enumeration.isOne());
    assertEquals(0, enumeration.getOne().getLiterals().size());
    assertEquals("enum", enumeration.getOne().getName());

    //should accept null unit, since enums have no units.
    nodeA().createAttribute(nodeA().getDefaultAttributeGroup(), "enumAttr", enumeration.getOne(), true, AttributeCategory.METADATA);

    PSSIFOption<Attribute> enumAttr = nodeA().getAttribute("enumAttr");
    assertTrue(enumAttr.isOne());
    assertEquals(enumeration.getOne(), enumAttr.getOne().getType());
    assertEquals("enumAttr", enumAttr.getOne().getName());
    assertEquals(Units.NONE, enumAttr.getOne().getUnit());

    metamodel.removeEnumeration(enumeration.getOne());
    assertTrue(metamodel.getEnumeration("enum").isNone());
    assertTrue(metamodel.getDataType("enum").isNone());

    assertTrue(nodeA().getAttribute("enum").isNone());
  }

  @Test
  public void testCreateLiteral() {
    metamodel.createEnumeration("enum");
    PSSIFOption<MutableEnumeration> enumeration = metamodel.getMutableEnumeration("enum");

    assertTrue(enumeration.isOne());

    EnumerationLiteral literal1 = enumeration.getOne().createLiteral("literal1");
    EnumerationLiteral literal2 = enumeration.getOne().createLiteral("literal2");

    assertEquals(2, enumeration.getOne().getLiterals().size());
    assertNotNull(enumeration.getOne().getLiteral("literal1"));
    assertNotNull(enumeration.getOne().getLiteral("literal2"));

    assertTrue(enumeration.getOne().getLiterals().contains(literal1));
    assertTrue(enumeration.getOne().getLiterals().contains(literal2));
  }

  @Test
  public void testRemoveLiteral() {
    metamodel.createEnumeration("enum");
    PSSIFOption<MutableEnumeration> enumeration = metamodel.getMutableEnumeration("enum");

    assertTrue(enumeration.isOne());

    EnumerationLiteral literal1 = enumeration.getOne().createLiteral("literal1");
    EnumerationLiteral literal2 = enumeration.getOne().createLiteral("literal2");

    assertEquals(2, enumeration.getOne().getLiterals().size());
    assertNotNull(enumeration.getOne().getLiteral("literal1"));
    assertNotNull(enumeration.getOne().getLiteral("literal2"));

    assertTrue(enumeration.getOne().getLiterals().contains(literal1));
    assertTrue(enumeration.getOne().getLiterals().contains(literal2));

    enumeration.getOne().removeLiteral(literal1);

    assertEquals(1, enumeration.getOne().getLiterals().size());
    assertTrue(enumeration.getOne().getLiteral("literal1").isNone());
    assertNotNull(enumeration.getOne().getLiteral("literal2"));

    assertFalse(enumeration.getOne().getLiterals().contains(literal1));
    assertTrue(enumeration.getOne().getLiterals().contains(literal2));
  }

  @Test(expected = PSSIFStructuralIntegrityException.class)
  public void testCreateDuplicateLiteral() {
    metamodel.createEnumeration("enum");
    PSSIFOption<MutableEnumeration> enumeration = metamodel.getMutableEnumeration("enum");

    assertTrue(enumeration.isOne());

    enumeration.getOne().createLiteral("literal1");
    enumeration.getOne().createLiteral("literal1");
  }

  @Test(expected = PSSIFStructuralIntegrityException.class)
  public void testDuplicateEnumerations() {
    metamodel.createEnumeration("enum");
    PSSIFOption<Enumeration> enumeration = metamodel.getEnumeration("enum");

    assertNotNull(enumeration);

    metamodel.createEnumeration("enum");
  }

  @Test(expected = PSSIFStructuralIntegrityException.class)
  public void testDuplicateAttributes() {
    nodeA().createAttribute(nodeA().getDefaultAttributeGroup(), "attr", PrimitiveDataType.INTEGER, Units.KILOGRAM, true, AttributeCategory.METADATA);
    nodeA().createAttribute(nodeA().getDefaultAttributeGroup(), "attr", PrimitiveDataType.DECIMAL, Units.INCH, true, AttributeCategory.METADATA);
  }

  @Test(expected = PSSIFStructuralIntegrityException.class)
  public void testCreateAttributeWithIncompatibleType() {
    nodeA().createAttribute(nodeA().getDefaultAttributeGroup(), "attr", PrimitiveDataType.DATE, Units.KILOGRAM, true, AttributeCategory.METADATA);
  }

  @Test(expected = PSSIFStructuralIntegrityException.class)
  public void testCreateEnumerationAttributeWithUnit() {
    MutableEnumeration enumeration = metamodel.createEnumeration("enum");
    enumeration.createLiteral("lit");
    nodeA().createAttribute(nodeA().getDefaultAttributeGroup(), "enumAttr", enumeration, Units.CENTIMETER, true, AttributeCategory.METADATA);
  }

  @Test
  public void testStaticDataTypes() {
    assertNotNull(metamodel.getDataType(PrimitiveDataType.BOOLEAN.getName()));
    assertNotNull(metamodel.getPrimitiveType(PrimitiveDataType.BOOLEAN.getName()));

    assertNotNull(metamodel.getDataType(PrimitiveDataType.DATE.getName()));
    assertNotNull(metamodel.getPrimitiveType(PrimitiveDataType.DATE.getName()));

    assertNotNull(metamodel.getDataType(PrimitiveDataType.DECIMAL.getName()));
    assertNotNull(metamodel.getPrimitiveType(PrimitiveDataType.DECIMAL.getName()));

    assertNotNull(metamodel.getDataType(PrimitiveDataType.INTEGER.getName()));
    assertNotNull(metamodel.getPrimitiveType(PrimitiveDataType.INTEGER.getName()));

    assertNotNull(metamodel.getDataType(PrimitiveDataType.STRING.getName()));
    assertNotNull(metamodel.getPrimitiveType(PrimitiveDataType.STRING.getName()));
  }

  @Test
  public void testUnits() {
    assertNotNull(Units.CENTIMETER.getName());
    assertNotNull(Units.INCH.getName());
    assertNotNull(Units.KILOGRAM.getName());
    //TODO all other units!
  }

  @Test
  public void testAttributeInheritance() {
    MutableNodeType node = metamodel.getMutableNodeType(PSSIFConstants.ROOT_NODE_TYPE_NAME).getOne();
    AttributeGroup nodeGroup = node.createAttributeGroup("test");
    node.createAttribute(nodeGroup, "1", PrimitiveDataType.STRING, true, AttributeCategory.METADATA);

    MutableNodeType a = metamodel.getMutableNodeType("A").getOne();
    AttributeGroup aGroup = a.createAttributeGroup("test");
    a.createAttribute(aGroup, "2", PrimitiveDataType.STRING, true, AttributeCategory.METADATA);

    PSSIFOption<AttributeGroup> group = a.getAttributeGroup("test");
    assertTrue(group.isOne());
    assertEquals(nodeGroup.getAttributes().size() + 1, group.getOne().getAttributes().size());
  }

  private MutableNodeTypeBase nodeA() {
    return metamodel.getMutableNodeType("A").getOne();
  }

  private MutableEdgeType edge() {
    return metamodel.getMutableEdgeType("edge").getOne();
  }
}
