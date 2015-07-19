package reqtool.event.menu;

import graph.model.MyNode;
import gui.graph.GraphVisualization;

import javax.swing.JMenu;

/**
 * The Class TraceReqMenuEvent.
 */
public class TraceReqMenuEvent extends MenuEvent {
	
	/**
	 * Instantiates a new trace req menu event.
	 *
	 * @param node the node to trace
	 * @param menu the parent menu
	 * @param gViz the graph visualization
	 */
	public TraceReqMenuEvent(MyNode node, JMenu menu, GraphVisualization gViz) {
		super(node, menu, gViz);
	}
	
}
