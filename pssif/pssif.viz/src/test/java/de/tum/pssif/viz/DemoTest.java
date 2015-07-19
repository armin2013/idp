package de.tum.pssif.viz;

import org.junit.Test;

import de.tum.pssif.core.common.PSSIFConstants;
import de.tum.pssif.core.common.PSSIFOption;
import de.tum.pssif.core.metamodel.Attribute;
import de.tum.pssif.core.metamodel.EdgeType;
import de.tum.pssif.core.metamodel.Metamodel;
import de.tum.pssif.core.metamodel.NodeType;
import de.tum.pssif.core.metamodel.external.PSSIFCanonicMetamodelCreator;
import de.tum.pssif.core.model.Model;
import de.tum.pssif.core.model.Node;
import de.tum.pssif.core.model.impl.ModelImpl;



public class DemoTest {

  private static Metamodel getMetamodel() {
    return PSSIFCanonicMetamodelCreator.create();
  }

  private static Model getModel() {
    //FIXME if need be, create some stuff in the model. see tests in core to find out how
    return new ModelImpl();
  }

  @Test
  public void testAllNodeTypes() {
    //This is how you obtain all node types
    getMetamodel().getNodeTypes();
    //for each of them you can do stuff
  }

  public void testAllNodesOfType() {
    //This is how you find a particular node type
    NodeType rootNodeType = getMetamodel().getNodeType(PSSIFConstants.ROOT_NODE_TYPE_NAME).getOne();
    //This is how you obtain all instance nodes of a given node type
    PSSIFOption<Node> nodes = rootNodeType.apply(getModel(), true);
    if (nodes.isNone()) {
      System.out.println("no nodes");
    }
    else if (nodes.isOne()) {
      System.out.println("one node");
    }
    else if (nodes.isMany()) {
      System.out.println("many nodes");
    }
    else {
      throw new RuntimeException("Never happens.");
    }
  }

  public void testAllEdgeTypes() {
    //This is how you obtain all edge types
    getMetamodel().getEdgeTypes();
    //for each of them you can do stuff
  }

  public void testAllAttributesOfANodeType() {
    //This is how you find a particular node type
    NodeType rootNodeType = getMetamodel().getNodeType(PSSIFConstants.ROOT_NODE_TYPE_NAME).getOne();
    //And this is how you get the attributes
    for (Attribute attribute : rootNodeType.getAttributes()) {
      System.out.println(attribute.getName());
    }
  }

  public void testAllAttributesOfAnEdgeType() {
    //This is how you find a particular edge type
    EdgeType rootEdgeType = getMetamodel().getEdgeType(PSSIFConstants.ROOT_EDGE_TYPE_NAME).getOne();
    //And this is how you get the attributes
    for (Attribute attribute : rootEdgeType.getAttributes()) {
      System.out.println(attribute.getName());
    }
  }

  public void testGetEdgeDirection() {
    //Note: this one won't run, because there is no information in the model
	  // TODO don't know how to fix this test....
/*
    //Get hold of a node type
    NodeType nodeType = getMetamodel().getNodeType(PSSIFConstants.ROOT_NODE_TYPE_NAME).getOne();

    //Get hold of an edge type
    
    EdgeType edgeType = nodeType.getOutgoings().iterator().next();

    //Get hold of a node (will throw an exception if there is more than one node of this type)
    Node node = nodeType.apply(getModel(), true).getOne();

    //Get hold of edges outgoing from the node
   
    PSSIFOption<Edge> outgoingEdges = edgeType.getIncoming().apply(node);

    //For each edge: retrieve its direction
    Attribute edgeDirection = edgeType.getAttribute(PSSIFConstants.BUILTIN_ATTRIBUTE_DIRECTED).getOne();
    for (Edge edge : outgoingEdges.getMany()) {
      System.out.println("direction: " + edgeDirection.get(edge));
    }*/

  }
}
