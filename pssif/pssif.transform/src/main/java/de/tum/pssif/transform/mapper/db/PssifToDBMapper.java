package de.tum.pssif.transform.mapper.db;

import model.MyModelContainer;

public interface PssifToDBMapper {

	/**
	 * Saves the existing Model from MyModelContainer to the Database by
	 * deleting the whole Database before
	 * 
	 * @param model
	 *            MyModelContainer from which the model should be saved
	 * @param modelname
	 *            Name of the Model in the Database
	 */
	void modelToDB();

	/**
	 * Saves changes of the Model to the Database
	 * 
	 * @param modelname
	 *            Name of the Model in the Database
	 */
	void saveToDB();

	/**
	 * Removes the whole Model from the Database
	 */
	void removeModel();

}
