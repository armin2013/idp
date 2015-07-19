package de.tum.pssif.transform.transformation;

import de.tum.pssif.core.common.PSSIFOption;
import de.tum.pssif.core.metamodel.Attribute;
import de.tum.pssif.core.metamodel.ElementType;
import de.tum.pssif.core.metamodel.mutable.MutableElementType;


public abstract class HideAttributeTransformation<T extends MutableElementType> extends AbstractTransformation {
  private final String type;
  private final String attribute;

  public HideAttributeTransformation(ElementType type, Attribute attribute) {
    this.type = type.getName();
    this.attribute = attribute.getName();
  }

  protected String getType() {
    return type;
  }

  protected String getAttribute() {
    return attribute;
  }

  protected abstract PSSIFOption<T> getActualTarget(Viewpoint view);

  @Override
  public final void apply(Viewpoint view) {
    for (T actualTarget : getActualTarget(view).getMany()) {
      for (Attribute a : actualTarget.getAttribute(attribute).getMany()) {
        actualTarget.removeAttribute(a);
      }
    }
  }
}
