package org.pssif.consistencyDataStructures;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.pssif.comparedDataStructures.ComparedLabelPair;
import org.pssif.comparedDataStructures.ComparedNodePair;
import org.pssif.comparedDataStructures.ComparedNormalizedTokensPair;
import org.pssif.mainProcesses.Methods;
import org.pssif.mergedDataStructures.MergedNodePair;

import de.tum.pssif.core.metamodel.NodeType;
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
 * This singleton class stores all the information relevant for the consistency
 * checking process. It stores several list relevant for the processes.
 * 
 * With this class we know:
 * 
 * - which IDs already matched (so we don't match them again as we go up in the
 * class hierachy in the compairson process)
 * 
 * - the similarity results for token & label pairs to be able to look them up
 * in future compairsons
 * 
 * - the node pairs which exceed the similarity thresholds
 * 
 * - the node pairs which have to be transferred to the new model
 * 
 * - the node pairs which will be linked by a tracelink
 * 
 * - the node pairs which will be merged into one
 * 
 * - the junction nodes which have to be transferred to the new model
 * 
 * @author Andreas
 * 
 */
public class ConsistencyData {

	private static ConsistencyData instance = null;

	private ConsistencyData() {

		IDMapping = new HashSet<String>();
		this.comparedLabelPairs = new LinkedList<ComparedLabelPair>();
		this.comparedTokensPairs = new LinkedList<ComparedNormalizedTokensPair>();
		this.comparedNodePairs = new LinkedList<ComparedNodePair>();

		this.mergedNodePairs = new LinkedList<MergedNodePair>();
		this.unmatchedNodesOrigin = new LinkedList<NodeAndType>();
	}

	public static ConsistencyData getInstance() {
		if (instance == null) {
			instance = new ConsistencyData();
		}

		return instance;
	}

	/**
	 * stores the already compared IDs as the pair (originModelElementID,
	 * newModelElementID)
	 * 
	 * Variable is volatile, will be lost at serialization!
	 */
	private volatile Set<String> IDMapping;

	/**
	 * stores the label pairs which were already matched together with the
	 * similarity metric results
	 */
	private List<ComparedLabelPair> comparedLabelPairs;

	/**
	 * stores the tokens pairs which were already matched together with the
	 * similarity metric results
	 */
	private List<ComparedNormalizedTokensPair> comparedTokensPairs;

	/**
	 * stores compared Nodes with similarity information
	 * 
	 * Variable is volatile, will be lost at serialization!
	 */
	private volatile List<ComparedNodePair> comparedNodePairs;

	/**
	 * stores merged/traced Nodes
	 * 
	 * Variable is volatile, will be lost at serialization!
	 */
	private volatile List<MergedNodePair> mergedNodePairs;

	/**
	 * this list stores the nodes from the original model which can't be traced
	 * or merged into the new version of the model.
	 * 
	 * Variable is volatile, will be lost at serialization!
	 */
	private volatile List<NodeAndType> unmatchedNodesOrigin;

	/**
	 * this list stores the junction nodes from the original model which weren't
	 * found in the new version of the model.
	 * 
	 * Variable is volatile, will be lost at serialization!
	 */
	private volatile List<NodeAndType> unmatchedJunctionnodesOrigin;

	/**
	 * a list with all nodes of the original model together with their according
	 * type
	 */
	private List<NodeAndType> allNodesOrigin;

	/**
	 * these are the tresholds for the syntactic, semantic and contextual
	 * metrics. Depending on these tresholds and the results of a match between
	 * two nodes the two nodes are proposed as equal to the user if one of the
	 * tresholds is exceeded
	 */
	private static double semanticThreshold;
	private static double syntacticThreshold;
	private static double contextThreshold;

	/**
	 * @return the comparedNodePairs
	 */
	public List<ComparedNodePair> getComparedNodePairs() {
		return comparedNodePairs;
	}

	/**
	 * This method initializes the treshold variables
	 * 
	 * @param synTreshhold
	 *            the treshold for the syntactic result
	 * @param semTreshhold
	 *            the treshold for the semantic result
	 * @param conTreshhold
	 *            the treshold for the context result
	 */
	public static void initThresholds(double synTreshhold, double semTreshhold,
			double conTreshhold) {
		syntacticThreshold = synTreshhold;
		semanticThreshold = semTreshhold;
		contextThreshold = conTreshhold;
	}

	/**
	 * THis method takes the result of the application of all similarity metrics
	 * onto two nodes and saves the result into the ID Mapping (so Nodes are
	 * only matched once), the already compared labels & tokens and the compared
	 * Node pairs
	 * 
	 * @param comparedNodePair
	 *            the result of the matching process
	 * @return true if the new compared elements were added to all relevant
	 *         variables. false if something went wrong
	 */
	public boolean putComparedEntry(ComparedNodePair comparedNodePair) {

		boolean success = true;

		success = success
				&& IDMapping.add(Methods.findGlobalID(
						comparedNodePair.getNodeOriginalModel(),
						comparedNodePair.getTypeOriginModel())
						+ Methods.findGlobalID(
								comparedNodePair.getNodeNewModel(),
								comparedNodePair.getTypeNewModel()));
		success = success
				&& comparedLabelPairs
						.add(comparedNodePair.getLabelComparison());
		success = success
				&& comparedTokensPairs.add(comparedNodePair
						.getTokensComparison());
		success = success && comparedNodePairs.add(comparedNodePair);

		return success;
	}

	/**
	 * THis method takes the result of the application of all merging metrics
	 * onto two nodes and saves the result into the ID Mapping (so Nodes are
	 * only merged once) and into the mergedNodePairs list.
	 * 
	 * @param mergedNodePair
	 * @return true if the new merged elements were added to all relevant
	 *         variables. false if something went wrong
	 */
	public boolean putMergedEntry(MergedNodePair mergedNodePair) {

		boolean success = true;

		success = success
				&& IDMapping.add(Methods.findGlobalID(
						mergedNodePair.getNodeOriginalModel(),
						mergedNodePair.getTypeOriginModel())
						+ Methods.findGlobalID(
								mergedNodePair.getNodeNewModel(),
								mergedNodePair.getTypeNewModel()));

		success = success && mergedNodePairs.add(mergedNodePair);

		return success;
	}

	public void putUnmatchedJunctionnodeEntry(NodeAndType junctionNode) {
		this.unmatchedJunctionnodesOrigin.add(junctionNode);
	}

	/**
	 * This method returns if a match between the two given IDs is necessary
	 * 
	 * @param globalIDNodeOrigin
	 *            the global ID of the original node
	 * @param globalIDNodeNew
	 *            the global ID of the new node
	 * @return a bool saying whether the two nodes have been matched already
	 * @true if a match is necessary
	 * @false if two nodes have already been matched
	 */
	public boolean matchNecessary(String globalIDNodeOrigin,
			String globalIDNodeNew) {
		return (!IDMapping.contains(globalIDNodeOrigin + globalIDNodeNew));
	}

	/**
	 * This method returns the result of a match in form of a ComparedNodePair
	 * between the two given nodes if they have been matched once
	 * 
	 * @param tempNodeOrigin
	 *            the node from the original model
	 * @param tempNodeNew
	 *            the node from the new model
	 * @return the result of a match in form of a ComparedNodePair between the
	 *         two given nodes if they have been matched once
	 */
	public ComparedNodePair getNodeMatch(Node tempNodeOrigin, Node tempNodeNew) {

		for (ComparedNodePair comparedNodePair : comparedNodePairs) {
			if (comparedNodePair.getNodeOriginalModel().equals(tempNodeOrigin)
					&& comparedNodePair.getNodeNewModel().equals(tempNodeNew)) {
				return comparedNodePair;
			}
		}
		return null;
	}

	/**
	 * This method returns the result of a match between two nodes if one of the
	 * nodes has already been compared with at least one other node. So the
	 * normalizations don't have to be computed again.
	 * 
	 * @param tempNode
	 *            the node which is looked up in the stored results
	 * @param tempNodeType
	 *            the type of tempNode
	 * @return a ComparedNodePair which holds inter alia the normalized label of
	 *         the looked up token
	 */
	public ComparedNodePair nodeAlreadyCompared(Node tempNode,
			NodeType tempNodeType) {

		String tempNodeID = Methods.findGlobalID(tempNode, tempNodeType);

		String tempSearchedOriginNodeID, tempSearchedNewNodeID;

		for (ComparedNodePair comparedNodePair : comparedNodePairs) {
			tempSearchedOriginNodeID = Methods.findGlobalID(
					comparedNodePair.getNodeOriginalModel(),
					comparedNodePair.getTypeOriginModel());

			tempSearchedNewNodeID = Methods.findGlobalID(
					comparedNodePair.getNodeNewModel(),
					comparedNodePair.getTypeNewModel());

			if (tempSearchedNewNodeID.equals(tempNodeID)
					|| tempSearchedOriginNodeID.equals(tempNodeID)) {
				return comparedNodePair;
			}
		}
		return null;
	}

	/**
	 * This methods evaluates which of the compared node pairs have at least one
	 * similarity score that exceeds the treshold. All those node pairs are
	 * added to a list and then returned.
	 * 
	 * @return the list of node pairs which Syntactic- OR Semantic- OR Context-
	 *         Similarity exceeds the according treshold.
	 */
	public List<ComparedNodePair> getEqualsCandidateList() {
		List<ComparedNodePair> equalsCandidates = new LinkedList<ComparedNodePair>();

		for (ComparedNodePair actPair : comparedNodePairs) {
			if ((actPair.getWeightedSyntacticResult() >= ConsistencyData.syntacticThreshold)
					|| (actPair.getWeightedSemanticResult() >= ConsistencyData.semanticThreshold)
					|| (actPair.getWeightedContextResult() >= ConsistencyData.contextThreshold)) {
				equalsCandidates.add(actPair);
			}
		}
		return equalsCandidates;
	}

	/**
	 * This method evaluates which of the compared node pairs were selected by
	 * the user to be linked as equals. All selected elements are added to a
	 * list and then returned.
	 * 
	 * @return the list of node pairs which the user chose being linked equal.
	 */
	public List<ComparedNodePair> getEqualsList() {
		List<ComparedNodePair> equalsList = new LinkedList<ComparedNodePair>();

		for (ComparedNodePair actPair : comparedNodePairs) {
			if (actPair.isEquals()) {
				equalsList.add(actPair);
			}
		}
		return equalsList;
	}

	/**
	 * This methods evaluates which of the node pairs compared in the merging
	 * process were marked as to be traced. All those node pairs are added to a
	 * list and then returned.
	 * 
	 * @return the list of node pairs which were marked as to be traced
	 */
	public List<MergedNodePair> getTraceCandidateList() {
		List<MergedNodePair> traceCandidates = new LinkedList<MergedNodePair>();

		for (MergedNodePair actPair : mergedNodePairs) {
			if (actPair.isTraceLink()) {
				traceCandidates.add(actPair);
			}
		}
		return traceCandidates;
	}

	/**
	 * This method returns a list witch node pairs which were proven to be the
	 * same by the applied smilarity metrics
	 * 
	 * @return a list witch node pairs which were proven to be the same by the
	 *         applied smilarity metrics
	 */
	public List<MergedNodePair> getMergedList() {
		List<MergedNodePair> mergedList = new LinkedList<MergedNodePair>();

		for (MergedNodePair actPair : mergedNodePairs) {
			if (actPair.isMerge()) {
				mergedList.add(actPair);
			}
		}

		return mergedList;
	}

	/**
	 * This method returns a list witch node pairs which were proven to be
	 * different versions of each other by the applied smilarity metrics
	 * 
	 * @return a list witch node pairs which were proven to be different
	 *         versions of each other by the applied smilarity metrics
	 */
	public List<MergedNodePair> getTracedList() {
		List<MergedNodePair> tracedList = new LinkedList<MergedNodePair>();

		for (MergedNodePair actPair : mergedNodePairs) {
			if (actPair.isTraceLink()) {
				tracedList.add(actPair);
			}
		}

		return tracedList;
	}

	/**
	 * this method takes alle nodes from the original model and creates a list
	 * with the nodes which haven't been merged or traced with another node.
	 */
	public void createUnmatchedNodeList() {
		boolean isMatchedNode;

		for (NodeAndType actNode : allNodesOrigin) {
			isMatchedNode = false;
			for (MergedNodePair mergedNode : mergedNodePairs) {
				if (mergedNode.isMerge() || mergedNode.isTraceLink()) {
					if (actNode.getNode().equals(
							mergedNode.getNodeOriginalModel())) {
						isMatchedNode = true;
					}
				}
			}
			if (!isMatchedNode) {
				this.unmatchedNodesOrigin.add(actNode);
			}
		}
	}

	public List<NodeAndType> getUnmatchedNodeList() {
		return this.unmatchedNodesOrigin;
	}

	public List<MergedNodePair> getMergedNodePairs() {
		return this.mergedNodePairs;
	}

	public List<NodeAndType> getUnmatchedJunctionnodesList() {
		return this.unmatchedJunctionnodesOrigin;
	}

	public void resetComparedNodePairList() {
		this.comparedNodePairs = new LinkedList<ComparedNodePair>();
	}

	public void resetMergedNodePairList() {
		this.mergedNodePairs = new LinkedList<MergedNodePair>();
	}

	public void resetUnmatchedNodesList() {
		this.unmatchedNodesOrigin = new LinkedList<NodeAndType>();
	}

	public void resetUnmatchedJunctionnodesOrigin() {
		this.unmatchedJunctionnodesOrigin = new LinkedList<NodeAndType>();
	}

	public void setAllNodesOrigin(List<NodeAndType> allNodesOrigin) {
		this.allNodesOrigin = allNodesOrigin;
	}
}
