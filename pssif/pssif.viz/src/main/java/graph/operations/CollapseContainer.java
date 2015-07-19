package graph.operations;



import graph.model.MyEdge;
import graph.model.MyNode;

import java.util.LinkedList;

/**
 * A container which stores all the information about the operations which had been executed to enable the collapse or expand operation.
 * It provides the information how to redo or undo such an operation 
 * @author Luc
 *
 */
public class CollapseContainer {
	
	/**
	 * Contains the Edges which have to be remove, when the collapsed node will be expanded again
	 */
	private LinkedList<MyEdge> removeEdges;
	/**
	 * Contains the Nodes which have to be added, when the collapsed node will be expanded again
	 */
	private LinkedList<MyNode> addNodes;
	/**
	 * Contains the Edges which have to be added, when the collapsed node will be expanded again
	 */
	private LinkedList<MyEdge> addEdges;
	
	public CollapseContainer()
	{
		this.addEdges = new LinkedList<MyEdge>();
		this.addNodes = new LinkedList<MyNode>();
		this.removeEdges = new LinkedList<MyEdge>();
	}
	/**
	 * Add new Edge to the graph. Have to be removed later
	 * @param edge the specific edge
	 */
	public void addNewEdges(MyEdge edge)
	{
		this.removeEdges.add(edge);
	}
	
	/**
	 * Add an Edge which will be restored later
	 * @param edge the specific edge
	 */
	public void addOldEdge (MyEdge edge)
	{
		this.addEdges.add(edge);
	}
	
	/**
	 * Add a Node which will be restored later
	 * @param node the specific edge
	 */
	public void addOldNode (MyNode node)
	{
		this.addNodes.add(node);
	}
	
	/**
	 * Get all the Edges which have to be removed with the expand operation
	 * @return a list with all the Edges
	 */
	public LinkedList<MyEdge> getNewEdges() {
		return removeEdges;
	}
	
	/**
	 * Get all the Nodes which have to be added again with the expand operation
	 * @return a list with all the Nodes
	 */
	public LinkedList<MyNode> getOldNodes() {
		return addNodes;
	}

	/**
	 * Get all the Edges which have to be added again with the expand operation
	 * @return a list with all the Edges
	 */
	public LinkedList<MyEdge> getOldEdges() {
		return addEdges;
	}	
}