package org.pssif.matchingLogic;

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

import java.util.List;

import org.pssif.consistencyDataStructures.Token;
import org.pssif.levenstheinDistance.Levenshtein;
import org.pssif.mainProcesses.Methods;

import de.tum.pssif.core.metamodel.Metamodel;
import de.tum.pssif.core.metamodel.NodeType;
import de.tum.pssif.core.model.Model;
import de.tum.pssif.core.model.Node;

/**
 * 
 * This class represents a matcher based on the levenshtein distance
 * (implemented in the levenshteinDistance folder). It calculates the weighted
 * string edit distance and returns a similarity value in the interval [0,1].
 * The result is 1 or near 1 if two Strings are equal or similar and near zero
 * if they are unsimilar.
 * 
 * @author Andreas
 * 
 */
public class StringEditDistanceMatcher extends MatchMethod {

	private final Levenshtein levenshtein = new Levenshtein();

	public StringEditDistanceMatcher(MatchingMethods matchMethod,
			boolean isActive, double weigth) {
		super(matchMethod, isActive, weigth);
	}

	@Override
	public double executeMatching(Node tempNodeOrigin, Node tempNodeNew,
			Model originalModel, Model newModel, Metamodel metaModelOriginal,
			Metamodel metaModelNew, NodeType actTypeOriginModel,
			NodeType actTypeNewModel, String labelOrigin, String labelNew,
			List<Token> tokensOrigin, List<Token> tokensNew) {
		double result = 0;

		result = levenshtein.getSimilarity(
				Methods.getStringFromTokens(tokensOrigin),
				Methods.getStringFromTokens(tokensNew));

		return result;
	}

}
