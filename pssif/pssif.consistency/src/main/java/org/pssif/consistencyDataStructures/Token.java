package org.pssif.consistencyDataStructures;

/**
This file is part of PSSIF Consistency. It is responsible for keeping consistency between different requirements models or versions of models.
Copyright (C) 2014 Andreas Genz

    PSSIF Consistency is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    PSSIF Consistency is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with PSSIF Consistency.  If not, see <http://www.gnu.org/licenses/>.
    
    Feel free to contact me via eMail: genz@in.tum.de
*/


/**
 * this class represents a token. Each token holds a word
 * as a String and an according wordWeigth (plus variables necessary for
 * computing the weight)
 * 
 * @author Andreas
 * 
 * 
 */
public class Token {

	public Token(String string) {
		this.word = string;
		this.documentCounter = 0;
		this.wordWeigth = 0;
	}

	/**
	 * the word held by this token
	 */
	private String word;

	/**
	 * the computed tf-idf weight of this token
	 */
	private double wordWeigth;

	/**
	 * counts in how many documents(nodes) this token appears
	 */
	private int documentCounter;

	/**
	 * variables describing temporary results for the vsm
	 */
	private double idf, tf;

	/**
	 * @return the word
	 */
	public String getWord() {
		return this.word;
	}

	/**
	 * @param word
	 *            the word to set
	 */
	public void setWord(String word) {
		this.word = word;
	}

	/**
	 * @return the documentCounter
	 */
	public int getDocumentCounter() {
		return documentCounter;
	}

	/**
	 * @param documentCounter
	 *            the documentCounter to set
	 */
	public void setDocumentCounter(int documentCounter) {
		this.documentCounter = documentCounter;
	}

	/**
	 * @return the idf
	 */
	public double getIdf() {
		return idf;
	}

	/**
	 * @param idf
	 *            the idf to set
	 */
	public void setIdf(double idf) {
		this.idf = idf;
	}

	/**
	 * @return the tf
	 */
	public double getTf() {
		return tf;
	}

	/**
	 * @param tf
	 *            the tf to set
	 */
	public void setTf(double tf) {
		this.tf = tf;
	}

	/**
	 * @return the wordWeigth
	 */
	public double getWordWeigth() {
		return wordWeigth;
	}

	/**
	 * this method increases the DocumentCounter by +1
	 */
	public void incrementDocumentCounter() {
		this.documentCounter += 1;
	}

	/**
	 * This method coputes the tf-idf word weight for this token (relative to
	 * the document corpus)
	 */
	public void computeWordWeigth() {
		double weigth = 0;

		weigth = tf * idf;

		this.wordWeigth = weigth;
	}

}
