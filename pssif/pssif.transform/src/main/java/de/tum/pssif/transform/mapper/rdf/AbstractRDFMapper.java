package de.tum.pssif.transform.mapper.rdf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.List;

import com.hp.hpl.jena.ontology.OntDocumentManager;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.rulesys.GenericRuleReasoner;
import com.hp.hpl.jena.reasoner.rulesys.Rule;
import com.hp.hpl.jena.shared.JenaException;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.util.LocationMapper;

import org.apache.jena.riot.RDFDataMgr;

import de.tum.pssif.core.common.PSSIFConstants;
import de.tum.pssif.core.metamodel.Metamodel;
import de.tum.pssif.core.metamodel.external.URIs;
import de.tum.pssif.core.model.Model;
import de.tum.pssif.transform.IoMapper;
import de.tum.pssif.transform.Mapper;
import de.tum.pssif.transform.ModelMapper;

public class AbstractRDFMapper implements Mapper {
	protected String lang=null;
	OntModel pssifOnt = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);

	public AbstractRDFMapper() {
		lang="TURTLE";
		String path = PSSIFConstants.META_MODEL_PATH;

			try {
				pssifOnt.read(new FileInputStream(new File(path)), URIs.pssifNS,
						"TURTLE");
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// dm.addModel(URIs.pssifUri, pssifOnt);
			OntDocumentManager dm = OntDocumentManager.getInstance();
			dm.addModel(URIs.pssifNS, pssifOnt);
//			  LocationMapper locMap=new LocationMapper();
//			  locMap.addAltEntry(URIs.pssifUri, "file:///"+(System.getProperty("user.home")).replaceAll("\\\\", "/")+"/Meta-Model.rdf");
//				FileManager.get().setLocationMapper(locMap);
//				FileManager.get().loadModel(URIs.pssifUri, lang);
				

	}

	public final Model read(Metamodel metamodel, InputStream inputStream) {

		OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM_TRANS_INF);
		model.read(inputStream, URIs.modelNS, lang);
		// List rules = Rule
		// .parseRules(Rule.rulesParserFromReader(new BufferedReader(
		// new InputStreamReader(
		// RDFTTLMapper.class
		// .getResourceAsStream("/rdfmapper/rules/input.rules")))));

		// GenericRuleReasoner reasoner = new GenericRuleReasoner(rules);
		// reasoner.setTransitiveClosureCaching(true);
		// InfModel inf = ModelFactory.createInfModel(reasoner, model);
		model.write(System.out);
		RDFInputMapper mapper = new RDFInputMapper(model, metamodel);

		return mapper.getPssifModel();
	}

	public final void write(Metamodel metamodel, Model model,
			OutputStream outputStream) {
		RDFOutputMapper mapper = new RDFOutputMapper(model);
		OntModel results = mapper.model;
		results.setNsPrefix("pssif", URIs.pssifNS);
	
		results.write(outputStream, lang, null);
	}

	protected Metamodel getView(Metamodel metamodel) {
		return null;
	}

	protected ModelMapper getModelMapper() {
		return null;
	}

	protected IoMapper getIoMapper() {
		return null;
	}
}
