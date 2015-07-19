package de.tum.pssif.transform.mapper.graphml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.junit.Test;

import de.tum.pssif.core.metamodel.Metamodel;
import de.tum.pssif.core.metamodel.external.PSSIFCanonicMetamodelCreator;
import de.tum.pssif.core.model.Model;
import de.tum.pssif.transform.Mapper;
import de.tum.pssif.transform.MapperFactory;


public class GraphMlWriteTest {
  private final Metamodel     metamodel     = PSSIFCanonicMetamodelCreator.create();
  private final GraphMLMapper canonicMapper = new PSSIFMapper();

  @Test
  public void testWrite() {

    InputStream in = getClass().getResourceAsStream("/flow.graphml");
    GraphMLMapper importer = new UFMMapper();

    Metamodel metamodel = PSSIFCanonicMetamodelCreator.create();

    Model model = importer.read(metamodel, in);

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    Mapper mapper = MapperFactory.getMapper(MapperFactory.UOFP);
    mapper.write(metamodel, model, out);

    byte[] result = out.toByteArray();
    String str = new String(result);
    System.out.println(str);
    //TODO
  }

  @Test
  public void testWriteEpk() {
    InputStream in = getClass().getResourceAsStream("/visio/epk-data.vsdx");
    Mapper mapper = MapperFactory.getMapper(MapperFactory.EPK);
    Model model = mapper.read(metamodel, in);

    ByteArrayOutputStream os = new ByteArrayOutputStream();
    canonicMapper.write(metamodel, model, os);
    System.out.println(os.toString());

    Model readModel = canonicMapper.read(metamodel, new ByteArrayInputStream(os.toByteArray()));
  }
}
