package reqtool.event;

import graph.model.MyNode;
import gui.graph.GraphVisualization;

/**
 * The Class NewDeriveRequirementEvent.
 */
public class NewDeriveRequirementEvent extends GvizNodeEvent{

	/**
	 * Instantiates a new new derive requirement event.
	 *
	 * @param node the node to derive
	 * @param gViz the graph visualization
	 */
	public NewDeriveRequirementEvent(MyNode node, GraphVisualization gViz) {
		super(node, gViz);
	}

}
