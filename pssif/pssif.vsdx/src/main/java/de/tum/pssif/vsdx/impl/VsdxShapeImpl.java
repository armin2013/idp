package de.tum.pssif.vsdx.impl;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import de.tum.pssif.vsdx.VsdxConnector;
import de.tum.pssif.vsdx.VsdxMaster;
import de.tum.pssif.vsdx.VsdxShape;


public class VsdxShapeImpl implements VsdxShape {

  protected final int               masterId;
  private final int                 id;
  private final Set<VsdxShapeImpl>  inners = Sets.newHashSet();
  private final Map<String, String> props  = Maps.newHashMap();

  private VsdxDocumentImpl          document;
  private String                    text   = "";

  public VsdxShapeImpl(int id, int masterId) {
    this.masterId = masterId;
    this.id = id;
  }

  int setDocument(VsdxDocumentImpl document) {
    this.document = document;
    int maxId = id;
    for (VsdxShapeImpl inner : inners) {
      maxId = Math.max(maxId, inner.setDocument(document));
    }
    return maxId;
  }

  void addInnerShape(VsdxShapeImpl inner) {
    this.inners.add(inner);
  }

  void setInnerShapes(Set<VsdxShapeImpl> inners) {
    this.inners.clear();
    this.inners.addAll(inners);
  }

  @Override
  public Set<VsdxShape> getShapes() {
    return Sets.<VsdxShape> newHashSet(inners);
  }

  Set<VsdxShapeImpl> innerShapesImpl() {
    return Sets.newHashSet(inners);
  }

  @Override
  public VsdxDocumentImpl getVsdxDocument() {
    return this.document;
  }

  @Override
  public VsdxShape createNewShape(VsdxMaster master) {
    VsdxShapeImpl newInnerShape = new VsdxShapeImpl(document.getNewShapeId(), master.getId());
    this.inners.add(newInnerShape);
    return newInnerShape;
  }

  @Override
  public void setText(String text) {
    this.text = text;
  }

  @Override
  public String getText() {
    return this.text;
  }

  @Override
  public int getId() {
    return this.id;
  }

  @Override
  public VsdxMaster getMaster() {
    return document.getMaster(masterId);
  }

  int getMasterId() {
    return masterId;
  }

  public String toString() {
    return "VsdxShape(shapeId: " + id + " | masterId: " + masterId + " | text: " + text + ")";
  }

  @Override
  public boolean isConnector() {
    return this instanceof VsdxConnector;
  }

  @Override
  public Set<String> getCustomPropertyNames() {
    return Sets.newHashSet(this.props.keySet());
  }

  @Override
  public String getCustomPropertyValue(String customPropertyName) {
    return this.props.get(customPropertyName);
  }

  @Override
  public void setCustomProperty(String name, String value) {
    this.props.put(name, value);
  }

}
