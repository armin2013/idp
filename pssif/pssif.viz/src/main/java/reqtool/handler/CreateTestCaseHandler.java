package reqtool.handler;

import reqtool.event.CreateTestCaseEvent;
import reqtool.graph.TestCaseCreatorPopup;

import com.google.common.eventbus.Subscribe;

/**
 * The Class CreateTestCaseHandler.
 */
public class CreateTestCaseHandler implements EventHandler {
	
	/**
	 * Handle new test case event.
	 *
	 * @param event the event
	 */
	@Subscribe
	public void handleNewTestCaseEvent(CreateTestCaseEvent event) {
		TestCaseCreatorPopup crPopup = new TestCaseCreatorPopup(event.getSelectedNode());
		crPopup.showPopup();
		event.getGViz().updateGraph();
	}
	
}
