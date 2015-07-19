package de.tum.pssif.transform.io;

import java.util.Set;
import java.util.UUID;

import com.google.common.collect.Sets;


class SysMlIoMapperUml {

  private SysMlIoMapperUml() {
    //Noop. This is just a container
  }

  static class UmlModel {
    final UUID              id;
    final String            name;

    Set<UmlPackagedElement> packagedElements = Sets.newHashSet();

    UmlModel(UUID id, String name) {
      this.id = id;
      this.name = name;
    }

    /**
     * Postprocessing: Attibutes which reference a type should find this type within the model.
     */
    void resolve() {
      for (UmlPackagedElement element : this.packagedElements) {
        element.resolve(this);
      }
    }
  }

  static class UmlPackagedElement {
    //both name and id
    final String            identifier;
    final Set<UmlAttribute> attributes = Sets.newHashSet();

    UmlPackagedElement(String identifier) {
      this.identifier = identifier;
    }

    private void resolve(UmlModel model) {
      for (UmlAttribute attribute : this.attributes) {
        if (attribute instanceof UmlReferencedTypeAttribute) {
          ((UmlReferencedTypeAttribute) attribute).resolve(model);
        }
      }
    }

  }

  abstract static class UmlAttributeId {
    public String toString() {
      return serializeId();
    }

    abstract String serializeId();
  }

  static class UmlAttributeIdUUID extends UmlAttributeId {
    final UUID id;

    UmlAttributeIdUUID(UUID id) {
      this.id = id;
    }

    @Override
    String serializeId() {
      return id.toString();
    }
  }

  static class UmlAttributeIdString extends UmlAttributeId {
    final String id;

    UmlAttributeIdString(String id) {
      this.id = id;
    }

    @Override
    String serializeId() {
      return id;
    }
  }

  static abstract class UmlAttribute {
    final UmlAttributeId id;
    final String         name;

    public UmlAttribute(UmlAttributeId id, String name) {
      this.id = id;
      this.name = name;
    }

  }

  static class UmlStringAttribute extends UmlAttribute {

    public UmlStringAttribute(String id, String name) {
      super(new UmlAttributeIdUUID(UUID.fromString(id)), name);
    }

  }

  static class UmlPortAttribute extends UmlAttribute {

    public UmlPortAttribute(String id, String name) {
      super(new UmlAttributeIdString(id), name);
    }

  }

  static class UmlReferencedTypeAttribute extends UmlAttribute {

    UmlPackagedElement referencedType;

    public UmlReferencedTypeAttribute(String id, String name) {
      super(new UmlAttributeIdUUID(UUID.fromString(id)), name);
    }

    private void resolve(UmlModel model) {
      for (UmlPackagedElement element : model.packagedElements) {
        if (this.name.equalsIgnoreCase(element.identifier)) {
          this.referencedType = element;
          break;
        }
      }

    }
  }

}
