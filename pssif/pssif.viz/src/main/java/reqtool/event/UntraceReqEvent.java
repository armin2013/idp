package reqtool.event;

import graph.model.MyNode;
import gui.graph.GraphVisualization;

/**
 * The Class UntraceReqEvent.
 */
public class UntraceReqEvent extends GvizNodeEvent {
	
	/**
	 * Instantiates a new untrace req event.
	 *
	 * @param node the node
	 * @param gViz the graph visualization
	 */
	public UntraceReqEvent(MyNode node, GraphVisualization gViz) {
		super(node, gViz);
	}
	
}
