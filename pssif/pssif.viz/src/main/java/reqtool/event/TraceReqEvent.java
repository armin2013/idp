package reqtool.event;

import graph.model.MyNode;
import gui.graph.GraphVisualization;

/**
 * The Class TraceReqEvent.
 */
public class TraceReqEvent extends GvizNodeEvent {
	
	/**
	 * Instantiates a new trace requirement event.
	 *
	 * @param node the node
	 * @param gViz the graph visualization
	 */
	public TraceReqEvent(MyNode node, GraphVisualization gViz) {
		super(node, gViz);
	}
	
}
