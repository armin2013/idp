package de.tum.pssif.transform.mapper;

import java.util.Set;

import com.google.common.collect.Sets;

import de.tum.pssif.core.metamodel.EdgeType;
import de.tum.pssif.core.metamodel.JunctionNodeType;
import de.tum.pssif.core.metamodel.Metamodel;
import de.tum.pssif.core.metamodel.NodeType;
import de.tum.pssif.core.metamodel.external.PSSIFCanonicMetamodelCreator;
import de.tum.pssif.transform.ModelMapper;
import de.tum.pssif.transform.model.EpkModelMapper;
import de.tum.pssif.transform.transformation.AliasJunctionNodeTypeTransformation;
import de.tum.pssif.transform.transformation.AliasNodeTypeTransformation;
import de.tum.pssif.transform.transformation.DeinstantifyEdgeTypeTransformation;
import de.tum.pssif.transform.transformation.DeinstantifyNodeTypeTransformation;
import de.tum.pssif.transform.transformation.HideEdgeTypeTransformation;
import de.tum.pssif.transform.transformation.HideJunctionNodeTypeTransformation;
import de.tum.pssif.transform.transformation.HideNodeTypeTransformation;
import de.tum.pssif.transform.transformation.RenameNodeTypeTransformation;
import de.tum.pssif.transform.transformation.ReverseConnectionMappingTransformation;
import de.tum.pssif.transform.transformation.delegating.DelegatingEdgeTypeTransformation;


public class EpkMapper extends BaseVisioMapper {

  private static final String      EPK_TEMPLATE     = "/visio/epk-template.vsdx";
  private static final Set<String> EPK_NODE_MASTERS = Sets.newHashSet("Event", "Function", "Organizational unit", "Process path", "XOR", "OR", "AND",
                                                        "Information/ Material", "Main process", "Component", "Enterprise area", "Process group");
  private static final Set<String> EPK_EDGE_MASTERS = Sets.newHashSet("Dynamic connector");

  public EpkMapper() {
    super(EPK_TEMPLATE, EPK_NODE_MASTERS, EPK_EDGE_MASTERS);
  }

  @Override
  protected Metamodel getView(Metamodel metamodel) {
    return createEpkView(metamodel);
  }

  @Override
  protected ModelMapper getModelMapper() {
    return new EpkModelMapper();
  }

  private static Metamodel createEpkView(Metamodel metamodel) {
    Metamodel view = new RenameNodeTypeTransformation(metamodel.getNodeType("Event").getOne(), "Dev Event").apply(metamodel);
    view = new RenameNodeTypeTransformation(view.getNodeType("Function").getOne(), "Abstract Function").apply(view);
    view = new RenameNodeTypeTransformation(view.getNodeType("Activity").getOne(), "Function").apply(view);
    view = new RenameNodeTypeTransformation(view.getNodeType("State").getOne(), "Event").apply(view);
    view = new AliasNodeTypeTransformation(view.getNodeType("Block").getOne(), "Information/ Material", "Information/ Material").apply(view);
    view = new AliasNodeTypeTransformation(view.getNodeType("Block").getOne(), "Organizational unit", "Organizational unit").apply(view);
    view = new HideNodeTypeTransformation(view.getNodeType("Block").getOne()).apply(view);
    view = new AliasJunctionNodeTypeTransformation(view.getJunctionNodeType(PSSIFCanonicMetamodelCreator.TAGS.get("N_CONJUNCTION")).getOne(), "XOR", "XOR")
        .apply(view);
    view = new AliasJunctionNodeTypeTransformation(view.getJunctionNodeType(PSSIFCanonicMetamodelCreator.TAGS.get("N_CONJUNCTION")).getOne(), "AND", "AND")
        .apply(view);
    view = new AliasJunctionNodeTypeTransformation(view.getJunctionNodeType(PSSIFCanonicMetamodelCreator.TAGS.get("N_CONJUNCTION")).getOne(), "OR", "OR")
        .apply(view);
    view = new HideJunctionNodeTypeTransformation(view.getJunctionNodeType(PSSIFCanonicMetamodelCreator.TAGS.get("N_CONJUNCTION")).getOne()).apply(view);

    DelegatingEdgeTypeTransformation dynamicConnector = new DelegatingEdgeTypeTransformation("Dynamic connector");
    EdgeType controlFlow = view.getEdgeType("Control Flow").getOne();
    EdgeType informationFlow = view.getEdgeType("Information Flow").getOne();
    EdgeType performs = view.getEdgeType("Performs").getOne();
    NodeType function = view.getNodeType("Function").getOne();
    NodeType event = view.getNodeType("Event").getOne();
    NodeType information = view.getNodeType("Information/ Material").getOne();
    NodeType organizationalUnit = view.getNodeType("Organizational unit").getOne();
    JunctionNodeType xor = view.getJunctionNodeType("XOR").getOne();
    JunctionNodeType or = view.getJunctionNodeType("OR").getOne();
    JunctionNodeType and = view.getJunctionNodeType("AND").getOne();
    dynamicConnector.delegate(function, event, controlFlow.getMapping(function, event).getOne());
    dynamicConnector.delegate(event, function, controlFlow.getMapping(event, function).getOne());

    dynamicConnector.delegate(function, xor, controlFlow.getMapping(function, xor).getOne());
    dynamicConnector.delegate(function, or, controlFlow.getMapping(function, or).getOne());
    dynamicConnector.delegate(function, and, controlFlow.getMapping(function, and).getOne());
    dynamicConnector.delegate(xor, function, controlFlow.getMapping(xor, function).getOne());
    dynamicConnector.delegate(or, function, controlFlow.getMapping(or, function).getOne());
    dynamicConnector.delegate(and, function, controlFlow.getMapping(and, function).getOne());

    dynamicConnector.delegate(event, xor, controlFlow.getMapping(event, xor).getOne());
    dynamicConnector.delegate(event, or, controlFlow.getMapping(event, or).getOne());
    dynamicConnector.delegate(event, and, controlFlow.getMapping(event, and).getOne());
    dynamicConnector.delegate(xor, event, controlFlow.getMapping(xor, event).getOne());
    dynamicConnector.delegate(or, event, controlFlow.getMapping(or, event).getOne());
    dynamicConnector.delegate(and, event, controlFlow.getMapping(and, event).getOne());

    dynamicConnector.delegate(information, function, informationFlow.getMapping(information, function).getOne());
    dynamicConnector.delegate(information, event, informationFlow.getMapping(information, event).getOne());

    dynamicConnector.delegate(organizationalUnit, function, performs.getMapping(information, function).getOne());
    dynamicConnector.delegate(organizationalUnit, event, performs.getMapping(information, event).getOne());

    view = dynamicConnector.apply(view);

    //correct direction of those edges, just to be not to strict
    view = new ReverseConnectionMappingTransformation(view.getEdgeType("Dynamic connector").getOne()
        .getMapping(view.getNodeType("Information/ Material").getOne(), view.getNodeType("Function").getOne()).getOne()).apply(view);
    view = new ReverseConnectionMappingTransformation(view.getEdgeType("Dynamic connector").getOne()
        .getMapping(view.getNodeType("Organizational unit").getOne(), view.getNodeType("Function").getOne()).getOne()).apply(view);
    view = new ReverseConnectionMappingTransformation(view.getEdgeType("Dynamic connector").getOne()
        .getMapping(view.getNodeType("Information/ Material").getOne(), view.getNodeType("Event").getOne()).getOne()).apply(view);
    view = new ReverseConnectionMappingTransformation(view.getEdgeType("Dynamic connector").getOne()
        .getMapping(view.getNodeType("Organizational unit").getOne(), view.getNodeType("Event").getOne()).getOne()).apply(view);

    view = new HideEdgeTypeTransformation(view.getEdgeType("Control Flow").getOne()).apply(view);
    view = new HideEdgeTypeTransformation(view.getEdgeType("Information Flow").getOne()).apply(view);
    view = new HideEdgeTypeTransformation(view.getEdgeType("Performs").getOne()).apply(view);

    for (NodeType nt : view.getNodeTypes()) {
      if (!EPK_NODE_MASTERS.contains(nt.getName())) {
        view = new DeinstantifyNodeTypeTransformation(nt).apply(view);
      }
    }

    for (EdgeType et : view.getEdgeTypes()) {
      if (!EPK_EDGE_MASTERS.contains(et.getName())) {
        view = new DeinstantifyEdgeTypeTransformation(et).apply(view);
      }
    }

    return view;
  }

}
