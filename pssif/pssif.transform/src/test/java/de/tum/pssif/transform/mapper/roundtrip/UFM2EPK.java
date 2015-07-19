package de.tum.pssif.transform.mapper.roundtrip;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.junit.Test;

import de.tum.pssif.core.metamodel.Metamodel;
import de.tum.pssif.core.metamodel.external.PSSIFCanonicMetamodelCreator;
import de.tum.pssif.transform.Mapper;
import de.tum.pssif.transform.MapperFactory;


public class UFM2EPK {
  @Test
  public void testRoundtrip() throws FileNotFoundException {
    Metamodel metamodel = PSSIFCanonicMetamodelCreator.create();

    InputStream in = getClass().getResourceAsStream("/flow.graphml");
    Mapper ufmMapper = MapperFactory.getMapper(MapperFactory.UOFP);
    Mapper epkMapper = MapperFactory.getMapper(MapperFactory.EPK);
    epkMapper.write(metamodel, ufmMapper.read(metamodel, in), new FileOutputStream("target/UFM2EPK_Roundtrip.vsdx"));
  }

  //  @Test
  //  public void testRoundtripWithMoreEPK() throws FileNotFoundException {
  //    Metamodel metamodel = PSSIFCanonicMetamodelCreator.create();
  //
  //    InputStream in = getClass().getResourceAsStream("/flow.graphml");
  //    Mapper ufmMapper = MapperFactory.getMapper(MapperFactory.UOFP);
  //    Mapper epkMapper = MapperFactory.getMapper(MapperFactory.EPK);
  //    epkMapper.write(metamodel, ufmMapper.read(metamodel, in), new FileOutputStream("target/UFM2EPK_Roundtrip.vsdx"));
  //    Model reReadModel = epkMapper.read(metamodel, new FileInputStream("target/UFM2EPK_Roundtrip.vsdx"));
  //    epkMapper.write(metamodel, reReadModel, new FileOutputStream("target/UFM2EPK_Roundtrip2.vsdx"));
  //  }
}
