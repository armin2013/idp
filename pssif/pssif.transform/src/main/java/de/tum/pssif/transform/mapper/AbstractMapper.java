package de.tum.pssif.transform.mapper;

import java.io.InputStream;
import java.io.OutputStream;

import de.tum.pssif.core.metamodel.Metamodel;
import de.tum.pssif.core.model.Model;
import de.tum.pssif.transform.IoMapper;
import de.tum.pssif.transform.Mapper;
import de.tum.pssif.transform.ModelMapper;
import de.tum.pssif.transform.graph.Graph;


public abstract class AbstractMapper implements Mapper {

  @Override
  public final Model read(Metamodel metamodel, InputStream inputStream) {
    Graph graph = getIoMapper().read(inputStream);
    Metamodel view = getView(metamodel);
    ModelMapper modelMapper = getModelMapper();
    return modelMapper.read(view, graph);
  }

  @Override
  public final void write(Metamodel metamodel, Model model, OutputStream outputStream) {
    Metamodel view = getView(metamodel);
    ModelMapper modelMapper = getModelMapper();
    Graph graph = modelMapper.write(view, model);
    getIoMapper().write(graph, outputStream);
  }

  protected abstract Metamodel getView(Metamodel metamodel);

  protected abstract ModelMapper getModelMapper();

  protected abstract IoMapper getIoMapper();

}
