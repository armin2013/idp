package reqtool.handler;

import reqtool.controller.RequirementToolbox;
import reqtool.event.HideContainementEvent;
import reqtool.event.ShowContainementEvent;

import com.google.common.eventbus.Subscribe;

/**
 * The Class ContainementVisibilityHandler.
 */
public class ContainementVisibilityHandler implements EventHandler {
	
	/**
	 * Handle show containement event.
	 *
	 * @param event the event
	 */
	@Subscribe
	public void handleShowContainement(ShowContainementEvent event) {
		RequirementToolbox.showContainment(event.getSelectedNode());
		event.getGViz().updateGraph();
	}
	
	/**
	 * Handle hide containement event.
	 *
	 * @param event the event
	 */
	@Subscribe
	public void handleHideContainement(HideContainementEvent event) {
		RequirementToolbox.hideContainment(event.getSelectedNode());
		event.getGViz().updateGraph();
	}

}
