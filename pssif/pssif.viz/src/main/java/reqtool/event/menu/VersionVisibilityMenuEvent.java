package reqtool.event.menu;

import javax.swing.JMenu;

import graph.model.MyNode;
import gui.graph.GraphVisualization;

/**
 * The Class VersionVisibilityMenuEvent.
 */
public class VersionVisibilityMenuEvent extends MenuEvent {

	/**
	 * Instantiates a new version visibility menu event.
	 *
	 * @param node the selected node
	 * @param menu the parent menu
	 * @param gViz the graph visualization
	 */
	public VersionVisibilityMenuEvent(MyNode node, JMenu menu, GraphVisualization gViz) {
		super(node, menu, gViz);
	}

}
