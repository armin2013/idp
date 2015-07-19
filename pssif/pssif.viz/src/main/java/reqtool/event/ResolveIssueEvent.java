package reqtool.event;

import graph.model.MyNode;
import gui.graph.GraphVisualization;

/**
 * The Class ResolveIssueEvent.
 */
public class ResolveIssueEvent extends GvizNodeEvent {
	
	/**
	 * Instantiates a new resolve issue event.
	 *
	 * @param node the node
	 * @param gViz the graph visualization
	 */
	public ResolveIssueEvent(MyNode node, GraphVisualization gViz) {
		super(node, gViz);
	}
	
}
