package de.tum.pssif.transform.mapper.visio;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Set;

import org.junit.Test;

import com.google.common.collect.Sets;

import de.tum.pssif.core.metamodel.external.PSSIFCanonicMetamodelCreator;
import de.tum.pssif.core.model.Model;
import de.tum.pssif.transform.Mapper;
import de.tum.pssif.transform.MapperFactory;
import de.tum.pssif.transform.graph.Graph;
import de.tum.pssif.transform.io.VisioIoMapper;


public class MyTestClass {

  public static final Set<String> BPMN_NODE_MASTERS = Sets.newHashSet("Task", "Gateway", "Intermediate Event", "End Event", "Start Event",
                                                        "Collapsed Sub-Process", "Expanded Sub-Process", "Text Annotation", "Message", "Data Object",
                                                        "Data Store", "Pool / Lane");

  public static final Set<String> BPMN_EDGE_MASTERS = Sets.newHashSet("Sequence Flow", "Association", "Message Flow");

  public static final Set<String> EPK_NODE_MASTERS  = Sets.newHashSet("Event", "Function", "Organizational unit", "Process path", "XOR", "OR", "AND",
                                                        "Information/ Material", "Main process", "Component", "Enterprise area", "Process group");

  public static final Set<String> EPK_EDGE_MASTERS  = Sets.newHashSet("Dynamic connector");

  @Test
  public void testReadEpkToGraph() throws IOException {
    VisioIoMapper mapper = new VisioIoMapper("", EPK_NODE_MASTERS, EPK_EDGE_MASTERS);
    Graph graph = mapper.read(getClass().getResourceAsStream("/visio/epk-data.vsdx"));

    mapper = new VisioIoMapper("/visio/epk-template.vsdx", EPK_NODE_MASTERS, EPK_EDGE_MASTERS);
    mapper.write(graph, new FileOutputStream("target/testWriteEpkWithGraph.vsdx"));
  }

  @Test
  public void testEpkRoundtrip() throws FileNotFoundException {
    Mapper epkMapper = MapperFactory.getMapper(MapperFactory.EPK);
    Model model = epkMapper.read(PSSIFCanonicMetamodelCreator.create(), getClass().getResourceAsStream("/visio/epk-data.vsdx"));
    MapperFactory.getMapper(MapperFactory.EPK).write(PSSIFCanonicMetamodelCreator.create(), model, new FileOutputStream("target/epkRoundtrip.vsdx"));
  }

  //  @Test
  //  public void testEpkGraphConsistency() {
  //    VisioIoMapper mapper = new VisioIoMapper("", EPK_NODE_MASTERS, EPK_EDGE_MASTERS);
  //    Graph graph = mapper.read(getClass().getResourceAsStream("/visio/epk-data.vsdx"));
  //    List<String> hyperEdgeMasters = Lists.newArrayList("AND", "OR", "XOR");
  //    for (Node node : graph.getNodes()) {
  //      if (hyperEdgeMasters.contains(node.getType())) {
  //        System.out.println("nodeId: " + node.getId() + " | incomming: " + node.getIncoming() + " | outgoing: " + node.getOutgoing());
  //      }
  //      else {
  //        System.out.println("master: " + node.getType() + " id: " + node.getId());
  //      }
  //    }
  //  }

  @Test
  public void testReadBpmnToGraph() throws FileNotFoundException {
    VisioIoMapper mapper = new VisioIoMapper("", BPMN_NODE_MASTERS, BPMN_EDGE_MASTERS);
    Graph graph = mapper.read(getClass().getResourceAsStream("/visio/bpmn-data.vsdx"));
    mapper = new VisioIoMapper("/visio/bpmn-template.vsdx", BPMN_NODE_MASTERS, BPMN_EDGE_MASTERS);
    mapper.write(graph, new FileOutputStream("target/testWriteBpmnWithGraph.vsdx"));
  }

}
