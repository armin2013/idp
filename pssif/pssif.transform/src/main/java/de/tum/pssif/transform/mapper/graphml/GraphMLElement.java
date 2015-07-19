package de.tum.pssif.transform.mapper.graphml;

import java.util.Map;


public interface GraphMLElement {
  public String getId();

  public String getType();

  public Map<String, String> getValues();

  public Map<String, String> getAnnotations();
}
