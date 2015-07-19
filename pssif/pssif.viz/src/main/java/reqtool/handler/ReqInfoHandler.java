package reqtool.handler;

import com.google.common.eventbus.Subscribe;

import reqtool.event.ReqInfoEvent;
import reqtool.graph.RequirementInfoPopup;

/**
 * The Class ReqInfoHandler.
 */
public class ReqInfoHandler implements EventHandler {
	
	/**
	 * Handle requirement info event.
	 *
	 * @param event the event
	 */
	@Subscribe
	public void handleEvent(ReqInfoEvent event) {
		if (new RequirementInfoPopup(event.getSelectedNode()).showPopup() ) {
		}
	}

}
