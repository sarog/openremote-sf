package org.openremote.web.console.client.unit;

import java.util.HashMap;
import java.util.Map;
import org.openremote.web.console.controller.Controller;
import org.openremote.web.console.controller.EnumControllerCommand;
import org.openremote.web.console.controller.message.ControllerMessage;
import org.openremote.web.console.controller.message.ControllerRequestMessage;
import org.openremote.web.console.controller.message.ControllerResponseMessage;
import org.openremote.web.console.controller.message.EnumControllerResponseCode;
import org.openremote.web.console.event.ConsoleUnitEventManager;
import org.openremote.web.console.event.controller.ControllerMessageEvent;
import org.openremote.web.console.event.controller.ControllerMessageHandler;
import org.openremote.web.console.event.hold.HoldEvent;
import org.openremote.web.console.event.hold.HoldHandler;
import org.openremote.web.console.event.rotate.RotationEvent;
import org.openremote.web.console.event.rotate.RotationHandler;
import org.openremote.web.console.event.swipe.SwipeEvent;
import org.openremote.web.console.event.swipe.SwipeHandler;
import org.openremote.web.console.event.tap.DoubleTapEvent;
import org.openremote.web.console.event.tap.DoubleTapHandler;
import org.openremote.web.console.event.tap.TapEvent;
import org.openremote.web.console.event.tap.TapHandler;
import org.openremote.web.console.panel.PanelIdentity;
import org.openremote.web.console.rpc.json.PanelIdentityJso;
import org.openremote.web.console.screen.view.ScreenView;
import org.openremote.web.console.screen.view.LoadingScreenView;
import org.openremote.web.console.screen.view.TestScreenView;
import org.openremote.web.console.service.ControllerService;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ConsoleUnit extends SimplePanel implements RotationHandler, SwipeHandler, HoldHandler, TapHandler, DoubleTapHandler, ControllerMessageHandler {
	public static final String CONSOLE_HTML_ELEMENT_ID = "consoleUnit";
	public static final String LOGO_TEXT_LEFT = "Open";
	public static final String LOGO_TEXT_RIGHT = "Remote";
	private VerticalPanel componentContainer;
	protected ConsoleDisplay consoleDisplay;
	protected int width;
	protected int height;
	private ScreenView loadingScreen;
	private String orientation = "portrait";
	private ControllerService controllerService;
	private int requestId = 0;
	private Map<Integer, EnumControllerCommand> requestMap = new HashMap<Integer, EnumControllerCommand>();
	
	public ConsoleUnit() {
		this(ConsoleDisplay.DEFAULT_DISPLAY_WIDTH, ConsoleDisplay.DEFAULT_DISPLAY_HEIGHT);
	}
	
	public ConsoleUnit(int width, int height) {
		// Create console container to store display and possibly logo for resizable units
		componentContainer = new VerticalPanel();
		componentContainer.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		super.add(componentContainer);
		
		// Set console unit properties
		setSize(width, height);
		this.getElement().setId(CONSOLE_HTML_ELEMENT_ID);
		this.addStyleName("consoleUnit");
		
		// Create a display and add to console container
		consoleDisplay = new ConsoleDisplay(width, height);
		add(consoleDisplay);
		
		// Register gesture and controller message handlers
		registerHandlers();
	
		// Create and show loading screen
		loadingScreen = new LoadingScreenView();
		loadingScreen = new TestScreenView();
		setScreen(loadingScreen);
		
		// Get Panel list
		Controller controller = new Controller();
		controller.setName("TEST");
		controller.setUrl("http://192.168.1.68:8080/controller");
		controllerService = new ControllerService(controller);
		requestMap.put(requestId, EnumControllerCommand.GET_PANEL_LIST);
		controllerService.sendCommand(requestId++, new ControllerRequestMessage(EnumControllerCommand.GET_PANEL_LIST, new String[0]));
	}
	
	@Override
	public void onRotate(RotationEvent event) {
		boolean rotateDisplay = false;
		
		setOrientation(event);
		setPosition(event.getWindowWidth(), event.getWindowHeight());
		
		// Load in the inverse screen to what is currently loaded
		
		
		// Adjust console display
		consoleDisplay.setOrientation(event, rotateDisplay);
	}
	
	@Override
	public void onHold(HoldEvent event) {
		//TODO Handle Hold Event	
	}

	@Override
	public void onSwipe(SwipeEvent event) {
		//TODO Handle swipe Event
	}

	@Override
	public void onTap(TapEvent event) {
		//TODO Handle Tap Event
	}
	
	@Override
	public void onDoubleTap(DoubleTapEvent event) {
		//TODO Handle Double Tap
	}
	
	@Override
	public void add(Widget widget) {
		componentContainer.add(widget);
	}
	
	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;
	}
	
	public ConsoleDisplay getConsoleDisplay() {
		return this.consoleDisplay;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	/**
	 * Position the console unit in the centre of the window
	 */
	public void setPosition(int winWidth, int winHeight) {
		AbsolutePanel consoleContainer = (AbsolutePanel)this.getParent();
		consoleContainer.setWidgetPosition(this, (winWidth/2)-(width/2), (winHeight/2)-(height/2));
	}

	/**
	 * Adjusts the CSS class to either landscape or portrait
	 * @param orientation
	 */
	public void setOrientation(RotationEvent event) {
		String orientation = event.getOrientation();

		if ("portrait".equals(orientation)) {
			getElement().removeClassName("landscapeConsole");
			getElement().addClassName("portraitConsole");
		} else {
			getElement().removeClassName("portraitConsole");
			getElement().addClassName("landscapeConsole");
		}

		this.orientation = orientation;
	}
	
	public String getOrientation() {
		return orientation;
	}
	
	public void hide() {
		 setVisible(false);
	}
	
	public void show() {
		 setVisible(true);
	}
	
	// Display specified screen
	public void setScreen(ScreenView screen) {
		consoleDisplay.setScreen(loadingScreen);	
	}
	
	public void registerHandlers() {
		HandlerManager eventBus = ConsoleUnitEventManager.getInstance().getEventBus();
		// Gesture Handlers
		eventBus.addHandler(RotationEvent.getType(), this);
		eventBus.addHandler(SwipeEvent.getType(), this);
		eventBus.addHandler(HoldEvent.getType(), this);
		eventBus.addHandler(TapEvent.getType(), this);
		eventBus.addHandler(DoubleTapEvent.getType(), this);
		// Controller Handler
		eventBus.addHandler(ControllerMessageEvent.getType(), this);
	}

	/**
	 * Need a cleaner way of identifying Controller Message payload type
	 * the below implementation is dependent on the use of the JSON Controller
	 * Connector, which is not the design intention
	 */
	@Override
	public void onControllerMessage(ControllerMessageEvent event) {
		// TODO Auto-generated method stub
		ControllerMessage message = event.getMessage();
		switch (message.getType())
		{
			case COMMAND_REQUEST:
				
				break;
			case COMMAND_RESPONSE:
				ControllerResponseMessage response = (ControllerResponseMessage)message;
				int requestId = response.getRequestId();
				EnumControllerResponseCode code = response.getResponseCode();
				if (code != EnumControllerResponseCode.OK) {
					Window.alert("HEUSTON WE HAVE A PROBLEM!");
					break;
				}
				// Determine the command that was initially sent
				EnumControllerCommand requestedCommand = requestMap.get(requestId);
				if (requestedCommand != null) {
					requestMap.remove(requestId);
					switch (requestedCommand) {
						case GET_PANEL_LIST: 
						JsArray<PanelIdentityJso> panels = (JsArray<PanelIdentityJso>)response.getResponseObject();
						Window.alert("We Have Received: " + panels.length() + " Panel Identities");
					}
				}
				break;
			case SENSOR_VALUE_CHANGE:
		}
	}
}
