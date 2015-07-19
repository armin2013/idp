package reqtool.event.menu;

import javax.swing.JPopupMenu;

import graph.model.MyNode;
import gui.graph.GraphVisualization;
import reqtool.event.GvizNodeEvent;
import reqtool.event.NodeEvent;

// TODO: Auto-generated Javadoc
/**
 * The Class CreateReqMenuEvent.
 */
public class CreateReqMenuEvent extends GvizNodeEvent {
	
	/** The popup. */
	private JPopupMenu popup;

	/**
	 * Instantiates a new creates the req menu event.
	 *
	 * @param node the node
	 * @param popup the popup
	 * @param gViz the g viz
	 */
	public CreateReqMenuEvent(MyNode node, JPopupMenu popup, GraphVisualization gViz) {
		super(node, gViz);
		this.setPopup(popup);
	}

	/**
	 * Gets the popup.
	 *
	 * @return the popup
	 */
	public JPopupMenu getPopup() {
		return popup;
	}

	/**
	 * Sets the popup.
	 *
	 * @param popup the new popup
	 */
	private void setPopup(JPopupMenu popup) {
		this.popup = popup;
	}

}
