package org.openremote.web.console.unit;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.openremote.web.console.controller.Controller;
import org.openremote.web.console.controller.ControllerCredentials;
import org.openremote.web.console.controller.ControllerCredentialsImpl;
import org.openremote.web.console.controller.EnumControllerResponseCode;
import org.openremote.web.console.event.ConsoleUnitEventManager;
import org.openremote.web.console.event.hold.*;
import org.openremote.web.console.event.rotate.*;
import org.openremote.web.console.event.sensor.SensorChangeEvent;
import org.openremote.web.console.event.swipe.*;
import org.openremote.web.console.event.swipe.SwipeEvent.SwipeDirection;
import org.openremote.web.console.event.ui.*;
import org.openremote.web.console.panel.Panel;
import org.openremote.web.console.panel.entity.Gesture;
import org.openremote.web.console.panel.entity.Navigate;
import org.openremote.web.console.panel.entity.Screen;
import org.openremote.web.console.panel.entity.TabBar;
import org.openremote.web.console.service.*;
import org.openremote.web.console.util.BrowserUtils;
import org.openremote.web.console.util.PollingHelper;
import org.openremote.web.console.view.ScreenViewImpl;
import org.openremote.web.console.widget.TabBarComponent;
import com.google.gwt.event.shared.HandlerManager;

public class ConsoleUnit extends SimplePanel implements RotationHandler, SwipeHandler, HoldHandler, NavigateHandler, CommandSendHandler {
	public static final String CONSOLE_HTML_ELEMENT_ID = "consoleUnit";
	public static final String LOGO_TEXT_LEFT = "Open";
	public static final String LOGO_TEXT_RIGHT = "Remote";
	private VerticalPanel componentContainer;
	protected ConsoleDisplay consoleDisplay;
	protected int width;
	protected int height;
	private String orientation = "portrait";
	private ControllerService controllerService = new JSONPControllerService();
	private PanelService panelService = new PanelServiceImpl();
	private LocalDataService dataService = new LocalDataServiceImpl();
	private ScreenViewService screenViewService = new ScreenViewService();
	//private PanelCredentials currentPanelCredentials;
	private ControllerCredentials currentControllerCredentials;
	private String currentPanelName;
	private Integer currentGroupId;
	private Integer currentScreenId;
	private TabBarComponent currentTabBar;
	private ScreenViewImpl currentScreen;
	private Map<SwipeDirection, Gesture> gestureMap = new HashMap<SwipeDirection, Gesture>();
	private Map<Integer, PollingHelper> pollingHelperMap = new HashMap<Integer, PollingHelper>();
	ScreenViewImpl loadingScreen;
	private PopupPanel alertPopup;
	
	AsyncControllerCallback<Map<Integer, String>> pollingCallback = new AsyncControllerCallback<Map<Integer, String>>() {
		@Override
		public void onSuccess(Map<Integer, String> result) {
			HandlerManager eventBus = ConsoleUnitEventManager.getInstance().getEventBus();
			for (Iterator<Integer> it = result.keySet().iterator(); it.hasNext();) {
				Integer id = it.next();
				String sensorValue = result.get(id);
				eventBus.fireEvent(new SensorChangeEvent(id, sensorValue));
			}
		}
	};
	
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
		consoleDisplay = new ConsoleDisplay();
		add(consoleDisplay);
		consoleDisplay.onAdd(width, height);
		
		// Register gesture and controller message handlers
		registerHandlers();
		
		// Initialise Popup
		alertPopup = new PopupPanel(true);
		alertPopup.setWidget(new Label());
	}
	
	public void resize(int width, int height) {
		if (width != this.width || height != this.height) {
			setSize(width, height);
			consoleDisplay.resize(width, height);
		}
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
	
	public void doResize(int width, int height) {
		if (getOrientation().equalsIgnoreCase("portrait")) {
			setSize(width, height);
			consoleDisplay.doResize(width, height);
		} else {
			setSize(height, width);
			consoleDisplay.doResize(height, width);			
		}
	}

	/**
	 * Adjusts the CSS class to either landscape or portrait
	 * @param orientation
	 */
	public void setOrientation(String orientation) {
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
	
	public void initialise() {
		ControllerCredentials controllerCreds;
		String panelName = "";
		
		// Display loading screen
//		loadingScreen = new ControllerCredentialsScreenView();//screenViewService.getScreenView(ScreenViewService.LOADING_SCREEN_ID);
//		loadScreenView(loadingScreen);
//		return;
		
		loadingScreen = screenViewService.getScreenView(ScreenViewService.LOADING_SCREEN_ID);
		loadScreenView(loadingScreen);

		dataService.setDefaultControllerCredentials(new ControllerCredentialsImpl("test", "http://multimation.co.uk:8080/controller", "", "", "Mobile"));
		
		// Check for default Controller in Settings
		controllerCreds = dataService.getDefaultControllerCredentials();
		if (controllerCreds != null) {
			panelName = controllerCreds.getDefaultPanel();
		}
		
		if (panelName != null && !panelName.equals("")) {
			loadControllerAndPanel(controllerCreds, panelName);
		} else {
			// Check for Last Controller and Panel in Cache
			controllerCreds = dataService.getLastControllerCredentials();
			if (controllerCreds != null) {
				panelName = controllerCreds.getDefaultPanel();
			}
			if (panelName == null || panelName.equals("")) {
				panelName = dataService.getLastPanelName();
			}
			
			if (panelName != null && !panelName.equals("")) {
				loadControllerAndPanel(controllerCreds, panelName);
			} else {
				// No panel or controller to load so go to settings
				loadSettings("No History");		
			}
		}	
	}
	
	/*
	 * Clean up currently loaded panel and go to loading screen
	 */
	private void destroy() {
		// TODO:
	}
	
	public void alert(String msg) {
		if (msg != null && msg.length() > 0) {
			Label label = (Label)alertPopup.getWidget();
			label.setText(msg);
			alertPopup.show();
		}
	}
	
	private void loadControllerAndPanel(ControllerCredentials controllerCreds, String panelName) {
		if (controllerCreds == null) {
			loadSettings();
		} else {
			// Instantiate controller from credentials and check it is alive then load the panel by name
			currentControllerCredentials = controllerCreds;
			currentPanelName = panelName;
			controllerService.setController(new Controller(controllerCreds));
			controllerService.isAlive(new AsyncControllerCallback<Boolean>() {
				@Override
				public void onSuccess(Boolean isAlive) {
					if (isAlive) {
						// If current panel name set try and load it otherwise prompt for panel to load
						if (currentPanelName != null && !currentPanelName.equalsIgnoreCase("")) {
							loadPanel(currentPanelName);
						} else {
							// TODO: Panel Selection 
							loadSettings("SELECT PANEL TO LOAD");
						}
					} else {
						// TODO: Controller Selection 
						loadSettings("Controller Not Found");
					}
				}
			});
		}
	}
	
	private void loadPanel(String panelName) {
		controllerService.getPanel(panelName, new AsyncControllerCallback<Panel>() {
			@Override
			public void onFailure(EnumControllerResponseCode response) {
				if(response == EnumControllerResponseCode.PANEL_NOT_FOUND) {
					// TODO: Panel Selection
					loadSettings("Load Panel Selection");
				}
			}
			
			@Override
			public void onSuccess(Panel result) {
				if (result != null) {
					panelService.setCurrentPanel(result);
					
					// Get default group ID
					Integer defaultGroupId = panelService.getDefaultGroupId();
					Screen defaultScreen = panelService.getDefaultScreen(defaultGroupId);
					
					if (defaultScreen != null) {
						// Load default Screen for default group
						loadScreen(defaultGroupId, defaultScreen);
					} else {
						loadSettings("Failed to load Panel");
					}
				} else {
					// TODO: Panel Selection
					loadSettings("Failed to load Panel");
				}
			}			
		});
	}
	
	private void unLoadController() {	
		panelService.setCurrentPanel(null);
		controllerService.setController(null);
		unLoadScreen();
	}
	
	private void loadScreen(Screen screen) {
		loadScreen(currentGroupId, screen);
	}
	
	private void loadScreen(Integer groupId, Screen screen) {
		boolean screenChanged = false;
		boolean groupChanged = false;
		boolean screenOrientationChanged = false;
		boolean tabBarChanged = false;
		Integer oldScreenId = currentScreenId;
		
		if (screen == null) {
			return;
		}
		
		if (currentScreenId != screen.getId()) {
			screenChanged = true;
		}
		if (currentGroupId != groupId) {
			groupChanged = true;
		}
		
		if (!screenChanged && !groupChanged) {
			return;
		}
		
		currentScreenId = screen.getId();
		currentGroupId = groupId;
		
		if (screenChanged) {
			// Check if orientation has changed
			String newScreenOrientation = panelService.getScreenOrientation(currentScreenId);
			String currentDisplayOrientation = consoleDisplay.getOrientation();
			if(!newScreenOrientation.equalsIgnoreCase(currentDisplayOrientation)) {
				screenOrientationChanged = true;
				
				// Adjust console display to suit new screen
				consoleDisplay.setOrientation(newScreenOrientation);				
			}
			
			ScreenViewImpl screenView = screenViewService.getScreenView(screen);
		
			if (screenView != null) {
				loadScreenView(screenView);
				
				// Configure gestures
				setGestureMap(screen.getGesture());
				
				// Stop previous polling and start new polling
				if (pollingHelperMap.containsKey(oldScreenId)) {
					PollingHelper pollingHelper = pollingHelperMap.get(oldScreenId);
					if (pollingHelper != null) {
						pollingHelper.stopMonitoring();
					}
				}
				if (!pollingHelperMap.containsKey(currentScreenId)) {
					Set<Integer> sensorIds = screenView.getSensorIds();
					if (sensorIds == null || sensorIds.size() == 0) {
						pollingHelperMap.put(currentScreenId, null);
					} else {
						pollingHelperMap.put(currentScreenId, new PollingHelper(sensorIds, pollingCallback));
					}
				}
				PollingHelper pollingHelper = pollingHelperMap.get(currentScreenId);
				if (pollingHelper != null) {
					pollingHelper.startSensorMonitoring();
				}
			} else {
				loadSettings();
				return;
			}
		}
		
		if (groupChanged) {
			// Get Tab Bar for this group
			TabBar tabBar = panelService.getTabBar(currentGroupId);
			if (tabBar != null) {
				TabBarComponent tabBarComponent = new TabBarComponent(tabBar);
				tabBarChanged = loadTabBar(tabBarComponent);
			}
		}
		
		if (currentTabBar == null) {
			return;
		}
		
		// Update the tab bar only if it's not new
		if (screenOrientationChanged && !tabBarChanged) {
			currentTabBar.refresh();
			consoleDisplay.setComponentPosition(currentTabBar,  0, consoleDisplay.getHeight() - currentTabBar.getHeight());
		}
		
		if (screenChanged) {
			currentTabBar.onScreenViewChange(currentScreenId);			
		}
	}
	
	private void unLoadScreen() {
		if (currentScreen != null) {
			currentGroupId = null;
			currentScreenId = null;
			
			clearGestureMap();
			unLoadScreenView();
			unLoadTabBar();
			for (Integer screenId : pollingHelperMap.keySet()) {
				PollingHelper helper = pollingHelperMap.get(screenId);
				if (helper != null) {
					helper.stopMonitoring();
				}
			}
			pollingHelperMap.clear();
		}
	}
	
	private void setGestureMap(List<Gesture> gestures) {
		clearGestureMap();
		
		if (gestures != null) {
			for (Gesture gesture : gestures) {
				SwipeDirection direction = SwipeDirection.enumValueOf(gesture.getType());
				gestureMap.put(direction, gesture);
			}
		}
	}
	
	private void clearGestureMap() {
		gestureMap.put(SwipeDirection.LEFT, null);
		gestureMap.put(SwipeDirection.RIGHT, null);
		gestureMap.put(SwipeDirection.UP, null);
		gestureMap.put(SwipeDirection.DOWN, null);
	}
	
	private void getIsSecure() {
		controllerService.isSecure(new AsyncControllerCallback<Boolean>() {
			@Override
			public void onSuccess(Boolean result) {
				if (result) {
					// TODO:
					Window.alert("I'm Secure!");
				} else {
					// TODO:
					Window.alert("I'm not Secure");
				}
			}			
		});
	}
	
	public void loadSettings() {
		loadSettings(null);
	}
	
	public void loadSettings(String displayMessage) {
		// TODO Load the settings screen
		Window.alert("LOAD SETTINGS: " + displayMessage);
	}
	
	private void loadScreenView(ScreenViewImpl screenView) {
		if (screenView == null) {
			return;
		}
	
		if (currentScreen != null && screenView != currentScreen) {
			consoleDisplay.removeComponent(currentScreen);
		}
	
		consoleDisplay.addComponent(screenView);
		
		currentScreen = screenView;
	}
	
	private void unLoadScreenView() {
		if (currentScreen != null) {
			consoleDisplay.removeComponent(currentScreen);
		}
	}
	
	private boolean loadTabBar(TabBarComponent tabBar) {
		boolean changeOccurred = false;
		if (tabBar == null) {
			return changeOccurred;
		}
	
		if (currentTabBar != null && tabBar != currentTabBar) {
			consoleDisplay.removeComponent(currentTabBar);
		}
		consoleDisplay.addComponent(tabBar, 0, consoleDisplay.getHeight() - tabBar.getHeight());
		currentTabBar = tabBar;
		changeOccurred = true;
		
		return changeOccurred;
	}
	
	private void unLoadTabBar() {
		if (currentTabBar != null) {
			consoleDisplay.removeComponent(currentTabBar);
		}
		currentTabBar = null;
	}
	
	public ControllerService getControllerService() {
		return this.controllerService;
	}
	
	/*
	 * **********************************************
	 * Event Handlers below here
	 * **********************************************
	 */
	private void registerHandlers() {
		HandlerManager eventBus = ConsoleUnitEventManager.getInstance().getEventBus();
		eventBus.addHandler(RotationEvent.getType(), this);
		eventBus.addHandler(SwipeEvent.getType(), this);
		eventBus.addHandler(HoldEvent.getType(), this);
		eventBus.addHandler(NavigateEvent.getType(), this);
		eventBus.addHandler(CommandSendEvent.getType(), this);
	}
	
	@Override
	public void onRotate(RotationEvent event) {
		String orientation = event.getOrientation();
		setOrientation(orientation);
		// Resize if mobile
		if(BrowserUtils.isMobile) {
			doResize(event.getWindowWidth(), event.getWindowHeight());
			// Refresh the tab bar
			if (currentTabBar != null) {
				currentTabBar.refresh();
				consoleDisplay.setComponentPosition(currentTabBar,  0, consoleDisplay.getHeight() - currentTabBar.getHeight());
			}
		}
		setPosition(event.getWindowWidth(), event.getWindowHeight());
		
		// Load in the inverse screen to what is currently loaded if screen orientation doesn't match console orientation
		if (panelService.isInitialized()) {
			if (!orientation.equalsIgnoreCase(panelService.getScreenOrientation(currentScreenId))) {
				Screen inverseScreen = panelService.getInverseScreen(currentScreenId);
				if (inverseScreen != null) {
					loadScreen(currentGroupId, inverseScreen);
				}
			}
		}
	}
	
	@Override
	public void onHold(HoldEvent event) {
		if (event.getSource() == consoleDisplay) {
			loadSettings();
		}
	}

	@Override
	public void onSwipe(SwipeEvent event) {
		Gesture gesture = gestureMap.get(event.getDirection());
		boolean gestureHandled = false;
		Navigate navigate = null;
		Boolean hasControlCommand = null;
		Integer commandId = null;
		
		if (gesture != null) {
			navigate = gesture.getNavigate();
			hasControlCommand = gesture.getHasControlCommand();
			commandId = gesture.getId();
			HandlerManager eventBus = ConsoleUnitEventManager.getInstance().getEventBus();
			
			if (navigate != null) {
				if (navigate.getToGroup() != currentGroupId || navigate.getToScreen() != currentScreenId) {
					gestureHandled = true;
					eventBus.fireEvent(new NavigateEvent(navigate));
				}
			} else if (hasControlCommand) {
				gestureHandled = true;
				eventBus.fireEvent(new CommandSendEvent(commandId, "swipe", null));
			}
		}
		
		if (gestureHandled) {
			return;
		}
			
		switch (event.getDirection()) {
			case LEFT:
				Screen nextScreen = panelService.getNextScreen(currentGroupId, currentScreenId);
				loadScreen(nextScreen);
				break;
			case RIGHT:
				Screen prevScreen = panelService.getPreviousScreen(currentGroupId, currentScreenId);
				loadScreen(prevScreen);
				break;
		}
	}
	
	@Override
	public void onNavigate(NavigateEvent event) {
		Navigate navigate = event.getNavigate();
		if (navigate != null) {
			String to = navigate.getTo();
			Integer toGroupId = navigate.getToGroup();
			Integer toScreenId = navigate.getToScreen();
			
			if (to != null && !to.equals("")) {
				// TODO Load System Screen
				Window.alert("Load System Screen");
			} else if(toGroupId != null && toScreenId != null) {
				Screen screen = panelService.getScreenById(toScreenId);
				loadScreen(toGroupId, screen);
			}
		}
	}

	@Override
	public void onCommandSend(CommandSendEvent event) {
		if (event != null) {
			controllerService.sendCommand(event.getCommandId() + "/" + event.getCommand(), new AsyncControllerCallback<Boolean>() {
				@Override
				public void onSuccess(Boolean result) {
					if (!result) {
						Window.alert("Command Send Failed!");
					}
				}			
			});
		}
	}
	
	public void onError(EnumControllerResponseCode errorCode) {
		switch (errorCode) {
			case XML_CHANGED:
				// Try and reload the panel
				unLoadController();
				// Wait 2s before reloading to allow controller time to re-initialise
				Timer reloadTimer = new Timer() {
					@Override
					public void run() {
						loadControllerAndPanel(currentControllerCredentials, currentPanelName);
					}
				};
				reloadTimer.schedule(2000);
				break;
			default:
				unLoadController();
				loadSettings(errorCode.getDescription());
				break;
		}
	}
}
