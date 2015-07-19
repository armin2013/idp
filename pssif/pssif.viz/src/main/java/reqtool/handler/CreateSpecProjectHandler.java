package reqtool.handler;

import reqtool.controller.wizard.SpecificationProjectWizard;
import reqtool.event.CreateSpecProjectEvent;

import com.google.common.eventbus.Subscribe;

/**
 * The Class CreateSpecProjectHandler.
 */
public class CreateSpecProjectHandler implements EventHandler {
	
	/** The requirement project importer. */
	private SpecificationProjectWizard reqProjImporter;
	
	/**
	 * Instantiates a new creates the specification project handler.
	 */
	public CreateSpecProjectHandler() {
		reqProjImporter = new SpecificationProjectWizard();
	}

	/**
	 * Handle specification project event.
	 *
	 * @param event the event
	 * @return true, if successful
	 */
	@Subscribe
	public boolean handleSpecProjectEvent(CreateSpecProjectEvent event) {
		return reqProjImporter.open(event.getFrame());
	}
	
}
