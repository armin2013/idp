package de.tum.pssif.transform.model;

import de.tum.pssif.core.common.PSSIFConstants;
import de.tum.pssif.core.common.PSSIFOption;
import de.tum.pssif.core.common.PSSIFValue;
import de.tum.pssif.core.metamodel.Attribute;
import de.tum.pssif.core.metamodel.ConnectionMapping;
import de.tum.pssif.core.metamodel.EdgeType;
import de.tum.pssif.core.metamodel.Metamodel;
import de.tum.pssif.core.metamodel.NodeType;
import de.tum.pssif.core.model.Model;
import de.tum.pssif.core.model.impl.ModelImpl;
import de.tum.pssif.transform.ModelMapper;
import de.tum.pssif.transform.graph.AElement;
import de.tum.pssif.transform.graph.Edge;
import de.tum.pssif.transform.graph.Graph;
import de.tum.pssif.transform.graph.Node;


public class BpmnModelMapper implements ModelMapper {
  @Override
  public Model read(Metamodel metamodel, Graph graph) {
    Model result = new ModelImpl();

    createNodes(metamodel, graph, result);
    createEdges(metamodel, graph, result);

    return result;
  }

  @Override
  public Graph write(Metamodel metamodel, Model model) {
    // TODO Auto-generated method stub
    return null;
  }

  private void createEdges(Metamodel metamodel, Graph graph, Model result) {
    for (Edge e : graph.getEdges()) {
      String sourceTypeName = e.getSource().getType();
      String targetTypeName = e.getTarget().getType();

      PSSIFOption<NodeType> sourceType = metamodel.getNodeType(sourceTypeName);
      PSSIFOption<NodeType> targetType = metamodel.getNodeType(targetTypeName);
      if (sourceType.isOne() && targetType.isOne()) {
        if ("Organizational unit".equals(sourceTypeName)) {
          createEdge(result, e, sourceType.getOne(), targetType.getOne(), metamodel.getEdgeType("Performs").getOne(), false);
        }
        else if ("Organizational unit".equals(targetTypeName)) {
          createEdge(result, e, sourceType.getOne(), targetType.getOne(), metamodel.getEdgeType("Performs").getOne(), true);
        }
        else if ("Information/ Material".equals(sourceTypeName)) {
          createEdge(result, e, sourceType.getOne(), targetType.getOne(), metamodel.getEdgeType("Information Flow").getOne(), false);
        }
        else if ("Information/ Material".equals(targetTypeName)) {
          createEdge(result, e, sourceType.getOne(), targetType.getOne(), metamodel.getEdgeType("Information Flow").getOne(), true);
        }
        else {
          PSSIFOption<EdgeType> type = metamodel.getEdgeType(e.getType());

          if (type.isOne()) {
            createEdge(result, e, sourceType.getOne(), targetType.getOne(), type.getOne(), false);
          }
          else {
            System.out.println("missed " + e.getType());
          }
        }
      }
      else {
        System.out.println("missed source/target type of " + e + "(" + e.getSource().getType() + "," + e.getTarget().getType() + ")");
      }
    }
  }

  private void createEdge(Model result, Edge e, NodeType sourceType, NodeType targetType, EdgeType type, boolean swap) {
    PSSIFOption<ConnectionMapping> mapping = type.getMapping(sourceType, targetType);
    PSSIFOption<de.tum.pssif.core.model.Node> source = sourceType.apply(result, e.getSource().getId(), true);
    PSSIFOption<de.tum.pssif.core.model.Node> target = targetType.apply(result, e.getTarget().getId(), true);
    if (source.isOne() && target.isOne() && mapping.isOne()) {
      de.tum.pssif.core.model.Edge edge = null;
      if (swap) {
        edge = mapping.getOne().create(result, target.getOne(), source.getOne());
      }
      else {
        edge = mapping.getOne().create(result, source.getOne(), target.getOne());
      }
      setAttributes(e, type, edge);
    }
    else {
      System.out.println("missed mapping for " + e.getType() + "(" + sourceType + "," + targetType + ")");
    }
  }

  private void setAttributes(AElement e, EdgeType type, de.tum.pssif.core.model.Edge edge) {
    Attribute id = type.getAttribute(PSSIFConstants.BUILTIN_ATTRIBUTE_ID).getOne();
    Attribute directed = type.getAttribute(PSSIFConstants.BUILTIN_ATTRIBUTE_DIRECTED).getOne();
    id.set(edge, PSSIFOption.one(PSSIFValue.create(e.getId())));
    directed.set(edge, PSSIFOption.one(PSSIFValue.create(Boolean.TRUE)));
  }

  private void createNodes(Metamodel metamodel, Graph graph, Model result) {
    for (Node n : graph.getNodes()) {
      PSSIFOption<NodeType> type = metamodel.getNodeType(n.getType());
      if (type.isOne()) {
        System.out.println("missed " + n.getType());
        continue;
      }
      Attribute id = type.getOne().getAttribute(PSSIFConstants.BUILTIN_ATTRIBUTE_ID).getOne();
      de.tum.pssif.core.model.Node node = type.getOne().create(result);
      id.set(node, PSSIFOption.one(PSSIFValue.create(n.getId())));
      for (String attrName : n.getAttributeNames()) {
        PSSIFOption<Attribute> attr = type.getOne().getAttribute(attrName);
        if (attr.isOne()) {
          attr.getOne().set(node, PSSIFOption.one(attr.getOne().getType().fromObject(n.getAttributeValue(attrName))));
        }
      }
    }
  }
}
