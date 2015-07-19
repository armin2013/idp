package de.tum.pssif.core.model.impl;

import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicLong;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import de.tum.pssif.core.common.PSSIFConstants;
import de.tum.pssif.core.common.PSSIFOption;
import de.tum.pssif.core.common.PSSIFValue;
import de.tum.pssif.core.metamodel.Attribute;
import de.tum.pssif.core.metamodel.NodeType;
import de.tum.pssif.core.metamodel.impl.CreateEdgeOperation;
import de.tum.pssif.core.metamodel.impl.CreateJunctionNodeOperation;
import de.tum.pssif.core.metamodel.impl.CreateNodeOperation;
import de.tum.pssif.core.metamodel.impl.ReadEdgesOperation;
import de.tum.pssif.core.metamodel.impl.ReadNodeOperation;
import de.tum.pssif.core.metamodel.impl.ReadNodesOperation;
import de.tum.pssif.core.metamodel.impl.RemoveNodeOperation;
import de.tum.pssif.core.model.Edge;
import de.tum.pssif.core.model.JunctionNode;
import de.tum.pssif.core.model.Model;
import de.tum.pssif.core.model.Node;

public class ModelImpl implements Model {
	private final AtomicLong idGenerator = new AtomicLong();
	private Multimap<String, Node> nodes = HashMultimap.create();
	private Multimap<ConnectionMappingSignature, Edge> edges = HashMultimap
			.create();

	@Override
	public Node apply(CreateNodeOperation op) {
		Node result = new NodeImpl(this);

		/**
		 * @author Andreas
		 */

		PSSIFOption<Attribute> globalIdAttribute = op.getType().getAttribute(
				PSSIFConstants.BUILTIN_ATTRIBUTE_GLOBAL_ID);

		globalIdAttribute.getOne()
				.set(result,
						PSSIFOption.one(PSSIFValue.create(UUID.randomUUID()
								.toString())));
		/**
		 * until here
		 */

		nodes.put(op.getType().getName(), result);

		return result;
	}

	@Override
	public Node apply(CreateJunctionNodeOperation op) {
		JunctionNode result = new JunctionNodeImpl(this);

		nodes.put(op.getType().getName(), result);
		return result;
	}

	@Override
	public Edge apply(CreateEdgeOperation op) {
		Edge result = new EdgeImpl(this, op.getFrom(), op.getTo());

		/**
		 * @author Andrea
		 */

		PSSIFOption<Attribute> globalIdAttribute = op.getType().getAttribute(
				PSSIFConstants.BUILTIN_ATTRIBUTE_GLOBAL_ID);

		globalIdAttribute.getOne()
				.set(result,
						PSSIFOption.one(PSSIFValue.create(UUID.randomUUID()
								.toString())));
		/**
		 * until here
		 */

		edges.put(new ConnectionMappingSignature(op.getMapping()), result);
		return result;
	}

	@Override
	public PSSIFOption<Node> apply(ReadNodesOperation op) {
		return PSSIFOption.many(nodes.get(op.getType().getName()));
	}

	@Override
	public PSSIFOption<Node> apply(ReadNodeOperation op) {
		for (Node n : nodes.get(op.getType().getName())) {
			if (n.getId().equals(op.getId())) {
				return PSSIFOption.one(n);
			}
		}
		return PSSIFOption.none();
	}

	@Override
	public PSSIFOption<Edge> apply(ReadEdgesOperation op) {
		for (ConnectionMappingSignature candidate : edges.keySet()) {
			if (candidate.isCompatibleWith(op.getMapping())) {
				return PSSIFOption.many(edges.get(candidate));
			}
		}
		return PSSIFOption.none();
	}

	@Override
	public boolean apply(RemoveNodeOperation op) {
		return nodes.remove(op.getType(), op.getNode());
	}

	@Override
	public synchronized String generateId() {
		return "pssif_artificial_id_" + idGenerator.getAndIncrement();
	}

	public boolean removeNode(NodeType type, Node node) {
		return nodes.remove(type.getName(), node);
	}

	@Override
	public void removeEdge(Edge edge) {
		Entry<ConnectionMappingSignature, Edge> remEdge = null;
		for (Entry<ConnectionMappingSignature, Edge> e : edges.entries()) {
			if (e.getValue().equals(edge)) {
				remEdge = e;
				break;
			}
		}
		if (remEdge != null) {
			edges.remove(remEdge.getKey(), remEdge.getValue());
		}
	}
}
