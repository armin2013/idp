package reqtool.event.menu;

import graph.model.MyNode;
import gui.graph.GraphVisualization;

import javax.swing.JMenu;

import reqtool.event.GvizNodeEvent;

/**
 * The Class MenuEvent.
 */
public class MenuEvent extends GvizNodeEvent {
	
	/** The parent menu. */
	private JMenu menu;
	
	/**
	 * Instantiates a new menu event.
	 *
	 * @param node the selected node
	 * @param menu the parent menu
	 * @param gViz the graph visualization
	 */
	public MenuEvent(MyNode node, JMenu menu, GraphVisualization gViz) {
		super(node, gViz);
		this.setMenu(menu);
	}

	/**
	 * Gets the menu.
	 *
	 * @return the menu
	 */
	public JMenu getMenu() {
		return menu;
	}

	/**
	 * Sets the menu.
	 *
	 * @param menu the new menu
	 */
	private void setMenu(JMenu menu) {
		this.menu = menu;
	}
}
