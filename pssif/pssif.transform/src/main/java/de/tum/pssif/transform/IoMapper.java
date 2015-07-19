package de.tum.pssif.transform;

import java.io.InputStream;
import java.io.OutputStream;

import de.tum.pssif.transform.graph.Graph;


public interface IoMapper {

  Graph read(InputStream in);

  void write(Graph graph, OutputStream out);

}
