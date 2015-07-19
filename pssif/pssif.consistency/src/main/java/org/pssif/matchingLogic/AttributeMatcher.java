package org.pssif.matchingLogic;

import java.util.List;

import org.pssif.consistencyDataStructures.Token;
import org.pssif.settings.Constants;

import de.tum.pssif.core.common.PSSIFOption;
import de.tum.pssif.core.common.PSSIFValue;
import de.tum.pssif.core.metamodel.Attribute;
import de.tum.pssif.core.metamodel.Metamodel;
import de.tum.pssif.core.metamodel.NodeType;
import de.tum.pssif.core.model.Model;
import de.tum.pssif.core.model.Node;

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
 * This class represents an implementation of the attribute matching algorithm.
 * This match method compares the set attributes of two nodes and returns a
 * similarity value based on the set attributes.
 * 
 * @author Andreas
 * 
 */
public class AttributeMatcher extends MatchMethod {

	private static final boolean debugMode = false;

	public AttributeMatcher(MatchingMethods matchMethod, boolean isActive,
			double weight) {
		super(matchMethod, isActive, weight);
	}

	@Override
	public double executeMatching(Node tempNodeOrigin, Node tempNodeNew,
			Model originalModel, Model newModel, Metamodel metaModelOriginal,
			Metamodel metaModelNew, NodeType actTypeOriginModel,
			NodeType actTypeNewModel, String labelOrigin, String labelNew,
			List<Token> tokensOrigin, List<Token> tokensNew) {
		double result = 0;

		result = iterateOverAttributeTypes(tempNodeOrigin, actTypeOriginModel,
				tempNodeNew, actTypeNewModel);
		return result;
	}

	/**
	 * This method iterates over every given attribute saved in the
	 * pssifAttributes array. Then it looks up for the two nodes if the
	 * attribute is set in both nodes and if yes the values are compared.
	 * 
	 * @param tempNodeOrigin
	 * @param tempNodeOriginType
	 * @param tempNodeNew
	 * @param tempNodeNewType
	 * @return the similarity of the two given nodes based on their attribute
	 *         similarity
	 */
	private double iterateOverAttributeTypes(Node tempNodeOrigin,
			NodeType tempNodeOriginType, Node tempNodeNew,
			NodeType tempNodeNewType) {
		PSSIFOption<Attribute> optionOrigin, optionNew;
		Attribute attrOrigin, attrNew;
		PSSIFOption<PSSIFValue> attrValueOrigin, attrValueNew;
		PSSIFValue tempValueOrigin;
		PSSIFValue tempValueNew;

		double nrOfAttributesOrigin = 0;
		double nrOfAttributesNew = 0;

		double nrOfSimilarAttributes = 0;

		for (String typeName : Constants.pssifAttributes) {
			attrOrigin = null;
			attrNew = null;
			attrValueOrigin = null;
			attrValueNew = null;
			tempValueOrigin = null;
			tempValueNew = null;

			optionOrigin = tempNodeOriginType.getAttribute(typeName);
			optionNew = tempNodeNewType.getAttribute(typeName);

			if (optionOrigin.isOne()) {
				attrOrigin = optionOrigin.getOne();

				attrValueOrigin = attrOrigin.get(tempNodeOrigin);

				if (attrValueOrigin.isOne()) {
					nrOfAttributesOrigin++;
				}
			}

			if (optionNew.isOne()) {
				attrNew = optionNew.getOne();

				attrValueNew = attrNew.get(tempNodeNew);

				if (attrValueNew.isOne()) {
					nrOfAttributesNew++;
				}
			}

			if (attrValueOrigin != null && attrValueNew != null
					&& attrValueOrigin.isOne()) {
				tempValueOrigin = attrValueOrigin.getOne();

				if (attrValueNew.isOne()) {
					tempValueNew = attrValueNew.getOne();

					if (attributesAreSimilar(tempValueOrigin, tempValueNew)) {
						nrOfSimilarAttributes++;
					}
				}
			}
		}

		if ((nrOfAttributesOrigin == 0) && (nrOfAttributesNew == 0)) {
			return 1;
		} else {
			return (nrOfSimilarAttributes / (Math.max(nrOfAttributesOrigin,
					nrOfAttributesNew)));
		}
	}

	private boolean attributesAreSimilar(final PSSIFValue attrOrigin,
			final PSSIFValue attrNew) {
		boolean result = false;

		if (debugMode) {
			System.out.println("Node Origin has one attribute with value: "
					+ attrOrigin.getValue().toString());

			System.out.println("Node New has the attribute with value: "
					+ attrNew.getValue().toString());
		}

		if (attrOrigin.isString()) {
			String tmpOrigin = attrOrigin.asString().replaceAll("\\s+", "")
					.toLowerCase();
			String tmpNew = attrNew.asString().replaceAll("\\s+", "")
					.toLowerCase();

			result = tmpOrigin.equals(tmpNew);
		} else {
			result = attrOrigin.getValue().equals(attrNew.getValue());
		}

		return result;
	}
}