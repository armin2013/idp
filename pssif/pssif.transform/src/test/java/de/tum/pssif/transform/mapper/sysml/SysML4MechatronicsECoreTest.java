package de.tum.pssif.transform.mapper.sysml;

import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.util.UUID;

import org.junit.Test;

import de.tum.pssif.core.common.PSSIFConstants;
import de.tum.pssif.core.common.PSSIFOption;
import de.tum.pssif.core.common.PSSIFValue;
import de.tum.pssif.core.metamodel.Attribute;
import de.tum.pssif.core.metamodel.ConnectionMapping;
import de.tum.pssif.core.metamodel.EdgeType;
import de.tum.pssif.core.metamodel.Metamodel;
import de.tum.pssif.core.metamodel.NodeTypeBase;
import de.tum.pssif.core.metamodel.external.PSSIFCanonicMetamodelCreator;
import de.tum.pssif.core.model.Element;
import de.tum.pssif.core.model.Model;
import de.tum.pssif.core.model.Node;
import de.tum.pssif.core.model.impl.ModelImpl;
import de.tum.pssif.transform.mapper.SysMlMapper;


public class SysML4MechatronicsECoreTest {

  @Test
  public void writeTest() {
    Metamodel metamodel = PSSIFCanonicMetamodelCreator.create();
    Model model = getModel();
    SysMlMapper mapper = new SysMlMapper();
    try {
      mapper.write(metamodel, model, new FileOutputStream(new File("C:" + File.separator + "Dev" + File.separator + "sysml.xmi")));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      fail("NOPE!");
    }
  }

  @Test
  public void testWriteReadWriteRoundtrip() {
    Metamodel metamodel = PSSIFCanonicMetamodelCreator.create();
    Model model = getModel();
    SysMlMapper mapper = new SysMlMapper();
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    mapper.write(metamodel, model, out);
    mapper = new SysMlMapper();
    model = mapper.read(metamodel, new ByteArrayInputStream(out.toByteArray()));
    mapper = new SysMlMapper();
    try {
      mapper.write(metamodel, model, new FileOutputStream(new File("C:" + File.separator + "Dev" + File.separator + "sysml-roundtrip.xmi")));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      fail("NOPE!");
    }
  }

  private Model getModel() {
    Metamodel metamodel = PSSIFCanonicMetamodelCreator.create();
    Model model = new ModelImpl();

    //Blocks
    NodeTypeBase mechBlock = metamodel.getBaseNodeType(PSSIFCanonicMetamodelCreator.TAGS.get("N_MECHANIC")).getOne();
    Attribute mechName = mechBlock.getAttribute(PSSIFConstants.BUILTIN_ATTRIBUTE_NAME).getOne();
    Attribute mechVersion = mechBlock.getAttribute(PSSIFConstants.BUILTIN_ATTRIBUTE_VERSION).getOne();

    NodeTypeBase elBlock = metamodel.getBaseNodeType(PSSIFCanonicMetamodelCreator.TAGS.get("N_ELECTRONIC")).getOne();
    Attribute elName = mechBlock.getAttribute(PSSIFConstants.BUILTIN_ATTRIBUTE_NAME).getOne();
    Attribute elVersion = mechBlock.getAttribute(PSSIFConstants.BUILTIN_ATTRIBUTE_VERSION).getOne();

    NodeTypeBase softBlock = metamodel.getBaseNodeType(PSSIFCanonicMetamodelCreator.TAGS.get("N_SOFTWARE")).getOne();
    Attribute softName = mechBlock.getAttribute(PSSIFConstants.BUILTIN_ATTRIBUTE_NAME).getOne();
    Attribute softVersion = mechBlock.getAttribute(PSSIFConstants.BUILTIN_ATTRIBUTE_VERSION).getOne();

    NodeTypeBase moduleBlock = metamodel.getBaseNodeType(PSSIFCanonicMetamodelCreator.TAGS.get("N_MODULE")).getOne();
    Attribute moduleName = moduleBlock.getAttribute(PSSIFConstants.BUILTIN_ATTRIBUTE_NAME).getOne();
    Attribute moduleVersion = moduleBlock.getAttribute(PSSIFConstants.BUILTIN_ATTRIBUTE_VERSION).getOne();

    Node mechBlock1 = mechBlock.create(model);
    setId(mechBlock1);
    mechVersion.set(mechBlock1, poPv(BigInteger.valueOf(1)));
    mechName.set(mechBlock1, poPv("Mechanical Block 1"));
    Node mechBlock2 = mechBlock.create(model);
    setId(mechBlock2);
    mechVersion.set(mechBlock2, poPv(BigInteger.valueOf(1)));
    mechName.set(mechBlock2, poPv("Mechanical Block 2"));
    Node mechBlock3 = mechBlock.create(model);
    setId(mechBlock3);
    mechVersion.set(mechBlock3, poPv(BigInteger.valueOf(1)));
    mechName.set(mechBlock3, poPv("Mechanical Block 3"));

    Node elBlock1 = elBlock.create(model);
    setId(elBlock1);
    elVersion.set(elBlock1, poPv(BigInteger.valueOf(1)));
    elName.set(elBlock1, poPv("Electronic Block 1"));
    Node elBlock2 = elBlock.create(model);
    setId(elBlock2);
    elVersion.set(elBlock2, poPv(BigInteger.valueOf(1)));
    elName.set(elBlock2, poPv("Electronic Block 2"));
    Node elBlock3 = elBlock.create(model);
    setId(elBlock3);
    elVersion.set(elBlock3, poPv(BigInteger.valueOf(1)));
    elName.set(elBlock3, poPv("Electronic Block 3"));

    Node softBlock1 = softBlock.create(model);
    setId(softBlock1);
    softVersion.set(softBlock1, poPv(BigInteger.valueOf(1)));
    softName.set(softBlock1, poPv("Software Block 1"));
    Node softBlock2 = softBlock.create(model);
    setId(softBlock2);
    softVersion.set(softBlock2, poPv(BigInteger.valueOf(1)));
    softName.set(softBlock2, poPv("Software Block 2"));
    Node softBlock3 = softBlock.create(model);
    setId(softBlock3);
    softVersion.set(softBlock3, poPv(BigInteger.valueOf(1)));
    softName.set(softBlock3, poPv("Software Block 3"));

    Node moduleBlock1 = moduleBlock.create(model);
    setId(moduleBlock1);
    moduleVersion.set(moduleBlock1, poPv(BigInteger.valueOf(1)));
    moduleName.set(moduleBlock1, poPv("Module 1"));
    Node moduleBlock2 = moduleBlock.create(model);
    setId(moduleBlock2);
    moduleVersion.set(moduleBlock2, poPv(BigInteger.valueOf(1)));
    moduleName.set(moduleBlock2, poPv("Module 2"));

    //block 2 block containment
    EdgeType containment = metamodel.getEdgeType(PSSIFCanonicMetamodelCreator.TAGS.get("E_RELATIONSHIP_INCLUSION_CONTAINS")).getOne();
    ConnectionMapping mod2SoftCM = containment.getMapping(moduleBlock, softBlock).getOne();
    ConnectionMapping mod2MechCM = containment.getMapping(moduleBlock, mechBlock).getOne();
    ConnectionMapping mod2ElCM = containment.getMapping(moduleBlock, elBlock).getOne();

    mod2SoftCM.create(model, moduleBlock1, softBlock1);
    mod2SoftCM.create(model, moduleBlock1, softBlock2);
    mod2MechCM.create(model, moduleBlock1, mechBlock1);
    mod2MechCM.create(model, moduleBlock1, mechBlock2);
    mod2ElCM.create(model, moduleBlock1, elBlock1);

    mod2SoftCM.create(model, moduleBlock2, softBlock3);
    mod2MechCM.create(model, moduleBlock2, mechBlock3);
    mod2ElCM.create(model, moduleBlock2, elBlock2);
    mod2ElCM.create(model, moduleBlock2, elBlock3);

    //ports
    NodeTypeBase mechPort = metamodel.getBaseNodeType(PSSIFCanonicMetamodelCreator.TAGS.get("N_PORT_MECHANIC")).getOne();
    NodeTypeBase elPort = metamodel.getBaseNodeType(PSSIFCanonicMetamodelCreator.TAGS.get("N_PORT_ELECTRONIC")).getOne();
    NodeTypeBase softPort = metamodel.getBaseNodeType(PSSIFCanonicMetamodelCreator.TAGS.get("N_PORT_SOFTWARE")).getOne();
    Attribute portNameAttribute = metamodel.getBaseNodeType(PSSIFCanonicMetamodelCreator.TAGS.get("N_PORT")).getOne()
        .getAttribute(PSSIFConstants.BUILTIN_ATTRIBUTE_NAME).getOne();
    Attribute portConjugated = metamodel.getBaseNodeType(PSSIFCanonicMetamodelCreator.TAGS.get("N_PORT")).getOne()
        .getAttribute(PSSIFCanonicMetamodelCreator.TAGS.get("A_CONJUGATED")).getOne();

    //block 2 port containment
    ConnectionMapping mechBlock2MechPort = containment.getMapping(mechBlock, mechPort).getOne();
    ConnectionMapping elBlock2elPort = containment.getMapping(elBlock, elPort).getOne();
    ConnectionMapping elBlock2softPort = containment.getMapping(elBlock, softPort).getOne();
    ConnectionMapping elBlock2mechPort = containment.getMapping(elBlock, mechPort).getOne();
    ConnectionMapping softBlock2SoftPort = containment.getMapping(softBlock, softPort).getOne();

    Node mechPort1 = mechPort.create(model);
    setId(mechPort1);
    portNameAttribute.set(mechPort1, poPv("Mechanical Port 1"));

    Node mechPort2 = mechPort.create(model);
    setId(mechPort2);
    portNameAttribute.set(mechPort2, poPv("Mechanical Port 2"));

    Node elPort1 = elPort.create(model);
    setId(elPort1);
    portNameAttribute.set(elPort1, poPv("Electrical Port 1"));
    portConjugated.set(elPort1, poPv(Boolean.valueOf(true)));

    Node elPort2 = elPort.create(model);
    setId(elPort2);
    portNameAttribute.set(elPort2, poPv("Electrical Port 2"));
    portConjugated.set(elPort2, poPv(Boolean.valueOf(false)));

    Node softPort1 = softPort.create(model);
    setId(softPort1);
    portNameAttribute.set(softPort1, poPv("Software Port 1"));

    Node softPort2 = softPort.create(model);
    setId(softPort2);
    portNameAttribute.set(softPort2, poPv("Software Port 2"));

    mechBlock2MechPort.create(model, mechBlock1, mechPort1);
    elBlock2mechPort.create(model, elBlock1, mechPort2);
    elBlock2elPort.create(model, elBlock2, elPort1);
    elBlock2elPort.create(model, elBlock1, elPort2);
    elBlock2softPort.create(model, elBlock2, softPort2);
    softBlock2SoftPort.create(model, softBlock1, softPort1);

    //port 2 port connections
    EdgeType isConnectedTo = metamodel.getEdgeType(PSSIFCanonicMetamodelCreator.TAGS.get("E_IS_CONNECTED_TO")).getOne();
    ConnectionMapping elIsConnectedTo = isConnectedTo.getMapping(elPort, elPort).getOne();
    ConnectionMapping mechIsConnectedTo = isConnectedTo.getMapping(mechPort, mechPort).getOne();
    ConnectionMapping softIsConnectedTo = isConnectedTo.getMapping(softPort, softPort).getOne();

    elIsConnectedTo.create(model, elPort1, elPort2);
    mechIsConnectedTo.create(model, mechPort1, mechPort2);
    softIsConnectedTo.create(model, softPort1, softPort2);

    //functionalities
    NodeTypeBase functionality = metamodel.getNodeType(PSSIFCanonicMetamodelCreator.TAGS.get("N_FUNCTIONALITY")).getOne();
    Attribute functNameAttribute = functionality.getAttribute(PSSIFConstants.BUILTIN_ATTRIBUTE_NAME).getOne();

    Node functionality1 = functionality.create(model);
    setId(functionality1);
    functNameAttribute.set(functionality1, poPv("Functionality 1"));

    Node functionality2 = functionality.create(model);
    setId(functionality1);
    functNameAttribute.set(functionality2, poPv("Functionality 2"));

    Node functionality3 = functionality.create(model);
    setId(functionality1);
    functNameAttribute.set(functionality3, poPv("Functionality 3"));

    //block 2 functionality
    EdgeType fulfils = metamodel.getEdgeType(PSSIFCanonicMetamodelCreator.TAGS.get("E_FULFILLS")).getOne();
    ConnectionMapping block2FunctionalityFulfillment = fulfils.getMapping(metamodel.getNodeType(PSSIFCanonicMetamodelCreator.TAGS.get("N_BLOCK")).getOne(),
        functionality).getOne();
    block2FunctionalityFulfillment.create(model, elBlock1, functionality1);
    block2FunctionalityFulfillment.create(model, elBlock2, functionality2);
    block2FunctionalityFulfillment.create(model, elBlock2, functionality3);

    //port 2 functionality
    EdgeType isMandatoryFor = metamodel.getEdgeType(PSSIFCanonicMetamodelCreator.TAGS.get("E_IS_MANDATORY_FOR")).getOne();
    ConnectionMapping port2functionalityMandatory = isMandatoryFor.getMapping(metamodel.getNodeType(PSSIFCanonicMetamodelCreator.TAGS.get("N_PORT")).getOne(),
        functionality).getOne();
    port2functionalityMandatory.create(model, elPort2, functionality1);
    port2functionalityMandatory.create(model, elPort1, functionality3);
    port2functionalityMandatory.create(model, softPort2, functionality3);
    port2functionalityMandatory.create(model, mechPort2, functionality2);

    //attribute values for interface blocks
    //    Enumeration direction = metamodel.getEnumeration(PSSIFCanonicMetamodelCreator.ENUM_DIRECTION).getOne();
    //    EnumerationLiteral inLiteral = direction.getLiteral(PSSIFCanonicMetamodelCreator.ENUM_DIRECTION_IN).getOne();
    //    EnumerationLiteral outLiteral = direction.getLiteral(PSSIFCanonicMetamodelCreator.ENUM_DIRECTION_OUT).getOne();
    //    EnumerationLiteral inOutLiteral = direction.getLiteral(PSSIFCanonicMetamodelCreator.ENUM_DIRECTION_IN_OUT).getOne();

    Attribute portDirectionAttribute = metamodel.getBaseNodeType(PSSIFCanonicMetamodelCreator.TAGS.get("N_PORT")).getOne()
        .getAttribute(PSSIFCanonicMetamodelCreator.TAGS.get("A_DIRECTION")).getOne();
    Attribute portDataTypeAttribute = metamodel.getBaseNodeType(PSSIFCanonicMetamodelCreator.TAGS.get("N_PORT")).getOne()
        .getAttribute(PSSIFCanonicMetamodelCreator.TAGS.get("A_DATA_TYPE")).getOne();

    portDataTypeAttribute.set(mechPort1, poPv("SysML4Mechatronics Data Type 1"));
    portDirectionAttribute.set(mechPort1, poPv("In"));

    portDataTypeAttribute.set(mechPort2, poPv("SysML4Mechatronics Data Type 2"));
    portDirectionAttribute.set(mechPort2, poPv("Out"));

    portDataTypeAttribute.set(softPort1, poPv("SysML4Mechatronics Data Type 3"));
    portDirectionAttribute.set(softPort1, poPv("Provided"));

    portDataTypeAttribute.set(softPort2, poPv("SysML4Mechatronics Data Type 4"));
    portDirectionAttribute.set(softPort2, poPv("Required"));

    portDataTypeAttribute.set(elPort1, poPv("SysML4Mechatronics Data Type 5"));
    portDirectionAttribute.set(elPort1, poPv("InOut"));

    portDataTypeAttribute.set(elPort2, poPv("SysML4Mechatronics Data Type 6"));
    portDirectionAttribute.set(elPort2, poPv("InOut"));

    return model;
  }

  private static PSSIFOption<PSSIFValue> poPv(Object object) {
    return PSSIFOption.one(PSSIFValue.create(object));
  }

  private static void setId(Element element) {
    element.setId(UUID.randomUUID().toString());
  }

}
