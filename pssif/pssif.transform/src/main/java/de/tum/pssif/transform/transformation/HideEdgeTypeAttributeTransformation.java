package de.tum.pssif.transform.transformation;

import de.tum.pssif.core.common.PSSIFOption;
import de.tum.pssif.core.metamodel.Attribute;
import de.tum.pssif.core.metamodel.EdgeType;
import de.tum.pssif.core.metamodel.mutable.MutableEdgeType;


public class HideEdgeTypeAttributeTransformation extends HideAttributeTransformation<MutableEdgeType> {
  public HideEdgeTypeAttributeTransformation(EdgeType type, Attribute attribute) {
    super(type, attribute);
  }

  @Override
  protected PSSIFOption<MutableEdgeType> getActualTarget(Viewpoint view) {
    return view.getMutableEdgeType(getType());
  }
}
