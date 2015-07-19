package de.tum.pssif.transform;

import de.tum.pssif.core.metamodel.Metamodel;
import de.tum.pssif.core.model.Model;
import de.tum.pssif.transform.graph.Graph;


public interface ModelMapper {

  Model read(Metamodel metamodel, Graph graph);

  Graph write(Metamodel metamodel, Model model);
}
