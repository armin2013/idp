package reqtool.event;

import graph.model.MyNode;
import gui.graph.GraphVisualization;

/**
 * The Class GvizNodeEvent.
 */
public class GvizNodeEvent extends NodeEvent {
	
	/** The graph visualization. */
	private GraphVisualization gViz;

	/**
	 * Instantiates a new gviz node event.
	 *
	 * @param node the selected node
	 * @param gViz the graph visualization
	 */
	public GvizNodeEvent(MyNode node, GraphVisualization gViz) {
		super(node);
		setGViz(gViz);
	}

	/**
	 * Gets the graph visualization.
	 *
	 * @return the graph visualization
	 */
	public GraphVisualization getGViz() {
		return gViz;
	}

	/**
	 * Sets the graph visualization.
	 *
	 * @param gViz the new graph visualization
	 */
	private void setGViz(GraphVisualization gViz) {
		this.gViz = gViz;
	}
}
