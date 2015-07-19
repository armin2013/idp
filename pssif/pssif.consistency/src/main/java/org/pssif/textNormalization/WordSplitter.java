package org.pssif.textNormalization;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.pssif.consistencyDataStructures.Token;
import org.pssif.consistencyExceptions.NormalizationException;

import de.abelssoft.wordtools.jwordsplitter.AbstractWordSplitter;
import de.abelssoft.wordtools.jwordsplitter.impl.GermanWordSplitter;

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
 * This class splits a given token if possible (if it's a word compound) into
 * smaller tokens. Therefore the jwordsplitter in version 3.4 is used (a dev
 * version of jwordsplitter is available but won't work).
 * 
 * @author Andreas
 * 
 */
public class WordSplitter {

	private AbstractWordSplitter splitter;

	public WordSplitter() {

		try{
		this.splitter = new GermanWordSplitter(true);} catch (IOException e){
			throw new NormalizationException("The german wordsplitter couln't be initialized properly!", e);
		}

		splitter.setStrictMode(true);
	}

	/**
	 * This method takes a list of tokens and iterates over it. For every token
	 * it looks up whether the token is a compound and if yes it is split into 2
	 * or more tokens. These are then added to the result. If a token can't be
	 * split it's added unchanged to the result.
	 * 
	 * @param tokens
	 *            the token list where splittable tokens are splitted
	 * @return the list of splitted and not splittable tokens
	 * 
	 */
	public List<Token> splitTokens(List<Token> tokens) {
		List<Token> newSequence = new LinkedList<Token>();
		Collection<String> splitResult;

		for (Token token : tokens) {

			splitResult = splitter.splitWord(token.getWord());

			if (!splitResult.isEmpty()) {
				for (String str : splitResult) {
					newSequence.add(new Token(str));
				}
			} else {
				newSequence.add(token);
			}
		}

		return newSequence;
	}

}
