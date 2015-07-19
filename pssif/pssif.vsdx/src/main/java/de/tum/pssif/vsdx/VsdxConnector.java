package de.tum.pssif.vsdx;

public interface VsdxConnector extends VsdxShape {

  VsdxShape getSourceShape();

  VsdxShape getTargetShape();

}
