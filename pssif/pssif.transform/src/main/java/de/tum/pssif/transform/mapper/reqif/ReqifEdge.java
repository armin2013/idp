package de.tum.pssif.transform.mapper.reqif;

public interface ReqifEdge extends ReqifElement {

	String getSourceId();

	String getTargetId();

	Boolean isDirected();

}
