package org.pssif.matchingLogic;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.pssif.comparedDataStructures.ComparedNodePair;
import org.pssif.consistencyDataStructures.ConsistencyData;
import org.pssif.consistencyDataStructures.NodeAndType;
import org.pssif.consistencyDataStructures.Token;
import org.pssif.mainProcesses.Methods;
import org.pssif.textNormalization.Normalizer;

import de.tum.pssif.core.common.PSSIFConstants;
import de.tum.pssif.core.metamodel.ConnectionMapping;
import de.tum.pssif.core.metamodel.EdgeType;
import de.tum.pssif.core.metamodel.JunctionNodeType;
import de.tum.pssif.core.metamodel.Metamodel;
import de.tum.pssif.core.metamodel.NodeType;
import de.tum.pssif.core.metamodel.NodeTypeBase;
import de.tum.pssif.core.metamodel.external.PSSIFCanonicMetamodelCreator;
import de.tum.pssif.core.metamodel.impl.ReadFromNodesOperation;
import de.tum.pssif.core.metamodel.impl.ReadToNodesOperation;
import de.tum.pssif.core.model.Edge;
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
 * This class represents the implementation of the context matching metric. It
 * compares the nodes sorrounding the two given ones with the chosen metrics.
 * 
 * @author Andreas
 * 
 */
public class ContextMatcher extends MatchMethod {

	/**
	 * these two variables store the weight with which the syntactic and
	 * semantic results of the sorrounding nodes add to the contextual
	 * similarity result
	 */
	private static final double snytacticWeight = 0.5;
	private static final double semanticWeight = 0.5;

	private List<MatchMethod> matchMethods;
	private Normalizer normalizer;

	private Model originalModel;
	private Model newModel;
	private Metamodel metaModelOriginal;
	private Metamodel metaModelNew;

	private ComparedNodePair tempNodePair;

	/**
	 * the list of nodes sorrounding the original node
	 */
	private Set<NodeAndType> sorroundingNodesOrigin;

	/**
	 * the list of nodes sorrounding the new node
	 */
	private Set<NodeAndType> sorroundingNodesNew;

	NodeTypeBase conjunctionNodeType;

	public ContextMatcher(MatchingMethods matchMethod, boolean isActive,
			double weigth) {
		super(matchMethod, isActive, weigth);
	}

	/**
	 * @param matchMethods
	 *            the matchMethods to set
	 */
	public void setMatchMethods(List<MatchMethod> matchMethods) {
		this.matchMethods = matchMethods;
	}

	/**
	 * @param normalizer
	 *            the normalizer to set
	 */
	public void setNormalizer(Normalizer normalizer) {
		this.normalizer = normalizer;
	}

	/**
	 * This method first gets all neighbours of the two nodes and then compares
	 * each of the neighbours of the original node with every neighbour of the
	 * new node.
	 */
	@Override
	public double executeMatching(Node tempNodeOrigin, Node tempNodeNew,
			Model originalModel, Model newModel, Metamodel metaModelOriginal,
			Metamodel metaModelNew, NodeType actTypeOriginModel,
			NodeType actTypeNewModel, String labelOrigin, String labelNew,
			List<Token> tokensOrigin, List<Token> tokensNew) {

		this.originalModel = originalModel;
		this.newModel = newModel;
		this.metaModelOriginal = metaModelOriginal;
		this.metaModelNew = metaModelNew;

		this.conjunctionNodeType = metaModelOriginal.getBaseNodeType(
				PSSIFCanonicMetamodelCreator.TAGS.get("N_CONJUNCTION")).getOne();

		if ((actTypeOriginModel.getName().equals(conjunctionNodeType.getName()) || (actTypeNewModel
				.getName().equals(conjunctionNodeType.getName())))) {
			return 0.0;
		} else {

			tempNodePair = null;

			sorroundingNodesOrigin = new HashSet<NodeAndType>();
			sorroundingNodesNew = new HashSet<NodeAndType>();

			/**
			 * retrieving all from/to nodes connected with the two nodes
			 */
			sorroundingNodesOrigin = typeIteration(tempNodeOrigin,
					actTypeOriginModel, true, true, true);
			sorroundingNodesNew = typeIteration(tempNodeNew, actTypeNewModel,
					true, true, false);

			/**
			 * calculate the similarity of the contexts and return the result
			 */
			return compareSorroundingNodes(tempNodeOrigin, actTypeOriginModel,
					tempNodeNew, actTypeNewModel, labelOrigin, labelNew);
		}
	}

	/**
	 * this method calls the method findSorroundingNodes() with the current
	 * type. This ensures that all nodes are compared with all from/to nodes of
	 * the edges.
	 * 
	 * @param tempNode
	 *            the node from which sorrounding nodes shall be found
	 * @param incoming
	 *            says whether the incoming edges of the given node shall be
	 *            searched for sorrounding nodes
	 * @param outgoing
	 *            says whether the outgoing edges of the given node shall be
	 *            searched for sorrounding nodes
	 * @param isOriginalNode
	 *            says whteher the given node is from the original model or not
	 * @return the found sorrounding nodes of the given node
	 */
	public Set<NodeAndType> typeIteration(Node tempNode,
			NodeTypeBase typeTempNode, boolean incoming, boolean outgoing,
			boolean isOriginalNode) {

		return findSorroundingNodes(typeTempNode, tempNode, incoming, outgoing,
				isOriginalNode);
	}

	/**
	 * This method calls methods which retrieve and return the incoming and
	 * outgoing neighbourhood of the given node. Then all found neighbours for
	 * the given node are returned.
	 * 
	 * @param type
	 *            the type of the given node
	 * @param tempNode
	 *            the node from which sorrounding nodes shall be found
	 * @param incoming
	 *            says whether the incoming edges of the given node shall be
	 *            searched for sorrounding nodes
	 * @param outgoing
	 *            says whether the outgoing edges of the given node shall be
	 *            searched for sorrounding nodes
	 * @param isOriginalNode
	 *            says whteher the given node is from the original model or not
	 * @return the found sorrounding nodes of the given node
	 */
	private Set<NodeAndType> findSorroundingNodes(NodeTypeBase type,
			Node tempNode, boolean incoming, boolean outgoing,
			boolean isOriginalNode) {

		Set<NodeAndType> result = new HashSet<NodeAndType>();

		if (incoming) {
			if (isOriginalNode) {
				result.addAll(addIncomingNeighbourhood(metaModelOriginal, type,
						tempNode, isOriginalNode));
			} else {
				result.addAll(addIncomingNeighbourhood(metaModelNew, type,
						tempNode, isOriginalNode));
			}
		}
		if (outgoing) {
			if (isOriginalNode) {
				result.addAll(addOutgoingNeighbourhood(metaModelOriginal, type,
						tempNode, isOriginalNode));
			} else {
				result.addAll(addOutgoingNeighbourhood(metaModelNew, type,
						tempNode, isOriginalNode));
			}
		}
		return result;
	}

	@SuppressWarnings("unused")
	private List<NodeAndType> convertSetToLinkedList(Set<Node> setToConvert,
			NodeTypeBase actNodeType) {
		List<NodeAndType> result = new LinkedList<NodeAndType>();

		for (Node tempNode : setToConvert) {
			result.add(new NodeAndType(tempNode, actNodeType));
		}

		return result;
	}

	/**
	 * This method retrieves the incoming neighbourhood of the given node. This
	 * means that nodes which are connected over incoming edges to the given
	 * node are collected and returned. If a conjunction is connected over an
	 * incoming edge to the given node, the incoming neighbours of this
	 * conjunction is collected and returned.
	 * 
	 * @param metamodel
	 *            the metamodel according to the given node
	 * @param nodeType
	 *            the type of the given node
	 * @param nodeOfInterest
	 *            the node from which sorrounding nodes shall be found
	 * @param isOriginalNode
	 *            says whteher the given node is from the original model or not
	 * @return the incoming neighbours of the given node
	 */
	private Set<NodeAndType> addIncomingNeighbourhood(Metamodel metamodel,
			NodeTypeBase nodeType, Node nodeOfInterest, boolean isOriginalNode) {

		EdgeType controlFlow = metaModelOriginal.getEdgeType(
				PSSIFCanonicMetamodelCreator.TAGS.get("E_FLOW_CONTROL")).getOne();

		Set<NodeAndType> result = new HashSet<NodeAndType>();

		for (EdgeType edgeType : metamodel.getEdgeTypes()) {
			for (ConnectionMapping incomingMapping : edgeType
					.getIncomingMappings(nodeType)) {
				for (Edge incomingEdge : incomingMapping
						.applyIncoming(nodeOfInterest)) {

					if (incomingMapping.applyFrom(incomingEdge) != null) {
						if (incomingMapping
								.getFrom()
								.getName()
								.equals(PSSIFCanonicMetamodelCreator.TAGS.get("ENUM_CONJUNCTION"))) {

							for (ConnectionMapping incomingM : controlFlow
									.getIncomingMappings(incomingMapping
											.getFrom())) {
								for (Edge incomingE : incomingM
										.applyIncoming(incomingMapping
												.applyFrom(incomingEdge))) {

									result.add(new NodeAndType(incomingM
											.applyFrom(incomingE), incomingM
											.getFrom()));
								}
							}
						} else {
							if (!nodeType
									.getName()
									.equals(PSSIFCanonicMetamodelCreator.TAGS.get("ENUM_CONJUNCTION"))) {
								result.add(new NodeAndType(incomingMapping
										.applyFrom(incomingEdge),
										incomingMapping.getFrom()));
							}
						}
					}
				}
			}
		}
		return result;
	}

	/**
	 * This method retrieves the outgoing neighbourhood of the given node. This
	 * means that nodes which are connected over outgoing edges to the given
	 * node are collected and returned. If a conjunction is connected over an
	 * outgoing edge to the given node, the outgoing neighbours of this
	 * conjunction is collected and returned.
	 * 
	 * @param metamodel
	 *            the metamodel according to the given node
	 * @param nodeType
	 *            the type of the given node
	 * @param nodeOfInterest
	 *            the node from which sorrounding nodes shall be found
	 * @param isOriginalNode
	 *            says whteher the given node is from the original model or not
	 * @return the incoming neighbours of the given node
	 */
	private Set<NodeAndType> addOutgoingNeighbourhood(Metamodel metamodel,
			NodeTypeBase nodeType, Node nodeOfInterest, boolean isOriginalNode) {

		EdgeType controlFlow = metaModelOriginal.getEdgeType(
				PSSIFCanonicMetamodelCreator.TAGS.get("E_FLOW_CONTROL")).getOne();

		Set<NodeAndType> result = new HashSet<NodeAndType>();

		for (EdgeType edgeType : metamodel.getEdgeTypes()) {
			for (ConnectionMapping outgoingMapping : edgeType
					.getOutgoingMappings(nodeType)) {
				for (Edge outgoingEdge : outgoingMapping
						.applyOutgoing(nodeOfInterest)) {
					if (outgoingMapping.applyTo(outgoingEdge) != null) {
						if (outgoingMapping
								.getTo()
								.getName()
								.equals(PSSIFCanonicMetamodelCreator.TAGS.get("ENUM_CONJUNCTION"))) {

							for (ConnectionMapping outgoingM : controlFlow
									.getOutgoingMappings(outgoingMapping
											.getTo())) {
								for (Edge outgoingE : outgoingM
										.applyOutgoing(outgoingMapping
												.applyTo(outgoingEdge))) {
									result.add(new NodeAndType(outgoingM
											.applyTo(outgoingE), outgoingM
											.getTo()));
								}
							}
						} else {
							if (!nodeType
									.getName()
									.equals(PSSIFCanonicMetamodelCreator.TAGS.get("ENUM_CONJUNCTION"))) {
								result.add(new NodeAndType(outgoingMapping
										.applyTo(outgoingEdge), outgoingMapping
										.getTo()));
							}
						}
					}
				}
			}
		}
		return result;
	}

	/**
	 * This method iterates over all found nodes of the two models and compares
	 * them based on the set matching methods. If two nodes have already been
	 * compared before, the former result is used instead of calculating the
	 * similarities again. After every iteration the similarity value is added
	 * to the contextual result. Then the sum of similarity is divided through
	 * the maximum number of sorrounding nodes of either the original node or
	 * the new node.
	 * 
	 * @param tempNodeOrigin
	 *            the node from the original model
	 * @param actTypeOriginModel
	 *            the type of tempNodeOrigin
	 * @param tempNodeNew
	 *            the node from the new model
	 * @param actTypeNewModel
	 *            the type of tempNodeNew
	 * @param labelOrigin
	 * @param labelNew
	 * @return the contextual similarity between the two given nodes
	 */
	private double compareSorroundingNodes(Node tempNodeOrigin,
			NodeType actTypeOriginModel, Node tempNodeNew,
			NodeType actTypeNewModel, String labelOrigin, String labelNew) {
		double similaritySum = 0;
		double result = 0;

		for (NodeAndType tempNodeSorroundingOrigin : sorroundingNodesOrigin) {
			for (NodeAndType tempNodeSorroundingNew : sorroundingNodesNew) {

				if (nodesAlreadyCompared(tempNodeSorroundingOrigin.getNode(),
						tempNodeSorroundingNew.getNode())) {
					similaritySum += calculateWeightedSimilarities();
				} else {
					similaritySum += computeSimilarity(
							tempNodeSorroundingOrigin.getNode(),
							tempNodeSorroundingOrigin.getType(),
							tempNodeSorroundingNew.getNode(),
							tempNodeSorroundingNew.getType());
				}
			}
		}
		double denominator = Math.max(sorroundingNodesOrigin.size(),
				sorroundingNodesNew.size());

		if (denominator != 0) {
			result = (similaritySum / denominator);
		} else {
			return 0;
		}
		if (result > 1) {
			return 1;
		} else {
			return result;
		}
	}

	/**
	 * This method calculates the similarity of two nodes based on the given
	 * match methods. It work's similar as to the method in
	 * MatchingProcess.java.
	 * 
	 * @param tempNodeOrigin
	 *            the node from the original model
	 * @param actTypeOriginModel
	 *            the node type of the original node
	 * @param tempNodeNew
	 *            the node from the new model
	 * @param actTypeNewModel
	 *            the node type of the new node
	 * @return the similarity of two given nodes based on the chosen matching
	 *         methods
	 */
	private double computeSimilarity(Node tempNodeOrigin,
			NodeTypeBase actTypeOriginModelBase, Node tempNodeNew,
			NodeTypeBase actTypeNewModelBase) {

		double result = 0;
		double currentMetricResult = 0;

		NodeType actTypeOriginModel = (NodeType) actTypeOriginModelBase;
		NodeType actTypeNewModel = (NodeType) actTypeNewModelBase;

		/**
		 * here the strings of the old and the new node are read from the model
		 */
		String labelOriginNode = Methods.findName(actTypeOriginModel,
				tempNodeOrigin);
		String labelNewNode = Methods.findName(actTypeNewModel, tempNodeNew);

		String labelOriginNodeNormalized, labelNewNodeNormalized;

		/**
		 * variables to store the different normalized tokens
		 */
		List<Token> tokensOriginNodeNormalized = normalizer
				.createNormalizedTokensFromLabel(labelOriginNode, true, true,
						true, true);
		List<Token> tokensNewNodeNormalized = normalizer
				.createNormalizedTokensFromLabel(labelNewNode, true, true,
						true, true);
		List<Token> tokensOriginNodeNormalizedCompundedUnstemmed = normalizer
				.createNormalizedTokensFromLabel(labelOriginNode, true, true,
						false, false);
		List<Token> tokensNewNodeNormalizedCompundedUnstemmed = normalizer
				.createNormalizedTokensFromLabel(labelNewNode, true, true,
						false, false);

		labelOriginNodeNormalized = normalizer.normalizeLabel(labelOriginNode);

		labelNewNodeNormalized = normalizer.normalizeLabel(labelNewNode);

		Iterator<MatchMethod> currentMatchMethod = matchMethods.iterator();

		/**
		 * Here the tokenized forms are created if necessary. Thereby it's
		 * important to note that the syntactic metrics require a normalization
		 * without compund splitting and without stemming. Whereas the semantic
		 * metrics require full normalization.
		 * 
		 * Then every active match Method is applied to the two nodes here.
		 */
		while (currentMatchMethod.hasNext()) {
			MatchMethod currentMethod = currentMatchMethod.next();
			currentMetricResult = 0;

			/**
			 * Depending on the current match method different token sets of the
			 * labels have to be given to the match method. If the method is
			 * StringEditDistance or Hyphen we don't use token normalization
			 * with compoundsplitting or stemming. Thatfore we have to separate
			 * these two cases.
			 */
			if (currentMethod.isActive()) {
				if (currentMethod.getMatchMethod() == MatchingMethods.CONTEXT_MATCHING) {
					continue;
				}
				if ((currentMethod.getMatchMethod() == MatchingMethods.STRING_EDIT_DISTANCE_MATCHING)
						|| (currentMethod.getMatchMethod() == MatchingMethods.HYPHEN_MATCHING)) {

					currentMetricResult = currentMethod.executeMatching(
							tempNodeOrigin, tempNodeNew, originalModel,
							newModel, metaModelOriginal, metaModelNew,
							actTypeOriginModel, actTypeNewModel,
							labelOriginNodeNormalized, labelNewNodeNormalized,
							tokensOriginNodeNormalizedCompundedUnstemmed,
							tokensNewNodeNormalizedCompundedUnstemmed);
				} else {

					currentMetricResult = currentMethod
							.executeMatching(tempNodeOrigin, tempNodeNew,
									originalModel, newModel, metaModelOriginal,
									metaModelNew, actTypeOriginModel,
									actTypeNewModel, labelOriginNodeNormalized,
									labelNewNodeNormalized,
									tokensOriginNodeNormalized,
									tokensNewNodeNormalized);
				}

				switch (currentMethod.getMatchMethod()) {
				case EXACT_STRING_MATCHING:
				case DEPTH_MATCHING:
				case STRING_EDIT_DISTANCE_MATCHING:
				case HYPHEN_MATCHING:
					result += ((currentMetricResult * currentMethod.getWeigth()));
					break;
				case LINGUISTIC_MATCHING:
				case VECTOR_SPACE_MODEL_MATCHING:
				case LATENT_SEMANTIC_INDEXING_MATCHING:
				case ATTRIBUTE_MATCHING:
					result += ((currentMetricResult * currentMethod.getWeigth()));
					break;
				default:
					break;
				}
			}
		}
		return result;
	}

	/**
	 * If two nodes have already been compared, their compairson result is
	 * retrieved and used instead of calculating the similarities again.
	 * 
	 * @return The former weighted similarity score for the two nodes.
	 */
	private double calculateWeightedSimilarities() {
		double result = 0;

		if (normalizer.isSyntacticMetricActive()
				&& normalizer.isSemanticMetricActive()) {
			result += (getWeightedSyntacticSimilarity() * snytacticWeight);
			result += (getWeightedSemanticSimilarity() * semanticWeight);
		} else {
			if (normalizer.isSyntacticMetricActive()) {
				result += getWeightedSyntacticSimilarity();
			} else {
				result += getWeightedSemanticSimilarity();
			}
		}

		return result;
	}

	/**
	 * This method looks up whether two nodes have already been compared. If yes
	 * the result is stored in the variable tempNodePair. It can then be used by
	 * the context matcher to look up results from past compairsons.
	 * 
	 * @return whether the two nodes have already been compared or not
	 */
	private boolean nodesAlreadyCompared(Node tempNodeOrigin, Node tempNodeNew) {

		this.tempNodePair = ConsistencyData.getInstance().getNodeMatch(
				tempNodeOrigin, tempNodeNew);

		return (this.tempNodePair != null);
	}

	/**
	 * @return The weighted combination of all results of the syntactic match
	 *         methods.
	 */
	public double getWeightedSyntacticSimilarity() {
		double result = 0;

		double exactMatch = tempNodePair.getLabelComparison()
				.getExactMatchResult();
		double depthMatch = tempNodePair.getDepthMatchResult();
		double stringEditDistanceMatch = tempNodePair.getTokensComparison()
				.getStringEditDistanceResult();
		double hyphenMatch = tempNodePair.getTokensComparison()
				.getHyphenMatchResult();

		Iterator<MatchMethod> currentMatchMethod = matchMethods.iterator();

		while (currentMatchMethod.hasNext()) {
			MatchMethod currentMethod = currentMatchMethod.next();

			switch (currentMethod.getMatchMethod()) {
			case EXACT_STRING_MATCHING:
				result += currentMethod.getWeigth() * exactMatch;
				break;
			case DEPTH_MATCHING:
				result += currentMethod.getWeigth() * depthMatch;
				break;
			case STRING_EDIT_DISTANCE_MATCHING:
				result += currentMethod.getWeigth() * stringEditDistanceMatch;
				break;
			case HYPHEN_MATCHING:
				result += currentMethod.getWeigth() * hyphenMatch;
				break;
			default:
				;
			}
		}
		return result;
	}

	/**
	 * @return The weighted combination of all results of the semantic match
	 *         methods.
	 */
	public double getWeightedSemanticSimilarity() {
		double result = 0;

		double linguisticMatch = tempNodePair.getTokensComparison()
				.getLinguisticMatchResult();
		double vsmMatch = tempNodePair.getTokensComparison()
				.getVsmMatchResult();
		double lsiMatch = tempNodePair.getTokensComparison()
				.getLsiMatchResult();
		double attributeMatch = tempNodePair.getAttributeMatchResult();

		Iterator<MatchMethod> currentMatchMethod = matchMethods.iterator();

		while (currentMatchMethod.hasNext()) {
			MatchMethod currentMethod = currentMatchMethod.next();

			switch (currentMethod.getMatchMethod()) {
			case LINGUISTIC_MATCHING:
				result += currentMethod.getWeigth() * linguisticMatch;
				break;
			case VECTOR_SPACE_MODEL_MATCHING:
				result += currentMethod.getWeigth() * vsmMatch;
				break;
			case LATENT_SEMANTIC_INDEXING_MATCHING:
				result += currentMethod.getWeigth() * lsiMatch;
				break;
			case ATTRIBUTE_MATCHING:
				result += currentMethod.getWeigth() * attributeMatch;
				break;
			default:
				;
			}
		}

		return result;
	}
}
