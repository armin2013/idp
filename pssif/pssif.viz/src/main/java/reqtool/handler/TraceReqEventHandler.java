package reqtool.handler;

import reqtool.controller.RequirementTracer;
import reqtool.event.TraceReqEvent;
import reqtool.event.UntraceReqEvent;

import com.google.common.eventbus.Subscribe;

/**
 * The Class TraceReqEventHandler.
 */
public class TraceReqEventHandler implements EventHandler {

	/**
	 * Handle trace requirement event.
	 *
	 * @param event the event
	 */
	@Subscribe
	public void handleTraceReqEvent(TraceReqEvent event) {
		RequirementTracer.traceRequirement(event.getSelectedNode());
		event.getGViz().traceNodes();
	}
	
	/**
	 * Handle untrace requirement event.
	 *
	 * @param event the event
	 */
	@Subscribe
	public void handleUntraceReqEvent(UntraceReqEvent event) {
		RequirementTracer.stopTracing();
		event.getGViz().stopTracingNodes();
	}
}
