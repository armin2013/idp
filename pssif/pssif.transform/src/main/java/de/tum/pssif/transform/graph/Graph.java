package de.tum.pssif.transform.graph;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;


public class Graph {

  private final Map<String, Node> nodes = Maps.newHashMap();
  private final Map<String, Edge> edges = Maps.newHashMap();

  public Node createNode(String id) {
    Node node = new Node(id);
    nodes.put(id, node);
    return node;
  }

  public Edge createEdge(String id) {
    Edge edge = new Edge(id);
    edges.put(id, edge);
    return edge;
  }

  public void connect(Node n1, Edge e, Node n2) {
    e.setSource(n1);
    e.setTarget(n2);
    n1.addOutgoing(e);
    n2.addIncoming(e);
  }

  public void disconnect(Node n1, Edge e, Node n2) {
    e.setSource(null);
    e.setTarget(null);
    n1.removeOutgoing(e);
    n2.removeIncoming(e);
  }

  public Set<Node> getNodes() {
    return Sets.newHashSet(nodes.values());
  }

  public Set<Edge> getEdges() {
    return Sets.newHashSet(edges.values());
  }

  public Node findNode(String id) {
    return nodes.get(id);
  }

  public Edge findEdge(String id) {
    return edges.get(id);
  }

  public String toString() {
    StringBuilder b = new StringBuilder();
    b.append("Nodes:\n");
    for (Node node : nodes.values()) {
      b.append(node);
      b.append("\n");
    }
    b.append("Edges:\n");
    for (Edge edge : edges.values()) {
      b.append(edge);
      b.append("\n");
    }
    return b.toString();
  }

}
