package reqtool.event;

import graph.model.MyNode;
import gui.graph.GraphVisualization;

/**
 * The Class VerifiTestCaseEvent.
 */
public class VerifyTestCaseEvent extends GvizNodeEvent {

	/**
	 * Instantiates a new verify test case event.
	 *
	 * @param node the node
	 * @param gViz the graph visualization
	 */
	public VerifyTestCaseEvent(MyNode node, GraphVisualization gViz) {
		super(node, gViz);
	}

}
