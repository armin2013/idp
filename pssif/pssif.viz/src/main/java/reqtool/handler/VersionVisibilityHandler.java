package reqtool.handler;

import reqtool.controller.RequirementVersionManager;
import reqtool.event.HideVersionsEvent;
import reqtool.event.ShowVersionsEvent;

import com.google.common.eventbus.Subscribe;

/**
 * The Class VersionVisibilityHandler.
 */
public class VersionVisibilityHandler implements EventHandler {

	/**
	 * Handle show versions event.
	 *
	 * @param event the event
	 */
	@Subscribe
	public void handleShowVersionsEvent(ShowVersionsEvent event) {
		RequirementVersionManager.showVersions(event.getSelectedNode());
		event.getGViz().updateGraph();
	}
	
	/**
	 * Handle hide versions event.
	 *
	 * @param event the event
	 */
	@Subscribe
	public void handleHideVersionsEvent(HideVersionsEvent event) {
		RequirementVersionManager.hideVersions(event.getSelectedNode());
		event.getGViz().updateGraph();
	}
	
}
