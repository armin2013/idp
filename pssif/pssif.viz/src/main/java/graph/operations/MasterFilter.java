package graph.operations;

import graph.model.MyEdgeType;
import graph.model.MyNodeType;
import gui.GraphView;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * The Masterfilter class manages all the filters (Node and Type Filter, Attribute Filters,...). It makes sure that after every apply
 * or undo operation all the active filters are applied to the model
 * @author Luc
 *
 */
public class MasterFilter {
	/**
	 * Which Node Attribute Filters are currently active
	 */
	private LinkedList<String> activeNodeAttributeFilters;
	/**
	 * Which Edge Attribute Filters are currently activated
	 */
	private LinkedList<String> activeEdgeAttributeFilters;
	
	/**
	 * Which Edge and Node Type Filters are currently activated
	 */
	private LinkedList<String> activeNodeAndEdgeTypeFilters;
	/**
	 * Map the Edge and Node Type name to the appropriate GraphViewContainer
	 */
	private HashMap<String, GraphViewContainer> nodeAndEdgeTypeFilters;
	
	
	private GraphView graphview;
	
	public MasterFilter(GraphView graphview)
	{
		this.graphview = graphview;
		this.activeEdgeAttributeFilters = new LinkedList<String>();
		this.activeNodeAttributeFilters = new LinkedList<String>();
		this.activeNodeAndEdgeTypeFilters = new LinkedList<String>();
		this.nodeAndEdgeTypeFilters = this.graphview.getGraph().getAllGraphViews();
	}
	
	/**
	 * Add a new Node Attribute Filter
	 * @param condition the String which defines the condition (only used for mapping purposes)
	 * @param activate should the filter be immediately active
	 */
	public void addNodeAttributFilter(String condition, boolean activate)
	{
		activeNodeAttributeFilters.add(condition);
		
		if (activate)
		{
			try
			{
				activeNodeAttributeFilters.remove(condition);
				applyNodeAttributeFilter(condition);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Removes a Node Attribute Filter. The filter will also be deactivated
	 * @param condition the String which defines the condition (only used for mapping purposes)
	 */
	public void removeNodeAttributFilter(String condition) throws Exception
	{
		activeNodeAttributeFilters.remove(condition);
		
		undoNodeAttributeFilter(condition);
		
		AttributeFilter.removeNodeCondition(condition);
		
		applyAllActiveFilters();
	}
	
	/**
	 * Add a new Edge Attribute Filter
	 * @param condition the String which defines the condition (only used for mapping purposes)
	 * @param activate should the filter be immediately active
	 */
	public void addEdgeAttributFilter(String condition, boolean activate)
	{
		activeEdgeAttributeFilters.add(condition);
		if (activate)
		{
			try
			{
				activeEdgeAttributeFilters.remove(condition);
				applyEdgeAttributeFilter(condition);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		
	}
	
	/**
	 * Removes a Edge Attribute Filter. The filter will also be deactivated
	 * @param condition the String which defines the condition (only used for mapping purposes)
	 */
	public void removeEdgeAttributFilter(String condition) throws Exception
	{
		activeEdgeAttributeFilters.remove(condition);
		
		undoEdgeAttributeFilter(condition);
		
		AttributeFilter.removeEdgeCondition(condition);
		
		applyAllActiveFilters();
		
	}
	
	/**
	 * Apply a given Node Attribute Filter to the PSS-IF Model. 
	 * @param condition the String which defines the condition (only used for mapping purposes)
	 * @throws Exception if the condition is not well formed, an Exception will be thrown. 
	 * (Should never happen, because there would already be an Exception while creating this conditon)
	 */
	public void applyNodeAttributeFilter(String condition) throws Exception
	{
		activeNodeAttributeFilters.add(condition);
		
		AttributeFilter.applyNodeCondition(condition);
		
		//this.graphview.getGraph().updateGraph();
		applyAllActiveFilters();
	}
	
	/**
	 * Undo a given Node Attribute Filter to the PSS-IF Model. 
	 * @param condition the String which defines the condition (only used for mapping purposes)
	 * @throws Exception if the condition is not well formed, an Exception will be thrown. 
	 * (Should never happen, because there would already be an Exception while creating this conditon)
	 */
	public void undoNodeAttributeFilter(String condition) throws Exception 
	{
		activeNodeAttributeFilters.remove(condition);
		
		AttributeFilter.undoNodeCondition(condition/*, activeNodeAttributFilters*/);
		
		applyAllActiveFilters();
	}
	
	/**
	 * Apply a given Edge Attribute Filter to the PSS-IF Model. 
	 * @param condition the String which defines the condition (only used for mapping purposes)
	 * @throws Exception if the condition is not well formed, an Exception will be thrown. 
	 * (Should never happen, because there would already be an Exception while creating this conditon)
	 */
	public void applyEdgeAttributeFilter(String condition) throws Exception
	{
		activeEdgeAttributeFilters.add(condition);
		
		AttributeFilter.applyEdgeCondition(condition);
		
		//this.graphview.getGraph().updateGraph();
		applyAllActiveFilters();
	}
	
	/**
	 * Undo a given Edge Attribute Filter to the PSS-IF Model. 
	 * @param condition the String which defines the condition (only used for mapping purposes)
	 * @throws Exception if the condition is not well formed, an Exception will be thrown. 
	 * (Should never happen, because there would already be an Exception while creating this conditon)
	 */
	public void undoEdgeAttributeFilter(String condition) throws Exception
	{
		activeEdgeAttributeFilters.remove(condition);
		
		AttributeFilter.undoEdgeCondition(condition/*,activeEdgeAttributFilters*/);
		
		applyAllActiveFilters();
	}
	
	/**
	 * Add a new Node and Edge Type Filter (also called GraphViews)
	 * @param nodes which NodeTypes should be visible
	 * @param edges which EdgeTypes should be visible
	 * @param viewName the name of the View
	 * @param activate should the filter be immediately active
	 */
	public void addNodeAndEdgeTypeFilter(LinkedList<MyNodeType> nodes, LinkedList<MyEdgeType> edges, String viewName, boolean activate)
	{
		GraphViewContainer gvc = new GraphViewContainer(nodes, edges, viewName);
		this.nodeAndEdgeTypeFilters.put(viewName, gvc);
		
		if (activate)
		{
			applyNodeAndEdgeTypeFilter(viewName);
			
			applyAllActiveFilters();
		}
	}
	
	/**
	 * Remove a Node and Edge Type Filter (also called GraphViews)
	 * @param viewName the name of the View which should be removed
	 */
	public void removeNodeAndEdgeTypeFilter(String viewName)
	{
		this.activeNodeAndEdgeTypeFilters.remove(viewName);
		
		undoNodeAndEdgeTypeFilter(viewName);
		
		applyAllActiveFilters();
		
	}
	
	/**
	 * Apply a given Node and Edge Type Filter (also called GraphViews) to the PSS-IF Model. 
	 * @param viewName the name of the View which should be applied
	 */
	public void applyNodeAndEdgeTypeFilter(String viewName)
	{
		activeNodeAndEdgeTypeFilters.add(viewName);
		
		GraphViewContainer gvc = this.nodeAndEdgeTypeFilters.get(viewName);
		NodeAndEdgeTypeFilter.filter(gvc.getSelectedNodeTypes(), gvc.getSelectedEdgeTypes(), viewName);
		
		//this.graphview.getGraph().updateGraph();
		applyAllActiveFilters();
	}
	
	/**
	 * Undo a given Node and Edge Type Filter (also called GraphViews) to the PSS-IF Model. 
	 * @param viewName the name of the View which should be undone
	 */
	public void undoNodeAndEdgeTypeFilter(String name)
	{
		activeNodeAndEdgeTypeFilters.remove(name);
		
		NodeAndEdgeTypeFilter.undoFilter(name);
		
		applyAllActiveFilters();
		
	}
	
	/**
	 * Apply all active Node and Edge Type Filter (also called GraphViews) and Attribute Filter (Nodes and Edges) on the PSS-IF Model
	 */
	private void applyAllActiveFilters()
	{
		applyAllActiveNodeAndEdgeTypeFilters();
		applyAllActiveAttributFilters();
		
		this.graphview.getGraph().updateGraph();
	}
	
	/**
	 * Apply all active Attribute Filter (Nodes and Edges) on the PSS-IF Model
	 */
	private void applyAllActiveAttributFilters()
	{
		try
		{
			for (String condition : activeNodeAttributeFilters)
			{
				AttributeFilter.applyNodeCondition(condition);
			}
			
			for (String condition : activeEdgeAttributeFilters)
			{
				AttributeFilter.applyEdgeCondition(condition);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Apply all active Node and Edge Type Filter (also called GraphViews) on the PSS-IF Model
	 */
	private void applyAllActiveNodeAndEdgeTypeFilters()
	{
		NodeAndEdgeTypeFilter.applyAllFilters();
	}
	
	/**
	 * Check if a specific Node Attribute Filter is currently active
	 * @param condition the String which defines the condition (only used for mapping purposes)
	 * @return true if the filter is active, otherwise false
	 */
	public boolean NodeAttributFilterActive (String condition)
	{
		return activeNodeAttributeFilters.contains(condition);
	}
	
	/**
	 * Check if a specific Edge Attribute Filter is currently active
	 * @param condition the String which defines the condition (only used for mapping purposes)
	 * @return true if the filter is active, otherwise false
	 */
	public boolean EdgeAttributFilterActive (String condition)
	{
		return activeEdgeAttributeFilters.contains(condition);
	}
	
	/**
	 * Check if a specific Node and Edge Type Filter (also called GraphView) is currently active
	 * @param viewName the name of the View
	 * @return true if the filter is active, otherwise false
	 */
	public boolean NodeAndEdgeTypeFilterActive (String viewName)
	{
		return activeNodeAndEdgeTypeFilters.contains(viewName);
	}
}
