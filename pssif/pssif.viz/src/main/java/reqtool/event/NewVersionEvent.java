package reqtool.event;

import graph.model.MyNode;
import gui.graph.GraphVisualization;

/**
 * The Class NewVersionEvent.
 */
public class NewVersionEvent extends GvizNodeEvent {

	/**
	 * Instantiates a new new version event.
	 *
	 * @param node the node
	 * @param gViz the graph visualization
	 */
	public NewVersionEvent(MyNode node, GraphVisualization gViz) {
		super(node, gViz);
	}

}
