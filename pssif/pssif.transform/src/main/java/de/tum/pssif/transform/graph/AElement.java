package de.tum.pssif.transform.graph;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;


public abstract class AElement {

  private final String        id;
  private String              type;

  private Map<String, String> values = Maps.newHashMap();

  AElement(String id) {
    this.id = id;
  }

  public String getId() {
    return id;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public void setAttribute(String name, String value) {
    this.values.put(name, value);
  }

  public String getAttributeValue(String attributeName) {
    return this.values.get(attributeName);
  }

  public Set<String> getAttributeNames() {
    return Sets.newHashSet(this.values.keySet());
  }

  @Override
  public int hashCode() {
    return (getClass().getName() + id).hashCode();
  }

  public abstract boolean equals(Object obj);

}
