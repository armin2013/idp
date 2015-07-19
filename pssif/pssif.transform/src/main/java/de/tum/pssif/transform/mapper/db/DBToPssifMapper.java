package de.tum.pssif.transform.mapper.db;

public interface DBToPssifMapper {

	/**
	 * Converts the contents of a dataset model to a pssif.core.Model and a
	 * MyModelContainer to be displayed
	 */
	void DBToModel();

}
