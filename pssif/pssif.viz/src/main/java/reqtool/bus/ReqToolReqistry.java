package reqtool.bus;

import reqtool.event.Event;
import reqtool.handler.ContainementVisibilityHandler;
import reqtool.handler.CreateSpecProjectHandler;
import reqtool.handler.CreateTestCaseHandler;
import reqtool.handler.EventHandler;
import reqtool.handler.NewDeriveRequirementHandler;
import reqtool.handler.NewVersionHandler;
import reqtool.handler.ReqInfoHandler;
import reqtool.handler.ResolveIssueHandler;
import reqtool.handler.TraceReqEventHandler;
import reqtool.handler.VerifyTestCaseHandler;
import reqtool.handler.VersionVisibilityHandler;
import reqtool.handler.menu.CreateReqMenuEventHandler;
import reqtool.handler.menu.TraceReqMenuEventHandler;
import reqtool.handler.menu.VersionsVisibilityMenuHandler;

import com.google.common.eventbus.EventBus;

/**
 * The Class ReqToolReqistry.
 */
public class ReqToolReqistry {
	
	/** The event bus. */
	private EventBus eventBus;
	
	/** The instance. */
	private static ReqToolReqistry instance;
	
	/**
	 * New instance.
	 *
	 * @return the ReqToolReqistry
	 */
	public static ReqToolReqistry newInstance() {
		instance = null;
		return getInstance();
	}
	
	/**
	 * Gets the single instance of ReqToolReqistry.
	 *
	 * @return single instance of ReqToolReqistry
	 */
	public static ReqToolReqistry getInstance() {
		if (instance == null) {
			instance = new ReqToolReqistry();
		}
		return instance;
	}
	
	/**
	 * Instantiates a new ReqToolReqistry.
	 */
	private ReqToolReqistry() {
		eventBus = new EventBus();
		initStaticReqVisitors();
		initStaticReqMenuVisitors();
	}
	
	/**
	 * Post an event when it's triggered.
	 *
	 * @param event the event
	 */
	public void post(Event event) {
		eventBus.post(event);
	}
	
	/**
	 * Register an event handler.
	 *
	 * @param handler the handler
	 */
	public void register(EventHandler handler) {
		 eventBus.register(handler);
	}

	/**
	 * Inits the static requirement event handlers.
	 */
	private void initStaticReqVisitors() {
		
		register(new ResolveIssueHandler());
		register(new NewVersionHandler());
		register(new TraceReqEventHandler());
		register(new VersionVisibilityHandler());
		register(new CreateTestCaseHandler());
		register(new VerifyTestCaseHandler());
		register(new ContainementVisibilityHandler());
		register(new CreateSpecProjectHandler());
		register(new NewDeriveRequirementHandler());
		register(new ReqInfoHandler());
		
	}
	
	/**
	 * Inits the static requirement event menu visitors.
	 */
	private void initStaticReqMenuVisitors() {
		
		register(new CreateReqMenuEventHandler());
		register(new TraceReqMenuEventHandler());
		register(new VersionsVisibilityMenuHandler());
		
	}
	
}
