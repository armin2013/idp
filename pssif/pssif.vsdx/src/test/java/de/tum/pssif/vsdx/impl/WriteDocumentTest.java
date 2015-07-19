package de.tum.pssif.vsdx.impl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import junit.framework.Assert;

import org.junit.Test;

import de.tum.pssif.vsdx.VsdxDocument;
import de.tum.pssif.vsdx.exception.VsdxException;


public class WriteDocumentTest {

  @Test
  public void testEpkDocumentRoundtrip() {
    //TODO
    VsdxDocument document = VsdxDocumentLoaderFactory.INSTANCE.create().loadDocument(getClass().getResourceAsStream("/epk-data.vsdx"));
    try {
      document.getDocumentWriter().write(new FileOutputStream("target/testWriteEpk.vsdx"));
    } catch (FileNotFoundException e) {
      //fail
      Assert.fail();
    }
    try {
      document = VsdxDocumentLoaderFactory.INSTANCE.create().loadDocument(new FileInputStream("target/testWriteEpk.vsdx"));
    } catch (VsdxException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  @Test
  public void testBpmnDocumentRoundtrip() {
    //TODO
    VsdxDocument document = VsdxDocumentLoaderFactory.INSTANCE.create().loadDocument(getClass().getResourceAsStream("/bpmn-data.vsdx"));
    try {
      document.getDocumentWriter().write(new FileOutputStream("target/testWriteBpmn.vsdx"));
    } catch (FileNotFoundException e) {
      //fail
      Assert.fail();
    }
    try {
      document = VsdxDocumentLoaderFactory.INSTANCE.create().loadDocument(new FileInputStream("target/testWriteBpmn.vsdx"));
    } catch (VsdxException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

}
