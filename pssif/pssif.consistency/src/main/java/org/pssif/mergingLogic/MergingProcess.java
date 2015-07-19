package org.pssif.mergingLogic;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.pssif.comparedDataStructures.ComparedLabelPair;
import org.pssif.consistencyDataStructures.ConsistencyData;
import org.pssif.consistencyDataStructures.NodeAndType;
import org.pssif.consistencyDataStructures.Token;
import org.pssif.consistencyExceptions.ConsistencyException;
import org.pssif.mainProcesses.Methods;
import org.pssif.matchingLogic.MatchMethod;
import org.pssif.matchingLogic.MatchingMethods;
import org.pssif.mergedDataStructures.MergedNodePair;
import org.pssif.settings.Constants;
import org.pssif.textNormalization.Normalizer;

import de.tum.pssif.core.common.PSSIFConstants;
import de.tum.pssif.core.common.PSSIFOption;
import de.tum.pssif.core.exception.PSSIFIllegalAccessException;
import de.tum.pssif.core.exception.PSSIFStructuralIntegrityException;
import de.tum.pssif.core.metamodel.ConnectionMapping;
import de.tum.pssif.core.metamodel.EdgeType;
import de.tum.pssif.core.metamodel.JunctionNodeType;
import de.tum.pssif.core.metamodel.Metamodel;
import de.tum.pssif.core.metamodel.NodeType;
import de.tum.pssif.core.metamodel.NodeTypeBase;
import de.tum.pssif.core.metamodel.external.PSSIFCanonicMetamodelCreator;
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
 * 
 * This class conducts the merging process between two models. Therefore it
 * compares every node of the first imported model with every node of the same
 * type in the recently imported model. Depending on the similarity result of
 * every compared node pair the pair is marked as to be linked by a tracelink or
 * marked as to be merged or as to be transferred to the new model.
 * 
 * @author Andreas
 * 
 */
public class MergingProcess {

	private static final boolean debugMode = false;

	/**
	 * the first imported model
	 */
	private Model originalModel;

	/**
	 * the recently imported model
	 */
	private Model newModel;

	private Metamodel metaModelOriginal, metaModelNew;

	private Normalizer normalizer;

	/**
	 * a list with the match methods which are applied in merging process
	 */
	private List<MatchMethod> matchMethods;

	/**
	 * the methods applied in model merging
	 */
	private MatchMethod exactMatcher;
	private MatchMethod attributeMatcher;
	private MatchMethod stringEditDistanceMatcher;

	/**
	 * a list with all nodes from the original model with their according type.
	 * It's needed to transfer all nodes who weren't matched or traced with a
	 * new node in the new model.
	 */
	private List<NodeAndType> allNodesOrigin;

	/**
	 * Here a new MergingProcess object is created, a normalizer is initialized
	 * with the according match methods (exact, attribute, stringEdit matcher)
	 * and the merging process is started.
	 * 
	 * In the merging process first the node pairs are searched which have to be
	 * merged, traced or transferred. Then the junction nodes of the origin
	 * model are checked if they have to be transferred to the new model.
	 * 
	 * @param originalModel
	 *            the first imported model
	 * @param newModel
	 *            the recent imported model
	 * @param metaModelOriginal
	 *            the metamodel of the first model
	 * @param metaModelNew
	 *            the metamodel of the new model
	 */
	public MergingProcess(Model originalModel, Model newModel,
			Metamodel metaModelOriginal, Metamodel metaModelNew) {
		this.originalModel = originalModel;
		this.newModel = newModel;
		this.metaModelOriginal = metaModelOriginal;
		this.metaModelNew = metaModelNew;

		this.allNodesOrigin = new LinkedList<NodeAndType>();

		initializeMatchMethods();

		this.normalizer = Normalizer.initialize(matchMethods);

		fillAllNodesOrigin();

		startTypeAndNodeIteration();

		ConsistencyData.getInstance().resetUnmatchedJunctionnodesOrigin();

		handleConjunctions();

		ConsistencyData.getInstance().setAllNodesOrigin(allNodesOrigin);
	}

	private void fillAllNodesOrigin() {

		PSSIFOption<Node> nodesOriginalModel;

		for (NodeType actType : metaModelOriginal.getNodeTypes()) {
			nodesOriginalModel = actType.apply(originalModel, false);

			if (nodesOriginalModel.isOne()) {
				allNodesOrigin.add(new NodeAndType(nodesOriginalModel.getOne(),
						actType));
			}
			if (nodesOriginalModel.isMany()) {

				for (Node actNode : nodesOriginalModel.getMany()) {
					allNodesOrigin.add(new NodeAndType(actNode, actType));
				}
			}
		}
	}

	/**
	 * This method starts the matching of every junction node from the original
	 * model with every junction node of the new model to see which junction
	 * nodes aren't in the new model anymore and have to be copied into the new
	 * model.
	 */
	private void handleConjunctions() {
		JunctionNodeType junctionNodeType = metaModelOriginal
				.getJunctionNodeType(PSSIFCanonicMetamodelCreator.TAGS.get("N_CONJUNCTION"))
				.getOne();

		PSSIFOption<Node> junctionNodesOriginalModel;

		junctionNodesOriginalModel = junctionNodeType
				.apply(originalModel, true);

		if (junctionNodesOriginalModel.isNone()) {
			if (debugMode) {
				System.out
						.println("There are no nodes of the type \""
								+ junctionNodeType.getName()
								+ "\" in the original model. Continuing with the next type");
			}
		} else {
			if (junctionNodesOriginalModel.isOne()) {
				if (debugMode) {
					System.out
							.println("There is one node of the type \""
									+ junctionNodeType.getName()
									+ "\" in the original model. Comparing with new model.");
				}

				Node tempNodeOrigin = junctionNodesOriginalModel.getOne();

				if (compareWithJunctionsOfNewModel(tempNodeOrigin,
						junctionNodeType)) {
					// the junction node is still in the new model. We have
					// nothing to do at this point
				} else {
					ConsistencyData.getInstance()
							.putUnmatchedJunctionnodeEntry(
									new NodeAndType(tempNodeOrigin,
											junctionNodeType));
				}

			} else {
				if (debugMode) {
					System.out
							.println("There are many nodes of the type \""
									+ junctionNodeType.getName()
									+ "\" in the original model. Comparing them with new model.");
				}

				Set<Node> tempNodesOrigin = junctionNodesOriginalModel
						.getMany();

				Iterator<Node> tempNodeOriginIterator = tempNodesOrigin
						.iterator();

				while (tempNodeOriginIterator.hasNext()) {

					Node tempNodeOrigin = tempNodeOriginIterator.next();

					if (compareWithJunctionsOfNewModel(tempNodeOrigin,
							junctionNodeType)) {
						// the junction node is still in the new model. We have
						// nothing to do at this point
					} else {
						ConsistencyData.getInstance()
								.putUnmatchedJunctionnodeEntry(
										new NodeAndType(tempNodeOrigin,
												junctionNodeType));
					}
				}
			}
		}
	}

	/**
	 * This method iterates over every junction node of the new model. Then it
	 * compares the given junction node with every from the new model.
	 * 
	 * @param tempNodeOrigin
	 *            the junction node of the first imported model
	 * @param junctionNodeType
	 *            the type of the node
	 * @return whether the given node has any corresponding junction nodes in
	 *         the new model
	 */
	private boolean compareWithJunctionsOfNewModel(Node tempNodeOrigin,
			JunctionNodeType junctionNodeType) {
		PSSIFOption<Node> junctionNodesNewModel;

		junctionNodesNewModel = junctionNodeType.apply(newModel, true);

		boolean matchFound = false;

		if (junctionNodesNewModel.isNone()) {
			if (debugMode) {
				System.out.println("There are no nodes of the type \""
						+ junctionNodeType.getName()
						+ "\" in the new model. Continuing with the next type");
			}
		} else {
			if (junctionNodesNewModel.isOne()) {
				if (debugMode) {
					System.out
							.println("There is one node of the type \""
									+ junctionNodeType.getName()
									+ "\" in the new model. Comparing with given node.");
				}

				Node tempNodeNew = junctionNodesNewModel.getOne();

				return matchJunctionnodes(junctionNodeType, tempNodeOrigin,
						tempNodeNew);

			} else {
				if (debugMode) {
					System.out
							.println("There are many nodes of the type \""
									+ junctionNodeType.getName()
									+ "\" in the new model. Comparing them with given node.");
				}
				boolean anyMatch = false;

				Set<Node> tempNodesNew = junctionNodesNewModel.getMany();

				Iterator<Node> tempNodeNewIterator = tempNodesNew.iterator();

				while (tempNodeNewIterator.hasNext()) {

					Node tempNodeNew = tempNodeNewIterator.next();

					matchJunctionnodes(junctionNodeType, tempNodeOrigin,
							tempNodeNew);

					anyMatch = anyMatch
							|| matchJunctionnodes(junctionNodeType,
									tempNodeOrigin, tempNodeNew);

				}
				return anyMatch;
			}
		}

		return matchFound;

	}

	/**
	 * This method compares the context of the two given junction nodes to check
	 * if the two nodes can be assumed being equal.
	 * 
	 * @param junctionNodeType
	 *            the type of the nodes
	 * @param tempNodeOrigin
	 *            the junction node of the firstly imported model
	 * @param tempNodeNew
	 *            the junction node of the new model
	 * @return whether the two given nodes have at least one incoming and one
	 *         outgoing node in common
	 */
	private boolean matchJunctionnodes(JunctionNodeType junctionNodeType,
			Node tempNodeOrigin, Node tempNodeNew) {
		boolean matchFound = false;
		boolean incomingMatch = false;
		boolean outgoingMatch = false;

		for (EdgeType edgeType : metaModelNew.getEdgeTypes()) {
			for (ConnectionMapping incomingMapping : edgeType
					.getIncomingMappings(junctionNodeType)) {
				for (Edge incomingEdge : incomingMapping
						.applyIncoming(tempNodeOrigin)) {

					for (Edge incomingEdgeNew : incomingMapping
							.applyIncoming(tempNodeNew)) {
						incomingMatch = incomingMatch
								|| applyMatchMethodsToJunctionContext(
										incomingMapping.applyFrom(incomingEdge),
										junctionNodeType, incomingMapping
												.applyFrom(incomingEdgeNew),
										junctionNodeType);
					}
				}
			}
			for (ConnectionMapping outgoingMapping : edgeType
					.getOutgoingMappings(junctionNodeType)) {
				for (Edge outgoingEdge : outgoingMapping
						.applyOutgoing(tempNodeOrigin)) {

					for (Edge outgoingEdgeNew : outgoingMapping
							.applyOutgoing(tempNodeNew)) {
						outgoingMatch = outgoingMatch
								|| applyMatchMethodsToJunctionContext(
										outgoingMapping.applyTo(outgoingEdge),
										junctionNodeType,
										outgoingMapping
												.applyFrom(outgoingEdgeNew),
										junctionNodeType);
					}
				}
			}

		}
		matchFound = incomingMatch && outgoingMatch;
		return matchFound;
	}

	/**
	 * This method compares two given nodes and returns true if they are fully
	 * equal.
	 * 
	 * @param contextOrigin
	 *            the sorrounding node of the origin junction node
	 * @param typeOrigin
	 *            the type of the origin node
	 * @param contextNew
	 *            the sorrounding node of the new junction node
	 * @param typeNew
	 *            the type of the new node
	 * @return true if the two given nodes are fully equals. False otherwise.
	 */
	private boolean applyMatchMethodsToJunctionContext(Node contextOrigin,
			NodeTypeBase typeOrigin, Node contextNew, NodeTypeBase typeNew) {
		if ((typeOrigin.getName().equals(
				PSSIFCanonicMetamodelCreator.TAGS.get("N_CONJUNCTION")) && typeOrigin
				.getName().equals(typeNew.getName()))) {
			// both contextual nodes of the junctions are junctions, so they are
			// assumed
			// to be equal
			return true;
		}

		return (attributeMatcher.executeMatching(contextOrigin, contextNew,
				originalModel, newModel, metaModelOriginal, metaModelNew,
				(NodeType) typeOrigin, (NodeType) typeNew,
				Methods.findName(typeOrigin, contextOrigin),
				Methods.findName(typeNew, contextNew), null, null) >= 1)
				&& (exactMatcher.executeMatching(contextOrigin, contextNew,
						originalModel, newModel, metaModelOriginal,
						metaModelNew, (NodeType) typeOrigin,
						(NodeType) typeNew,
						Methods.findName(typeOrigin, contextOrigin),
						Methods.findName(typeNew, contextNew), null, null) >= 1);
	}

	/**
	 * creates the exact & attribute & stringEditDistance match method and adds
	 * them to the matchMethods list
	 */
	private void initializeMatchMethods() {
		matchMethods = new LinkedList<MatchMethod>();

		exactMatcher = MatchMethod.createMatchMethodObject(
				MatchingMethods.EXACT_STRING_MATCHING, true, 1.0);

		matchMethods.add(exactMatcher);

		attributeMatcher = MatchMethod.createMatchMethodObject(
				MatchingMethods.ATTRIBUTE_MATCHING, true, 1.0);

		matchMethods.add(attributeMatcher);

		stringEditDistanceMatcher = MatchMethod.createMatchMethodObject(
				MatchingMethods.STRING_EDIT_DISTANCE_MATCHING, true, 1.0);

		matchMethods.add(stringEditDistanceMatcher);

	}

	/**
	 * this method checks if the original Model contains at least one node and
	 * then calls the typeIteration Method. If the original model is empty no
	 * merge can be conducted so no further methods are called for compairson
	 */
	public void startTypeAndNodeIteration() {
		NodeType rootNodeType = metaModelOriginal.getNodeType(
				PSSIFConstants.ROOT_NODE_TYPE_NAME).getOne();

		PSSIFOption<Node> nodesOriginalModel = rootNodeType.apply(
				originalModel, true);

		if (nodesOriginalModel.isNone()) {
			if (debugMode) {
				System.out.println("no nodes in the original model");
			}

			throw new PSSIFStructuralIntegrityException(
					"No nodes could be found in the original model. This should never happen. "
							+ "Maybe the root node type was changed.");
		} else {
			if (nodesOriginalModel.isOne()) {
				if (debugMode) {
					System.out.println("one node in the original model");
				}
			} else if (nodesOriginalModel.isMany()) {
				if (debugMode) {
					System.out.println("many nodes in the original model");
				}
			} else {
				// doesn't happen
			}
			typeIteration();

		}
	}

	/**
	 * this method iterates over every node type of the pssif metamodel and
	 * calls the method iterateNodesOfType() with the current type. This ensures
	 * that all nodes from the original model are compared with all nodes of the
	 * same type of the new model.
	 * 
	 * It starts with the children of the Type DevelopmentArtifact and then
	 * continues with the children of the Type Solution Artifact. After that the
	 * Development and Solution Artifact Type and finally the Node Type are
	 * iterated.
	 */
	public void typeIteration() {

		Constants.initialize();
		
		for (int i = 0; i < Constants.PSIFFDevArtifactSubClasses.length; i++) {
			iterateNodesOfType(Constants.PSIFFDevArtifactSubClasses[i], false);
		}

		for (int i = 0; i < Constants.PSIFFSolArtifactSubClasses.length; i++) {
			iterateNodesOfType(Constants.PSIFFSolArtifactSubClasses[i], false);
		}

		iterateNodesOfType(PSSIFCanonicMetamodelCreator.TAGS.get("N_DEV_ARTIFACT"), false);

		iterateNodesOfType(PSSIFCanonicMetamodelCreator.TAGS.get("N_SOL_ARTIFACT"), false);
		iterateNodesOfType(PSSIFConstants.ROOT_NODE_TYPE_NAME, false);
	}

	/**
	 * this method gets all nodes from the original model with the given type.
	 * If there is no node with the type in the model the method is finished. If
	 * there is one node with the type in the model the node is given to the
	 * method iterateOverTypesOfNewModel(). If there are many nodes each node is
	 * given seperately to the method iterateOverTypesOfNewModel().
	 * 
	 * @param type
	 *            the type of nodes which are looked up in the original model to
	 *            do the compairson
	 * @param includeSubtypes
	 *            a bool specifying whether nodes with a subtype of type shall
	 *            too be part of the compairson process
	 */
	public void iterateNodesOfType(String type, boolean includeSubtypes) {

		NodeType actTypeOriginModel;
		PSSIFOption<Node> actNodesOriginalModel;

		if (metaModelOriginal.getNodeType(type).isOne()) {
			actTypeOriginModel = metaModelOriginal.getNodeType(type).getOne();
		} else {
			throw new PSSIFIllegalAccessException(
					"The type \""
							+ type
							+ "\" couln't be found in the original model. Maybe the type was deleted in the metamodel.");
		}

		actNodesOriginalModel = actTypeOriginModel.apply(originalModel,
				includeSubtypes);

		if (actNodesOriginalModel.isNone()) {
			if (debugMode) {
				System.out
						.println("There are no nodes of the type \""
								+ type
								+ "\" in the original model. Continuing with the next type");
			}
		} else {
			if (actNodesOriginalModel.isOne()) {
				if (debugMode) {
					System.out
							.println("There is one node of the type \""
									+ type
									+ "\" in the original model. Comparing with new model.");
				}

				Node tempNodeOrigin = actNodesOriginalModel.getOne();

				iterateOverNodesOfNewModel(tempNodeOrigin, actTypeOriginModel);

			} else {
				if (debugMode) {
					System.out
							.println("There are many nodes of the type \""
									+ type
									+ "\" in the original model. Comparing them with new model.");
				}

				Set<Node> tempNodesOrigin = actNodesOriginalModel.getMany();

				Iterator<Node> tempNodeOriginIterator = tempNodesOrigin
						.iterator();

				while (tempNodeOriginIterator.hasNext()) {

					Node tempNodeOrigin = tempNodeOriginIterator.next();

					iterateOverNodesOfNewModel(tempNodeOrigin,
							actTypeOriginModel);

				}
			}
		}
	}

	/**
	 * nodes of the actual type are searched in the new model to compare them
	 * with the original node.
	 * 
	 * @param tempNodeOrigin
	 *            the node of the first model which is compared to nodes in the
	 *            new model
	 * @param actTypeOriginModel
	 *            the type of the node
	 */
	public void iterateOverNodesOfNewModel(Node tempNodeOrigin,
			NodeType actTypeOriginModel) {

		NodeType actTypeNewModel = actTypeOriginModel;

		PSSIFOption<Node> actNodesNewModel = actTypeNewModel.apply(newModel,
				false);

		matchNodeWithNodesOfActTypeOfNewModel(tempNodeOrigin, actNodesNewModel,
				actTypeOriginModel, actTypeNewModel);
	}

	/**
	 * 
	 * This method gets real nodes from the psiffOption and calls the method
	 * matchNodeWithNode.
	 * 
	 * @param tempNodeOrigin
	 *            node from the original model
	 * @param actNodesNewModel
	 *            the nodes in the new model with the same type as the original
	 *            node
	 * @param actTypeOriginModel
	 *            the type of the original node
	 * @param actTypeNewModel
	 *            the type of the nodes in the list actNodesNewModel
	 */
	public void matchNodeWithNodesOfActTypeOfNewModel(Node tempNodeOrigin,
			PSSIFOption<Node> actNodesNewModel, NodeType actTypeOriginModel,
			NodeType actTypeNewModel) {
		if (actNodesNewModel.isNone()) {
			if (debugMode) {
				System.out
						.println("There is no node in the new model of the type \""
								+ actTypeNewModel.getName()
								+ "\" to match. Continuing with next node from the original model.");
			}
		} else {
			if (actNodesNewModel.isOne()) {
				if (debugMode) {
					System.out
							.println("There is one node in the new model of the type \""
									+ actTypeNewModel.getName()
									+ "\" to match. Start matching.");
				}

				matchNodeWithNode(tempNodeOrigin, actNodesNewModel.getOne(),
						actTypeOriginModel, actTypeNewModel);

			} else {
				if (debugMode) {
					System.out
							.println("There are many nodes in the new model of the type \""
									+ actTypeNewModel.getName()
									+ "\" to match. Start matching.");
				}

				Set<Node> tempNodesNew = actNodesNewModel.getMany();

				Iterator<Node> tempNodeNew = tempNodesNew.iterator();

				while (tempNodeNew.hasNext()) {

					matchNodeWithNode(tempNodeOrigin, tempNodeNew.next(),
							actTypeOriginModel, actTypeNewModel);
				}

			}
		}
	}

	/**
	 * If the two given nodes haven't already been matched they are given to a
	 * matching function.
	 * 
	 * @param tempNodeOrigin
	 *            a node from the original model
	 * @param tempNodeNew
	 *            a node from the new model
	 * @param actTypeOriginModel
	 *            the type of the original node
	 * @param actTypeNewModel
	 *            the type of the new node
	 */
	public void matchNodeWithNode(Node tempNodeOrigin, Node tempNodeNew,
			NodeType actTypeOriginModel, NodeType actTypeNewModel) {

		String globalIDNodeOrigin = Methods.findGlobalID(tempNodeOrigin,
				actTypeOriginModel);
		String globalIDNodeNew = Methods.findGlobalID(tempNodeNew,
				actTypeNewModel);

		if (ConsistencyData.getInstance().matchNecessary(globalIDNodeOrigin,
				globalIDNodeNew)) {
			matchNodes(tempNodeOrigin, tempNodeNew, actTypeOriginModel,
					actTypeNewModel);
		} else {
			if (debugMode) {
				System.out
						.println("These two nodes have already been compared.");
			}
		}

	}

	/**
	 * This method conducts the similarity analysis of the two given nodes with
	 * help of the matchMethods. Then, depending on the result they are marked
	 * as to be traced, merged or nothing.
	 * 
	 * After that the result is stored in the consistencyData instance.
	 * 
	 * @param tempNodeOrigin
	 *            a node from the original model
	 * @param tempNodeNew
	 *            a node from the new model
	 * @param actTypeOriginModel
	 *            the type of the original node
	 * @param actTypeNewModel
	 *            the type of the new node
	 */
	private void matchNodes(Node tempNodeOrigin, Node tempNodeNew,
			NodeType actTypeOriginModel, NodeType actTypeNewModel) {

		String labelOrigin = Methods.findName(actTypeOriginModel,
				tempNodeOrigin);
		String labelNew = Methods.findName(actTypeNewModel, tempNodeNew);

		String labelOriginNormalized = normalizer.normalizeLabel(labelOrigin);
		String labelNewNormalized = normalizer.normalizeLabel(labelNew);

		List<Token> labelOriginSEDNormalized = normalizer
				.createNormalizedTokensFromLabel(labelOrigin, true, false,
						false, false);
		List<Token> labelNewSEDNormalized = normalizer
				.createNormalizedTokensFromLabel(labelNew, true, false, false,
						false);

		boolean traceLink = false;
		boolean merge = false;

		double exactMatchResult = exactMatcher.executeMatching(tempNodeOrigin,
				tempNodeNew, originalModel, newModel, metaModelOriginal,
				metaModelNew, actTypeOriginModel, actTypeNewModel,
				labelOriginNormalized, labelNewNormalized, null, null);

		double attributeMatchResult = attributeMatcher.executeMatching(
				tempNodeOrigin, tempNodeNew, originalModel, newModel,
				metaModelOriginal, metaModelNew, actTypeOriginModel,
				actTypeNewModel, labelOriginNormalized, labelNewNormalized,
				null, null);

		double stringEditDistanceResult = stringEditDistanceMatcher
				.executeMatching(tempNodeOrigin, tempNodeNew, originalModel,
						newModel, metaModelOriginal, metaModelNew,
						actTypeOriginModel, actTypeNewModel, null, null,
						labelOriginSEDNormalized, labelNewSEDNormalized);

		if ((attributeMatchResult >= 1.0) && (exactMatchResult == 1)) {
			merge = true;

			saveMergedNodePair(tempNodeOrigin, tempNodeNew, actTypeOriginModel,
					actTypeNewModel, labelOrigin, labelNew,
					labelOriginNormalized, labelNewNormalized, traceLink,
					merge, exactMatchResult, attributeMatchResult,
					stringEditDistanceResult);
		} else {
			if ((attributeMatchResult >= 0.75)
					&& (stringEditDistanceResult >= 0.8)) {
				traceLink = true;

				saveMergedNodePair(tempNodeOrigin, tempNodeNew,
						actTypeOriginModel, actTypeNewModel, labelOrigin,
						labelNew, labelOriginNormalized, labelNewNormalized,
						traceLink, merge, exactMatchResult,
						attributeMatchResult, stringEditDistanceResult);
			}
		}
	}

	/**
	 * This method takes the results of the matching of two nodes, prepares them
	 * for saving and then saves them in the consistency data object.
	 */
	private void saveMergedNodePair(Node tempNodeOrigin, Node tempNodeNew,
			NodeType actTypeOriginModel, NodeType actTypeNewModel,
			String labelOrigin, String labelNew, String labelOriginNormalized,
			String labelNewNormalized, boolean traceLink, boolean merge,
			double exactMatchResult, double attributeMatchResult,
			double stringEditDistanceMatchResult) {
		ComparedLabelPair comparedLabelPair = new ComparedLabelPair(
				labelOrigin, labelNew, labelOriginNormalized,
				labelNewNormalized);

		comparedLabelPair.setExactMatchResult(exactMatchResult);
		
		if(exactMatchResult >= 1){
			comparedLabelPair.setEquals(true);
		}

		MergedNodePair mergedNodePair = new MergedNodePair(tempNodeOrigin,
				tempNodeNew, actTypeOriginModel, actTypeNewModel,
				comparedLabelPair, traceLink, merge);

		mergedNodePair.setAttributeMatchResult(attributeMatchResult);
		mergedNodePair
				.setStringEditDistanceResult(stringEditDistanceMatchResult);

		if (ConsistencyData.getInstance().putMergedEntry(mergedNodePair)) {
			// saving worked correctly
		} else {
			throw new ConsistencyException(
					"The result of the currently matched node pair, nodeOrigin: "
							+ comparedLabelPair.getLabelOrigin() + " nodeNew: "
							+ comparedLabelPair.getLabelNew()
							+ " couldn't be saved in the consistency data.");
		}
	}
}