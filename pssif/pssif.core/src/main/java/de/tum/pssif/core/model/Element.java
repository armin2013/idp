package de.tum.pssif.core.model;

import java.util.Map.Entry;

import de.tum.pssif.core.common.PSSIFOption;
import de.tum.pssif.core.common.PSSIFValue;
import de.tum.pssif.core.metamodel.impl.GetValueOperation;
import de.tum.pssif.core.metamodel.impl.SetValueOperation;

public interface Element {
	Model getModel();

	void setId(String id);

	String getId();

	void apply(SetValueOperation op);

	PSSIFOption<PSSIFValue> apply(GetValueOperation op);

	void annotate(String key, String value);

	void annotate(String key, String value, boolean overwrite);

	PSSIFOption<Entry<String, String>> getAnnotations();

	PSSIFOption<String> getAnnotation(String key);
}
