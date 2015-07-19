package reqtool.handler;

import com.google.common.eventbus.Subscribe;

import reqtool.event.ResolveIssueEvent;
import reqtool.graph.IssueResolverPopup;

/**
 * The Class ResolveIssueHandler.
 */
public class ResolveIssueHandler implements EventHandler{
	
	/**
	 * Handler the resolve issue event.
	 *
	 * @param event the event
	 */
	@Subscribe
	public void handleResolveIssue(ResolveIssueEvent event) {
		IssueResolverPopup popup = new IssueResolverPopup(event.getSelectedNode());
		popup.showPopup();
		event.getGViz().updateGraph();
	}
}
