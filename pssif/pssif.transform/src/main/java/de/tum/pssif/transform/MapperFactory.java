package de.tum.pssif.transform;

import de.tum.pssif.core.common.PSSIFUtil;
import de.tum.pssif.core.exception.PSSIFException;
import de.tum.pssif.transform.mapper.BpmnMapper;
import de.tum.pssif.transform.mapper.EpkMapper;
import de.tum.pssif.transform.mapper.SysMlMapper;
import de.tum.pssif.transform.mapper.graphml.PSSIFMapper;
import de.tum.pssif.transform.mapper.graphml.UFMMapper;
import de.tum.pssif.transform.mapper.rdf.RDFTTLMapper;
import de.tum.pssif.transform.mapper.rdf.RDFXMLMapper;
import de.tum.pssif.transform.mapper.reqif.ReqifMapper;


public final class MapperFactory {

  /**
   * Umsatzorientierte Funktionsplanung.
   */
  public static final String UOFP  = "uofp";

  /**
   * SysML.
   */
  public static final String SYSML = "sysml";

  /**
   * EPK.
   */
  public static final String EPK   = "epk";

  /**
   * BPMN.
   */
  public static final String BPMN  = "bpmn";

  /**
   * PSSIF.
   */
  public static final String PSSIF = "PSSIF";

  /**
   * REQ-IF
   */
  public static final String REQ_IF = "reqIf";

  /**
   * RDF
   */
  public static final String RDF_TTL = "RDF/Turtle";
  public static final String RDF_XML = "RDF/XML";



  public static Mapper getMapper(String name) {
    if (PSSIFUtil.areSame(UOFP, name)) {
      return new UFMMapper();
    }
    else if (PSSIFUtil.areSame(SYSML, name)) {
      return new SysMlMapper();
    }
    else if (PSSIFUtil.areSame(EPK, name)) {
      return new EpkMapper();
    }
    else if (PSSIFUtil.areSame(BPMN, name)) {
      return new BpmnMapper();
    }
    else if (PSSIFUtil.areSame(PSSIF, name)) {
      return new PSSIFMapper();
    }
    else if (PSSIFUtil.areSame(REQ_IF, name)) {
    	return new ReqifMapper();
    }
    else if (PSSIFUtil.areSame(RDF_TTL, name)) {
    	return new RDFTTLMapper();
    }
    else if (PSSIFUtil.areSame(RDF_XML, name)) {
    	return new RDFXMLMapper();
    }
    throw new PSSIFException("No mapper found for name: " + name);
  }

  private MapperFactory() {
    //Nothing here
  }

}
