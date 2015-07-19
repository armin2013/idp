package de.tum.pssif.core.metamodel.impl;

import de.tum.pssif.core.metamodel.ConnectionMapping;
import de.tum.pssif.core.metamodel.EdgeType;
import de.tum.pssif.core.model.Edge;
import de.tum.pssif.core.model.Model;
import de.tum.pssif.core.model.Node;

public final class CreateEdgeOperation {
	private final ConnectionMapping mapping;
	private final Node from;
	private final Node to;

	/* package */CreateEdgeOperation(ConnectionMapping mapping, Node from,
			Node to) {
		this.mapping = mapping;
		this.from = from;
		this.to = to;
	}

	public ConnectionMapping getMapping() {
		return mapping;
	}

	public Node getFrom() {
		return from;
	}

	public Node getTo() {
		return to;
	}

	public EdgeType getType() {
		return mapping.getType();
	}

	public Edge apply(Model model) {
		return model.apply(this);
	}
}
