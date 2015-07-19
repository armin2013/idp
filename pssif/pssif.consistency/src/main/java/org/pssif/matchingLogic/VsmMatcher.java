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

import org.pssif.consistencyDataStructures.DocumentCorpus;
import org.pssif.consistencyDataStructures.Token;
import org.pssif.mainProcesses.MatchingProcess;
import org.pssif.textNormalization.Normalizer;

import de.tum.pssif.core.metamodel.Metamodel;
import de.tum.pssif.core.metamodel.NodeType;
import de.tum.pssif.core.model.Model;
import de.tum.pssif.core.model.Node;

/**
 * 
 * This class represents the implementation of the Vector space model metric.
 * Before executing this metric for every node pair the document corpus is built
 * by the method initializeDocumentCorpus(). Then the idf weight for every token
 * of the document vocabulary is calculated.
 * 
 * Then the metric can be applied to node pairs. Therefore for every node the tf
 * (termfrequency of token in label) weights for its tokens are computed and
 * assigned to them.
 * 
 * Afterwards the full token vector for every node is calculated and both token
 * vectors are given to the calculateCosineSimilarity() method.
 * 
 * Then the result of the metric is returned.
 * 
 * @author Andreas
 * 
 * 
 */
public class VsmMatcher extends MatchMethod {

	private DocumentCorpus corpusData;

	public VsmMatcher(MatchingMethods matchMethod, boolean isActive,
			double weigth) {
		super(matchMethod, isActive, weigth);
	}

	/**
	 * First the tfWeights for the given tokens are computed.
	 * 
	 * Then the two vector representations of the given nodes are compared using
	 * the cosine similarity.
	 */
	@Override
	public double executeMatching(Node tempNodeOrigin, Node tempNodeNew,
			Model originalModel, Model newModel, Metamodel metaModelOriginal,
			Metamodel metaModelNew, NodeType actTypeOriginModel,
			NodeType actTypeNewModel, String labelOrigin, String labelNew,
			List<Token> tokensOrigin, List<Token> tokensNew) {

		double result = 0;

		computeTFWeigths(tokensOrigin);
		computeTFWeigths(tokensNew);

		result = calculateCosineSimilarity(
				corpusData.getFullTokenList(tokensOrigin),
				corpusData.getFullTokenList(tokensNew));

		return result;
	}

	/**
	 * This method calculates the similarity of two given token vectors
	 * based on the cosine similarity function
	 * 
	 * @param tokensOrigin
	 *            the tokens of the original model
	 * @param tokensNew
	 *            the tokens of the recent imported model
	 * @return the similarity of the two token sets based on the cosine
	 *         similarity
	 */
	private double calculateCosineSimilarity(List<Token> tokensOrigin,
			List<Token> tokensNew) {
		double cosine = 1;

		double numerator = 0;
		double denominator = 0;
		double tempDenominatorOrigin = 0, tempDenominatorNew = 0;

		Token tempOrigin, tempNew;

		for (int i = 0; i < tokensOrigin.size(); i++) {
			tempOrigin = tokensOrigin.get(i);
			tempNew = tokensNew.get(i);

			if ((tempOrigin != null) && (tempNew != null)) {
				numerator += tempOrigin.getWordWeigth()
						* tempNew.getWordWeigth();
			}

			if (tempOrigin != null) {
				tempDenominatorOrigin += Math.pow(tokensOrigin.get(i)
						.getWordWeigth(), 2);
			}
			if (tempNew != null) {
				tempDenominatorNew += Math.pow(
						tokensNew.get(i).getWordWeigth(), 2);
			}
		}

		denominator = Math.sqrt(tempDenominatorOrigin * tempDenominatorNew);

		if ((numerator == 0) || (denominator == 0)) {
			return 0;
		}

		cosine = (numerator / denominator);

		return cosine;
	}

	/**
	 * this method creates a new DocumentCorpus object and triggers the building
	 * of the document corpus in the corpusData object.
	 * 
	 * @param normalizer
	 * @param matchingProcess
	 */
	public void initializeDocumentCorpus(Normalizer normalizer,
			MatchingProcess matchingProcess) {

		corpusData = new DocumentCorpus(normalizer, matchingProcess);
		corpusData.iterateOverAllNodes();

	}

	/**
	 * triggers the computation of the IDF weights.
	 */
	public void computeIDFWeigths() {
		corpusData.computeIDFWeigths();
	}

	/**
	 * This method takes a list of tokens and calculates the tf value for every
	 * single token by iterating twice over the given vector to see how often a
	 * token appears in the list.
	 * 
	 * @param tokensOrigin
	 *            the list of tokens which tf (term frequency) values shall be
	 *            calculated.
	 */
	private void computeTFWeigths(List<Token> tokensOrigin) {

		double tokenCounter = 0;

		for (Token actToken : tokensOrigin) {
			tokenCounter = 0;
			if (actToken.getTf() == 0) {
				for (Token actToken2 : tokensOrigin) {
					if (actToken.getWord().equals(actToken2.getWord())) {
						tokenCounter++;
					}
				}
				actToken.setTf(tokenCounter);
			}
		}
	}

}
