package de.tum.pssif.vsdx;

import java.util.Set;


public interface VsdxPage extends VsdxShapeContainer {

  VsdxConnector createNewConnector(VsdxMaster master, VsdxShape fromShape, VsdxShape toShape);

  Set<VsdxConnector> getConnectors();

}
