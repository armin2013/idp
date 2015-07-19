package de.tum.pssif.transform.mapper.graphml;

public interface GraphMLEdge extends GraphMLElement {
  String getSourceId();

  String getTargetId();

  Boolean isDirected();
}
