package reqtool.handler;

import com.google.common.eventbus.Subscribe;

import reqtool.event.NewVersionEvent;
import reqtool.graph.VersionManagerPopup;

/**
 * The Class NewVersionHandler.
 */
public class NewVersionHandler implements EventHandler{

	/**
	 * Handle the new version node event.
	 *
	 * @param event the event
	 */
	@Subscribe
	public void handleEvent(NewVersionEvent event) {
		if (VersionManagerPopup.showPopup(event.getSelectedNode())) {
			event.getGViz().updateGraph();
		}
	}
	
}
