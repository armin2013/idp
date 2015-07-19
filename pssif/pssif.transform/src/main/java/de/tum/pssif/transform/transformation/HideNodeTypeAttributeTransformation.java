package de.tum.pssif.transform.transformation;

import de.tum.pssif.core.common.PSSIFOption;
import de.tum.pssif.core.metamodel.Attribute;
import de.tum.pssif.core.metamodel.NodeTypeBase;
import de.tum.pssif.core.metamodel.mutable.MutableNodeTypeBase;


public class HideNodeTypeAttributeTransformation extends HideAttributeTransformation<MutableNodeTypeBase> {
  public HideNodeTypeAttributeTransformation(NodeTypeBase type, Attribute attribute) {
    super(type, attribute);
  }

  @Override
  protected PSSIFOption<MutableNodeTypeBase> getActualTarget(Viewpoint view) {
    return view.getMutableBaseNodeType(getType());
  }
}
