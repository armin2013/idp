package de.tum.pssif.transform.transformation.joined;

import java.util.Map.Entry;

import de.tum.pssif.core.common.PSSIFOption;
import de.tum.pssif.core.common.PSSIFValue;
import de.tum.pssif.core.metamodel.impl.GetValueOperation;
import de.tum.pssif.core.metamodel.impl.ReadFromNodesOperation;
import de.tum.pssif.core.metamodel.impl.ReadToNodesOperation;
import de.tum.pssif.core.metamodel.impl.SetValueOperation;
import de.tum.pssif.core.model.Edge;
import de.tum.pssif.core.model.Model;
import de.tum.pssif.core.model.Node;

public class UnjoinedEdge implements Edge {
	private final Edge baseEdge;
	private final Node unjoinedFrom;
	private final Node unjoinedTo;

	public UnjoinedEdge(Edge baseEdge, Node unjoinedFrom, Node unjoinedTo) {
		this.baseEdge = baseEdge;
		this.unjoinedFrom = unjoinedFrom;
		this.unjoinedTo = unjoinedTo;
	}

	@Override
	public Model getModel() {
		return baseEdge.getModel();
	}

	@Override
	public void setId(String id) {
		baseEdge.setId(id);
	}

	@Override
	public String getId() {
		return baseEdge.getId();
	}

	@Override
	public void apply(SetValueOperation op) {
		baseEdge.apply(op);
	}

	@Override
	public PSSIFOption<PSSIFValue> apply(GetValueOperation op) {
		return baseEdge.apply(op);
	}

	@Override
	public void annotate(String key, String value) {
		baseEdge.annotate(key, value);
	}

	@Override
	public void annotate(String key, String value, boolean overwrite) {
		baseEdge.annotate(key, value, overwrite);
	}

	@Override
	public PSSIFOption<Entry<String, String>> getAnnotations() {
		return baseEdge.getAnnotations();
	}

	@Override
	public PSSIFOption<String> getAnnotation(String key) {
		return baseEdge.getAnnotation(key);
	}

	@Override
	public Node apply(ReadFromNodesOperation op) {
		return unjoinedFrom;
	}

	@Override
	public Node apply(ReadToNodesOperation op) {
		return unjoinedTo;
	}
}
