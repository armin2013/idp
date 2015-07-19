package de.tum.pssif.core.metamodel;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.tum.pssif.core.common.PSSIFOption;
import de.tum.pssif.core.common.PSSIFUtil;
import de.tum.pssif.core.exception.PSSIFStructuralIntegrityException;
import de.tum.pssif.core.metamodel.impl.MetamodelImpl;
import de.tum.pssif.core.metamodel.mutable.MutableEdgeType;
import de.tum.pssif.core.metamodel.mutable.MutableMetamodel;
import de.tum.pssif.core.metamodel.mutable.MutableNodeTypeBase;


public class MetamodelTest {
  private MutableMetamodel metamodel;

  @Before
  public void init() {
    metamodel = new MetamodelImpl();
  }

  @Test
  public void testCreateNodeType() {
    MutableNodeTypeBase mnt = metamodel.createNodeType("A");
    Assert.assertNotNull(mnt);
    Assert.assertEquals("A", mnt.getName());

    Assert.assertEquals(2, metamodel.getBaseNodeTypes().size());
    Assert.assertEquals(2, metamodel.getMutableBaseNodeTypes().size());

    PSSIFOption<NodeTypeBase> nt = metamodel.getBaseNodeType("A");
    Assert.assertTrue(nt.isOne());
    Assert.assertEquals(mnt, nt.getOne());
    Assert.assertEquals(nt, metamodel.getMutableNodeType("A"));
  }

  @Test
  public void testCreateEdgeType() {
    MutableEdgeType met = metamodel.createEdgeType("A");
    Assert.assertNotNull(met);
    Assert.assertEquals("A", met.getName());
    Assert.assertNotEquals("a", met.getName());

    Assert.assertEquals(2, metamodel.getEdgeTypes().size());
    Assert.assertEquals(2, metamodel.getMutableEdgeTypes().size());

    PSSIFOption<EdgeType> et = metamodel.getEdgeType("A");
    Assert.assertTrue(et.isOne());
    Assert.assertEquals(met, et.getOne());
    Assert.assertEquals(et, metamodel.getMutableEdgeType("A"));
  }

  @Test(expected = PSSIFStructuralIntegrityException.class)
  public void testCreateNodeTypeDuplicateName() {
    MutableNodeTypeBase mnt = metamodel.createNodeType("Node");
    Assert.assertNotNull(mnt);
    Assert.assertEquals(PSSIFUtil.normalize("Node"), mnt.getName());

    Assert.assertEquals(1, metamodel.getBaseNodeTypes().size());
    Assert.assertEquals(1, metamodel.getMutableBaseNodeTypes().size());

    metamodel.createNodeType("node");
  }

  @Test(expected = PSSIFStructuralIntegrityException.class)
  public void testCreateEdgeTypeDuplicateName() {
    MutableEdgeType met = metamodel.createEdgeType("Edge");
    Assert.assertNotNull(met);
    Assert.assertEquals(PSSIFUtil.normalize("Edge"), met.getName());

    Assert.assertEquals(1, metamodel.getEdgeTypes().size());
    Assert.assertEquals(1, metamodel.getMutableEdgeTypes().size());

    metamodel.createEdgeType("edge");
  }

  @Test(expected = PSSIFStructuralIntegrityException.class)
  public void testCreateNodeTypeWithNullName() {
    metamodel.createNodeType(null);
  }

  @Test(expected = PSSIFStructuralIntegrityException.class)
  public void testCreateEdgeTypeWithNullName() {
    metamodel.createEdgeType(null);
  }

  @Test(expected = PSSIFStructuralIntegrityException.class)
  public void testCreateNodeTypeWithEmptyName() {
    metamodel.createNodeType(" ");
  }

  @Test(expected = PSSIFStructuralIntegrityException.class)
  public void testCreateEdgeTypeWithEmptyName() {
    metamodel.createEdgeType(" ");
  }
}
