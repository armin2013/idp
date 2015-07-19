package de.tum.pssif.transform.mapper.db;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.swing.JOptionPane;

import model.ModelBuilder;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

import de.tum.pssif.core.common.PSSIFConstants;
import de.tum.pssif.core.common.PSSIFOption;
import de.tum.pssif.core.common.PSSIFValue;
import de.tum.pssif.core.metamodel.Attribute;
import de.tum.pssif.core.metamodel.ConnectionMapping;
import de.tum.pssif.core.metamodel.DataType;
import de.tum.pssif.core.metamodel.EdgeType;
import de.tum.pssif.core.metamodel.ElementType;
import de.tum.pssif.core.metamodel.JunctionNodeType;
import de.tum.pssif.core.metamodel.Metamodel;
import de.tum.pssif.core.metamodel.NodeType;
import de.tum.pssif.core.metamodel.NodeTypeBase;
import de.tum.pssif.core.metamodel.PrimitiveDataType;
import de.tum.pssif.core.metamodel.external.PSSIFCanonicMetamodelCreator;
import de.tum.pssif.core.model.Edge;
import de.tum.pssif.core.model.Element;
import de.tum.pssif.core.model.JunctionNode;
import de.tum.pssif.core.model.Node;
import de.tum.pssif.core.model.impl.JunctionNodeImpl;
import de.tum.pssif.core.model.impl.ModelImpl;
import de.tum.pssif.transform.mapper.rdf.RDFInputMapper;

//DB to Model Mapper
public class DBToPssifMapperImpl implements DBToPssifMapper {

	public Model model;
	public static DatabaseImpl db;
	private de.tum.pssif.core.model.Model pssifModel;
	private Metamodel metamodel = PSSIFCanonicMetamodelCreator.create();
	private HashMap<String, Node> nodes = new HashMap<>();
	private HashMap<String, Edge> edges = new HashMap<>();

	@Override
	public void DBToModel() {
		db = new DatabaseImpl();
		pssifModel = new ModelImpl();
		model = db.getRdfModel();

		// db.begin(ReadWrite.READ);
		// db.end();

		new RDFInputMapper(ModelFactory.createOntologyModel(
				OntModelSpec.OWL_DL_MEM, model), ModelBuilder.getMetamodel(),pssifModel);
		ModelBuilder.addModel(ModelBuilder.getMetamodel(), pssifModel);
	}

	// NODES

}
