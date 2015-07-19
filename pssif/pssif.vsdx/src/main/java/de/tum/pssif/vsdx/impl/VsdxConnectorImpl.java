package de.tum.pssif.vsdx.impl;

import de.tum.pssif.vsdx.VsdxConnector;
import de.tum.pssif.vsdx.VsdxShape;


public class VsdxConnectorImpl extends VsdxShapeImpl implements VsdxConnector {

  private VsdxShapeImpl source;
  private VsdxShapeImpl target;

  public VsdxConnectorImpl(int id, int masterId) {
    super(id, masterId);
  }

  void setSource(VsdxShapeImpl source) {
    this.source = source;
  }

  void setTarget(VsdxShapeImpl target) {
    this.target = target;
  }

  @Override
  public VsdxShape getSourceShape() {
    return source;
  }

  @Override
  public VsdxShape getTargetShape() {
    return target;
  }

}
