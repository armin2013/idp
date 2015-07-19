package reqtool.event;

import graph.model.MyNode;
import gui.graph.GraphVisualization;

/**
 * The Class ShowContainementEvent.
 */
public class ShowContainementEvent extends GvizNodeEvent {

	/**
	 * Instantiates a new show containement event.
	 *
	 * @param node the node
	 * @param gViz the graph visualization
	 */
	public ShowContainementEvent(MyNode node, GraphVisualization gViz) {
		super(node, gViz);
	}

}
