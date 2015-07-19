package de.tum.pssif.core.model;

import com.google.common.collect.Multimap;

import de.tum.pssif.core.common.PSSIFOption;
import de.tum.pssif.core.metamodel.EdgeType;
import de.tum.pssif.core.metamodel.NodeType;
import de.tum.pssif.core.metamodel.impl.CreateEdgeOperation;
import de.tum.pssif.core.metamodel.impl.CreateJunctionNodeOperation;
import de.tum.pssif.core.metamodel.impl.CreateNodeOperation;
import de.tum.pssif.core.metamodel.impl.ReadEdgesOperation;
import de.tum.pssif.core.metamodel.impl.ReadNodeOperation;
import de.tum.pssif.core.metamodel.impl.ReadNodesOperation;
import de.tum.pssif.core.metamodel.impl.RemoveNodeOperation;

public interface Model {
	/**
	 * Creates a node.
	 * 
	 * @param op
	 *            The node create operation supplied by the NodeType.
	 * @return The created node.
	 */
	Node apply(CreateNodeOperation op);

	/**
	 * Creates a junction node.
	 * 
	 * @param op
	 *            The node create operation supplied by the NodeType.
	 * @return The created node.
	 */
	Node apply(CreateJunctionNodeOperation op);

	/**
	 * Creates an edge.
	 * 
	 * @param op
	 *            The edge create operation supplied by the Connection Mapping
	 *            of an Edge Type.
	 * @return The created edge.
	 */
	Edge apply(CreateEdgeOperation op);

	/**
	 * Retrieves a collection of nodes.
	 * 
	 * @param op
	 *            The node read operation supplied by a Node Type.
	 * @return A PSS-IF Option containing the nodes.
	 */
	PSSIFOption<Node> apply(ReadNodesOperation op);

	/**
	 * Retrieves a specific node from the model
	 * 
	 * @param op
	 *            The node read operation supplied by a Node Type.
	 * @return A PSS-IF Option containing the node.
	 */
	PSSIFOption<Node> apply(ReadNodeOperation op);

	/**
	 * Retrieves a collection of nodes.
	 * 
	 * @param op
	 *            The node read operation supplied by a Node Type.
	 * @return A PSS-IF Option containing the nodes.
	 */
	PSSIFOption<Edge> apply(ReadEdgesOperation op);

	/**
	 * Removes a node.
	 * 
	 * @param op
	 *            The node is gone..
	 * @return True if removed.
	 */
	boolean apply(RemoveNodeOperation op);

	boolean removeNode(NodeType type, Node node);

	String generateId();

	void removeEdge(Edge edge);

}
