package de.tum.pssif.transform.mapper;

import java.util.Set;

import com.google.common.collect.Sets;

import de.tum.pssif.core.metamodel.Metamodel;
import de.tum.pssif.transform.ModelMapper;
import de.tum.pssif.transform.model.BpmnModelMapper;
import de.tum.pssif.transform.transformation.AliasNodeTypeTransformation;
import de.tum.pssif.transform.transformation.RenameEdgeTypeTransformation;


public class BpmnMapper extends BaseVisioMapper {

  //TODO
  private static final String     BPMN_TEMPLATE     = "/visio/bpmn-template.vsdx";

  public static final Set<String> BPMN_NODE_MASTERS = Sets.newHashSet("Task", "Gateway", "Intermediate Event", "End Event", "Start Event",
                                                        "Collapsed Sub-Process", "Expanded Sub-Process", "Text Annotation", "Message", "Data Object",
                                                        "Data Store", "Pool / Lane");

  public static final Set<String> BPMN_EDGE_MASTERS = Sets.newHashSet("Sequence Flow", "Association", "Message Flow");

  public BpmnMapper() {
    super(BPMN_TEMPLATE, BPMN_NODE_MASTERS, BPMN_EDGE_MASTERS);
  }

  @Override
  protected Metamodel getView(Metamodel metamodel) {
    Metamodel view = new AliasNodeTypeTransformation(metamodel.getNodeType("Block").getOne(), "Pool / Lane", "Pool / Lane").apply(metamodel);
    view = new AliasNodeTypeTransformation(view.getNodeType("State").getOne(), "Start Event", "Start Event").apply(view);
    view = new AliasNodeTypeTransformation(view.getNodeType("Activity").getOne(), "Task", "Task").apply(view);
    view = new AliasNodeTypeTransformation(view.getNodeType("State").getOne(), "End Event", "End Event").apply(view);
    view = new RenameEdgeTypeTransformation(view.getEdgeType("Control Flow").getOne(), "Sequence Flow").apply(view);
    // TODO Auto-generated method stub
    return metamodel;
  }

  @Override
  protected ModelMapper getModelMapper() {
    // TODO Auto-generated method stub
    return new BpmnModelMapper();
  }

}
