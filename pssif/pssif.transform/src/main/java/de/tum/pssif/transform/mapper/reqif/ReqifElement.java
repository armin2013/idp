package de.tum.pssif.transform.mapper.reqif;

import java.util.Map;

public interface ReqifElement {
	public String getId();

	public String getType();

	public Map<String, String> getValues();
}
