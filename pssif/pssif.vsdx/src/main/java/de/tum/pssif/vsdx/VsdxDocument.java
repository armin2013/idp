package de.tum.pssif.vsdx;

import java.util.Set;


public interface VsdxDocument {

  Set<VsdxMaster> getMasters();

  VsdxPage getPage();

  VsdxMaster getMaster(int id);

  VsdxMaster getMaster(String name);

  boolean hasMaster(String name);

  boolean hasMaster(int id);

  VsdxDocumentWriter getDocumentWriter();

}
