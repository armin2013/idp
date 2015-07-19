package model;

import graph.model.IMyNode;
import graph.model.MyEdge;
import graph.model.MyEdgeType;
import graph.model.MyEdgeTypes;
import graph.model.MyJunctionNode;
import graph.model.MyJunctionNodeType;
import graph.model.MyJunctionNodeTypes;
import graph.model.MyNode;
import graph.model.MyNodeType;
import graph.model.MyNodeTypes;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import de.tum.pssif.core.common.PSSIFConstants;
import de.tum.pssif.core.common.PSSIFOption;
import de.tum.pssif.core.common.PSSIFValue;
import de.tum.pssif.core.metamodel.ConnectionMapping;
import de.tum.pssif.core.metamodel.EdgeType;
import de.tum.pssif.core.metamodel.JunctionNodeType;
import de.tum.pssif.core.metamodel.Metamodel;
import de.tum.pssif.core.metamodel.NodeType;
import de.tum.pssif.core.model.Edge;
import de.tum.pssif.core.model.Model;
import de.tum.pssif.core.model.Node;
import de.tum.pssif.transform.mapper.db.PssifToDBMapperImpl;

/**
 * A container which holds a model and a metamodel The container provides also
 * all the information from the model and metamodel in a format which can be
 * displayed in a Graph or Matrix
 * 
 * @author Luc
 *
 */
public class MyModelContainer {

	private Model model;
	private Metamodel meta;
	private MyNodeTypes nodeTypes;
	private MyEdgeTypes edgeTypes;
	private MyJunctionNodeTypes junctionNodeTypes;
	private LinkedList<MyNode> nodes;
	private LinkedList<MyJunctionNode> junctionNodes;
	private LinkedList<MyEdge> edges;
	private static int newNodeIdCounter;
	private static int newEdgeIdCounter;

	public MyModelContainer(Model model, Metamodel meta) {
		if (model != null && meta != null) {
			this.model = model;
			this.meta = meta;

			nodes = new LinkedList<MyNode>();
			edges = new LinkedList<MyEdge>();
			junctionNodes = new LinkedList<MyJunctionNode>();

			createNodeTypes();
			createJunctionNodeTypes();
			createEdgeTypes();

			createNodes();
			createJunctionNodes();
			createEdges();

		} else {
			throw new NullPointerException("Metamodel or model null!");
		}
	}

	/**
	 * parse the metamodel and create all the NodeTypes
	 */
	private void createNodeTypes() {
		Collection<NodeType> types = meta.getNodeTypes();

		nodeTypes = new MyNodeTypes(types);
	}

	/**
	 * parse the metamodel and create all the EdgeTypes
	 */
	private void createEdgeTypes() {
		Collection<EdgeType> types = meta.getEdgeTypes();

		edgeTypes = new MyEdgeTypes(types);
	}

	/**
	 * parse the metamodel and create all the JunctionNodeTypes
	 */
	private void createJunctionNodeTypes() {
		Collection<JunctionNodeType> types = meta.getJunctionNodeTypes();

		junctionNodeTypes = new MyJunctionNodeTypes(types);
	}

	/**
	 * parse the model and create all the Nodes
	 */
	private void createNodes() {
		for (MyNodeType t : nodeTypes.getAllNodeTypes()) {
			PSSIFOption<Node> tempNodes = t.getType().apply(model, false);

			if (tempNodes.isMany()) {
				for (Node tempNode : tempNodes.getMany()) {
					MyNode newNode = new MyNode(tempNode, t);
					PssifToDBMapperImpl.newNodes.add(newNode);
					nodes.add(newNode);
				}
			}
			if (tempNodes.isOne()) {
				MyNode newNode = new MyNode(tempNodes.getOne(), t);
				PssifToDBMapperImpl.newNodes.add(newNode);
				nodes.add(newNode);
			}

		}
	}

	/**
	 * parse the model and create all the JunctionNodes
	 */
	private void createJunctionNodes() {
		for (MyJunctionNodeType t : junctionNodeTypes.getAllJunctionNodeTypes()) {
			PSSIFOption<Node> tempNodes = t.getType().apply(model, false);

			if (tempNodes.isMany()) {
				for (Node tempNode : tempNodes.getMany()) {
					MyJunctionNode newNode = new MyJunctionNode(tempNode, t);
					PssifToDBMapperImpl.newJunctionNodes.add(newNode);
					junctionNodes.add(newNode);
				}
			}
			if (tempNodes.isOne()) {
				MyJunctionNode newNode = new MyJunctionNode(tempNodes.getOne(),
						t);
				PssifToDBMapperImpl.newJunctionNodes.add(newNode);
				junctionNodes.add(newNode);
			}

		}
	}

	/**
	 * create all the Edges which are contained in the Model
	 */
	private void createEdges() {
		for (MyEdgeType t : edgeTypes.getAllEdgeTypes()) {

			PSSIFOption<ConnectionMapping> tmp = t.getType().getMappings();
			if (tmp != null && (tmp.isMany() || tmp.isOne())) {
				Set<ConnectionMapping> mappings = new HashSet<ConnectionMapping>();
				if (tmp.isMany())
					mappings = tmp.getMany();
				else if (tmp.isOne()) {
					mappings.add(tmp.getOne());
				}

				for (ConnectionMapping mapping : mappings) {

					PSSIFOption<Edge> edges = mapping.apply(model);
					if (edges.isMany()) {
						for (Edge e : edges.getMany()) {
							Node source = mapping.applyFrom(e);
							Node target = mapping.applyTo(e);
							addEdge(new MyEdge(e, t, findNode(source),
									findNode(target)));
						}
					}

					if (edges.isOne()) {
						Edge e = edges.getOne();

						Node source = mapping.applyFrom(e);
						Node target = mapping.applyTo(e);
						addEdge(new MyEdge(e, t, findNode(source),
								findNode(target)));
					}
				}

			}
		}
	}

	/**
	 * Add a MyNode. Should not be used for MyNodes which where created via the
	 * Gui. Is not added to the Model!!!
	 * 
	 * @param node
	 *            the MyNode in context
	 */
	public void addNode(MyNode node) {
		if (!isContained(node)) {
			PssifToDBMapperImpl.newNodes.add(node);
			nodes.add(node);
		}
	}

	/**
	 * Add a MyEdge. Should not be used for MyEdges which where created via the
	 * Gui. Is not added to the Model!!!
	 * 
	 * @param edge
	 *            the MyEdge in context
	 */
	public void addEdge(MyEdge edge) {
		if (!isContained(edge)) {
			PssifToDBMapperImpl.newEdges.add(edge);
			edges.add(edge);
		}
	}

	/**
	 * get all Nodes from the Model
	 * 
	 * @return List with the Nodes
	 */
	public LinkedList<MyNode> getAllNodes() {
		return nodes;
	}

	/**
	 * get all Edges from the Model
	 * 
	 * @return List with the Edges
	 */
	public LinkedList<MyEdge> getAllEdges() {
		return edges;
	}

	/**
	 * check if the given MyNode is already contained in the model
	 * 
	 * @param node
	 *            the MyNode in context
	 * @return true if it is contained, false otherwise
	 */
	public boolean isContained(MyNode node) {
		return nodes.contains(node);
	}

	/**
	 * check if the given MyEdge is already contained in the model
	 * 
	 * @param node
	 *            the MyEdge in context
	 * @return true if it is contained, false otherwise
	 */
	public boolean isContained(MyEdge edge) {
		return edges.contains(edge);
	}

	public boolean isEmpty() {
		return nodes.size() + junctionNodes.size() + edges.size() == 0;
	}

	/**
	 * find the MyNode object which owns the given Node
	 * 
	 * @param n
	 *            the Node in context
	 * @return the MyNode object or null if no MyNode object exists for the
	 *         given Node
	 */
	public IMyNode findNode(Node n) {
		for (MyNode current : nodes) {
			if (current.getNode().equals(n)) {
				return current;
			}
		}

		for (MyJunctionNode current : junctionNodes) {
			if (current.getNode().equals(n)) {
				return current;
			}
		}

		return null;
	}

	/**
	 * find the MyJunctionNode object which owns the given Node
	 * 
	 * @param n
	 *            the Node in context
	 * @return the MyJunctionNode object or null if no MyNode object exists for
	 *         the given Node
	 */
	/*
	 * public MyJunctionNode findJunctionNode(Node n) { for (MyJunctionNode
	 * current : junctionNodes) { if (current.getNode().equals(n)) { return
	 * current; } }
	 * 
	 * return null; }
	 */

	/**
	 * find the MyEdge object which owns the given Edge
	 * 
	 * @param e
	 *            the Edge in context
	 * @return the MyEdge object or null if no MyEdge object exists for the
	 *         given Edge
	 */
	public MyEdge findEdge(Edge e) {
		for (MyEdge current : edges) {
			if (current.getEdge().equals(e)) {
				return current;
			}
		}

		return null;
	}

	/**
	 * get all Node Types from the Model
	 * 
	 * @return the NodeTypes object
	 */
	public MyNodeTypes getNodeTypes() {
		return nodeTypes;
	}

	/**
	 * get all Edge Types from the Model
	 * 
	 * @return the EdgeTypes object
	 */
	public MyEdgeTypes getEdgeTypes() {
		return edgeTypes;
	}

	/**
	 * add an Edge which is added during a collapse operation
	 * 
	 * @param newEdge
	 *            the new Edge
	 */
	public void addCollapserEdge(MyEdge newEdge) {
		newEdge.setCollapseEdge(true);
		PssifToDBMapperImpl.newEdges.add(newEdge);
		edges.add(newEdge);
	}

	/**
	 * remove an Edge which was added during a collapse operation
	 * 
	 * @param edge
	 *            the edge in question
	 */
	public void removeCollapserEdge(MyEdge edge) {
		if (edge.isCollapseEdge()) {
			PssifToDBMapperImpl.deletedEdges.add(edge);
			edges.remove(edge);
		}
	}

	/**
	 * Add a new Node which was created through the Gui
	 * 
	 * @param nodeName
	 *            The name of the new Node
	 * @param type
	 *            The type of the new Node
	 */
	public MyNode addNewNodeFromGUI(String nodeName, MyNodeType type) {
		NodeType nodeType = meta.getNodeType(type.getName()).getOne();

		Node newNode = nodeType.create(model);

		nodeType.getAttribute(PSSIFConstants.BUILTIN_ATTRIBUTE_NAME).getOne()
				.set(newNode, PSSIFOption.one(PSSIFValue.create(nodeName)));
		nodeType.getAttribute(PSSIFConstants.BUILTIN_ATTRIBUTE_ID)
				.getOne()
				.set(newNode,
						PSSIFOption.one(PSSIFValue.create(nodeName
								+ newNodeIdCounter++)));
		MyNode newMyNode = new MyNode(newNode, type);
		PssifToDBMapperImpl.newNodes.add(newMyNode);
		nodes.add(newMyNode);
		return newMyNode;
	}

	/**
	 * Remove a node from Gui
	 * 
	 * @param nodeName
	 *            The Node
	 */
	public boolean removeNodeFromGUI(MyNode node) {
		List<MyEdge> remEdges = new LinkedList<MyEdge>();
		for (MyEdge edge : edges) {
			if (edge.getSourceNode().equals(node)
					|| edge.getDestinationNode().equals(node)) {
				remEdges.add(edge);
			}
		}
		for (MyEdge edge : remEdges) {
			removeEdgeGUI(edge);
		}

		if (model != null) {
			model.removeNode(node.getNodeType().getType(), node.getNode());

		}
		PssifToDBMapperImpl.deletedNodes.add(node);
		return nodes.remove(node);
	}

	/**
	 * Add a new Edge which was created from the Gui
	 * 
	 * @param source
	 *            The start Node of the Edge
	 * @param destination
	 *            The destination Node of the Edge
	 * @param edgetype
	 *            The type of the Edge
	 * @param directed
	 *            should the new edge be directed
	 * @return true if the add operation was successful, false otherwise
	 */
	public boolean addNewEdgeGUI(MyNode source, MyNode destination,
			MyEdgeType edgetype, Boolean directed) {
		ConnectionMapping mapping = edgetype
				.getType()
				.getMapping(source.getNodeType().getType(),
						destination.getNodeType().getType()).getOne();

		if (mapping != null) {
			Edge newEdge = mapping.create(model, source.getNode(),
					destination.getNode());

			PSSIFOption<PSSIFValue> value = PSSIFOption.one(PSSIFValue
					.create(directed));
			edgetype.getType()
					.getAttribute(PSSIFConstants.BUILTIN_ATTRIBUTE_DIRECTED)
					.getOne().set(newEdge, value);
			PSSIFOption<PSSIFValue> id = PSSIFOption.one(PSSIFValue
					.create("egde" + newEdgeIdCounter++));
			edgetype.getType()
					.getAttribute(PSSIFConstants.BUILTIN_ATTRIBUTE_ID).getOne()
					.set(newEdge, id);

			MyEdge e = new MyEdge(newEdge, edgetype, source, destination);

			System.out
					.println(source.getRealName() + destination.getRealName());

			PssifToDBMapperImpl.newEdges.add(e);
			edges.add(e);
			return true;
		} else {
			return false;
		}
	}

	public Edge addNewEdge(MyNode source, MyNode destination,
			MyEdgeType edgeType, ConnectionMapping mapping, Boolean directed) {

		Edge newEdge = mapping.create(model, source.getNode(),
				destination.getNode());

		PSSIFOption<PSSIFValue> value = PSSIFOption.one(PSSIFValue
				.create(directed));
		edgeType.getType()
				.getAttribute(PSSIFConstants.BUILTIN_ATTRIBUTE_DIRECTED)
				.getOne().set(newEdge, value);
		PSSIFOption<PSSIFValue> id = PSSIFOption.one(PSSIFValue.create("egde"
				+ newEdgeIdCounter++));
		edgeType.getType().getAttribute(PSSIFConstants.BUILTIN_ATTRIBUTE_ID)
				.getOne().set(newEdge, id);

		MyEdge e = new MyEdge(newEdge, edgeType, source, destination);

		System.out.println(source.getRealName() + destination.getRealName());

		PssifToDBMapperImpl.newEdges.add(e);
		edges.add(e);
		return newEdge;

	}

	/**
	 * Remove Edge from the Gui
	 * 
	 * @param edge
	 *            The Edge which should be removed
	 */
	public void removeEdgeGUI(MyEdge edge) {
		PssifToDBMapperImpl.deletedEdges.add(edge);
		edges.remove(edge);
		model.removeEdge(edge.getEdge());
	}

	public Model getModel() {
		return model;
	}

	public Metamodel getMetamodel() {
		return meta;
	}

	public MyJunctionNodeTypes getJunctionNodeTypes() {
		return junctionNodeTypes;
	}

	public LinkedList<MyJunctionNode> getAllJunctionNodes() {
		return junctionNodes;
	}
}
