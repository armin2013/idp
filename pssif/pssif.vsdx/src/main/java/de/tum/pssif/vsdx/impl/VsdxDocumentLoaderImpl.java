package de.tum.pssif.vsdx.impl;

import java.io.InputStream;
import java.util.Set;

import com.google.common.collect.Sets;

import de.tum.pssif.vsdx.VsdxDocument;
import de.tum.pssif.vsdx.VsdxDocumentLoader;
import de.tum.pssif.vsdx.zip.ZipArchiveEntryWithData;
import de.tum.pssif.vsdx.zip.ZipReader;


public class VsdxDocumentLoaderImpl implements VsdxDocumentLoader {

  private Set<ZipArchiveEntryWithData> transferOnlyEntries = Sets.newHashSet();

  private ZipArchiveEntryWithData      mastersXml          = null;

  //we only read a page1 for now
  private ZipArchiveEntryWithData      page1Xml            = null;

  protected VsdxDocumentLoaderImpl() {
    //Nothing
  }

  @Override
  public VsdxDocument loadDocument(InputStream inputStream) {
    Set<ZipArchiveEntryWithData> vsdxEntries = ZipReader.create(inputStream).read();
    assignEntries(vsdxEntries);
    return new VsdxDocumentImpl(transferOnlyEntries, VsdxPageReader.INSTANCE.create(page1Xml), VsdxMasterRepositoryReader.INSTANCE.create(mastersXml));
  }

  private void assignEntries(Set<ZipArchiveEntryWithData> vsdxEntries) {
    for (ZipArchiveEntryWithData entry : vsdxEntries) {
      if ("visio/pages/page1.xml".equalsIgnoreCase(entry.getName())) {
        page1Xml = entry;
      }
      else if ("visio/masters/masters.xml".equalsIgnoreCase(entry.getName())) {
        mastersXml = entry;
        transferOnlyEntries.add(entry);
      }
      else {
        transferOnlyEntries.add(entry);
      }
    }
  }
}
