package de.tum.pssif.transform.model;

import java.util.Collection;

import de.tum.pssif.core.common.PSSIFConstants;
import de.tum.pssif.core.common.PSSIFOption;
import de.tum.pssif.core.common.PSSIFValue;
import de.tum.pssif.core.metamodel.Attribute;
import de.tum.pssif.core.metamodel.ConnectionMapping;
import de.tum.pssif.core.metamodel.EdgeType;
import de.tum.pssif.core.metamodel.Metamodel;
import de.tum.pssif.core.metamodel.NodeTypeBase;
import de.tum.pssif.core.model.Model;
import de.tum.pssif.core.model.impl.ModelImpl;
import de.tum.pssif.transform.ModelMapper;
import de.tum.pssif.transform.graph.AElement;
import de.tum.pssif.transform.graph.Edge;
import de.tum.pssif.transform.graph.Graph;
import de.tum.pssif.transform.graph.Node;


public class EpkModelMapper implements ModelMapper {
  @Override
  public Model read(Metamodel metamodel, Graph graph) {
    Model result = new ModelImpl();

    readNodes(metamodel, graph, result);
    readEdges(metamodel, graph, result);

    return result;
  }

  @Override
  public Graph write(Metamodel metamodel, Model model) {
    Graph result = new Graph();

    writeNodes(metamodel, result, model);
    writeEdges(metamodel, result, model);

    System.out.println(result);

    return result;
  }

  private void readEdges(Metamodel metamodel, Graph graph, Model model) {
    for (Edge e : graph.getEdges()) {
      String sourceTypeName = e.getSource().getType();
      String targetTypeName = e.getTarget().getType();

      PSSIFOption<NodeTypeBase> sourceType = metamodel.getBaseNodeType(sourceTypeName);
      PSSIFOption<NodeTypeBase> targetType = metamodel.getBaseNodeType(targetTypeName);
      if (sourceType.isOne() && targetType.isOne()) {
        PSSIFOption<EdgeType> type = metamodel.getEdgeType(e.getType());
        if (type.isOne()) {
          readEdge(model, e, sourceType.getOne(), targetType.getOne(), type.getOne(), false);
        }
        else {
          System.out.println("missed " + e.getType());
        }
      }
      else {
        System.out.println("missed source/target type of " + e + "(" + e.getSource().getType() + "," + e.getTarget().getType() + ")");
      }
    }
  }

  private void readEdge(Model model, Edge e, NodeTypeBase sourceType, NodeTypeBase targetType, EdgeType type, boolean swap) {
    PSSIFOption<ConnectionMapping> mapping = type.getMapping(sourceType, targetType);
    if (swap) {
      mapping = type.getMapping(targetType, sourceType);
    }
    PSSIFOption<de.tum.pssif.core.model.Node> source = sourceType.apply(model, e.getSource().getId(), true);
    PSSIFOption<de.tum.pssif.core.model.Node> target = targetType.apply(model, e.getTarget().getId(), true);
    if (source.isOne() && target.isOne() && mapping.isOne()) {
      de.tum.pssif.core.model.Edge edge = null;
      if (swap) {
        edge = mapping.getOne().create(model, target.getOne(), source.getOne());
      }
      else {
        edge = mapping.getOne().create(model, source.getOne(), target.getOne());
      }
      setAttributes(e, type, edge);
    }
    else {
      System.out.println("missed mapping for " + e.getType() + "(" + sourceType.getName() + "," + targetType.getName() + ")");
    }
  }

  private void setAttributes(AElement e, EdgeType type, de.tum.pssif.core.model.Edge edge) {
    Attribute id = type.getAttribute(PSSIFConstants.BUILTIN_ATTRIBUTE_ID).getOne();
    Attribute directed = type.getAttribute(PSSIFConstants.BUILTIN_ATTRIBUTE_DIRECTED).getOne();
    id.set(edge, PSSIFOption.one(PSSIFValue.create(e.getId())));
    directed.set(edge, PSSIFOption.one(PSSIFValue.create(Boolean.TRUE)));
  }

  private void readNodes(Metamodel metamodel, Graph graph, Model model) {
    for (Node n : graph.getNodes()) {
      PSSIFOption<NodeTypeBase> type = metamodel.getBaseNodeType(n.getType());
      if (type.isNone()) {
        System.out.println("missed " + n.getType());
        continue;
      }
      Attribute id = type.getOne().getAttribute(PSSIFConstants.BUILTIN_ATTRIBUTE_ID).getOne();
      de.tum.pssif.core.model.Node node = type.getOne().create(model);
      id.set(node, PSSIFOption.one(PSSIFValue.create(n.getId())));
      for (String attrName : n.getAttributeNames()) {
        PSSIFOption<Attribute> attr = type.getOne().getAttribute(attrName);
        if (attr.isOne()) {
          attr.getOne().set(node, PSSIFOption.one(attr.getOne().getType().fromObject(n.getAttributeValue(attrName))));
        }
      }
    }
  }

  private void writeNodes(Metamodel metamodel, Graph graph, Model model) {
    for (NodeTypeBase ntb : metamodel.getBaseNodeTypes()) {
      Collection<Attribute> attributes = ntb.getAttributes();
      PSSIFOption<de.tum.pssif.core.model.Node> nodes = ntb.apply(model, false);
      Attribute id = ntb.getAttribute(PSSIFConstants.BUILTIN_ATTRIBUTE_ID).getOne();
      for (de.tum.pssif.core.model.Node node : nodes.getMany()) {
        Node n = graph.createNode(id.get(node).getOne().asString());
        n.setType(ntb.getName());
        for (Attribute a : attributes) {
          PSSIFOption<PSSIFValue> value = a.get(node);
          if (value.isOne()) {
            n.setAttribute(a.getName(), a.getType().toString(value.getOne()));
          }
        }
      }
    }
  }

  private void writeEdges(Metamodel metamodel, Graph graph, Model model) {
    for (EdgeType et : metamodel.getEdgeTypes()) {
      Attribute idAttribute = et.getAttribute(PSSIFConstants.BUILTIN_ATTRIBUTE_ID).getOne();
      PSSIFOption<de.tum.pssif.core.model.Edge> edges = et.apply(model, false);
      for (de.tum.pssif.core.model.Edge e : edges.getMany()) {
        de.tum.pssif.core.model.Node from = et.applyFrom(e);
        de.tum.pssif.core.model.Node to = et.applyTo(e);
        Edge edge = graph.createEdge(idAttribute.get(e).getOne().asString());
        edge.setType(et.getName());
        graph.connect(graph.findNode(from.getId()), edge, graph.findNode(to.getId()));
      }
    }
  }
}
