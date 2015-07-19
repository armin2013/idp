package de.tum.pssif.transform.io;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;

import de.tum.pssif.transform.IoMapper;
import de.tum.pssif.transform.graph.Graph;
import de.tum.pssif.transform.io.SysMlIoMapperUml.UmlModel;
import de.tum.pssif.transform.io.SysMlIoMapperUml.UmlPackagedElement;


public class SysMlIoMapper implements IoMapper {

  @Override
  public Graph read(InputStream in) {
    XMLInputFactory factory = XMLInputFactory.newInstance();
    try {
      XMLStreamReader reader = factory.createXMLStreamReader(in, "UTF-8");
      Graph graph = new Graph();
      while (reader.hasNext()) {
        int event = reader.next();
        switch (event) {
          case XMLEvent.START_ELEMENT:
            startElement(reader);
            break;
        }
      }
      reader.close();
      return graph;
    } catch (XMLStreamException e) {
      throw new PSSIFIoException("Failed to read SysML XML", e);
    }
  }

  private void startElement(XMLStreamReader reader) throws XMLStreamException {
    String elementName = reader.getName().getLocalPart();
    String elementNs = reader.getName().getPrefix();
    if (SysMLTokens.UML.equalsIgnoreCase(elementNs) && SysMLTokens.MODEL.equalsIgnoreCase(elementName)) {
      readUmlModel(reader);
    }
    else if (SysMLTokens.SYS_ML.equals(elementNs)) {
      readSysMlProfile(reader);
    }
    else if (SysMLTokens.SFB768.equalsIgnoreCase(elementNs)) {
      readSfb768Element(reader);
    }
  }

  private void readUmlModel(XMLStreamReader reader) throws XMLStreamException {
    String modelId = reader.getAttributeValue(SysMLTokens.XML_NS_XMI_URI, SysMLTokens.ID);
    String modelName = reader.getAttributeValue(null, SysMLTokens.NAME);
    UmlModel model = new UmlModel(UUID.fromString(modelId), modelName);
  }

  private void readSysMlProfile(XMLStreamReader eader) {
    //TODO
  }

  private void readSfb768Element(XMLStreamReader reader) {
    //TODO
  }

  @Override
  public void write(Graph graph, OutputStream out) {
    // TODO Auto-generated method stub

  }

  private static class ReadContext {

    int                phase = 0;

    UmlModel           umlModel;
    UmlPackagedElement currentPackagedElement;
  }

}
