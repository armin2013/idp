package de.tum.pssif.vsdx;

import java.util.Set;


public interface VsdxShape extends VsdxShapeContainer {

  int getId();

  void setText(String text);

  String getText();

  VsdxMaster getMaster();

  boolean isConnector();

  Set<String> getCustomPropertyNames();

  String getCustomPropertyValue(String customPropertyName);

  void setCustomProperty(String name, String value);

}
