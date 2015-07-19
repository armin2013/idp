package de.tum.pssif.transform.mapper.graphml;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.junit.Test;

import de.tum.pssif.core.metamodel.Metamodel;
import de.tum.pssif.core.metamodel.external.PSSIFCanonicMetamodelCreator;
import de.tum.pssif.core.model.Model;
import de.tum.pssif.transform.Mapper;
import de.tum.pssif.transform.MapperFactory;


public class GraphMLReadTest {
  @Test
  public void testReadGraph() throws IOException {
    InputStream in = getClass().getResourceAsStream("/flow.graphml");

    GraphMLGraph graph = GraphMLGraph.read(in);

    for (GraphMLNode node : graph.getNodes()) {
      System.out.println("node: " + node.getId());

      System.out.println("attributes:");
      Map<String, String> attributes = node.getValues();
      for (String key : attributes.keySet()) {
        System.out.println("\t" + key + ": " + attributes.get(key));
      }
    }

    for (GraphMLEdge edge : graph.getEdges()) {
      System.out.println("edge: " + edge.getId() + "(" + edge.getSourceId() + "," + edge.getTargetId() + ")");

      System.out.println("attributes:");
      Map<String, String> attributes = edge.getValues();
      for (String key : attributes.keySet()) {
        System.out.println("\t" + key + ": " + attributes.get(key));
      }
    }

    //    while (vertices.hasNext()) {
    //      Vertex vertex = vertices.next();
    //      System.out.println("vertex: " + vertex.getId());
    //
    //      Iterator<Edge> outEdges = vertex.getEdges(Direction.OUT).iterator();
    //      Iterator<Edge> inEdges = vertex.getEdges(Direction.IN).iterator();
    //
    //      System.out.println("properties:");
    //      for (String key : vertex.getPropertyKeys()) {
    //        System.out.println("\t" + key + ": " + vertex.getProperty(key));
    //      }
    //
    //      System.out.println("out edges:");
    //      while (outEdges.hasNext()) {
    //        Edge edge = outEdges.next();
    //        System.out.println("\t" + edge.getId());
    //      }
    //
    //      System.out.println("in edges:");
    //      while (inEdges.hasNext()) {
    //        Edge edge = inEdges.next();
    //        System.out.println("\t" + edge.getId());
    //      }
    //
    //      System.out.println();
    //    }
  }

  @Test
  public void testReadFlowIntoModel() {
    InputStream in = getClass().getResourceAsStream("/flow.graphml");
    Mapper ufmMapper = MapperFactory.getMapper(MapperFactory.UOFP);
    Mapper pssifMapper = MapperFactory.getMapper(MapperFactory.PSSIF);

    Metamodel metamodel = PSSIFCanonicMetamodelCreator.create();
    Model model = ufmMapper.read(metamodel, in);
    pssifMapper.write(metamodel, model, System.out);
    System.out.println();
    System.out.println("=====");
    System.out.println("=====");
    System.out.println("=====");
    System.out.println("=====");
    System.out.println("=====");
    System.out.println();
    ufmMapper.write(metamodel, model, System.out);
  }

  //
  //  @Test
  //  public void testReadFlowIntoPSSIF() {
  //    InputStream in = getClass().getResourceAsStream("/flow.graphml");
  //    GraphMLMapper importer = new UFMMapper();
  //
  //    Metamodel metamodel = PSSIFCanonicMetamodelCreator.create();
  //    Metamodel view = new RenameEdgeTypeTransformation(metamodel.getEdgeType("Information Flow").getOne(), "InformationFlow").apply(metamodel);
  //    view = new RenameEdgeTypeTransformation(view.getEdgeType("Energy Flow").getOne(), "EnergyFlow").apply(view);
  //    Model model = importer.read(view, in);
  //
  //    NodeType viewedState = view.getNodeType("State").getOne();
  //    NodeType viewedFunction = view.getNodeType("Function").getOne();
  //
  //    EdgeType viewedInformationFlow = view.getEdgeType("InformationFlow").getOne();
  //    EdgeType viewedEnergyFlow = view.getEdgeType("EnergyFlow").getOne();
  //
  //    NodeType canonicState = metamodel.getNodeType("State").getOne();
  //    NodeType canonicFunction = metamodel.getNodeType("Function").getOne();
  //
  //    EdgeType canonicInformationFlow = metamodel.getEdgeType("Information Flow").getOne();
  //    EdgeType canonicEnergyFlow = metamodel.getEdgeType("Energy Flow").getOne();
  //
  //    Assert.assertEquals(8, viewedState.apply(model, true).size());
  //    Assert.assertEquals(5, viewedFunction.apply(model, true).size());
  //    Assert.assertEquals(8, canonicState.apply(model, true).size());
  //    Assert.assertEquals(5, canonicFunction.apply(model, true).size());
  //
  //    for (Node node : canonicState.apply(model, true).getMany()) {
  //      for (ConnectionMapping mapping : canonicInformationFlow.getOutgoingMappings(canonicState).getMany()) {
  //        PSSIFOption<ConnectionMapping> viewedMapping = viewedInformationFlow.getMapping(mapping.getFrom(), mapping.getTo());
  //        Assert.assertTrue(viewedMapping.isOne());
  //        Assert.assertEquals(mapping.applyIncoming(node, true), viewedMapping.getOne().applyIncoming(node, true));
  //        Assert.assertEquals(mapping.applyIncoming(node, false), viewedMapping.getOne().applyIncoming(node, false));
  //        Assert.assertEquals(mapping.applyOutgoing(node, true), viewedMapping.getOne().applyOutgoing(node, true));
  //        Assert.assertEquals(mapping.applyOutgoing(node, false), viewedMapping.getOne().applyOutgoing(node, false));
  //      }
  //      for (ConnectionMapping mapping : canonicEnergyFlow.getOutgoingMappings(canonicState).getMany()) {
  //        PSSIFOption<ConnectionMapping> viewedMapping = viewedEnergyFlow.getMapping(mapping.getFrom(), mapping.getTo());
  //        Assert.assertTrue(viewedMapping.isOne());
  //        Assert.assertEquals(mapping.applyIncoming(node, true), viewedMapping.getOne().applyIncoming(node, true));
  //        Assert.assertEquals(mapping.applyIncoming(node, false), viewedMapping.getOne().applyIncoming(node, false));
  //        Assert.assertEquals(mapping.applyOutgoing(node, true), viewedMapping.getOne().applyOutgoing(node, true));
  //        Assert.assertEquals(mapping.applyOutgoing(node, false), viewedMapping.getOne().applyOutgoing(node, false));
  //      }
  //    }
  //    for (Node node : canonicFunction.apply(model, true).getMany()) {
  //      for (ConnectionMapping mapping : canonicInformationFlow.getOutgoingMappings(canonicState).getMany()) {
  //        PSSIFOption<ConnectionMapping> viewedMapping = viewedInformationFlow.getMapping(mapping.getFrom(), mapping.getTo());
  //        Assert.assertTrue(viewedMapping.isOne());
  //        Assert.assertEquals(mapping.applyIncoming(node, true), viewedMapping.getOne().applyIncoming(node, true));
  //        Assert.assertEquals(mapping.applyIncoming(node, false), viewedMapping.getOne().applyIncoming(node, false));
  //        Assert.assertEquals(mapping.applyOutgoing(node, true), viewedMapping.getOne().applyOutgoing(node, true));
  //        Assert.assertEquals(mapping.applyOutgoing(node, false), viewedMapping.getOne().applyOutgoing(node, false));
  //      }
  //      for (ConnectionMapping mapping : canonicEnergyFlow.getOutgoingMappings(canonicState).getMany()) {
  //        PSSIFOption<ConnectionMapping> viewedMapping = viewedEnergyFlow.getMapping(mapping.getFrom(), mapping.getTo());
  //        Assert.assertTrue(viewedMapping.isOne());
  //        Assert.assertEquals(mapping.applyIncoming(node, true), viewedMapping.getOne().applyIncoming(node, true));
  //        Assert.assertEquals(mapping.applyIncoming(node, false), viewedMapping.getOne().applyIncoming(node, false));
  //        Assert.assertEquals(mapping.applyOutgoing(node, true), viewedMapping.getOne().applyOutgoing(node, true));
  //        Assert.assertEquals(mapping.applyOutgoing(node, false), viewedMapping.getOne().applyOutgoing(node, false));
  //      }
  //    }
  //  }

  @Test
  public void testReadHierarchicIntoPSSIF() {
    InputStream in = getClass().getResourceAsStream("/hierarchic.graphml");
    GraphMLMapper importer = new UFMMapper();

    Metamodel metamodel = PSSIFCanonicMetamodelCreator.create();
    Model model = importer.read(metamodel, in);
  }
}
