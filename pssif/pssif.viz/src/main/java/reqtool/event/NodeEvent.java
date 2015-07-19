package reqtool.event;

import graph.model.MyNode;

/**
 * The Class NodeEvent.
 */
public abstract class NodeEvent implements Event {
	
	/** The selected node. */
	private MyNode selectedNode;
	
	/**
	 * Instantiates a new node event.
	 *
	 * @param node the node
	 */
	public NodeEvent(MyNode node) {
		selectedNode = node;
	}

	/**
	 * Gets the selected node.
	 *
	 * @return the selected node
	 */
	public MyNode getSelectedNode() {
		return selectedNode;
	}

	/**
	 * Sets the selected node.
	 *
	 * @param selectedNode the new selected node
	 */
	public void setSelectedNode(MyNode selectedNode) {
		this.selectedNode = selectedNode;
	}
}
