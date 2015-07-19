package de.tum.pssif.core.metamodel.impl;

import java.util.Collection;
import java.util.Map;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import de.tum.pssif.core.common.PSSIFConstants;
import de.tum.pssif.core.common.PSSIFOption;
import de.tum.pssif.core.common.PSSIFUtil;
import de.tum.pssif.core.exception.PSSIFStructuralIntegrityException;
import de.tum.pssif.core.metamodel.AttributeCategory;
import de.tum.pssif.core.metamodel.DataType;
import de.tum.pssif.core.metamodel.EdgeType;
import de.tum.pssif.core.metamodel.Enumeration;
import de.tum.pssif.core.metamodel.JunctionNodeType;
import de.tum.pssif.core.metamodel.NodeType;
import de.tum.pssif.core.metamodel.NodeTypeBase;
import de.tum.pssif.core.metamodel.PrimitiveDataType;
import de.tum.pssif.core.metamodel.mutable.MutableEdgeType;
import de.tum.pssif.core.metamodel.mutable.MutableElementType;
import de.tum.pssif.core.metamodel.mutable.MutableEnumeration;
import de.tum.pssif.core.metamodel.mutable.MutableJunctionNodeType;
import de.tum.pssif.core.metamodel.mutable.MutableMetamodel;
import de.tum.pssif.core.metamodel.mutable.MutableNodeType;
import de.tum.pssif.core.metamodel.mutable.MutableNodeTypeBase;


public class MetamodelImpl implements MutableMetamodel {
  private Map<String, MutableEnumeration>      enumerations      = Maps.newHashMap();
  private Map<String, MutableNodeType>         nodeTypes         = Maps.newHashMap();
  private Map<String, MutableJunctionNodeType> junctionNodeTypes = Maps.newHashMap();
  private Map<String, MutableEdgeType>         edgeTypes         = Maps.newHashMap();

  public MetamodelImpl() {
    this(true);
  }

  protected MetamodelImpl(boolean createRootTypes) {
    if (createRootTypes) {
      MutableNodeType node = createNodeTypeInternal(PSSIFConstants.ROOT_NODE_TYPE_NAME);
      addDefaultAttributes(node);

      MutableEdgeType edge = createEdgeTypeInternal(PSSIFConstants.ROOT_EDGE_TYPE_NAME);
      addDefaultAttributes(edge);
      edge.createAttribute(edge.getDefaultAttributeGroup(), PSSIFConstants.BUILTIN_ATTRIBUTE_DIRECTED, PrimitiveDataType.BOOLEAN, true,
          AttributeCategory.METADATA);
      edge.createMapping(node, node);
    }
  }

  private final void addDefaultAttributes(MutableElementType type) {
    type.createAttribute(type.getDefaultAttributeGroup(), PSSIFConstants.BUILTIN_ATTRIBUTE_ID, PrimitiveDataType.STRING, true,
        AttributeCategory.METADATA);
    
    /**
     * @author Andreas
     */
    type.createAttribute(type.getDefaultAttributeGroup(), PSSIFConstants.BUILTIN_ATTRIBUTE_GLOBAL_ID, PrimitiveDataType.STRING, true,
            AttributeCategory.METADATA);
    /**
     * until here
     */
    
    type.createAttribute(type.getDefaultAttributeGroup(), PSSIFConstants.BUILTIN_ATTRIBUTE_NAME, PrimitiveDataType.STRING, true,
        AttributeCategory.METADATA);
    type.createAttribute(type.getDefaultAttributeGroup(), PSSIFConstants.BUILTIN_ATTRIBUTE_VALIDITY_START, PrimitiveDataType.DATE, true,
        AttributeCategory.TIME);
    type.createAttribute(type.getDefaultAttributeGroup(), PSSIFConstants.BUILTIN_ATTRIBUTE_VALIDITY_END, PrimitiveDataType.DATE, true,
        AttributeCategory.TIME);
    type.createAttribute(type.getDefaultAttributeGroup(), PSSIFConstants.BUILTIN_ATTRIBUTE_VERSION, PrimitiveDataType.STRING, true,
        AttributeCategory.METADATA);
    type.createAttribute(type.getDefaultAttributeGroup(), PSSIFConstants.BUILTIN_ATTRIBUTE_COMMENT, PrimitiveDataType.STRING, true,
        AttributeCategory.METADATA);
  }

  @Override
  public MutableEnumeration createEnumeration(String name) {
    PSSIFUtil.checkNameValidity(name);
    if (!getDataType(name).isNone()) {
      throw new PSSIFStructuralIntegrityException("a datatype with the name " + name + " already exists");
    }
    MutableEnumeration result = new EnumerationImpl(name);
    addEnumeration(result);
    return result;
  }

  protected final void addEnumeration(MutableEnumeration result) {
    enumerations.put(PSSIFUtil.normalize(result.getName()), result);
  }

  @Override
  public Collection<Enumeration> getEnumerations() {
    return ImmutableSet.<Enumeration> copyOf(enumerations.values());
  }

  @Override
  public PSSIFOption<Enumeration> getEnumeration(String name) {
    return PSSIFOption.<Enumeration> one(enumerations.get(PSSIFUtil.normalize(name)));
  }

  @Override
  public Collection<MutableEnumeration> getMutableEnumerations() {
    return ImmutableSet.copyOf(enumerations.values());
  }

  @Override
  public PSSIFOption<MutableEnumeration> getMutableEnumeration(String name) {
    return PSSIFOption.one(enumerations.get(PSSIFUtil.normalize(name)));
  }

  @Override
  public void removeEnumeration(Enumeration enumeration) {
    enumerations.remove(PSSIFUtil.normalize(enumeration.getName()));
  }

  @Override
  public Collection<PrimitiveDataType> getPrimitiveTypes() {
    return PrimitiveDataType.TYPES;
  }

  @Override
  public PSSIFOption<PrimitiveDataType> getPrimitiveType(String name) {
    for (PrimitiveDataType candidate : PrimitiveDataType.TYPES) {
      if (PSSIFUtil.areSame(candidate.getName(), name)) {
        return PSSIFOption.one(candidate);
      }
    }
    return PSSIFOption.none();
  }

  @Override
  public Collection<DataType> getDataTypes() {
    Collection<DataType> result = Sets.<DataType> newHashSet(enumerations.values());
    result.addAll(PrimitiveDataType.TYPES);
    return ImmutableSet.copyOf(result);
  }

  @Override
  public PSSIFOption<DataType> getDataType(String name) {
    return PSSIFOption.merge(getEnumeration(name), getPrimitiveType(name));
  }

  @Override
  public MutableNodeType createNodeType(String name) {
    MutableNodeType result = createNodeTypeInternal(name);
    result.inherit(getMutableNodeType(PSSIFConstants.ROOT_NODE_TYPE_NAME).getOne());
    return result;
  }

  private MutableNodeType createNodeTypeInternal(String name) {
    PSSIFUtil.checkNameValidity(name);
    if (!getBaseNodeType(name).isNone()) {
      throw new PSSIFStructuralIntegrityException("a nodetype with name '" + name + "' already exists");
    }
    MutableNodeType result = new NodeTypeImpl(name);
    addNodeType(result);
    return result;
  }

  protected final void addNodeType(MutableNodeType result) {
    nodeTypes.put(PSSIFUtil.normalize(result.getName()), result);
  }

  @Override
  public MutableJunctionNodeType createJunctionNodeType(String name) {
    PSSIFUtil.checkNameValidity(name);
    if (!getBaseNodeType(name).isNone()) {
      throw new PSSIFStructuralIntegrityException("a nodetype with name '" + name + "' already exists");
    }
    MutableJunctionNodeType result = new JunctionNodeTypeImpl(name);
    addDefaultAttributes(result);
    addJunctionNodeType(result);
    return result;
  }

  protected final void addJunctionNodeType(MutableJunctionNodeType result) {
    junctionNodeTypes.put(PSSIFUtil.normalize(result.getName()), result);
  }

  @Override
  public MutableEdgeType createEdgeType(String name) {
    MutableEdgeType result = createEdgeTypeInternal(name);
    result.inherit(getMutableEdgeType(PSSIFConstants.ROOT_EDGE_TYPE_NAME).getOne());
    return result;
  }

  private MutableEdgeType createEdgeTypeInternal(String name) {
    PSSIFUtil.checkNameValidity(name);
    if (!getEdgeType(name).isNone()) {
      throw new PSSIFStructuralIntegrityException("an edgetype with name '" + name + "' already exists");
    }
    EdgeTypeImpl result = new EdgeTypeImpl(name);
    addEdgeType(result);
    return result;
  }

  protected final void addEdgeType(MutableEdgeType result) {
    edgeTypes.put(PSSIFUtil.normalize(result.getName()), result);
  }

  @Override
  public Collection<NodeTypeBase> getBaseNodeTypes() {
    Collection<NodeTypeBase> result = Sets.<NodeTypeBase> newHashSet(getNodeTypes());
    result.addAll(getJunctionNodeTypes());
    return ImmutableSet.copyOf(result);
  }

  @Override
  public Collection<NodeType> getNodeTypes() {
    return ImmutableSet.<NodeType> copyOf(getMutableNodeTypes());
  }

  @Override
  public Collection<JunctionNodeType> getJunctionNodeTypes() {
    return ImmutableSet.<JunctionNodeType> copyOf(getMutableJunctionNodeTypes());
  }

  @Override
  public Collection<EdgeType> getEdgeTypes() {
    return ImmutableSet.<EdgeType> copyOf(edgeTypes.values());
  }

  @Override
  public PSSIFOption<NodeTypeBase> getBaseNodeType(String name) {
    return PSSIFOption.<NodeTypeBase> merge(getMutableNodeType(name), getMutableJunctionNodeType(name));
  }

  @Override
  public PSSIFOption<NodeType> getNodeType(String name) {
    return PSSIFOption.<NodeType> many(getMutableNodeType(name).getMany());
  }

  @Override
  public PSSIFOption<JunctionNodeType> getJunctionNodeType(String name) {
    return PSSIFOption.<JunctionNodeType> many(getMutableJunctionNodeType(name).getMany());
  }

  @Override
  public PSSIFOption<EdgeType> getEdgeType(String name) {
    return PSSIFOption.<EdgeType> one(edgeTypes.get(PSSIFUtil.normalize(name)));
  }

  @Override
  public Collection<MutableNodeTypeBase> getMutableBaseNodeTypes() {
    Collection<MutableNodeTypeBase> result = Sets.<MutableNodeTypeBase> newHashSet(getMutableNodeTypes());
    result.addAll(getMutableJunctionNodeTypes());
    return ImmutableSet.<MutableNodeTypeBase> copyOf(result);
  }

  @Override
  public Collection<MutableNodeType> getMutableNodeTypes() {
    return ImmutableSet.copyOf(nodeTypes.values());
  }

  @Override
  public Collection<MutableJunctionNodeType> getMutableJunctionNodeTypes() {
    return ImmutableSet.copyOf(junctionNodeTypes.values());
  }

  @Override
  public Collection<MutableEdgeType> getMutableEdgeTypes() {
    return ImmutableSet.copyOf(edgeTypes.values());
  }

  @Override
  public PSSIFOption<MutableNodeTypeBase> getMutableBaseNodeType(String name) {
    return PSSIFOption.merge(getMutableNodeType(name), getMutableJunctionNodeType(name));
  }

  @Override
  public PSSIFOption<MutableJunctionNodeType> getMutableJunctionNodeType(String name) {
    return PSSIFOption.one(junctionNodeTypes.get(PSSIFUtil.normalize(name)));
  }

  @Override
  public PSSIFOption<MutableNodeType> getMutableNodeType(String name) {
    return PSSIFOption.one(nodeTypes.get(PSSIFUtil.normalize(name)));
  }

  @Override
  public PSSIFOption<MutableEdgeType> getMutableEdgeType(String name) {
    return PSSIFOption.one(edgeTypes.get(PSSIFUtil.normalize(name)));
  }

  @Override
  public void removeNodeType(NodeType nodeType) {
    for (NodeType nt : getNodeType(nodeType.getName()).getMany()) {
      for (NodeType general : nt.getGeneral().getMany()) {
        general.unregisterSpecialization(nt);
      }
      nodeTypes.remove(PSSIFUtil.normalize(nt.getName()));
    }
  }

  @Override
  public void removeJunctionNodeType(JunctionNodeType junctionNodeType) {
    for (JunctionNodeType jnt : getJunctionNodeType(junctionNodeType.getName()).getMany()) {
      junctionNodeTypes.remove(PSSIFUtil.normalize(jnt.getName()));
    }
  }

  @Override
  public void removeEdgeType(EdgeType edgeType) {
    for (EdgeType et : getEdgeType(edgeType.getName()).getMany()) {
      for (EdgeType general : et.getGeneral().getMany()) {
        general.unregisterSpecialization(et);
      }
      edgeTypes.remove(PSSIFUtil.normalize(et.getName()));
    }
  }
}
