package graph.operations;

import graph.model.MyEdgeType;
import graph.model.MyNodeType;

import java.util.LinkedList;

/**
 * Contains information about a certain View on the Graph
 *
 */
public class GraphViewContainer {
	
	private LinkedList<MyNodeType> selectedNodeTypes;
	private LinkedList<MyEdgeType> selectedEdgeTypes;
	private String viewName;
	
	/**
	 * Creates a a new container with all the information about a certain View on the Graph
	 * @param selectedNodeTypes : which Node Types should be visible
	 * @param selectedEdgeTypes : which Edge Types should be visible
	 * @param viewName : the Name of the view
	 */
	public GraphViewContainer(LinkedList<MyNodeType> selectedNodeTypes, LinkedList<MyEdgeType> selectedEdgeTypes, String viewName)
	{
		this.viewName = viewName;
		this.selectedEdgeTypes = selectedEdgeTypes;
		this.selectedNodeTypes = selectedNodeTypes;
	}
	/**
	 * Creates an empty new container
	 */
	public GraphViewContainer()
	{
		this.selectedEdgeTypes = new LinkedList<MyEdgeType>();
		this.selectedNodeTypes = new LinkedList<MyNodeType>();
	}
	/**
	 * Get all the Node Types which should be displayed
	 * @return a list with all the Node Types
	 */
	public LinkedList<MyNodeType> getSelectedNodeTypes() {
		return selectedNodeTypes;
	}
	
	/**
	 * Set the Node Types which should be displayed in this View
	 * @param selectedNodeTypes a list with all the Node Types
	 */
	public void setSelectedNodeTypes(LinkedList<MyNodeType> selectedNodeTypes) {
		this.selectedNodeTypes = selectedNodeTypes;
	}
	
	/**
	 * Get all the Edges Types which should be displayed
	 * @return a list with all the Edge Types
	 */
	public LinkedList<MyEdgeType> getSelectedEdgeTypes() {
		return selectedEdgeTypes;
	}

	/**
	 * Set the Edge Types which should be displayed in this View
	 * @param selectedEdgeTypes a list with all the Edge Types
	 */
	public void setSelectedEdgeTypes(LinkedList<MyEdgeType> selectedEdgeTypes) {
		this.selectedEdgeTypes = selectedEdgeTypes;
	}

	public String getViewName() {
		return viewName;
	}

	public void setViewName(String viewName) {
		this.viewName = viewName;
	}
}
