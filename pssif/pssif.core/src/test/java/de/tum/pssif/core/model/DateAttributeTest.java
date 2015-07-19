package de.tum.pssif.core.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Date;

import org.junit.Test;

import de.tum.pssif.core.common.PSSIFConstants;
import de.tum.pssif.core.common.PSSIFOption;
import de.tum.pssif.core.common.PSSIFValue;
import de.tum.pssif.core.metamodel.Attribute;
import de.tum.pssif.core.metamodel.AttributeCategory;
import de.tum.pssif.core.metamodel.NodeType;
import de.tum.pssif.core.metamodel.PrimitiveDataType;
import de.tum.pssif.core.metamodel.impl.MetamodelImpl;
import de.tum.pssif.core.metamodel.mutable.MutableNodeType;
import de.tum.pssif.core.model.impl.ModelImpl;


public class DateAttributeTest {

  private static final String DATE_ATTR = "dateAttribute";

  @Test
  public void testSetDateAttributeValue() {
    MetamodelImpl metamodel = new MetamodelImpl();

    PSSIFOption<NodeType> rootNtSearch = metamodel.getNodeType(PSSIFConstants.ROOT_NODE_TYPE_NAME);
    assertTrue(rootNtSearch.isOne());

    MutableNodeType rootNt = (MutableNodeType) rootNtSearch.getOne();
    Attribute dateAttribute = rootNt.createAttribute(rootNt.getDefaultAttributeGroup(), DATE_ATTR, PrimitiveDataType.DATE, true,
        AttributeCategory.METADATA);

    Model model = new ModelImpl();
    Node node = rootNt.create(model);

    assertTrue(dateAttribute.get(node).isNone());

    Date date1 = new Date();

    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      fail("Could not sleep.");
    }

    Date date2 = new Date();

    assertFalse(date1.equals(date2));

    dateAttribute.set(node, PSSIFOption.one(PSSIFValue.create(date1)));

    assertEquals(date1, dateAttribute.get(node).getOne().getValue());

    dateAttribute.set(node, PSSIFOption.one(PSSIFValue.create(date2)));

    assertEquals(date2, dateAttribute.get(node).getOne().getValue());
  }
}
