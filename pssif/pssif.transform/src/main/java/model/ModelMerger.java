package model;

import graph.model.MyEdgeType;
import graph.model.MyJunctionNodeType;
import graph.model.MyNode;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.pssif.consistencyDataStructures.NodeAndType;
import org.pssif.consistencyExceptions.ConsistencyException;
import org.pssif.mainProcesses.Methods;
import org.pssif.mergedDataStructures.MergedNodePair;

import de.tum.pssif.core.common.PSSIFOption;
import de.tum.pssif.core.common.PSSIFValue;
import de.tum.pssif.core.metamodel.Attribute;
import de.tum.pssif.core.metamodel.AttributeGroup;
import de.tum.pssif.core.metamodel.ConnectionMapping;
import de.tum.pssif.core.metamodel.EdgeType;
import de.tum.pssif.core.metamodel.JunctionNodeType;
import de.tum.pssif.core.metamodel.Metamodel;
import de.tum.pssif.core.metamodel.NodeType;
import de.tum.pssif.core.metamodel.NodeTypeBase;
import de.tum.pssif.core.metamodel.external.PSSIFCanonicMetamodelCreator;
import de.tum.pssif.core.model.Edge;
import de.tum.pssif.core.model.Model;
import de.tum.pssif.core.model.Node;
import de.tum.pssif.transform.mapper.db.PssifToDBMapperImpl;

/**
 * THis class is responsible for conduction the merge between two models based
 * on the given information which nodes/junctions shall be transferred to the
 * new model and where tracelinks shall be created. Furthermore it's checked
 * whether junction nodes and edges have to be transferred from the old model.
 * 
 * @author Andreas (partly Luc as annotated)
 */
public class ModelMerger {

	private Model modelOrigin;
	private Model modelNew;
	private Metamodel metaModelOrigin, metaModelNew;

	private HashMap<Node, Node> oldToNewNodes;

	/**
	 * this variable maps the unmatched nodes of the old model to the respective
	 * node in the new model
	 */
	private Map<NodeAndType, Node> nodeTransferUnmatchedOldToNewModel;
	/**
	 * this variable maps the traced nodes of the old model to the respective
	 * node in the new model
	 */
	private Map<NodeAndType, Node> nodeTransferTracedOldToNewModel;
	/**
	 * this variable maps the unmerged junctionnodes of the old model to the
	 * respective node in the new model
	 */
	private Map<NodeAndType, Node> nodeTransferunmatchedJunctionsOldToNewModel;

	/**
	 * This list contains all nodes that don't have to be tranferred from the
	 * old to the new model. It's needed to transfer the edges of the nodes from
	 * the old model.
	 */
	private List<MergedNodePair> mergedNodes;

	/**
	 * This list contains all nodes that need to be created in the new model
	 * (from the old model) and have to be linked to a node in the new model by
	 * a tracelink.
	 */
	private List<MergedNodePair> tracedNodes;

	/**
	 * this list contains all nodes from the original node that have no
	 * corresponding node in the new model but have to be transferred to the new
	 * model.
	 */
	private List<NodeAndType> unmatchedNodesOrigin;

	/**
	 * This list contains all junctions from the orignal node which have no
	 * corresponding junction in the new model but have to be transferred to the
	 * new model.
	 */
	private List<NodeAndType> unmatchedJunctionnodesOrigin;

	/**
	 * the first imported model
	 */
	MyModelContainer activeModel;

	/**
	 * the recently imported model
	 */
	MyModelContainer newModel;

	/**
	 * merge two models into one model in respect of the given metamodel
	 * 
	 * @param model1
	 *            first model
	 * @param modelNew
	 *            second model
	 * @param metaModelOrigin
	 *            metamodel
	 * @return the merged model
	 * @Author Luc
	 */
	public Model mergeModels(Model model1, Model model2, Metamodel meta) {
		this.modelOrigin = model1;
		this.modelNew = model2;
		this.metaModelOrigin = meta;
		this.oldToNewNodes = new HashMap<Node, Node>();

		// printNbEdges(model1);
		// printNbNodes(model1);
		// start transformation operations
		addAllNodes();
		addAllJunctionNodes();
		addAllEdges();

		// printNbEdges(model1);
		// printNbNodes(model1);

		return this.modelOrigin;
	}

	/**
	 * This method starts the real merge between two given models.
	 * 
	 * @param modelOrigin
	 *            the first imported model
	 * @param modelNew
	 *            the recent imported model
	 * @param activeModel
	 *            the first imported model as a container
	 * @param metaModelOrigin
	 *            the meta model of the first model
	 * @return the recently imported model with the old model appropriately
	 *         merged into it
	 * @author Andreas
	 * @param unmatchedNodesOrigin
	 *            the nodes of the first model which have no trace/merge partner
	 *            in the recent model
	 * @param unmatchedJunctionnodesOrigin
	 *            the junctionnodes of the firstmodel which don't appear in the
	 *            new model
	 * @param tracedNodes
	 *            the nodes of the first model which have a trace partner in the
	 *            recent model
	 */
	public Model mergeModels(Model modelOrigin, Model modelNew,
			Metamodel metaModelOriginal, Metamodel metaModelNew,
			List<MergedNodePair> mergedNodes,
			List<NodeAndType> unmatchedNodesOrigin,
			List<NodeAndType> unmatchedJunctionnodesOrigin,
			List<MergedNodePair> tracedNodes, MyModelContainer activeModel) {
		this.modelOrigin = modelOrigin;
		this.modelNew = modelNew;
		this.metaModelOrigin = metaModelOriginal;
		this.metaModelNew = metaModelNew;
		this.mergedNodes = mergedNodes;
		this.unmatchedNodesOrigin = unmatchedNodesOrigin;
		this.activeModel = activeModel;
		this.tracedNodes = tracedNodes;
		this.unmatchedJunctionnodesOrigin = unmatchedJunctionnodesOrigin;

		nodeTransferUnmatchedOldToNewModel = new HashMap<NodeAndType, Node>();
		nodeTransferTracedOldToNewModel = new HashMap<NodeAndType, Node>();
		nodeTransferunmatchedJunctionsOldToNewModel = new HashMap<NodeAndType, Node>();

		// If there is a merge, delete DB and save the new model
		PssifToDBMapperImpl.merge = true;

		return merge();
	}

	/**
	 * This method adds every node and junctionnode together with their edges
	 * from the origin model which is not marked as to be merged to the new
	 * model.
	 * 
	 * The nodes which are marked as to be merged are deleted and not transfered
	 * to the new model but their edges are transfered to the new model.
	 * 
	 * Furthermore the different versions of nodes are copied to the new model
	 * and tracelinks between different versions of nodes are set.
	 * 
	 * @return the merged model
	 * @author Andreas
	 */
	private Model merge() {

		newModel = new MyModelContainer(modelNew, metaModelNew);

		transferUnmatchedNodesToNewModel();

		transferTracedNodesToNewModel();

		transferUnmatchedJunctionnodesToNewModel();

		transferJunctionEdges();

		transferOldEdges();

		createTracelinks();

		return newModel.getModel();
	}

	/**
	 * transferring the junction nodes from the origin model which don't appear
	 * in the new model to the new model
	 * 
	 * @author Andreas
	 */
	@SuppressWarnings("unused")
	private void transferUnmatchedJunctionnodesToNewModel() {
		for (NodeAndType actJunctionnode : unmatchedJunctionnodesOrigin) {
			Node newJunctionnode = addJunctionNodeToNewModel(
					actJunctionnode.getNode(),
					(JunctionNodeType) actJunctionnode.getType());

			if (actJunctionnode == null) {
				throw new ConsistencyException("The old junction node: "
						+ Methods.findName(actJunctionnode.getType(),
								actJunctionnode.getNode())
						+ " couln't be transferred/created in the new model.");
			} else {
				nodeTransferunmatchedJunctionsOldToNewModel.put(
						actJunctionnode, newJunctionnode);
			}

		}
	}

	/**
	 * transferring the old versions of nodes from the origin model which have a
	 * newer corresponding version in the new model to the new model so a
	 * tracelink can be created between the different versions
	 * 
	 * @author Andreas
	 */
	private void transferTracedNodesToNewModel() {
		for (MergedNodePair tracedPair : tracedNodes) {

			Node newNode = addNodeToNewModel(tracedPair.getNodeOriginalModel(),
					tracedPair.getTypeOriginModel());

			if (newNode == null) {
				throw new ConsistencyException("The old (to be traced) node: "
						+ Methods.findName(tracedPair.getTypeOriginModel(),
								tracedPair.getNodeOriginalModel())
						+ " couln't be transferred/created in the new model.");
			} else {
				nodeTransferTracedOldToNewModel.put(
						new NodeAndType(tracedPair.getNodeOriginalModel(),
								tracedPair.getTypeOriginModel()), newNode);
			}
		}
	}

	/**
	 * adding the nodes from the origin model which don't appear in the new
	 * model to the new model
	 * 
	 * @author Andreas
	 */
	private void transferUnmatchedNodesToNewModel() {
		for (NodeAndType unmergedNode : unmatchedNodesOrigin) {

			Node newNode = addNodeToNewModel(unmergedNode.getNode(),
					unmergedNode.getType());

			if (newNode == null) {
				throw new ConsistencyException("The old, unmatched node: "
						+ Methods.findName(unmergedNode.getType(),
								unmergedNode.getNode())
						+ " couln't be transferred/created in the new model.");
			} else {
				nodeTransferUnmatchedOldToNewModel.put(unmergedNode, newNode);
			}
		}
	}

	/**
	 * Here all edges of the transferred junctionnodes shall be recovered in the
	 * new model as they were in the original model.
	 * 
	 * Therefore this method checks for every junctionnode which was transferred
	 * to the new model if there is at least one merged node or copied
	 * node/junction to which the junctionnode can be connected. If at least one
	 * such node/junction is found an edge between the node/junction and the
	 * junctionnode is created.
	 * 
	 * @author Andreas
	 */
	private void transferJunctionEdges() {
		EdgeType controlFlow = metaModelOrigin.getEdgeType(
				PSSIFCanonicMetamodelCreator.TAGS.get("E_FLOW_CONTROL"))
				.getOne();

		for (NodeAndType actJunctionnode : unmatchedJunctionnodesOrigin) {

			Node newJunctionnode = null;
			MyJunctionNodeType newJunctionnodeType = null;

			NodeAndType tempFromEdgeNode = null, tempToEdgeNode = null;

			NodeAndType tempNodeOrigin = null;

			MyNode searchedFromNodeNew = null, searchedToNodeNew = null;

			Iterator<Entry<NodeAndType, Node>> iterator = nodeTransferunmatchedJunctionsOldToNewModel
					.entrySet().iterator();

			while (iterator.hasNext()) {
				Map.Entry<NodeAndType, Node> pairs = (Entry<NodeAndType, Node>) iterator
						.next();
				tempNodeOrigin = pairs.getKey();

				if (actJunctionnode.getNode().equals(tempNodeOrigin.getNode())) {
					newJunctionnode = pairs.getValue();
					newJunctionnodeType = new MyJunctionNodeType(
							(JunctionNodeType) pairs.getKey().getType());

				}
			}

			for (ConnectionMapping incomingMapping : controlFlow
					.getIncomingMappings(actJunctionnode.getType())) {
				for (Edge incomingEdge : incomingMapping
						.applyIncoming(actJunctionnode.getNode())) {
					tempFromEdgeNode = new NodeAndType(
							incomingMapping.applyFrom(incomingEdge),
							incomingMapping.getFrom());

					// handles the case that a junction node gets an incoming
					// edge from another junction node
					if (tempFromEdgeNode.getType().getName()
							.equals(actJunctionnode.getType().getName())) {

						Iterator<Entry<NodeAndType, Node>> temp = nodeTransferunmatchedJunctionsOldToNewModel
								.entrySet().iterator();

						while (temp.hasNext()) {
							Map.Entry<NodeAndType, Node> pairs = (Entry<NodeAndType, Node>) temp
									.next();
							tempNodeOrigin = pairs.getKey();

							if (tempNodeOrigin.getNode().equals(
									tempFromEdgeNode.getNode())) {
								// create the new edge in the new model
								Edge newEdge = incomingMapping.create(modelNew,
										pairs.getValue(), newJunctionnode);

								// transfer the attributes of the old to the new
								// edge
								transferEdgeAttributes(incomingEdge, newEdge,
										controlFlow);

								break;
							}
						}
						continue;
					}

					Iterator<Entry<NodeAndType, Node>> it = nodeTransferUnmatchedOldToNewModel
							.entrySet().iterator();

					while (it.hasNext()) {
						Map.Entry<NodeAndType, Node> pairs = (Entry<NodeAndType, Node>) it
								.next();
						tempNodeOrigin = pairs.getKey();

						// if this is true a from node of the unmatched junction
						// node was transferred to the new model. So we can
						// create a edge between this pair.
						if (Methods.findGlobalID(tempFromEdgeNode.getNode(),
								tempFromEdgeNode.getType()).equals(
								Methods.findGlobalID(tempNodeOrigin.getNode(),
										tempNodeOrigin.getType()))) {

							// here the two nodes which shall be connected by a
							// edge are retrieved in the new model
							for (MyNode tempNode : newModel.getAllNodes()) {
								if (Methods.findGlobalID(tempNode.getNode(),
										tempNode.getNodeType().getType())
										.equals(Methods.findGlobalID(
												pairs.getValue(),
												tempNodeOrigin.getType()))) {
									searchedFromNodeNew = tempNode;
								}
							}

							// create the new edge in the new model
							Edge newEdge = incomingMapping.create(modelNew,
									searchedFromNodeNew.getNode(),
									newJunctionnode);

							// transfer the attributes of the old to the new
							// edge
							transferEdgeAttributes(incomingEdge, newEdge,
									controlFlow);

							break;
						}
					}

					for (MergedNodePair actMerged : mergedNodes) {
						// if this is true a from node of the unmatched junction
						// node was merged into the new model. So we can
						// create a edge between this pair.
						if (Methods.findGlobalID(tempFromEdgeNode.getNode(),
								tempFromEdgeNode.getType()).equals(
								Methods.findGlobalID(
										actMerged.getNodeOriginalModel(),
										actMerged.getTypeOriginModel()))) {

							// here the two nodes which shall be connected by a
							// edge are retrieved in the new model
							for (MyNode tempNode : newModel.getAllNodes()) {
								if (Methods.findGlobalID(tempNode.getNode(),
										tempNode.getNodeType().getType())
										.equals(Methods.findGlobalID(
												actMerged.getNodeNewModel(),
												actMerged.getTypeNewModel()))) {
									searchedFromNodeNew = tempNode;
								}
							}

							// create the new edge in the new model
							Edge newEdge = incomingMapping.create(modelNew,
									searchedFromNodeNew.getNode(),
									newJunctionnode);

							// transfer the attributes of the old to the new
							// edge
							transferEdgeAttributes(incomingEdge, newEdge,
									controlFlow);

							break;
						}
					}

				}
			}

			for (ConnectionMapping outgoingMapping : controlFlow
					.getOutgoingMappings(actJunctionnode.getType())) {
				for (Edge outgoingEdge : outgoingMapping
						.applyOutgoing(actJunctionnode.getNode())) {

					tempToEdgeNode = new NodeAndType(
							outgoingMapping.applyTo(outgoingEdge),
							outgoingMapping.getTo());

					// handles the case that a junction node connects to another
					// junction node via an outgoing edge. This edge is already
					// created through the above for each iteration with the
					// ingoing mappings.
					if (tempFromEdgeNode.getType().getName()
							.equals(actJunctionnode.getType().getName())) {
						continue;
					}

					Iterator<Entry<NodeAndType, Node>> it = nodeTransferUnmatchedOldToNewModel
							.entrySet().iterator();

					while (it.hasNext()) {
						Map.Entry<NodeAndType, Node> pairs = (Entry<NodeAndType, Node>) it
								.next();
						tempNodeOrigin = pairs.getKey();

						// if this is true a to node of the unmatched junction
						// node was transferred to the new model. So we can
						// create a edge between this pair.
						if (Methods.findGlobalID(tempToEdgeNode.getNode(),
								tempToEdgeNode.getType()).equals(
								Methods.findGlobalID(tempNodeOrigin.getNode(),
										tempNodeOrigin.getType()))) {

							// here the two nodes which shall be connected by a
							// edge are retrieved in the new model
							for (MyNode tempNode : newModel.getAllNodes()) {
								if (Methods.findGlobalID(tempNode.getNode(),
										tempNode.getNodeType().getType())
										.equals(Methods.findGlobalID(
												pairs.getValue(),
												tempNodeOrigin.getType()))) {
									searchedToNodeNew = tempNode;
								}
							}

							// create the new edge in the new model
							Edge newEdge = outgoingMapping.create(modelNew,
									newJunctionnode,
									searchedToNodeNew.getNode());

							// transfer the attributes of the old to the new
							// edge
							transferEdgeAttributes(outgoingEdge, newEdge,
									controlFlow);

							break;
						}
					}

					for (MergedNodePair actMerged : mergedNodes) {
						// if this is true a to node of the unmatched junction
						// node was merged into the new model. So we can
						// create a edge between this pair.
						if (Methods.findGlobalID(tempToEdgeNode.getNode(),
								tempToEdgeNode.getType()).equals(
								Methods.findGlobalID(
										actMerged.getNodeOriginalModel(),
										actMerged.getTypeOriginModel()))) {

							// here the two nodes which shall be connected by a
							// edge are retrieved in the new model
							for (MyNode tempNode : newModel.getAllNodes()) {
								if (Methods.findGlobalID(tempNode.getNode(),
										tempNode.getNodeType().getType())
										.equals(Methods.findGlobalID(
												actMerged.getNodeNewModel(),
												actMerged.getTypeNewModel()))) {
									searchedToNodeNew = tempNode;
								}
							}

							// create the new edge in the new model
							Edge newEdge = outgoingMapping.create(modelNew,
									newJunctionnode,
									searchedToNodeNew.getNode());

							// transfer the attributes of the old to the new
							// edge
							transferEdgeAttributes(outgoingEdge, newEdge,
									controlFlow);

							break;
						}
					}
				}
			}
		}
	}

	/**
	 * Add a given Junctionnode to the new model and transfer the attributes and
	 * annotations of it.
	 * 
	 * @param dataNode
	 *            the model which should be transfered to the new model
	 * @param currentType
	 *            the type of the dataNode
	 * @return The newly created junction node object
	 * @author Andreas
	 */
	private Node addJunctionNodeToNewModel(Node dataNode,
			JunctionNodeType currentType) {
		// create Node
		Node newNode = currentType.create(modelNew);

		// transfer attribute groups
		Collection<AttributeGroup> attrgroups = currentType
				.getAttributeGroups();

		if (attrgroups != null) {
			for (AttributeGroup ag : attrgroups) {
				// transfer attribute values
				Collection<Attribute> attr = ag.getAttributes();

				for (Attribute a : attr) {
					PSSIFOption<PSSIFValue> attrvalue = a.get(dataNode);

					if (attrvalue != null) {
						currentType.getAttribute(a.getName()).getOne()
								.set(newNode, attrvalue);
					}
				}
			}
		}

		// transfer annotations

		PSSIFOption<Entry<String, String>> tmp = dataNode.getAnnotations();

		Set<Entry<String, String>> annotations = null;

		if (tmp != null && (tmp.isMany() || tmp.isOne())) {
			if (tmp.isMany())
				annotations = tmp.getMany();
			else {
				annotations = new HashSet<Entry<String, String>>();
				annotations.add(tmp.getOne());
			}
		}

		if (annotations != null) {
			for (Entry<String, String> a : annotations) {
				newNode.annotate(a.getKey(), a.getValue());
			}
		}

		this.newModel = new MyModelContainer(modelNew, newModel.getMetamodel());

		return newNode;
	}

	/**
	 * This method creates the tracelink edges in the new model between the
	 * nodepairs given in the traceNode list.
	 * 
	 * @author Andreas
	 */
	private void createTracelinks() {
		MyEdgeType edgeType = new MyEdgeType(metaModelNew.getEdgeType(
				PSSIFCanonicMetamodelCreator.TAGS
						.get("E_RELATIONSHIP_CHRONOLOGICAL_EVOLVES_TO"))
				.getOne(), 6);

		Iterator<Entry<NodeAndType, Node>> it = nodeTransferTracedOldToNewModel
				.entrySet().iterator();

		NodeAndType tempNodeOrigin;
		Node tempNodeTransferred;

		MyNode searchedFromNodeNew = null, searchedToNodeNew = null;

		// Iterate over all old nodes (which have a trace partner) transferred
		// to the new model
		while (it.hasNext()) {
			Map.Entry<NodeAndType, Node> pairs = (Entry<NodeAndType, Node>) it
					.next();
			tempNodeOrigin = pairs.getKey();
			tempNodeTransferred = pairs.getValue();

			for (MergedNodePair tracedPair : tracedNodes) {
				// here the tracepartner of the current node is searched
				if (Methods.findGlobalID(tempNodeOrigin.getNode(),
						tempNodeOrigin.getType()).equals(
						Methods.findGlobalID(tracedPair.getNodeOriginalModel(),
								tracedPair.getTypeOriginModel()))) {

					// here the two nodes which shall be connected by a
					// tracelink are retrieved in the new model
					for (MyNode tempNode : newModel.getAllNodes()) {
						if (Methods.findGlobalID(tempNode.getNode(),
								tempNode.getNodeType().getType()).equals(
								Methods.findGlobalID(pairs.getValue(),
										tempNodeOrigin.getType()))) {
							searchedFromNodeNew = tempNode;
						}
						if (Methods.findGlobalID(tempNode.getNode(),
								tempNode.getNodeType().getType()).equals(
								Methods.findGlobalID(
										tracedPair.getNodeNewModel(),
										tracedPair.getTypeNewModel()))) {
							searchedToNodeNew = tempNode;
						}
					}

					if (searchedToNodeNew == null) {
						throw new NullPointerException(
								"Error at tracelink generation: The To node: "
										+ Methods.findName(tracedPair
												.getTypeOriginModel(),
												tracedPair
														.getNodeOriginalModel())
										+ " should have been transferred to the new model but it couldn't be found in the new model.");
					}

					if (searchedFromNodeNew == null) {
						throw new NullPointerException(
								"Error at tracelink generation: The From node "
										+ Methods.findName(
												tempNodeOrigin.getType(),
												tempNodeOrigin.getNode())
										+ " should have been transferred to the new model. But it couldn't be found in the new model.");
					}

					// here the tracelink is generated
					newModel.addNewEdgeGUI(searchedFromNodeNew,
							searchedToNodeNew, edgeType, true);
				}
			}
		}
	}

	/**
	 * This method iterates over all nodes from the original model which have
	 * been transferred to the new model but have no merge nor tracepartner and
	 * calls methods to create the edges of every node as they were in the
	 * original model.
	 * 
	 * @author Andreas
	 */
	private void transferOldEdges() {
		Iterator<Entry<NodeAndType, Node>> it = nodeTransferUnmatchedOldToNewModel
				.entrySet().iterator();
		NodeAndType tempNodeOrigin;
		Node tempNodeTransferred;

		// Iterate over all old nodes transferred to the new model
		while (it.hasNext()) {
			Map.Entry<NodeAndType, Node> pairs = (Entry<NodeAndType, Node>) it
					.next();
			tempNodeOrigin = pairs.getKey();

			/**
			 * the old node in the new model
			 */
			tempNodeTransferred = pairs.getValue();

			checkForEdgesToTransfer(tempNodeTransferred,
					tempNodeOrigin.getType(), tempNodeOrigin.getNode(),
					tempNodeOrigin.getType());
		}
	}

	/**
	 * This method calls methods to check the incoming and outgoing edges of the
	 * given node node which was merged into the new model. So the sorrounding
	 * edges can be created in the new model.
	 * 
	 * @param nodeNew
	 *            the node in the new model to which the old edge shall link
	 * @param typeNodeNew
	 *            the type of nodeNew
	 * @param nodeOrigin
	 *            the node of the origin model from which the old edge shall
	 *            link
	 * @param typeNodeOrigin
	 *            the type of nodeOrigin
	 * @author Andreas
	 */
	private void checkForEdgesToTransfer(Node nodeNew,
			NodeTypeBase typeNodeNew, Node nodeOrigin,
			NodeTypeBase typeNodeOrigin) {

		checkIncomingEdges(nodeNew, typeNodeNew, nodeOrigin, typeNodeOrigin);
		checkOutgoingEdges(typeNodeOrigin, nodeOrigin, nodeNew, typeNodeNew);
	}

	/**
	 * This method gets every incoming edges of the given node in the original
	 * model. Then the edge is created in the new model, too, if the node at the
	 * other end of the edge is present in the new model. The edge is either
	 * created between the given node and an transferred node from the original
	 * node or an merged node in the new model.
	 * 
	 * @param nodeNew
	 *            the transferred node object
	 * @param nodeNewType
	 *            the type of the transferred node
	 * @param nodeOrigin
	 *            the node whichs edges are retrieved in the original mode
	 * @param nodeTypeOrigin
	 *            the type of the original node
	 * 
	 * @author Andreas
	 */
	private void checkIncomingEdges(Node nodeNew, NodeTypeBase nodeNewType,
			Node nodeOrigin, NodeTypeBase nodeTypeOrigin) {

		Node tempFromEdgeNode;
		NodeTypeBase tempFromEdgeNodeType;

		MyNode searchedFromNodeNew = null, searchedToNodeNew = null;

		NodeAndType tempNodeOrigin;

		// get all incoming edges here
		for (EdgeType edgeType : metaModelNew.getEdgeTypes()) {
			for (ConnectionMapping incomingMapping : edgeType
					.getIncomingMappings(nodeTypeOrigin)) {
				for (Edge incomingEdge : incomingMapping
						.applyIncoming(nodeOrigin)) {

					// // undirected edges have to be handled separateley. They
					// are
					// // only transferred in the method checkOutgoingEdges
					// because
					// // if these edges are transfered in both methods,
					// // checkIncoming and checkOutgoingEdges, the undirected
					// edge
					// // will appear twice in the new model

					tempFromEdgeNode = incomingMapping.applyFrom(incomingEdge);
					tempFromEdgeNodeType = incomingMapping.getFrom();

					// here a bug is fixed, that directed edges between
					// unmatched nodes double each time when a new model is
					// merged into the pssif fw. The problem is if a directed
					// edge is present between two unmatched nodes the edge
					// would appear twice in the merged model because the edge
					// is created once through the incoming and once through the
					// outgoing mapping
					// This problem is solved by ignoring the problematic edge
					// in the iteration of the incoming mappings and only
					// handling it in the iteration of the outgoing mappings.
					boolean outGoingMappingThere = false;

					for (ConnectionMapping outgoingMapping : edgeType
							.getOutgoingMappings(tempFromEdgeNodeType)) {
						for (Edge outgoingEdge : outgoingMapping
								.applyOutgoing(tempFromEdgeNode)) {
							if (outgoingMapping.applyTo(outgoingEdge).equals(
									nodeOrigin)) {
								for (NodeAndType unmatchedNode : unmatchedNodesOrigin) {
									if (unmatchedNode.getNode().equals(
											tempFromEdgeNode)) {
										outGoingMappingThere = true;
									}
								}
							}
						}
					}

					if (outGoingMappingThere) {
						continue;
					}

					// don't match conjunctions
					if (tempFromEdgeNodeType.getName().equals(
							PSSIFCanonicMetamodelCreator.TAGS
									.get("N_CONJUNCTION"))) {
						continue;
					}

					Iterator<Entry<NodeAndType, Node>> it = nodeTransferUnmatchedOldToNewModel
							.entrySet().iterator();

					// look up whether the from node of the old edge has also
					// been transfered to the new model. If this is the case the
					// edge is created between the two nodes in the new model
					while (it.hasNext()) {
						Map.Entry<NodeAndType, Node> pairs = (Entry<NodeAndType, Node>) it
								.next();
						tempNodeOrigin = pairs.getKey();

						if (Methods.findGlobalID(tempFromEdgeNode,
								tempFromEdgeNodeType).equals(
								Methods.findGlobalID(tempNodeOrigin.getNode(),
										tempNodeOrigin.getType()))) {

							// here the two nodes which shall be connected by a
							// edge are retrieved in the new model
							for (MyNode tempNode : newModel.getAllNodes()) {
								if (Methods.findGlobalID(tempNode.getNode(),
										tempNode.getNodeType().getType())
										.equals(Methods.findGlobalID(
												pairs.getValue(),
												tempNodeOrigin.getType()))) {
									searchedFromNodeNew = tempNode;
								}
								if (Methods.findGlobalID(tempNode.getNode(),
										tempNode.getNodeType().getType())
										.equals(Methods.findGlobalID(nodeNew,
												nodeNewType))) {
									searchedToNodeNew = tempNode;
								}
							}

							if (searchedFromNodeNew == null) {
								throw new NullPointerException(
										"Error at incoming edge transfer: The from node: "
												+ Methods.findName(
														tempFromEdgeNodeType,
														tempFromEdgeNode)
												+ " should have been transferred to the new model. But it couldn't be found in the new model.");
							}

							if (searchedToNodeNew == null) {
								throw new NullPointerException(
										"Error at incoming edge transfer: The to node "
												+ Methods.findName(
														nodeTypeOrigin,
														nodeOrigin)
												+ " should have been transferred to the new model. But it couldn't be found in the new model.");
							}

							// create the new edge in the new model
							Edge newEdge = incomingMapping.create(modelNew,
									searchedFromNodeNew.getNode(),
									searchedToNodeNew.getNode());

							// transfer the attributes of the old to the new
							// edge
							transferEdgeAttributes(incomingEdge, newEdge,
									edgeType);

							break;
						}
					}

					// if the from node has been deleted because it appears in
					// the recently imported model, too. Then the newer version
					// becomes the from node of the edge
					for (MergedNodePair actMerged : mergedNodes) {
						if (Methods.findGlobalID(tempFromEdgeNode,
								tempFromEdgeNodeType).equals(
								Methods.findGlobalID(
										actMerged.getNodeOriginalModel(),
										actMerged.getTypeOriginModel()))) {

							// here the two nodes which shall be connected by a
							// edge are retrieved in the new model
							for (MyNode tempNode : newModel.getAllNodes()) {
								if (Methods.findGlobalID(tempNode.getNode(),
										tempNode.getNodeType().getType())
										.equals(Methods.findGlobalID(
												actMerged.getNodeNewModel(),
												actMerged.getTypeNewModel()))) {
									searchedFromNodeNew = tempNode;
								}
								if (Methods.findGlobalID(tempNode.getNode(),
										tempNode.getNodeType().getType())
										.equals(Methods.findGlobalID(nodeNew,
												nodeNewType))) {
									searchedToNodeNew = tempNode;
								}
							}

							if (searchedFromNodeNew == null) {
								throw new NullPointerException(
										"Error at incoming edge transfer: The from node: "
												+ Methods.findName(
														actMerged
																.getTypeOriginModel(),
														actMerged
																.getNodeOriginalModel())
												+ " should be available as a new version in the new model but it couldn't be found in the new model.");
							}

							if (searchedToNodeNew == null) {
								throw new NullPointerException(
										"Error at incoming edge transfer: The to node "
												+ Methods.findName(
														nodeTypeOrigin,
														nodeOrigin)
												+ " should have been transferred to the new model. But it couldn't be found in the new model.");
							}

							// create the new edge in the new model
							Edge newEdge = incomingMapping.create(modelNew,
									searchedFromNodeNew.getNode(),
									searchedToNodeNew.getNode());

							// transfer the attributes of the old to the new
							// edge
							transferEdgeAttributes(incomingEdge, newEdge,
									edgeType);

							break;
						}
					}

				}
			}
		}
	}

	/**
	 * This method gets every outgoing edges of the given node in the original
	 * model. Then the edge is created in the new model, too, if the node at the
	 * other end of the edge is present in the new model. The edge is either
	 * created between the given node and an transferred node from the original
	 * node or an merged node in the new model.
	 * 
	 * @param nodeNew
	 *            the transferred node object
	 * @param nodeNewType
	 *            the type of the transferred node
	 * @param nodeOrigin
	 *            the node whichs edges are retrieved in the original mode
	 * @param nodeTypeOrigin
	 *            the type of the original node
	 * @author Andreas
	 */
	private void checkOutgoingEdges(NodeTypeBase nodeTypeOrigin,
			Node nodeOrigin, Node nodeNew, NodeTypeBase nodeNewType) {

		Node tempToEdgeNode;
		NodeTypeBase tempToEdgeNodeType;

		MyNode searchedFromNodeNew = null, searchedToNodeNew = null;

		NodeAndType tempNodeOrigin;

		// retrieving all outgoing edges here
		for (EdgeType edgeType : metaModelNew.getEdgeTypes()) {
			for (ConnectionMapping outgoingMapping : edgeType
					.getOutgoingMappings(nodeTypeOrigin)) {
				for (Edge outgoingEdge : outgoingMapping
						.applyOutgoing(nodeOrigin)) {

					tempToEdgeNode = outgoingMapping.applyTo(outgoingEdge);
					tempToEdgeNodeType = outgoingMapping.getTo();

					// don't match conjunctions
					if (tempToEdgeNodeType.getName().equals(
							PSSIFCanonicMetamodelCreator.TAGS
									.get("N_CONJUNCTION"))) {
						continue;
					}

					Iterator<Entry<NodeAndType, Node>> it = nodeTransferUnmatchedOldToNewModel
							.entrySet().iterator();

					// look up whether the to node of the old edge has also
					// been transfered to the new model. If this is the case the
					// edge is created between the two nodes in the new model
					while (it.hasNext()) {
						Map.Entry<NodeAndType, Node> pairs = (Entry<NodeAndType, Node>) it
								.next();
						tempNodeOrigin = pairs.getKey();

						if (Methods.findGlobalID(tempToEdgeNode,
								tempToEdgeNodeType).equals(
								Methods.findGlobalID(tempNodeOrigin.getNode(),
										tempNodeOrigin.getType()))) {

							// here the two nodes which shall be connected by a
							// edge are retrieved in the new model
							for (MyNode tempNode : newModel.getAllNodes()) {
								if (Methods.findGlobalID(tempNode.getNode(),
										tempNode.getNodeType().getType())
										.equals(Methods.findGlobalID(
												pairs.getValue(),
												tempNodeOrigin.getType()))) {
									searchedToNodeNew = tempNode;
								}
								if (Methods.findGlobalID(tempNode.getNode(),
										tempNode.getNodeType().getType())
										.equals(Methods.findGlobalID(nodeNew,
												nodeNewType))) {
									searchedFromNodeNew = tempNode;
								}
							}

							if (searchedToNodeNew == null) {
								throw new NullPointerException(
										"Error at outgoing edge transfer: The To node: "
												+ Methods.findName(
														tempToEdgeNodeType,
														tempToEdgeNode)
												+ " should have been transferred to the new model but it couldn't be found in the new model.");
							}

							if (searchedFromNodeNew == null) {
								throw new NullPointerException(
										"Error at outgoing edge transfer: The From node "
												+ Methods.findName(
														nodeTypeOrigin,
														nodeOrigin)
												+ " should have been transferred to the new model. But it couldn't be found in the new model.");
							}

							// create the new edge in the new model
							Edge newEdge = outgoingMapping.create(modelNew,
									searchedFromNodeNew.getNode(),
									searchedToNodeNew.getNode());

							// transfer the attributes of the old to the new
							// edge
							transferEdgeAttributes(outgoingEdge, newEdge,
									edgeType);

							break;
						}
					}

					// if the to node has been deleted because it appears in
					// the recently imported model, too. Then the newer version
					// becomes the to node of the edge
					for (MergedNodePair actMerged : mergedNodes) {
						if (Methods.findGlobalID(tempToEdgeNode,
								tempToEdgeNodeType).equals(
								Methods.findGlobalID(
										actMerged.getNodeOriginalModel(),
										actMerged.getTypeOriginModel()))) {

							// here the two nodes which shall be connected by a
							// edge are retrieved in the new model
							for (MyNode tempNode : newModel.getAllNodes()) {
								if (Methods.findGlobalID(tempNode.getNode(),
										tempNode.getNodeType().getType())
										.equals(Methods.findGlobalID(
												actMerged.getNodeNewModel(),
												actMerged.getTypeNewModel()))) {
									searchedToNodeNew = tempNode;
								}
								if (Methods.findGlobalID(tempNode.getNode(),
										tempNode.getNodeType().getType())
										.equals(Methods.findGlobalID(nodeNew,
												nodeNewType))) {
									searchedFromNodeNew = tempNode;
								}
							}

							if (searchedToNodeNew == null) {
								throw new NullPointerException(
										"Error at outgoing edge transfer: The To node: "
												+ Methods.findName(
														actMerged
																.getTypeOriginModel(),
														actMerged
																.getNodeOriginalModel())
												+ " should be available as a new version in the new model but it couldn't be found in the new model.");
							}

							if (searchedFromNodeNew == null) {
								throw new NullPointerException(
										"Error at outgoing edge transfer: The From node "
												+ Methods.findName(
														nodeTypeOrigin,
														nodeOrigin)
												+ " should have been transferred to the new model. But it couldn't be found in the new model.");
							}

							// create the new edge in the new model
							Edge newEdge = outgoingMapping.create(modelNew,
									searchedFromNodeNew.getNode(),
									searchedToNodeNew.getNode());

							// transfer the attributes of the old to the new
							// edge
							transferEdgeAttributes(outgoingEdge, newEdge,
									edgeType);

							break;
						}
					}

				}
			}
		}
	}

	/**
	 * add all the Nodes from modelNew to model1
	 * 
	 * @author Luc
	 */
	private void addAllNodes() {
		// loop over all Node types
		for (NodeType t : metaModelOrigin.getNodeTypes()) {
			// get all the Nodes of this type
			PSSIFOption<Node> tempNodes = t.apply(modelNew, false);

			if (tempNodes.isMany()) {
				Set<Node> many = tempNodes.getMany();
				for (Node n : many) {
					// copy it to model1
					addNode(n, t);
				}
			} else {
				if (tempNodes.isOne()) {
					Node current = tempNodes.getOne();
					// copy it to model1
					addNode(current, t);
				}
			}
		}
	}

	/**
	 * add all the JunctionNodes from modelNew to model1
	 * 
	 * @author Luc
	 */
	private void addAllJunctionNodes() {
		// loop over all JunctionNode types
		for (JunctionNodeType t : metaModelOrigin.getJunctionNodeTypes()) {
			// get all the Nodes of this type
			PSSIFOption<Node> tempNodes = t.apply(modelNew, false);

			if (tempNodes.isMany()) {
				Set<Node> many = tempNodes.getMany();
				for (Node n : many) {
					// copy it to model1
					addJunctionNode(n, t);
				}
			} else {
				if (tempNodes.isOne()) {
					Node current = tempNodes.getOne();
					// copy it to model1
					addJunctionNode(current, t);
				}
			}
		}
	}

	/**
	 * Add all the Edges from modelNew to model1
	 * 
	 * @author Luc
	 */
	private void addAllEdges() {
		for (EdgeType t : metaModelOrigin.getEdgeTypes()) {
			PSSIFOption<ConnectionMapping> tmp = t.getMappings();

			if (tmp != null && (tmp.isMany() || tmp.isOne())) {
				Set<ConnectionMapping> mappings;

				if (tmp.isMany())
					mappings = tmp.getMany();
				else {
					mappings = new HashSet<ConnectionMapping>();
					mappings.add(tmp.getOne());
				}

				for (ConnectionMapping mapping : mappings) {
					PSSIFOption<Edge> edges = mapping.apply(modelNew);
					if (edges.isMany()) {
						for (Edge e : edges.getMany()) {
							Node source = mapping.applyFrom(e);
							Node target = mapping.applyTo(e);

							Edge newEdge = mapping.create(modelOrigin,
									oldToNewNodes.get(source),
									oldToNewNodes.get(target));
							transferEdgeAttributes(e, newEdge, t);
						}
					} else if (edges.isOne()) {
						Edge e = edges.getOne();
						Node source = mapping.applyFrom(e);
						Node target = mapping.applyTo(e);

						Edge newEdge = mapping.create(modelOrigin,
								oldToNewNodes.get(source),
								oldToNewNodes.get(target));
						transferEdgeAttributes(e, newEdge, t);
					}
				}
			}
		}
	}

	/**
	 * Add a given Node to Model1
	 * 
	 * @param dataNode
	 *            the model which should be transfered to model1
	 * @param currentType
	 *            the type of the dataNode
	 * @Author Luc
	 */
	private void addNode(Node dataNode, NodeType currentType) {
		// create Node
		Node newNode = currentType.create(modelOrigin);

		oldToNewNodes.put(dataNode, newNode);

		// transfer attribute groups
		Collection<AttributeGroup> attrgroups = currentType
				.getAttributeGroups();

		if (attrgroups != null) {
			for (AttributeGroup ag : attrgroups) {
				// transfer attribute values
				Collection<Attribute> attr = ag.getAttributes();

				for (Attribute a : attr) {
					PSSIFOption<PSSIFValue> attrvalue = a.get(dataNode);

					// //don't transfer the global ID (otherwise causes problems
					// with the uniqueness of the ID)
					// if(a.getName().equals(PSSIFConstants.BUILTIN_ATTRIBUTE_GLOBAL_ID)){
					// continue;
					// }

					if (attrvalue != null) {
						currentType.getAttribute(a.getName()).getOne()
								.set(newNode, attrvalue);
					}
				}
			}
		}

		// transfer annotations

		PSSIFOption<Entry<String, String>> tmp = dataNode.getAnnotations();

		Set<Entry<String, String>> annotations = null;

		if (tmp != null && (tmp.isMany() || tmp.isOne())) {
			if (tmp.isMany())
				annotations = tmp.getMany();
			else {
				annotations = new HashSet<Entry<String, String>>();
				annotations.add(tmp.getOne());
			}
		}

		if (annotations != null) {
			for (Entry<String, String> a : annotations) {
				newNode.annotate(a.getKey(), a.getValue());
			}
		}
	}

	/**
	 * Add a given Node to the new model
	 * 
	 * @param dataNode
	 *            the node which should be transfered to the newmodel
	 * @param currentType
	 *            the type of the dataNode
	 * @author Andreas
	 */
	private Node addNodeToNewModel(Node dataNode, NodeTypeBase nodeTypeBase) {
		Model modelNew = newModel.getModel();

		// create Node
		Node newNode = nodeTypeBase.create(modelNew);

		// transfer attribute groups
		Collection<AttributeGroup> attrgroups = nodeTypeBase
				.getAttributeGroups();

		if (attrgroups != null) {
			for (AttributeGroup ag : attrgroups) {
				// transfer attribute values
				Collection<Attribute> attr = ag.getAttributes();

				for (Attribute a : attr) {

					// //don't transfer the global ID (otherwise causes problems
					// with the uniqueness of the ID)
					// if(a.getName().equals(PSSIFConstants.BUILTIN_ATTRIBUTE_GLOBAL_ID)){
					// continue;
					// }

					PSSIFOption<PSSIFValue> attrvalue = a.get(dataNode);

					if (attrvalue != null) {
						nodeTypeBase.getAttribute(a.getName()).getOne()
								.set(newNode, attrvalue);
					}
				}
			}
		}

		// transfer annotations

		PSSIFOption<Entry<String, String>> tmp = dataNode.getAnnotations();

		Set<Entry<String, String>> annotations = null;

		if (tmp != null && (tmp.isMany() || tmp.isOne())) {
			if (tmp.isMany())
				annotations = tmp.getMany();
			else {
				annotations = new HashSet<Entry<String, String>>();
				annotations.add(tmp.getOne());
			}
		}

		if (annotations != null) {
			for (Entry<String, String> a : annotations) {
				newNode.annotate(a.getKey(), a.getValue());
			}
		}

		this.newModel = new MyModelContainer(modelNew, newModel.getMetamodel());

		return newNode;
	}

	/**
	 * Add a given JunctionNode to Model1
	 * 
	 * @param dataNode
	 *            the model which should be transfered to model1
	 * @param currentType
	 *            the type of the dataNode
	 * @author Luc
	 */
	private void addJunctionNode(Node dataNode, JunctionNodeType currentType) {
		// create Node
		Node newNode = currentType.create(modelOrigin);

		oldToNewNodes.put(dataNode, newNode);

		// transfer attribute groups
		Collection<AttributeGroup> attrgroups = currentType
				.getAttributeGroups();

		if (attrgroups != null) {
			for (AttributeGroup ag : attrgroups) {
				// transfer attribute values
				Collection<Attribute> attr = ag.getAttributes();

				for (Attribute a : attr) {
					PSSIFOption<PSSIFValue> attrvalue = a.get(dataNode);

					if (attrvalue != null) {
						currentType.getAttribute(a.getName()).getOne()
								.set(newNode, attrvalue);
					}
				}
			}
		}

		// transfer annotations

		PSSIFOption<Entry<String, String>> tmp = dataNode.getAnnotations();

		Set<Entry<String, String>> annotations = null;

		if (tmp != null && (tmp.isMany() || tmp.isOne())) {
			if (tmp.isMany())
				annotations = tmp.getMany();
			else {
				annotations = new HashSet<Entry<String, String>>();
				annotations.add(tmp.getOne());
			}
		}

		if (annotations != null) {
			for (Entry<String, String> a : annotations) {
				newNode.annotate(a.getKey(), a.getValue());
			}
		}
	}

	/**
	 * transfer all the attributes and annotations from one Edge to the other
	 * 
	 * @param oldEdge
	 *            contains all the information which should be transfered
	 * @param newEdge
	 *            the edge which should get all the information
	 * @param type
	 *            the type of both edges
	 * @author Luc
	 */
	private void transferEdgeAttributes(Edge oldEdge, Edge newEdge,
			EdgeType type) {
		// transfer attribute groups
		Collection<AttributeGroup> attrgroups = type.getAttributeGroups();

		if (attrgroups != null) {
			for (AttributeGroup ag : attrgroups) {
				// transfer attribute values
				Collection<Attribute> attr = ag.getAttributes();

				for (Attribute a : attr) {
					PSSIFOption<PSSIFValue> attrvalue = a.get(oldEdge);

					if (attrvalue != null) {
						type.getAttribute(a.getName()).getOne()
								.set(newEdge, attrvalue);
					}
				}
			}
		}

		// transfer annotations

		PSSIFOption<Entry<String, String>> tmp = oldEdge.getAnnotations();

		Set<Entry<String, String>> annotations = null;

		if (tmp != null && (tmp.isMany() || tmp.isOne())) {
			if (tmp.isMany())
				annotations = tmp.getMany();
			else {
				annotations = new HashSet<Entry<String, String>>();
				annotations.add(tmp.getOne());
			}
		}

		if (annotations != null) {
			for (Entry<String, String> a : annotations) {
				newEdge.annotate(a.getKey(), a.getValue());
			}
		}
	}
}
