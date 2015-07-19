package de.tum.pssif.transform.graph;

public final class Edge extends AElement {

  private Node    source;
  private Node    target;

  private boolean directed = false;

  Edge(String id) {
    super(id);
  }

  public Node getSource() {
    return source;
  }

  void setSource(Node source) {
    this.source = source;
  }

  public Node getTarget() {
    return target;
  }

  void setTarget(Node target) {
    this.target = target;
  }

  public boolean isDirected() {
    return directed;
  }

  public void setDirected(boolean directed) {
    this.directed = directed;
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Edge)) {
      return false;
    }
    return getId().equals(((Edge) obj).getId());
  }

  public String toString() {
    //TODO attrs
    return "Edge(id=" + getId() + "|type=" + getType() + "|from=" + getSource().getId() + "|to=" + getTarget().getId() + ")";

  }
}
