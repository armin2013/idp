package de.tum.pssif.transform.mapper.reqif;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import de.tum.pssif.core.common.PSSIFConstants;
import de.tum.pssif.core.common.PSSIFOption;
import de.tum.pssif.core.common.PSSIFValue;
import de.tum.pssif.core.metamodel.Attribute;
import de.tum.pssif.core.metamodel.ConnectionMapping;
import de.tum.pssif.core.metamodel.EdgeType;
import de.tum.pssif.core.metamodel.ElementType;
import de.tum.pssif.core.metamodel.Metamodel;
import de.tum.pssif.core.metamodel.NodeType;
import de.tum.pssif.core.metamodel.NodeTypeBase;
import de.tum.pssif.core.model.Edge;
import de.tum.pssif.core.model.Element;
import de.tum.pssif.core.model.Model;
import de.tum.pssif.core.model.Node;
import de.tum.pssif.core.model.impl.ModelImpl;
import de.tum.pssif.transform.Mapper;

public class ReqifMapper implements Mapper {

	@Override
	public Model read(Metamodel metamodel, InputStream inputStream) {
		return readInternal(metamodel, inputStream);
	}
	
	@Override
	public void write(Metamodel metamodel, Model model, OutputStream outputStream) {
		// TODO Auto-generated method stub
	}
	
	private Model readInternal(Metamodel metamodel, InputStream inputStream) {
		Model result = new ModelImpl();

	    ReqifGraph graph = ReqifGraph.read(inputStream);

	    for (ReqifNode inNode : graph.getNodes()) {
	      PSSIFOption<NodeTypeBase> type = metamodel.getBaseNodeType(inNode.getType());
	      if (type.isOne()) {
	        readNode(result, inNode, type.getOne());
	      }
	      else {
	        System.out.println("NodeType " + inNode.getType() + " not found! Defaulting to Node");
	        readNode(result, inNode, metamodel.getNodeType(PSSIFConstants.ROOT_NODE_TYPE_NAME).getOne());
	      }
	    }

	    for (ReqifEdge inEdge : graph.getEdges()) {
	      PSSIFOption<EdgeType>type = metamodel.getEdgeType(inEdge.getType());
	      if (type.isOne()) {
	        readEdge(metamodel, result, graph, inEdge, type.getOne());
	      }
	      else {
	        System.out.println("EdgeType " + inEdge.getType() + " not found! Defaulting to Edge");
	        readEdge(metamodel, result, graph, inEdge, metamodel.getEdgeType(PSSIFConstants.ROOT_EDGE_TYPE_NAME).getOne());
	      }
	    }

	    return result;
	}
	
	private void readEdge(Metamodel metamodel, Model result, ReqifGraph graph, ReqifEdge inEdge, EdgeType type) {
	    ReqifNode inSourceNode = graph.getNode(inEdge.getSourceId());
	    ReqifNode inTargetNode = graph.getNode(inEdge.getTargetId());

	    PSSIFOption<NodeTypeBase> sourceType = metamodel.getBaseNodeType(inSourceNode.getType());
	    if (sourceType.isNone()) {
	      sourceType = metamodel.getBaseNodeType(PSSIFConstants.ROOT_NODE_TYPE_NAME);
	    }
	    PSSIFOption<Node> sourceNode = sourceType.getOne().apply(result, inSourceNode.getId(), true);

	    PSSIFOption<NodeTypeBase> targetType = metamodel.getBaseNodeType(inTargetNode.getType());
	    if (targetType.isNone()) {
	      targetType = metamodel.getBaseNodeType(PSSIFConstants.ROOT_NODE_TYPE_NAME);
	    }
	    PSSIFOption<Node> targetNode = targetType.getOne().apply(result, inTargetNode.getId(), true);

	    PSSIFOption<ConnectionMapping> mapping = type.getMapping(sourceType.getOne(), targetType.getOne());
	    if (mapping.isNone()) {
	      System.out.println(type.getName() + ": mapping " + sourceType.getOne().getName() + "-" + targetType.getOne().getName() + " not found! Defaulting to Edge");
	      type = metamodel.getEdgeType(PSSIFConstants.ROOT_EDGE_TYPE_NAME).getOne();
	      mapping = type.getMapping(sourceType.getOne(), targetType.getOne());
	    }
	    if (!sourceNode.isOne()) {
	      System.out.println("source node " + inSourceNode.getId() + " not found!");
	    }
	    if (!targetNode.isOne()) {
	      System.out.println("target node " + inTargetNode.getId() + " not found!");
	    }
	    if (sourceNode.isOne() && targetNode.isOne() && mapping != null) {
	      Edge edge = mapping.getOne().create(result, sourceNode.getOne(), targetNode.getOne());
	      type.getAttribute(PSSIFConstants.BUILTIN_ATTRIBUTE_ID).getOne().set(edge, PSSIFOption.one(PSSIFValue.create(inEdge.getId())));
	      type.getAttribute(PSSIFConstants.BUILTIN_ATTRIBUTE_DIRECTED).getOne().set(edge, PSSIFOption.one(PSSIFValue.create(inEdge.isDirected())));
	      readAttributes(type, edge, inEdge);
	      
	    }
	  }

	  private void readNode(Model result, ReqifNode inNode, NodeTypeBase type) {
	    PSSIFOption<Attribute> idAttribute = type.getAttribute(PSSIFConstants.BUILTIN_ATTRIBUTE_ID);
	    Node resultNode = type.create(result);
	    if (idAttribute.isOne()) {
	      idAttribute.getOne().set(resultNode, PSSIFOption.one(idAttribute.getOne().getType().fromObject(inNode.getId())));
	    }
	    else {
	      System.out.println("Attribute " + PSSIFConstants.BUILTIN_ATTRIBUTE_ID + " not found!");
	    }

	    readAttributes(type, resultNode, inNode);
	  }
	  
	  private static void readAttributes(ElementType type, Element element, ReqifElement inElement) {
		    Map<String, String> values = inElement.getValues();
		    for (String key : values.keySet()) {
		      PSSIFOption<Attribute> attribute = type.getAttribute(key);
		      if (attribute.isOne()) {
		        attribute.getOne().set(element, PSSIFOption.one(attribute.getOne().getType().fromObject(values.get(key))));
		      }
		      else {
		        System.out.println("Attribute " + key + " not found!");
		      }
		    }
		  }

}
