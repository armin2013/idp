package de.tum.pssif.vsdx;

import java.util.Set;


public interface VsdxShapeContainer {

  Set<VsdxShape> getShapes();

  VsdxDocument getVsdxDocument();

  VsdxShape createNewShape(VsdxMaster master);

}
