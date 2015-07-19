package de.tum.pssif.vsdx.impl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Set;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;

import com.google.common.collect.Sets;

import de.tum.pssif.vsdx.exception.VsdxXmlException;
import de.tum.pssif.vsdx.zip.ZipArchiveEntryWithData;


class VsdxMasterRepositoryReader {

  static final VsdxMasterRepositoryReader INSTANCE = new VsdxMasterRepositoryReader();

  private VsdxMasterRepositoryReader() {
    //Nothing
  }

  public VsdxMasterRepository create(ZipArchiveEntryWithData masters) {
    Set<VsdxMasterImpl> masterImpls = readMasters(masters);
    return new VsdxMasterRepository(masterImpls);
  }

  private Set<VsdxMasterImpl> readMasters(ZipArchiveEntryWithData mastersData) {
    Set<VsdxMasterImpl> masters = Sets.newHashSet();

    InputStream in = new ByteArrayInputStream(mastersData.getData());
    XMLInputFactory factory = XMLInputFactory.newInstance();
    try {
      XMLStreamReader reader = factory.createXMLStreamReader(in);
      while (reader.hasNext()) {
        int event = reader.next();

        switch (event) {
          case XMLEvent.START_ELEMENT:
            startElement(reader, masters);
            break;
        }
      }
    } catch (XMLStreamException e) {
      throw new VsdxXmlException("Failed to read masters xml file from visio document.", e);
    }
    return masters;
  }

  private void startElement(XMLStreamReader reader, Set<VsdxMasterImpl> masters) throws XMLStreamException {
    String elementName = reader.getName().getLocalPart();
    if (VsdxTokens.MASTER.equals(elementName)) {
      String name = reader.getAttributeValue(null, VsdxTokens.NAMEU);
      String id = reader.getAttributeValue(null, VsdxTokens.ID);
      masters.add(new VsdxMasterImpl(name, Integer.valueOf(id).intValue()));
    }
  }
}
