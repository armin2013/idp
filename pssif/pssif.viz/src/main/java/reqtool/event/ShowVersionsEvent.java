package reqtool.event;

import graph.model.MyNode;
import gui.graph.GraphVisualization;

/**
 * The Class ShowVersionsEvent.
 */
public class ShowVersionsEvent extends GvizNodeEvent {

	/**
	 * Instantiates a new show versions event.
	 *
	 * @param node the node
	 * @param gViz the graph visualization
	 */
	public ShowVersionsEvent(MyNode node, GraphVisualization gViz) {
		super(node, gViz);
	}

}
