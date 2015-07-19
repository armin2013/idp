package reqtool.handler;

import reqtool.controller.TestCaseVerifier;
import reqtool.event.VerifyTestCaseEvent;

import com.google.common.eventbus.Subscribe;

/**
 * The Class VerifyTestCaseHandler.
 */
public class VerifyTestCaseHandler implements EventHandler{

	/**
	 * Handle verify test case event.
	 *
	 * @param event the event
	 */
	@Subscribe
	public void handleVerifyTestCaseEvent(VerifyTestCaseEvent event) {
		new TestCaseVerifier(event.getSelectedNode()).verifyTestCase();
		event.getGViz().updateGraph();
	}
	
}
