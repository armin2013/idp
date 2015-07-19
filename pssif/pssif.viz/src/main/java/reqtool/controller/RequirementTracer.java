package reqtool.controller;

import graph.model.IMyNode;
import graph.model.MyEdge;
import graph.model.MyNode;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import model.ModelBuilder;
import de.tum.pssif.core.metamodel.external.PSSIFCanonicMetamodelCreator;

/**
 * The Class RequirementTracer.
 */
public class RequirementTracer {
	
	/** The node to trace. */
	private static MyNode mNode;
	
	/** The traced nodes. */
	private static LinkedList<MyNode> mTracedNodes = new LinkedList<MyNode>();
	
	/** The traceable edges. */
	private static List<String> traceableEdges = new ArrayList<String>();

	/**
	 * Trace requirement node.
	 *
	 * @param node the node
	 */
	public static void traceRequirement(MyNode node) {
		mNode = node;
		mTracedNodes.clear();
		
		traceableEdges.add(PSSIFCanonicMetamodelCreator.TAGS.get("E_RELATIONSHIP_CHRONOLOGICAL_BASED_ON"));
		traceableEdges.add(PSSIFCanonicMetamodelCreator.TAGS.get("E_RELATIONSHIP"));
		traceableEdges.add(PSSIFCanonicMetamodelCreator.TAGS.get("E_RELATIONSHIP_LOGICAL_SATISFIES"));

		for (MyEdge edge : ModelBuilder.getAllEdges()) {
			if (edge.getSourceNode().getNode().getId().equals(node.getNode().getId()) && traceableEdges.contains(edge.getEdgeType().getName())) {
				traceRequirementDestination((MyNode) edge.getDestinationNode());
			} else if (edge.getDestinationNode().getNode().getId().equals(node.getNode().getId()) && traceableEdges.contains(edge.getEdgeType().getName())) {
				traceRequirementSource((MyNode) edge.getSourceNode());
			}
		}
	}

	/**
	 * Trace requirement destination.
	 *
	 * @param node the node
	 */
	private static void traceRequirementDestination(MyNode node) {
		mTracedNodes.add(node);
		for (MyEdge edge : ModelBuilder.getAllEdges()) {
			if (edge.getSourceNode().getNode().getId().equals(node.getNode().getId()) && traceableEdges.contains(edge.getEdgeType().getName())) {
				mTracedNodes.add((MyNode) edge.getDestinationNode());
			}
		}
	}

	/** 
	 * Trace requirement source.
	 *
	 * @param node the node
	 */
	private static void traceRequirementSource(MyNode node) {
		mTracedNodes.add(node);
		for (MyEdge edge : ModelBuilder.getAllEdges()) {
			if (edge.getDestinationNode().getNode().getId().equals(node.getNode().getId()) && traceableEdges.contains(edge.getEdgeType().getName())) {
				mTracedNodes.add((MyNode) edge.getSourceNode());
			}
		}
	}

	/**
	 * Gets the traced node.
	 *
	 * @return the traced node
	 */
	public static IMyNode getTracedNode() {
		return mNode;
	}

	/**
	 * Checks if is the node is traced.
	 *
	 * @param node the node
	 * @return true, if is traced node
	 */
	public static boolean isTracedNode(IMyNode node) {
		if (mNode != null && mNode.getNode().getId().equals(node.getNode().getId())) {
			return true;
		}
		return false;
	}

	/**
	 * Checks if the node is on trace list.
	 *
	 * @param node the node
	 * @return true, if is on trace list
	 */
	public static boolean isOnTraceList(IMyNode node) {
		for (MyNode n : mTracedNodes) {
			if (n.getNode().getId().equals(node.getNode().getId())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Gets the traced nodes.
	 *
	 * @return the traced nodes
	 */
	public static LinkedList<MyNode> getTracedNodes() {
		return mTracedNodes;
	}

	/**
	 * Stop tracing.
	 */
	public static void stopTracing() {
		mNode = null;
		mTracedNodes.clear();
	}

}