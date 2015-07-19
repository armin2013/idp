package reqtool.event;

import graph.model.MyNode;
import gui.graph.GraphVisualization;

/**
 * The Class HideContainementEvent.
 */
public class HideContainementEvent extends GvizNodeEvent {

	/**
	 * Instantiates a new hide containement event.
	 *
	 * @param node the node
	 * @param gViz the g viz
	 */
	public HideContainementEvent(MyNode node, GraphVisualization gViz) {
		super(node, gViz);
	}

}
