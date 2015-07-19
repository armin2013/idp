package de.tum.pssif.transform.mapper.db;

import java.io.File;

import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.rdf.model.Model;

public interface Database {
//	/**
//	 * Creates a new RDF model if not already existing else return existing RDF
//	 * model
//	 * 
//	 * @param name
//	 *            Name of the dataset model
//	 * @return New/Existing RDFModel
//	 */
//	public RDFModel createModel(String name);

	/**
	 * Prints out names of all the models in a dataset Just for testing purpose
	 */
	public void printModelNames();

	/**
	 * Commits a transaction of the dataset
	 */
	void commit();

	/**
	 * Starts a transaction of the dataset
	 */
	void begin(ReadWrite rw);

	/**
	 * Ends a transaction of the dataset
	 */
	void end();

	/**
	 * Closes the dataset
	 */
	void close();

	/**
	 * Removes a Model from the dataset
	 * 
	 * @param uri
	 *            URI of the model to be removed
	 */
	void removeNamedModel(String uri);

	/**
	 * Saves the Model either to Fuseki Server or to the Dataset
	 * 
	 * @param model
	 *            model to be removed
	 */
	void saveModel(Model model);

}
