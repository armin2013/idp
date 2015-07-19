package de.tum.pssif.transform.mapper;

import java.util.Set;

import de.tum.pssif.transform.Mapper;
import de.tum.pssif.transform.io.VisioIoMapper;


public abstract class BaseVisioMapper extends AbstractMapper implements Mapper {

  private final VisioIoMapper visioMapper;

  protected BaseVisioMapper(String templateFile, Set<String> nodeMasters, Set<String> edgeMasters) {
    this.visioMapper = new VisioIoMapper(templateFile, nodeMasters, edgeMasters);
  }

  protected VisioIoMapper getIoMapper() {
    return this.visioMapper;
  }

}
