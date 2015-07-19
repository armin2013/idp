package de.tum.pssif.transform.transformation.moved;

import de.tum.pssif.core.common.PSSIFOption;
import de.tum.pssif.core.common.PSSIFValue;
import de.tum.pssif.core.metamodel.Attribute;
import de.tum.pssif.core.metamodel.ConnectionMapping;
import de.tum.pssif.core.metamodel.impl.AttributeImpl;
import de.tum.pssif.core.model.Edge;
import de.tum.pssif.core.model.Element;
import de.tum.pssif.core.model.Node;


public class MovedAttribute extends AttributeImpl {
  private final Attribute         baseAttribute;
  private final ConnectionMapping mapping;

  public MovedAttribute(String name, Attribute baseAttribute, ConnectionMapping mapping) {
    super(name, baseAttribute.getType(), baseAttribute.getUnit(), baseAttribute.isVisible(), baseAttribute.getCategory());
    this.baseAttribute = baseAttribute;
    this.mapping = mapping;
  }

  @Override
  public void set(Element element, PSSIFOption<PSSIFValue> value) {
    for (Edge e : mapping.applyOutgoing((Node) element).getMany()) {
      baseAttribute.set(mapping.applyTo(e), value);
    }
  }

  @Override
  public PSSIFOption<PSSIFValue> get(Element element) {
    for (Edge e : mapping.applyOutgoing((Node) element).getMany()) {
      return baseAttribute.get(mapping.applyTo(e));
    }
    return PSSIFOption.none();
  }
}
