package de.tum.pssif.core.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Sets;

import de.tum.pssif.core.common.PSSIFConstants;
import de.tum.pssif.core.common.PSSIFOption;
import de.tum.pssif.core.common.PSSIFValue;
import de.tum.pssif.core.exception.PSSIFStructuralIntegrityException;
import de.tum.pssif.core.metamodel.Attribute;
import de.tum.pssif.core.metamodel.ConnectionMapping;
import de.tum.pssif.core.metamodel.EdgeType;
import de.tum.pssif.core.metamodel.Metamodel;
import de.tum.pssif.core.metamodel.MetamodelProvider;
import de.tum.pssif.core.metamodel.NodeTypeBase;
import de.tum.pssif.core.model.impl.ModelImpl;


public class ModelTest {
  private Metamodel metamodel;
  private Model     model;

  @Before
  public void init() {
    metamodel = MetamodelProvider.create();
    model = new ModelImpl();
  }

  @Test
  public void testSimpleModel() {
    NodeTypeBase s = nt("S");
    NodeTypeBase a = nt("A");
    NodeTypeBase b = nt("B");

    EdgeType cf = et("CF");

    ConnectionMapping s_a = cm(cf, s, a);
    ConnectionMapping s_b = cm(cf, s, b);

    Node a1 = node(a, "a1");
    Node a2 = node(a, "a2");
    Node b1 = node(b, "b1");
    Node s1 = node(s, "s1");
    Edge cf1 = s_a.create(model, s1, a1);
    Edge cf2 = s_a.create(model, s1, a2);
    Edge cf3 = s_b.create(model, s1, b1);

    Assert.assertEquals(Sets.newHashSet(cf1, cf2, cf3), cf.apply(model, false).getMany());

    Assert.assertEquals(cf1, s_a.applyIncoming(a1).getOne());
    Assert.assertEquals(Sets.newHashSet(cf1, cf2), s_a.applyOutgoing(s1).getMany());
    Assert.assertEquals(s1, s_a.applyFrom(cf1));
    Assert.assertEquals(a1, s_a.applyTo(cf1));

    Assert.assertEquals(cf3, cf.applyIncoming(b1, true).getOne());
    Assert.assertEquals(Sets.newHashSet(cf1, cf2, cf3), cf.applyOutgoing(s1, true).getMany());
    Assert.assertEquals(s1, s_b.applyFrom(cf3));
    Assert.assertEquals(b1, s_b.applyTo(cf3));
  }

  @Test
  public void testSimpleJunctionNegativeFromFirst() {
    NodeTypeBase s = nt("S");
    NodeTypeBase jun = nt("jun");

    Node s1 = node(s, "s1");
    Node s2 = node(s, "s2");
    Node j1 = node(jun, "j1");
    Node j2 = node(jun, "j2");

    EdgeType cf = et("CF");
    ConnectionMapping s_j = cf.getMapping(s, jun).getOne();
    ConnectionMapping j_s = cf.getMapping(jun, s).getOne();
    ConnectionMapping j_j = cf.getMapping(jun, jun).getOne();

    Edge cf1 = s_j.create(model, s1, j1);
    Assert.assertNotNull(cf1);
    Edge cf2 = j_j.create(model, j1, j2);
    Assert.assertNotNull(cf2);
    try {
      j_s.create(model, j2, s2);
      Assert.fail();
    } catch (PSSIFStructuralIntegrityException e) {
      //expected
    }
  }

  @Test
  public void testSimpleJunctionNegativeToFirst() {
    NodeTypeBase s = nt("S");
    NodeTypeBase jun = nt("jun");

    Node s1 = node(s, "s1");
    Node s2 = node(s, "s2");
    Node j1 = node(jun, "j1");
    Node j2 = node(jun, "j2");

    EdgeType cf = et("CF");
    ConnectionMapping s_j = cf.getMapping(s, jun).getOne();
    ConnectionMapping j_s = cf.getMapping(jun, s).getOne();
    ConnectionMapping j_j = cf.getMapping(jun, jun).getOne();

    Edge cf1 = s_j.create(model, s1, j1);
    Assert.assertNotNull(cf1);
    Edge cf2 = j_s.create(model, j2, s2);
    Assert.assertNotNull(cf2);
    try {
      j_j.create(model, j1, j2);
      Assert.fail();
    } catch (PSSIFStructuralIntegrityException e) {
      //expected
    }
  }

  @Test
  public void testSimpleJunctionNegativeMultiplicity() {
    NodeTypeBase a = nt("A");
    NodeTypeBase s = nt("S");
    NodeTypeBase jun = nt("jun");

    Node s1 = node(s, "s1");
    Node s2 = node(s, "s2");
    Node a1 = node(a, "a1");
    Node a2 = node(a, "a2");
    Node j1 = node(jun, "j1");

    EdgeType cf = et("CF");
    ConnectionMapping s_j = cf.getMapping(s, jun).getOne();
    ConnectionMapping j_a = cf.getMapping(jun, a).getOne();

    s_j.create(model, s1, j1);
    s_j.create(model, s2, j1);
    j_a.create(model, j1, a1);
    try {
      j_a.create(model, j1, a2);
      Assert.fail();
    } catch (PSSIFStructuralIntegrityException e) {
      //expected
    }
  }

  @Test
  public void testSimpleJunctionNegativeEdgeType() {
    NodeTypeBase a = nt("A");
    NodeTypeBase s = nt("S");
    NodeTypeBase jun = nt("jun");

    Node s1 = node(s, "s1");
    Node s2 = node(s, "s2");
    Node a2 = node(a, "a2");
    Node j1 = node(jun, "j1");

    EdgeType cf = et("CF");
    EdgeType mf = et("MF");
    ConnectionMapping s_j = cf.getMapping(s, jun).getOne();
    ConnectionMapping j_a = mf.getMapping(jun, a).getOne();

    s_j.create(model, s1, j1);
    s_j.create(model, s2, j1);
    try {
      j_a.create(model, j1, a2);
      Assert.fail();
    } catch (PSSIFStructuralIntegrityException e) {
      //expected
    }
  }

  @Test
  public void testAttributeValues() {
    NodeTypeBase s = nt("S");
    Node s1 = node(s, "s1");
    PSSIFOption<Attribute> name = s.getAttribute(PSSIFConstants.BUILTIN_ATTRIBUTE_NAME);
    Assert.assertTrue(name.isOne());
    name.getOne().set(s1, PSSIFOption.one(PSSIFValue.create("s1")));
    PSSIFOption<PSSIFValue> val = name.getOne().get(s1);
    Assert.assertTrue(val.isOne());
    Assert.assertTrue(val.getOne().isString());
    Assert.assertEquals("s1", val.getOne().asString());
  }

  private NodeTypeBase nt(String name) {
    return metamodel.getBaseNodeType(name).getOne();
  }

  private EdgeType et(String name) {
    return metamodel.getEdgeType(name).getOne();
  }

  private Node node(NodeTypeBase type, String id) {
    PSSIFOption<? extends Node> result = type.apply(model, id, false);
    if (result.isNone()) {
      Node node = type.create(model);
      node.setId(id);
      result = PSSIFOption.one(node);
    }
    return result.getOne();
  }

  private ConnectionMapping cm(EdgeType et, NodeTypeBase from, NodeTypeBase to) {
    return et.getMapping(from, to).getOne();
  }
}
