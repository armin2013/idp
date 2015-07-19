package reqtool.event;

import graph.model.MyNode;
import gui.graph.GraphVisualization;

/**
 * The Class for the test case creation event.
 */
public class CreateTestCaseEvent extends GvizNodeEvent {

	/**
	 * Instantiates a new test case creation event.
	 *
	 * @param node the node for which the test case should be created
	 * @param gViz the graph visualization
	 */
	public CreateTestCaseEvent(MyNode node, GraphVisualization gViz) {
		super(node, gViz);
	}

}
