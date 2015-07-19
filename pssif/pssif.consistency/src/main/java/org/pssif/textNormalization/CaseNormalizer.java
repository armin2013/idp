package org.pssif.textNormalization;

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
 * class supplies a method that takes a list of tokens and converts every token
 * to lower case, except if it only consists of uppercase letters.
 * 
 * @author Andreas
 * 
 */
public class CaseNormalizer {

	/**
	 * This method takes a list of tokens and converts every token to lower
	 * case, except if it only consists of uppercase letters.
	 * 
	 * @param tokens
	 *            The list of tokens which shall be converted to lowercase
	 * @return the list of tokens converted to lowercase
	 */
	public List<Token> convertTokensToLowerCase(List<Token> tokens) {

		String temp;

		for (Token token : tokens) {

			temp = token.getWord();

			/**
			 * check if the tokens word doesnt consist of upper case letters
			 * only (if it only has uppercase letters it is assumed to be an
			 * abberevation and remains unnormalized)
			 */
			if (!(temp.toUpperCase() == temp)) {
				temp = temp.toLowerCase();
				token.setWord(temp);
			}
		}

		return tokens;
	}

}
