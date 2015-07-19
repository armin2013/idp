package de.tum.pssif.transform.mapper.graphml;

import java.io.InputStream;
import java.io.OutputStream;

import de.tum.pssif.core.metamodel.Metamodel;
import de.tum.pssif.core.model.Model;


public class PSSIFMapper extends GraphMLMapper {

  @Override
  public Model read(Metamodel metamodel, InputStream inputStream) {
    return readInternal(metamodel, inputStream);
  }

  @Override
  public void write(Metamodel metamodel, Model model, OutputStream outputStream) {
    writeInternal(metamodel, model, outputStream, true);
  }
}
