package de.tum.pssif.transform.mapper.db;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;






import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

public class RDFModelImpl implements RDFModel {
	private Model model;
	private String name;

	public RDFModelImpl(String name, Model model) {
		this.name = name;
		this.model = model;
	}

	@Override
	public void insert(Resource subject, Property predicate, Resource object) {
		// write triples to model
		model.add(subject, predicate, object);
	}

	@Override
	public void writeModelToFile(String name, String loc) {
		try {
			model.write(new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(loc + name + ".txt", false))),
					"RDF/XML");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void writeModelToTurtleFile(File file) {
		try {
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(file, false)));
			model.write(writer, "TTL");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<Resource> findAllSubjects() {
		List<Resource> ressources = new ArrayList<Resource>();
		ResIterator i = model.listSubjects();
		// add all subjects to the list
		while (i.hasNext())
			ressources.add(i.nextResource());
		return ressources;
	}

	@Override
	public List<Resource> findAllSubjects(Property p) {
		List<Resource> ressources = new ArrayList<Resource>();
		// find all subject with certain property
		ResIterator i = model.listSubjectsWithProperty(p);
		// add all subjects to the list
		while (i.hasNext())
			ressources.add(i.nextResource());
		return ressources;
	}





	@Override
	public void removeTriple(Resource s, Property p, Resource r) {
		removeStatement(new SimpleSelector(s, p, r));

	}

	@Override
	public void removeTriple(String s, String p, String o) {
		removeStatement(new SimpleSelector(model.getResource(s),
				model.getProperty(p), model.getResource(o)));
	}

	@Override
	public void removeTripleLiteral(String s, String p, String o) {
		removeStatement(new SimpleSelector(model.getResource(s),
				model.getProperty(p), o));
	}

	/**
	 * Removes a Statement.
	 * 
	 * @param selector
	 *            contains the Statements to be removed
	 */
	private void removeStatement(SimpleSelector selector) {
		List<Statement> statementsToRemove = new ArrayList<Statement>();

		// find all statements with given uris of s,p,r
		// normally there should be only 1 statement
		StmtIterator state = model.listStatements(selector);

		// add statements that should be removed to a list
		while (state.hasNext()) {
			Statement stmt = state.nextStatement();
			statementsToRemove.add(stmt);
		}
		// remove all listed statements from model
		for (Statement stmt : statementsToRemove) {
			model.remove(stmt);
		}
	}

	// delegate Methods to Model class
	@Override
	public void removeAll() {
		model.removeAll();
	}

	@Override
	public void removeStatement(Statement st) {
		model.remove(st);
	}

	@Override
	public Resource createResource(String uri) {
		return model.createResource(uri);
	}

	@Override
	public Resource getResource(String uri) {
		return model.getResource(uri);
	}

	@Override
	public Property createProperty(String uri) {
		return model.createProperty(uri);
	}

	@Override
	public Property getProperty(String uri) {
		return model.getProperty(uri);
	}

	@Override
	public void commit() {
		model.commit();
	}

	@Override
	public void begin() {
		model.begin();
	}

	@Override
	public void abort() {
		model.abort();
	}

	// Setter and Getter
	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public Model getModel() {
		return model;
	}

	public void setModel(Model model) {
		this.model = model;
	}
}
