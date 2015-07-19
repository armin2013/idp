package de.tum.pssif.vsdx.zip;

import java.util.Set;

import org.junit.Test;


public class ZipReaderTest {

  @Test
  public void testReadZip() {
    Set<ZipArchiveEntryWithData> entries = ZipReader.create(getClass().getResourceAsStream("/epk-template.vsdx")).read();
    for (ZipArchiveEntryWithData entry : entries) {
      System.out.println("entry: " + entry.getZipEntry().getName());
      System.out.println("bytes: " + entry.getData().length);
    }
    //And it works...
    //    FileOutputStream out = null;
    //    try {
    //      out = new FileOutputStream(new File("test.zip"));
    //    } catch (FileNotFoundException e) {
    //      e.printStackTrace();
    //    }
    //    ZipWriter writer = ZipWriter.create(out);
    //    writer.write(entries);
  }
}
