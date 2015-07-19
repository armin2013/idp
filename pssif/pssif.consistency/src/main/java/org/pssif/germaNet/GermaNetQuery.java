package org.pssif.germaNet;

import org.pssif.consistencyDataStructures.Token;

/**
 * This class is responsible for establishing a connection to the GermaNet and
 * sending queries to it.
 * 
 * @author Andreas
 * 
 */
public class GermaNetQuery {

	// TODO establish connection with wordNet database here and check if the
	// two given tokens are synonyms;
	public static boolean areSynonyms(Token tokenOrigin, Token tokenNew) {

		System.out.println("Error: The WordNet database couln't be found!");

		return false;
	}
}
