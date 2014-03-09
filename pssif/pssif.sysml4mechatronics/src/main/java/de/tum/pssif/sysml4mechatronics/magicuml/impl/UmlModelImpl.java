package de.tum.pssif.sysml4mechatronics.magicuml.impl;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import de.tum.pssif.sysml4mechatronics.common.IdentifiableNamedImpl;
import de.tum.pssif.sysml4mechatronics.common.SysML4MIdentifier;
import de.tum.pssif.sysml4mechatronics.common.SysML4MName;
import de.tum.pssif.sysml4mechatronics.magicuml.MagicumlClass;
import de.tum.pssif.sysml4mechatronics.magicuml.MagicumlDataType;
import de.tum.pssif.sysml4mechatronics.magicuml.MagicumlModel;
import de.tum.pssif.sysml4mechatronics.magicuml.MagicumlPackagedElement;
import de.tum.pssif.sysml4mechatronics.magicuml.MagicumlVisibility;


public class UmlModelImpl extends IdentifiableNamedImpl implements MagicumlModel {

  private final Map<SysML4MIdentifier, UmlClassImpl>    classesById     = Maps.newHashMap();
  private final Map<SysML4MName, UmlClassImpl>          classesByName   = Maps.newHashMap();
  private final Map<SysML4MIdentifier, UmlDataTypeImpl> dataTypesById   = Maps.newHashMap();
  private final Map<SysML4MName, UmlDataTypeImpl>       dataTypesByName = Maps.newHashMap();

  public UmlModelImpl(SysML4MIdentifier identifier, SysML4MName name) {
    super(identifier, name);
  }

  @Override
  public Set<MagicumlPackagedElement> getPackagedElements() {
    Set<MagicumlPackagedElement> result = Sets.newHashSet();
    result.addAll(getClasses());
    result.addAll(getDataTypes());
    return result;
  }

  @Override
  public Set<MagicumlClass> getClasses() {
    return Sets.<MagicumlClass> newHashSet(this.classesById.values());
  }

  @Override
  public Set<MagicumlDataType> getDataTypes() {
    return Sets.<MagicumlDataType> newHashSet(this.dataTypesById.values());
  }

  @Override
  public UmlPackagedElementImpl findPackagedElementByIdentifier(SysML4MIdentifier identitifer) {
    UmlPackagedElementImpl result = findClassByIdentifier(identitifer);
    if (result != null) {
      return result;
    }
    return findDataTypeByIdentifier(identitifer);
  }

  @Override
  public UmlClassImpl findClassByIdentifier(SysML4MIdentifier identitifer) {
    return this.classesById.get(identitifer);
  }

  @Override
  public UmlDataTypeImpl findDataTypeByIdentifier(SysML4MIdentifier identitifer) {
    return this.dataTypesById.get(identitifer);
  }

  @Override
  public UmlPackagedElementImpl findPackagedElementByName(SysML4MName name) {
    UmlPackagedElementImpl result = findClassByName(name);
    if (result != null) {
      return result;
    }
    return findDataTypeByName(name);
  }

  @Override
  public UmlClassImpl findClassByName(SysML4MName name) {
    return this.classesByName.get(name);
  }

  @Override
  public UmlDataTypeImpl findDataTypeByName(SysML4MName name) {
    return this.dataTypesByName.get(name);
  }

  @Override
  public UmlClassImpl createUmlClass(SysML4MIdentifier identitifer, SysML4MName name, MagicumlVisibility visibility) {
    UmlClassImpl clazz = new UmlClassImpl(identitifer, name, visibility);
    this.classesById.put(identitifer, clazz);
    this.classesByName.put(name, clazz);
    return clazz;
  }

  @Override
  public UmlDataTypeImpl createDataType(SysML4MIdentifier identitifer, SysML4MName name) {
    UmlDataTypeImpl dt = new UmlDataTypeImpl(identitifer, name);
    this.dataTypesById.put(identitifer, dt);
    this.dataTypesByName.put(name, dt);
    return dt;
  }

  @Override
  public void resolveReferences() {
    // TODO Auto-generated method stub

  }

}
