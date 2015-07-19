package de.tum.pssif.transform.mapper.graphml;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import com.google.common.collect.Sets;

import de.tum.pssif.core.common.PSSIFConstants;
import de.tum.pssif.core.common.PSSIFOption;
import de.tum.pssif.core.common.PSSIFValue;
import de.tum.pssif.core.metamodel.Attribute;
import de.tum.pssif.core.metamodel.ConnectionMapping;
import de.tum.pssif.core.metamodel.EdgeType;
import de.tum.pssif.core.metamodel.ElementType;
import de.tum.pssif.core.metamodel.Metamodel;
import de.tum.pssif.core.metamodel.NodeTypeBase;
import de.tum.pssif.core.model.Edge;
import de.tum.pssif.core.model.Element;
import de.tum.pssif.core.model.Model;
import de.tum.pssif.core.model.Node;
import de.tum.pssif.core.model.impl.ModelImpl;
import de.tum.pssif.transform.Mapper;
import de.tum.pssif.transform.mapper.graphml.GraphMLGraph.GraphMlAttrImpl;
import de.tum.pssif.transform.mapper.graphml.GraphMLGraph.GraphMlEdgeImpl;
import de.tum.pssif.transform.mapper.graphml.GraphMLGraph.GraphMlNodeImpl;

public abstract class GraphMLMapper implements Mapper {
	protected final Model readInternal(Metamodel metamodel, InputStream in) {
		Model result = new ModelImpl();

		GraphMLGraph graph = GraphMLGraph.read(in);

		for (GraphMLNode inNode : graph.getNodes()) {
			PSSIFOption<NodeTypeBase> type = metamodel.getBaseNodeType(inNode
					.getType());
			if (type.isOne()) {
				readNode(result, inNode, type.getOne());
			} else {
				System.out.println("NodeType " + inNode.getType()
						+ " not found! Defaulting to Node");
				readNode(
						result,
						inNode,
						metamodel.getNodeType(
								PSSIFConstants.ROOT_NODE_TYPE_NAME).getOne());
			}
		}

		for (GraphMLEdge inEdge : graph.getEdges()) {
			PSSIFOption<EdgeType> type = metamodel
					.getEdgeType(inEdge.getType());
			if (type.isOne()) {
				readEdge(metamodel, result, graph, inEdge, type.getOne());
			} else {
				System.out.println("EdgeType " + inEdge.getType()
						+ " not found! Defaulting to Edge");
				readEdge(metamodel, result, graph, inEdge, metamodel
						.getEdgeType(PSSIFConstants.ROOT_EDGE_TYPE_NAME)
						.getOne());
			}
		}

		return result;
	}

	private void readEdge(Metamodel metamodel, Model result,
			GraphMLGraph graph, GraphMLEdge inEdge, EdgeType type) {
		GraphMLNode inSourceNode = graph.getNode(inEdge.getSourceId());
		GraphMLNode inTargetNode = graph.getNode(inEdge.getTargetId());

		PSSIFOption<NodeTypeBase> sourceType = metamodel
				.getBaseNodeType(inSourceNode.getType());
		if (sourceType.isNone()) {
			sourceType = metamodel
					.getBaseNodeType(PSSIFConstants.ROOT_NODE_TYPE_NAME);
		}
		PSSIFOption<Node> sourceNode = sourceType.getOne().apply(result,
				inSourceNode.getId(), true);

		PSSIFOption<NodeTypeBase> targetType = metamodel
				.getBaseNodeType(inTargetNode.getType());
		if (targetType.isNone()) {
			targetType = metamodel
					.getBaseNodeType(PSSIFConstants.ROOT_NODE_TYPE_NAME);
		}
		PSSIFOption<Node> targetNode = targetType.getOne().apply(result,
				inTargetNode.getId(), true);

		PSSIFOption<ConnectionMapping> mapping = type.getMapping(
				sourceType.getOne(), targetType.getOne());
		if (mapping.isNone()) {
			System.out.println(type.getName() + ": mapping "
					+ sourceType.getOne().getName() + "-"
					+ targetType.getOne().getName()
					+ " not found! Defaulting to Edge");
			type = metamodel.getEdgeType(PSSIFConstants.ROOT_EDGE_TYPE_NAME)
					.getOne();
			mapping = type.getMapping(sourceType.getOne(), targetType.getOne());
		}
		if (!sourceNode.isOne()) {
			System.out.println("source node " + inSourceNode.getId()
					+ " not found!");
		}
		if (!targetNode.isOne()) {
			System.out.println("target node " + inTargetNode.getId()
					+ " not found!");
		}
		if (sourceNode.isOne() && targetNode.isOne() && mapping.isOne()) {
			Edge edge = mapping.getOne().create(result, sourceNode.getOne(),
					targetNode.getOne());
			type.getAttribute(PSSIFConstants.BUILTIN_ATTRIBUTE_ID)
					.getOne()
					.set(edge,
							PSSIFOption.one(PSSIFValue.create(inEdge.getId())));
			type.getAttribute(PSSIFConstants.BUILTIN_ATTRIBUTE_DIRECTED)
					.getOne()
					.set(edge,
							PSSIFOption.one(PSSIFValue.create(inEdge
									.isDirected())));
			readAttributes(type, edge, inEdge);
			readAnnotations(type, edge, inEdge);
		}
	}

	private void readNode(Model result, GraphMLNode inNode, NodeTypeBase type) {
		PSSIFOption<Attribute> idAttribute = type
				.getAttribute(PSSIFConstants.BUILTIN_ATTRIBUTE_ID);
		Node resultNode = type.create(result);
		if (idAttribute.isOne()) {
			idAttribute.getOne().set(
					resultNode,
					PSSIFOption.one(idAttribute.getOne().getType()
							.fromObject(inNode.getId())));
		} else {
			System.out.println("Attribute "
					+ PSSIFConstants.BUILTIN_ATTRIBUTE_ID + " not found!");
		}

		readAttributes(type, resultNode, inNode);
		readAnnotations(type, resultNode, inNode);

		PSSIFOption<Attribute> globalIdAttribute = type
				.getAttribute(PSSIFConstants.BUILTIN_ATTRIBUTE_GLOBAL_ID);

		// here the global id which is saved in the pssif file is overwritten by
		// a new unique id for the currently running application instance. This
		// prevents problems caused by loaded global ids.
		if (globalIdAttribute.isOne()) {
			globalIdAttribute.getOne().set(
					resultNode,
					PSSIFOption.one(PSSIFValue.create(UUID.randomUUID()
							.toString())));

		} else {
			System.out.println("Attribute "
					+ PSSIFConstants.BUILTIN_ATTRIBUTE_GLOBAL_ID
					+ " not found!");
		}
	}

	private static void readAttributes(ElementType type, Element element,
			GraphMLElement inElement) {
		Map<String, String> values = inElement.getValues();
		for (String key : values.keySet()) {
			PSSIFOption<Attribute> attribute = type.getAttribute(key);
			if (attribute.isOne()) {
				attribute.getOne().set(
						element,
						PSSIFOption.one(attribute.getOne().getType()
								.fromObject(values.get(key))));
			} else {
				System.out.println("Attribute " + key + " not found!");
			}
		}
	}

	private static void readAnnotations(ElementType type, Element element,
			GraphMLElement inElement) {
		Map<String, String> values = inElement.getAnnotations();
		for (String key : values.keySet()) {
			element.annotate(key, values.get(key));
		}
	}

	protected void writeInternal(Metamodel metamodel, Model model,
			OutputStream outputStream, boolean writeAnnotations) {
		GraphMLGraph graph = GraphMLGraph.create();
		GraphMlContext context = new GraphMlContext();
		for (NodeTypeBase nodeType : metamodel.getBaseNodeTypes()) {
			context = addNodesToGraph(graph, nodeType, model, context);
		}
		for (EdgeType edgeType : metamodel.getEdgeTypes()) {
			context = addEdgesToGraph(graph, edgeType, model, context);
		}
		context.addAttributesToGraph(graph);

		GraphMLGraph.write(graph, outputStream);
	}

	private GraphMlContext addNodesToGraph(GraphMLGraph graph,
			NodeTypeBase nodeType, Model model, GraphMlContext context) {
		Attribute idAttribute = nodeType.getAttribute(
				PSSIFConstants.BUILTIN_ATTRIBUTE_ID).getOne();
		context.registerNodeAttribute(new GraphMlAttrImpl(
				idAttribute.getName(), idAttribute.getType().getName()));
		for (Node pssifNode : nodeType.apply(model, false).getMany()) {
			GraphMlNodeImpl node = new GraphMlNodeImpl(id(idAttribute,
					pssifNode));
			node.setValue(GraphMLTokens.ELEMENT_TYPE, nodeType.getName());
			for (Attribute attr : nodeType.getAttributes()) {
				PSSIFOption<PSSIFValue> val = attr.get(pssifNode);
				context.registerNodeAttribute(new GraphMlAttrImpl(attr
						.getName(), attr.getType().getName()));
				if (!idAttribute.equals(attr) && val.isOne()) {
					node.setValue(attr.getName(), val.getOne().getValue()
							.toString());
				}
			}
			for (Entry<String, String> annotation : pssifNode.getAnnotations()
					.getMany()) {
				context.registerNodeAttribute(new GraphMlAttrImpl(annotation
						.getKey(), GraphMLTokens.STRING,
						GraphMLTokens.ANNOTATION));
				node.setAnnotations(annotation.getKey(), annotation.getValue());
			}
			graph.addNode(node);
		}
		return context;
	}

	private GraphMlContext addEdgesToGraph(GraphMLGraph graph,
			EdgeType edgeType, Model model, GraphMlContext context) {
		Attribute idAttribute = edgeType.getAttribute(
				PSSIFConstants.BUILTIN_ATTRIBUTE_ID).getOne();
		for (ConnectionMapping mapping : edgeType.getMappings().getMany()) {
			PSSIFOption<Edge> edges = mapping.apply(model);
			for (Edge pssifEdge : edges.getMany()) {
				Node sourceNode = mapping.applyFrom(pssifEdge);
				Node targetNode = mapping.applyTo(pssifEdge);
				GraphMlEdgeImpl edge = new GraphMlEdgeImpl(id(idAttribute,
						pssifEdge), id(idAttribute, sourceNode), id(
						idAttribute, targetNode), false);
				edge.setValue(GraphMLTokens.ELEMENT_TYPE, edgeType.getName());
				for (Attribute attr : edgeType.getAttributes()) {
					PSSIFOption<PSSIFValue> val = attr.get(pssifEdge);
					context.registerEdgeAttribute(new GraphMlAttrImpl(attr
							.getName(), attr.getType().getName()));
					if (!idAttribute.equals(attr) && val.isOne()) {
						edge.setValue(attr.getName(), val.getOne().getValue()
								.toString());
					}
				}
				for (Entry<String, String> annotation : pssifEdge
						.getAnnotations().getMany()) {
					context.registerEdgeAttribute(new GraphMlAttrImpl(
							annotation.getKey(), GraphMLTokens.STRING,
							GraphMLTokens.ANNOTATION));
					edge.setAnnotations(annotation.getKey(),
							annotation.getValue());
				}
				graph.addEdge(edge);
			}
		}
		return context;
	}

	private static String id(Attribute idAttribute, Element element) {
		return idAttribute.get(element).getOne().asString();
	}

	private static final class GraphMlContext {
		private Collection<GraphMlAttribute> nodeAttributes = Sets.newHashSet();
		private Collection<GraphMlAttribute> edgeAttributes = Sets.newHashSet();

		private void registerNodeAttribute(GraphMlAttribute attribute) {
			nodeAttributes.add(attribute);
		}

		private void registerEdgeAttribute(GraphMlAttribute attribute) {
			edgeAttributes.add(attribute);
		}

		private void addAttributesToGraph(GraphMLGraph graph) {
			graph.addNodeAttributes(nodeAttributes);
			graph.addEdgeAttributes(edgeAttributes);
		}
	}
}
