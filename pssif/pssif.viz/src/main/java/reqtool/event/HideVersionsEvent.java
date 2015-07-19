package reqtool.event;

import graph.model.MyNode;
import gui.graph.GraphVisualization;

/**
 * The Class HideVersionsEvent.
 */
public class HideVersionsEvent extends GvizNodeEvent {

	/**
	 * Instantiates a new hide versions event.
	 *
	 * @param node the selected node to hide versions for
	 * @param gViz the graph visualization
	 */
	public HideVersionsEvent(MyNode node, GraphVisualization gViz) {
		super(node, gViz);
	}

}
