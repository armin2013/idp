package org.pssif.consistencyDataStructures;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.pssif.consistencyExceptions.MatchMethodException;
import org.pssif.mainProcesses.MatchingProcess;
import org.pssif.mainProcesses.Methods;
import org.pssif.textNormalization.Normalizer;

import de.tum.pssif.core.common.PSSIFConstants;
import de.tum.pssif.core.common.PSSIFOption;
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
 * This class holds the data necessary for the VSM metric. This includes the
 * total number of documents in the document space and the vocabulary of the
 * document space.
 * 
 * @author Andreas
 * 
 */
public class DocumentCorpus {

	private LinkedList<Token> tokens;
	/**
	 * this variable counts the total number of documents(documents are here
	 * represented by nodes)
	 */
	private double numberOfDocuments;

	private MatchingProcess matchingProcess;
	private Normalizer normalizer;

	public DocumentCorpus(Normalizer normalizer, MatchingProcess matchingProcess) {

		this.normalizer = normalizer;
		this.matchingProcess = matchingProcess;
		this.tokens = new LinkedList<Token>();

	}

	/**
	 * This method searches for all nodes of the two models which shall be
	 * merged and calls a method to add all different tokens to the vocabulary
	 * list.
	 */
	public void iterateOverAllNodes() {
		Model originalModel = matchingProcess.getOriginalModel();
		Model newModel = matchingProcess.getNewModel();
		Metamodel metaModel = matchingProcess.getMetaModelOriginal();

		addNodes(PSSIFConstants.ROOT_NODE_TYPE_NAME, originalModel, metaModel);

		addNodes(PSSIFConstants.ROOT_NODE_TYPE_NAME, newModel, metaModel);
	}

	/**
	 * This method get's all nodes of the given type from the given model and
	 * calls a method addTokens() to add the tokens of a node to the vocabulary
	 * list.
	 * 
	 * @param type
	 *            the type of nodes which are looked up in the given model to do
	 *            the compairson
	 * @param model
	 *            the model in which the nodes of the given type shall be
	 *            searched
	 * @param metaModel
	 *            the according metamodel of the model
	 * 
	 */
	private void addNodes(String type, Model model, Metamodel metaModel) {

		NodeType actTypeModel;
		PSSIFOption<Node> actNodesModel;

		actTypeModel = metaModel.getNodeType(type).getOne();

		actNodesModel = actTypeModel.apply(model, true);

		if (actNodesModel.isNone()) {
			throw new MatchMethodException(
					"Document corpus for IR Methods couldn't be built because there are no nodes in one of the models");
		} else {
			if (actNodesModel.isOne()) {

				Node tempNode = actNodesModel.getOne();

				addTokens(tempNode, actTypeModel);

				numberOfDocuments++;

			} else {

				Set<Node> tempNodes = actNodesModel.getMany();

				Iterator<Node> tempNode = tempNodes.iterator();

				while (tempNode.hasNext()) {

					addTokens(tempNode.next(), actTypeModel);

					numberOfDocuments++;
				}

			}
		}
	}

	/**
	 * 
	 * This method get's the label of the given node and creates the normalized
	 * tokens of it. Then every token is added to the vocabulary list.
	 * 
	 * 
	 * @param tempNode
	 *            the node of which the tokens shall be added to the vocabulary.
	 * @param actTypeNode
	 *            the type of the node.
	 * 
	 */
	private void addTokens(Node tempNode, NodeType actTypeNode) {
		String labelNode = Methods.findName(actTypeNode, tempNode);

		List<Token> tempTokens = normalizer.createNormalizedTokensFromLabel(
				labelNode, true, true, true, true);

		for (Token token : tempTokens) {
			addTokenToData(token);
		}
	}

	/**
	 * This method adds the given token to the vocabulary list. But if a token
	 * with the same word is already present in the list the token in the list
	 * is modified and no additional token is added. Everytime a token is added
	 * or updated this tokens method incrementDocumentCounter() is called
	 * because the token is present in (currentDocumentCounter + 1) documents.
	 * 
	 * @param token
	 *            the token to add to the vocabulary list
	 */
	private void addTokenToData(Token token) {
		boolean tokenAlreadyStored = false;

		if (tokens.isEmpty()) {
			tokens.add(token);
		} else {

			for (Token savedToken : tokens) {
				if (savedToken.getWord().equals(token.getWord())) {
					tokenAlreadyStored = true;
					savedToken.incrementDocumentCounter();
					break;
				}
			}
			if (!tokenAlreadyStored) {
				token.incrementDocumentCounter();
				tokens.add(token);
			}
		}

	}

	/**
	 * This method calculates the idf (inverse document frequency) weights of
	 * every token in the vocabulary list.
	 */
	public void computeIDFWeigths() {
		double idf = 0;

		for (Token savedToken : tokens) {
			idf = Math.log(numberOfDocuments
					/ (savedToken.getDocumentCounter()));

			savedToken.setIdf(idf);
		}
	}

	/**
	 * This method computes out of the given token list and the internal
	 * vocabulary token list a specific list of tokens with general idf and
	 * specialized tf weights. This vector contains mainly null values because
	 * only the tokens present in the given token list will be contained by the
	 * full token list. This list is then used for the vsm model.
	 * 
	 * @param tokensOrigin
	 *            the list of tokens for which a full token list of the
	 *            vocabulary of the document space shall be generated.
	 * @return the full token list
	 */
	public List<Token> getFullTokenList(List<Token> tokensOrigin) {
		List<Token> result = new LinkedList<Token>();
		boolean tokenFound = false;
		Token tempOrigin;

		for (Token savedToken : tokens) {
			tokenFound = false;

			for (int i = 0; i < tokensOrigin.size(); i++) {
				tempOrigin = tokensOrigin.get(i);

				if (tempOrigin.getWord().equals(savedToken.getWord())) {
					tokenFound = true;

					tempOrigin.setIdf(savedToken.getIdf());
					tempOrigin.computeWordWeigth();

					result.add(tempOrigin);

					break;
				}
				if ((!tokenFound) && (i == (tokensOrigin.size() - 1))) {
					result.add(null);
				}
			}
		}
		return result;
	}

}
