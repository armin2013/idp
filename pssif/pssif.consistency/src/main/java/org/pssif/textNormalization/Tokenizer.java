package org.pssif.textNormalization;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.pssif.consistencyDataStructures.Token;

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
 * This class implements a normalization step used in the normalizer class. This
 * class supplies a method "findTokens" that gets a string and returns the string as a list
 * of tokens.
 * 
 * @author Andreas
 * 
 */
public class Tokenizer {

	private static char[] splitCharacters = { '.', ';', ',', ' ' }; 
	
	public Tokenizer(){
		Arrays.sort(splitCharacters);
	}
	
	/**
	 * modified by Andreas Genz
	 * 
	 * Source code: StringTokenizerOperator.java
	 * 
	 * from folder: Text Processing(Tokenizing, Stemming, Stopwords)
	 */

	/*
	 * RapidMiner Text Processing Extension
	 * 
	 * Copyright (C) 2001-2013 by Rapid-I and the contributors
	 * 
	 * Complete list of developers available at our web site:
	 * 
	 * http://rapid-i.com
	 * 
	 * This program is free software: you can redistribute it and/or modify it
	 * under the terms of the GNU Affero General Public License as published by
	 * the Free Software Foundation, either version 3 of the License, or (at
	 * your option) any later version.
	 * 
	 * This program is distributed in the hope that it will be useful, but
	 * WITHOUT ANY WARRANTY; without even the implied warranty of
	 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero
	 * General Public License for more details.
	 * 
	 * You should have received a copy of the GNU Affero General Public License
	 * along with this program. If not, see http://www.gnu.org/licenses/.
	 */
	public List<Token> findTokens(String label) {

		List<Token> newSequence = new LinkedList<Token>();

		char[] tokenChars = label.toCharArray();
		int start = 0;
		for (int i = 0; i < tokenChars.length; i++) {
			if (isSplitPoint(tokenChars[i], splitCharacters)) {
				if (i - start > 0) {
					newSequence.add(new Token((new String(tokenChars, start, i
							- start)).replaceAll("\\s+", "")));
				}
				start = i + 1;
			}
		}
		if (tokenChars.length - start > 0)
			newSequence.add(new Token((new String(tokenChars, start,
					tokenChars.length - start)).replaceAll("\\s+", "")));
		// replaceAll function fixes bug that the last found token can contain
		// any space characters (f.e. '\n').

		return newSequence;
	}

	/*
	 * RapidMiner Text Processing Extension
	 * 
	 * Copyright (C) 2001-2013 by Rapid-I and the contributors
	 * 
	 * Complete list of developers available at our web site:
	 * 
	 * http://rapid-i.com
	 * 
	 * This program is free software: you can redistribute it and/or modify it
	 * under the terms of the GNU Affero General Public License as published by
	 * the Free Software Foundation, either version 3 of the License, or (at
	 * your option) any later version.
	 * 
	 * This program is distributed in the hope that it will be useful, but
	 * WITHOUT ANY WARRANTY; without even the implied warranty of
	 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero
	 * General Public License for more details.
	 * 
	 * You should have received a copy of the GNU Affero General Public License
	 * along with this program. If not, see http://www.gnu.org/licenses/.
	 */
	private static boolean isSplitPoint(char character, char[] splitCharacters) {
		return Arrays.binarySearch(splitCharacters, character) >= 0;
	}

}
