package de.tum.pssif.transform.transformation.viewed;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

import de.tum.pssif.core.common.PSSIFConstants;
import de.tum.pssif.core.common.PSSIFOption;
import de.tum.pssif.core.metamodel.JunctionNodeType;
import de.tum.pssif.core.model.Model;
import de.tum.pssif.core.model.Node;


// FIXME abstract into AliasedBaseNodeType
public class AliasedJunctionNodeType extends ViewedJunctionNodeType {
  private final String annotation;

  public AliasedJunctionNodeType(JunctionNodeType baseType, String name, String annotation) {
    super(baseType, name);
    this.annotation = annotation;
  }

  @Override
  public PSSIFOption<Node> apply(Model model, boolean includeSubtypes) {
    return PSSIFOption.many(Collections2.filter(super.apply(model, includeSubtypes).getMany(), new Predicate<Node>() {
      @Override
      public boolean apply(Node input) {
        boolean result = false;
        for (String val : input.getAnnotation(PSSIFConstants.ALIAS_ANNOTATION_KEY).getMany()) {
          result |= val.equals(annotation);
        }
        return result;
      }
    }));
  }

  @Override
  public PSSIFOption<Node> apply(Model model, String id, boolean includeSubtypes) {
    return PSSIFOption.many(Collections2.filter(super.apply(model, id, includeSubtypes).getMany(), new Predicate<Node>() {
      @Override
      public boolean apply(Node input) {
        boolean result = false;
        for (String val : input.getAnnotation(PSSIFConstants.ALIAS_ANNOTATION_KEY).getMany()) {
          result |= val.equals(annotation);
        }
        return result;
      }
    }));
  }

  @Override
  public Node create(Model model) {
    Node result = super.create(model);
    result.annotate(PSSIFConstants.ALIAS_ANNOTATION_KEY, annotation);
    return result;
  }
}
