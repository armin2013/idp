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
import org.pssif.consistencyExceptions.MatchMethodException;

import de.tum.pssif.core.metamodel.Metamodel;
import de.tum.pssif.core.metamodel.NodeType;
import de.tum.pssif.core.model.Model;
import de.tum.pssif.core.model.Node;

/**
 * 
 * This class represents an abstract structure of a matching Method. It supplies
 * the standard attributes for a matching method to say what's the methods name
 * is, if it's active and how much is its weight to the whole similarity score.
 * 
 * If a class is derived by this class it must be implemented by implementing an
 * own version of the executeMatching() method.
 * 
 * @author Andreas
 * 
 */
public abstract class MatchMethod {

	/**
	 * @param matchMethod
	 *            the name of the initialized match method (instance of the enum
	 *            MatchingMethods)
	 * @param isActive
	 *            bool saying whether the method was activated by the user
	 * @param weigth
	 *            the method weight to the whole similarity score of two nodes
	 */
	public MatchMethod(MatchingMethods matchMethod, boolean isActive,
			double weigth) {
		super();
		this.matchMethod = matchMethod;
		this.isActive = isActive;
		this.weigth = weigth;
	}

	final MatchingMethods matchMethod;
	protected boolean isActive;
	protected double weigth;

	/**
	 * An abstract implementation of a matching method. Concrete implementations
	 * are to find in the classes extending this class
	 * 
	 * The method gets two nodes and some additional informations about them.
	 * Then a similarity score (depending on the applied metric) between [0,1]
	 * is calculated and returned.
	 * 
	 * This methods get's very much information about the currently matched two
	 * nodes. Though not all matching methods require that much data, some
	 * methods need quite a lot.
	 * 
	 * @param tempNodeOrigin
	 *            the node from the original model
	 * @param tempNodeNew
	 *            the node from the new model
	 * @param originalModel
	 *            the original (first imported) model
	 * @param newModel
	 *            the recently imported model
	 * @param metaModelOriginal
	 *            the metamodel of the originalModel
	 * @param metaModelNew
	 *            the metamodel of the newMOdel
	 * @param actTypeOriginModel
	 *            the type of tempNodeOrigin
	 * @param actTypeNewModel
	 *            the type of actTypeNewModel
	 * @param labelOrigin
	 *            the label of tempNodeOrigin
	 * @param labelNew
	 *            the label of labelNew
	 * @param tokensOrigin
	 *            the tokens of tempNodeOrigin
	 * @param tokensNew
	 *            the tokens of tokensNew
	 * @return the result of the applied match metric
	 */
	public abstract double executeMatching(Node tempNodeOrigin,
			Node tempNodeNew, Model originalModel, Model newModel,
			Metamodel metaModelOriginal, Metamodel metaModelNew,
			NodeType actTypeOriginModel, NodeType actTypeNewModel,
			String labelOrigin, String labelNew, List<Token> tokensOrigin,
			List<Token> tokensNew);

	/**
	 * This method creates a MatchMethod instance of the given type and with the
	 * given attributes
	 * 
	 * @param matchMethod
	 *            The type of match method which shall be created
	 * @param isActive
	 *            detemrining whether the created method shall be active
	 * @param weight
	 *            thr weight of the created method
	 * @return the createtd method
	 * 
	 */
	public static MatchMethod createMatchMethodObject(
			MatchingMethods matchMethod, boolean isActive, double weight) {
		MatchMethod newMatchMethod = null;

		switch (matchMethod) {
		case EXACT_STRING_MATCHING:
			newMatchMethod = new ExactMatcher(matchMethod, isActive, weight);
			break;
		case DEPTH_MATCHING:
			newMatchMethod = new DepthMatcher(matchMethod, isActive, weight);
			break;
		case STRING_EDIT_DISTANCE_MATCHING:
			newMatchMethod = new StringEditDistanceMatcher(matchMethod,
					isActive, weight);
			break;
		case HYPHEN_MATCHING:
			newMatchMethod = new HyphenMatcher(matchMethod, isActive, weight);
			break;
		case LINGUISTIC_MATCHING:
			newMatchMethod = new LinguisticMatcher(matchMethod, isActive,
					weight);
			break;
		case VECTOR_SPACE_MODEL_MATCHING:
			newMatchMethod = new VsmMatcher(matchMethod, isActive, weight);
			break;
		case LATENT_SEMANTIC_INDEXING_MATCHING:
			newMatchMethod = new LsiMatcher(matchMethod, isActive, weight);
			break;
		case ATTRIBUTE_MATCHING:
			newMatchMethod = new AttributeMatcher(matchMethod, isActive, weight);
			break;
		case CONTEXT_MATCHING:
			newMatchMethod = new ContextMatcher(matchMethod, isActive, weight);
			break;
		default:
			throw new MatchMethodException(
					"Couldn't create a correct match method with the given matchMethod type: "
							+ matchMethod
							+ " Maybe an invalid type was chosen or the corresponding implementation was deleted/changed!");
		}

		return newMatchMethod;
	}

	/**
	 * @return the matchMethod
	 */
	public MatchingMethods getMatchMethod() {
		return matchMethod;
	}

	/**
	 * @return the isActive
	 */
	public boolean isActive() {
		return isActive;
	}

	/**
	 * @return the weigth
	 */
	public double getWeigth() {
		return weigth;
	}

	/**
	 * @param isActive
	 *            the isActive to set
	 */
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	/**
	 * @param weigth
	 *            the weigth to set
	 */
	public void setWeigth(double weigth) {
		this.weigth = weigth;
	}

}
