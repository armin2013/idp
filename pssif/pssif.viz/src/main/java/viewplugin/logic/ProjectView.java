package viewplugin.logic;

import graph.model.MyEdgeType;
import graph.model.MyNodeType;
import graph.operations.GraphViewContainer;

import java.util.TreeMap;

/**
 * 
 * @author deniz
 *
 *         This Class represents a specific View on a Project. It is composed of
 *         a MasterFilter (visible Node and Edge Types, Attribute Filters) and a
 *         GraphView of the PSSIF Model.
 * 
 */
public class ProjectView {

	/**
	 * Contains information about a certain View on the Graph. Stores the Node &
	 * Edge Types which should be activated
	 */
	private GraphViewContainer graphViewContainer;

	/**
	 * Contains all Edge Attribute Filter Conditions as String and Array.
	 */
	private TreeMap<String, String[]> edgeAttributeFilterConditions;

	/**
	 * Contains all Edge Attribute Filter Conditions as String and Array.
	 */
	private TreeMap<String, String[]> nodeAttributeFilterConditions;

	/**
	 * The name of the Project View. This will be used for identification of a
	 * ProjectView in the ManageViews Popup.
	 */
	private String name;

	/**
	 * The Project View's Status. TRUE, if Project View is currently applied,
	 * else FALSE.
	 */
	private boolean activated;

	/**
	 * Constructor should only be instantiated via
	 * ViewManager.createProjectView().
	 * 
	 * @param name
	 * @param graphViewContainer
	 * @param nodeAttributeFilterConditions
	 * @param edgeAttributeFilterConditions
	 */
	ProjectView(String name, GraphViewContainer graphViewContainer,
			TreeMap<String, String[]> nodeAttributeFilterConditions,
			TreeMap<String, String[]> edgeAttributeFilterConditions) {
		this.name = name;
		this.graphViewContainer = graphViewContainer;
		this.nodeAttributeFilterConditions = nodeAttributeFilterConditions;
		this.edgeAttributeFilterConditions = edgeAttributeFilterConditions;
	}

	ProjectView(String name, GraphViewContainer graphViewContainer) {
		this.name = name;
		this.graphViewContainer = graphViewContainer;

		this.nodeAttributeFilterConditions = new TreeMap<String, String[]>();
		this.edgeAttributeFilterConditions = new TreeMap<String, String[]>();
	}

	public void addNodeFilterCondition(String nodeCondition, String[] params) {
		if (!nodeAttributeFilterConditions.containsKey(nodeCondition)) {
			nodeAttributeFilterConditions.put(nodeCondition, params);
		}
	}

	public void addEdgeFilterCondition(String edgeCondition, String[] params) {
		if (!edgeAttributeFilterConditions.containsKey(edgeCondition)) {
			edgeAttributeFilterConditions.put(edgeCondition, params);
		}
	}

	public void removeNodeFilterCondition(String nodeCondition) {
		if (nodeAttributeFilterConditions.containsKey(nodeCondition)) {
			nodeAttributeFilterConditions.remove(nodeCondition);
		}
	}

	public void removeEdgeFilterCondition(String edgeCondition) {
		if (edgeAttributeFilterConditions.containsKey(edgeCondition)) {
			edgeAttributeFilterConditions.remove(edgeCondition);
		}
	}

	/*
	 * GETTERS & SETTERS
	 */

	public GraphViewContainer getGraphViewContainer() {
		return graphViewContainer;
	}

	public void setGraphViewContainer(GraphViewContainer graphViewContainer) {
		this.graphViewContainer = graphViewContainer;
	}

	public TreeMap<String, String[]> getEdgeAttributeFilterConditions() {
		return edgeAttributeFilterConditions;
	}

	public TreeMap<String, String[]> getNodeAttributeFilterConditions() {
		return nodeAttributeFilterConditions;
	}

	public void setEdgeAttributeFilterConditions(
			TreeMap<String, String[]> edgeAttributeFilterConditions) {
		this.edgeAttributeFilterConditions = edgeAttributeFilterConditions;
	}

	public void setNodeAttributeFilterConditions(
			TreeMap<String, String[]> nodeAttributeFilterConditions) {
		this.nodeAttributeFilterConditions = nodeAttributeFilterConditions;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		String text = "";
		text += "ProjectView: " + this.name;
		text += "\n--------------------------------\n";
		text += "Active Node Types: ";
		for (MyNodeType x : this.graphViewContainer.getSelectedNodeTypes()) {
			text += x + ", ";
		}
		text += "\n--------------------------------\n";
		text += "Active Edge Types: ";
		for (MyEdgeType x : this.graphViewContainer.getSelectedEdgeTypes()) {
			text += x + ", ";
		}
		text += "\n--------------------------------\n";
		text += "Node Attribute Conditions: ";
		for (String x : this.nodeAttributeFilterConditions.keySet()) {
			text += x + ", ";
		}
		text += "\n--------------------------------\n";
		text += "Edge Attribute Conditions: ";
		for (String x : this.edgeAttributeFilterConditions.keySet()) {
			text += x + ", ";
		}

		return text;
	}

	public boolean isActivated() {
		return activated;
	}

	public void setActivated(boolean activated) {
		this.activated = activated;
	}

}
