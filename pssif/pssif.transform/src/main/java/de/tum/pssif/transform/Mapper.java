package de.tum.pssif.transform;

import java.io.InputStream;
import java.io.OutputStream;

import de.tum.pssif.core.metamodel.Metamodel;
import de.tum.pssif.core.model.Model;


public interface Mapper {

  Model read(Metamodel metamodel, InputStream inputStream);

  void write(Metamodel metamodel, Model model, OutputStream outputStream);

}
