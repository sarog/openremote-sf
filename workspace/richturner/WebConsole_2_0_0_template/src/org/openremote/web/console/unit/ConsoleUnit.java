package org.openremote.web.console.unit;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import org.openremote.web.console.client.WebConsole;
import org.openremote.web.console.controller.Controller;
import org.openremote.web.console.controller.ControllerCredentials;
import org.openremote.web.console.controller.ControllerCredentialsList;
import org.openremote.web.console.controller.EnumControllerResponseCode;
import org.openremote.web.console.event.ConsoleUnitEventManager;
import org.openremote.web.console.event.hold.*;
import org.openremote.web.console.event.rotate.*;
import org.openremote.web.console.event.sensor.SensorChangeEvent;
import org.openremote.web.console.event.swipe.*;
import org.openremote.web.console.event.swipe.SwipeEvent.SwipeDirection;
import org.openremote.web.console.event.ui.*;
import org.openremote.web.console.panel.Panel;
import org.openremote.web.console.panel.SystemPanel;
import org.openremote.web.console.panel.entity.DataValuePairContainer;
import org.openremote.web.console.panel.entity.Gesture;
import org.openremote.web.console.panel.entity.Navigate;
import org.openremote.web.console.panel.entity.Screen;
import org.openremote.web.console.panel.entity.TabBar;
import org.openremote.web.console.panel.entity.WelcomeFlag;
import org.openremote.web.console.service.*;
import org.openremote.web.console.util.BrowserUtils;
import org.openremote.web.console.util.PollingHelper;
import org.openremote.web.console.view.ScreenViewImpl;
import org.openremote.web.console.widget.ScreenIndicator;
import org.openremote.web.console.widget.TabBarComponent;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.shared.HandlerManager;
import com.google.web.bindery.autobean.shared.AutoBean;

public class ConsoleUnit extends VerticalPanel implements RotationHandler, WindowResizeHandler, SwipeHandler, HoldHandler, NavigateHandler, CommandSendHandler {
	public static final int MIN_WIDTH = 310;
	public static final int MIN_HEIGHT = 460;
	public static final int DEFAULT_DISPLAY_WIDTH = 310;
	public static final int DEFAULT_DISPLAY_HEIGHT = 460;
	public static final String DEFAULT_DISPLAY_COLOUR = "#000";
	public static final String CONSOLE_HTML_ELEMENT_ID = "consoleUnit";
	public static final int FRAME_WIDTH_TOP = 20;
	public static final int FRAME_WIDTH_BOTTOM = 50;
	public static final int FRAME_WIDTH_LEFT = 20;
	public static final int FRAME_WIDTH_RIGHT = 20;
	public static final int BOSS_WIDTH = 2;
	public static final String LOGO_TEXT_LEFT = "Open";
	public static final String LOGO_TEXT_RIGHT = "Remote";
	protected ConsoleDisplay consoleDisplay;
	private Boolean isFullscreen = true;
	protected int width;
	protected int height;
	private String orientation = "portrait";
	private ControllerService controllerService = JSONPControllerService.getInstance();
	private PanelService panelService = PanelServiceImpl.getInstance();
	private LocalDataService dataService = LocalDataServiceImpl.getInstance();
	private ScreenViewService screenViewService = ScreenViewService.getInstance();
	private ControllerCredentials currentControllerCredentials = null;
	private Panel systemPanel = null;
	private String currentPanelName = null;
	private Integer currentGroupId = 0;
	private Integer currentScreenId = 0;
	private Map<SwipeDirection, Gesture> gestureMap = new HashMap<SwipeDirection, Gesture>();
	private Map<Integer, PollingHelper> pollingHelperMap = new HashMap<Integer, PollingHelper>();
	private PopupPanel alertPopup;
	private HorizontalPanel logoPanel;
	private boolean invalidWarningDisplayed = false;
	
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
	
	public enum EnumSystemScreen {
//		CONTROLLER_LIST(50, 2, "controllerlist"),
//		ADD_EDIT_CONTROLLER(54, 5, "editcontroller"),
//		CONSOLE_SETTINGS(51, 3, "settings"),
//		LOGIN(52, 4, "login"),
//		LOGOUT(53, 4, "logout"),
//		PANEL_SELECTION(55, 6, "panelselection");
		
//TODO: System Screens - For now point everything to settings screen
		
		CONTROLLER_LIST(50, 2, "controllerlist"),
		EDIT_CONTROLLER(54, 5, "editcontroller"),
		ADD_CONTROLLER(56, 7, "addcontroller"),
		CONSOLE_SETTING(51, 3, "setting"),
		CONSOLE_SETTINGS(51, 3, "settings"),
		LOGIN(50, 2, "login"),
		LOGOUT(50, 2, "logout"),
		PANEL_SELECTION(55, 6, "panelselection");
		
		private final int id;
		private final int groupId;
		private final String name;
		
		EnumSystemScreen(int id, int groupId, String name) {
			this.id = id;
			this.groupId = groupId;
			this.name = name;
		}
		
		public int getId() {
			return id;
		}
		
		public int getGroupId() {
			return groupId;
		}
		
		public String getName() {
			return name;
		}
		
		public static EnumSystemScreen getSystemScreen(int id) {
			EnumSystemScreen result = null;
			for (EnumSystemScreen screen : EnumSystemScreen.values()) {
				if (screen.getId() == id) {
					result = screen;
					break;
				}
			}
			return result;
		}
		
		public static EnumSystemScreen getSystemScreen(String name) {
			EnumSystemScreen result = null;
			for (EnumSystemScreen screen : EnumSystemScreen.values()) {
				if (screen.getName().equalsIgnoreCase(name)) {
					result = screen;
					break;
				}
			}
			return result;
		}
	}
	
	public ConsoleUnit() {
		this(DEFAULT_DISPLAY_WIDTH, DEFAULT_DISPLAY_HEIGHT);
	}
	
	public ConsoleUnit(int width, int height) {
		setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		if (width > height) {
			int tempWidth = height;
			height = width;
			width = tempWidth;
		}
		this.width = width;
		this.height = height;
		
		// Create a display and add to console container
		consoleDisplay = new ConsoleDisplay();
		add(consoleDisplay);
		consoleDisplay.getElement().getStyle().setBackgroundColor(DEFAULT_DISPLAY_COLOUR);
		DOM.setStyleAttribute(RootPanel.getBodyElement(),"backgroundColor", DEFAULT_DISPLAY_COLOUR);
		DOM.setStyleAttribute(RootPanel.getBodyElement(),"background", DEFAULT_DISPLAY_COLOUR);
		//RootPanel.getBodyElement().setAttribute("onselect", "return false;");
		getElement().setAttribute("onselect", "return false;");
		consoleDisplay.getElement().setAttribute("onselect", "return false;");
		
		// Set console unit properties
		getElement().setId(CONSOLE_HTML_ELEMENT_ID);
		addStyleName("portraitConsole");
		addStyleName("consoleUnit");
		
		// Register gesture and controller message handlers
		registerHandlers();
	}
	
	public void setSize(int width, int height) {
		boolean setFullscreen = false;

		if (width > height) {
			int tempWidth = height;
			height = width;
			width = tempWidth;
		}

		if (BrowserUtils.isMobile) {
			setFullscreen = true;
		} else {
			int winWidth = BrowserUtils.getWindowWidth();
			int winHeight = BrowserUtils.getWindowHeight();
			int maxDim = width > height ? width : height; 
			String winOrientation = BrowserUtils.getWindowOrientation();
		
			width = width < MIN_WIDTH ? MIN_WIDTH : width;
			height = height < MIN_HEIGHT ? MIN_HEIGHT : height;	
			
			if (maxDim >= winWidth || maxDim >= winHeight) {
				if (winOrientation.equals("portrait")) {
					width = winWidth;
					height = winHeight;
				} else {
					width = winHeight;
					height = winWidth;
				}
				setFullscreen = true;
			}
		}
		
		if(isFullscreen != setFullscreen) {
			showFrame(!setFullscreen);
		}
		
		this.width = width;
		this.height = height;
		isFullscreen = setFullscreen;
		
		if (setFullscreen) {
			setOrientation(BrowserUtils.getWindowOrientation());
		}
		
		setPosition(BrowserUtils.getWindowWidth(), BrowserUtils.getWindowHeight());
		
		consoleDisplay.setSize(width, height);
	}
		
	private void showFrame(boolean showFrame) {
		if (showFrame) {
			removeStyleName("fullscreenConsole");
			
			// Create console frame
			createFrame();
		} else {
			// Set document body colour the same as the console display
			removeFrame();
		}
	}
	
	private void createFrame() {
		Style style = consoleDisplay.getElement().getStyle();
		style.setMarginTop(FRAME_WIDTH_TOP-BOSS_WIDTH, Unit.PX);
		style.setMarginRight(FRAME_WIDTH_RIGHT-BOSS_WIDTH, Unit.PX);
		style.setMarginLeft(FRAME_WIDTH_LEFT-BOSS_WIDTH, Unit.PX);
		style.setMarginBottom(-BOSS_WIDTH, Unit.PX);
		addStyleName("consoleFrame");
		addStyleName("resizableConsole");
		removeStyleName("fullscreenConsole");
		
		// Clear document body colour setting
		DOM.setStyleAttribute(RootPanel.getBodyElement(),"backgroundColor", "");
		DOM.setStyleAttribute(RootPanel.getBodyElement(),"background", "");
		
		// Add boss to screen
		style.setBorderWidth(BOSS_WIDTH,Unit.PX);
		style.setBorderStyle(BorderStyle.SOLID);
		style.setBorderColor("#333");
		
		// Add the logo along the bottom of the frame
		if (logoPanel == null) {
			logoPanel = new HorizontalPanel();
			logoPanel.setStylePrimaryName("consoleFrameLogo");
			logoPanel.setHeight(FRAME_WIDTH_BOTTOM + "px");
			DOM.setStyleAttribute(logoPanel.getElement(), "lineHeight", FRAME_WIDTH_BOTTOM + "px");
			Label logoLeft = new Label();
			logoLeft.setText(LOGO_TEXT_LEFT);
			logoLeft.setHeight(FRAME_WIDTH_BOTTOM + "px");
			logoLeft.getElement().setId("consoleFrameLogoLeft");
			logoPanel.add(logoLeft);
			Label logoRight = new Label();
			logoRight.setText(LOGO_TEXT_RIGHT);
			logoRight.setHeight(FRAME_WIDTH_BOTTOM + "px");
			logoRight.getElement().setId("consoleFrameLogoRight");
			logoPanel.add(logoRight);
			logoPanel.getElement().setAttribute("onselect", "return false;");
			logoLeft.getElement().setAttribute("onselect", "return false;");
			logoRight.getElement().setAttribute("onselect", "return false;");
		}
		add(logoPanel);
	}
	
	private void removeFrame() {
		Style style = consoleDisplay.getElement().getStyle();
		
		style.clearMargin();		
		style.clearBorderColor();
		style.clearBorderStyle();
		style.clearBorderWidth();
		remove(logoPanel);
		removeStyleName("resizableConsole");
		addStyleName("fullscreenConsole");
		removeStyleName("consoleFrame");
		DOM.setStyleAttribute(RootPanel.getBodyElement(),"backgroundColor", DEFAULT_DISPLAY_COLOUR);
		DOM.setStyleAttribute(RootPanel.getBodyElement(),"background", DEFAULT_DISPLAY_COLOUR);
	}
	
	public ConsoleDisplay getConsoleDisplay() {
		return this.consoleDisplay;
	}
	
	public int getWidth() {
		if (isFullscreen) {
			return width;
		} else {
			return width + FRAME_WIDTH_LEFT + FRAME_WIDTH_RIGHT;
		}
	}
	
	public int getHeight() {
		if (isFullscreen) {
			return height;
		}
		else {
			return height + FRAME_WIDTH_TOP + FRAME_WIDTH_BOTTOM;
		}		
	}
	
	/**
	 * Position the console unit in the centre of the window
	 */
	public void setPosition(int winWidth, int winHeight) {
		int xPos = 0;
		int yPos = 0;
		if (BrowserUtils.isIE && orientation.equalsIgnoreCase("landscape")) {
			xPos = (int)Math.round(((double)winWidth/2)-(getHeight()/2));
			yPos = (int)Math.round(((double)winHeight/2)-(getWidth()/2));
		} else {
			xPos = (int)Math.round(((double)winWidth/2)-(getWidth()/2));
			yPos = (int)Math.round(((double)winHeight/2)-(getHeight()/2));
		}
		BrowserUtils.getConsoleContainer().setWidgetPosition(this, xPos, yPos);
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
		setPosition(BrowserUtils.getWindowWidth(), BrowserUtils.getWindowHeight());
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
	
	public void alert(String msg) {
		if (msg != null && msg.length() > 0) {
			Label label = (Label)alertPopup.getWidget();
			label.setText(msg);
			alertPopup.show();
		}
	}
	
	private void loadController(ControllerCredentials controllerCreds) {
		if (controllerCreds == null) {
			loadSettings(EnumSystemScreen.CONTROLLER_LIST, null);
		} else {
			// Instantiate controller from credentials and check it is alive then load the panel by name
			currentControllerCredentials = controllerCreds;
			controllerService.setController(new Controller(controllerCreds));
			controllerService.isAlive(new AsyncControllerCallback<Boolean>() {
				@Override
				public void onSuccess(Boolean isAlive) {
					if (isAlive) {
						currentPanelName = currentControllerCredentials.getDefaultPanel();
						
						// If current panel name set try and load it otherwise prompt for panel to load
						if (currentPanelName != null && !currentPanelName.equalsIgnoreCase("")) {
							dataService.setLastControllerCredentials(currentControllerCredentials);
							loadPanel(currentPanelName);
						} else {
							loadSettings(EnumSystemScreen.PANEL_SELECTION, null);
						}
					} else {
						loadSettings(EnumSystemScreen.CONTROLLER_LIST, null);
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
					loadSettings(EnumSystemScreen.PANEL_SELECTION, null);
				}
			}
			
			@Override
			public void onSuccess(Panel result) {
				if (result != null) {
					setPanel(result);
					initialisePanel();
				}
			}			
		});
	}
	
	private void setPanel(Panel result) {
		if (result != null) {
			unloadPanel();
			
			panelService.setCurrentPanel(result);
		}
//			// If desktop resize console unit to match panel definition
//			if (!isFullscreen) {
//				PanelSize size = panelService.getPanelSize();
//				setSize(size.getWidth(), size.getHeight());
//			}
	}
	
	private void initialisePanel() {
		if (panelService.isInitialized()) {
			// Reset warning flag
			invalidWarningDisplayed = false;
			
			// Get default group ID
			Integer defaultGroupId = panelService.getDefaultGroupId();
			Screen defaultScreen = panelService.getDefaultScreen(defaultGroupId);
			
			if (defaultScreen != null) {
				// Load default Screen for default group
				loadDisplay(defaultGroupId, defaultScreen, null);
			} else {
				loadSettings(EnumSystemScreen.PANEL_SELECTION, null);
			}
		} else {
			loadSettings(EnumSystemScreen.PANEL_SELECTION, null);
		}
	}
	
	private void unloadPanel() {
		panelService.setCurrentPanel(null);
		screenViewService.reset();
		consoleDisplay.clearDisplay();
		clearGestureMap();

		for (Integer screenId : pollingHelperMap.keySet()) {
			PollingHelper helper = pollingHelperMap.get(screenId);
			if (helper != null) {
				helper.stopMonitoring();
			}
		}
		pollingHelperMap.clear();

		currentGroupId = 0;
		currentScreenId = 0;		
	}
	
	private void unloadControllerAndPanel() {
		unloadPanel();
		unloadController();
	}
	
	private void unloadController() {
		controllerService.setController(null);
	}
	
	public void reloadControllerAndPanel() {
		unloadControllerAndPanel();
		
		// Wait 2s before reloading to allow controller time to re-initialise
		Timer reloadTimer = new Timer() {
			@Override
			public void run() {
				loadController(currentControllerCredentials);
			}
		};
		reloadTimer.schedule(2000);
	}
	
	public void restart() {
		unloadControllerAndPanel();
		onAdd();
	}
	
	private void loadSettings(EnumSystemScreen systemScreen, List<DataValuePairContainer> data) {
		if (systemPanel == null) {
			systemPanel = SystemPanel.get();
		}
		
		if (systemPanel == null) {
			Window.alert("Cannot load system panel definition");
			return;
		}
		
		if (panelService.getCurrentPanel() != systemPanel) { 
			unloadPanel();
			setPanel(systemPanel);
		}
		
		Integer groupId = systemScreen.getGroupId();
		Screen screen = panelService.getScreenById(systemScreen.getId());
		
		if (screen != null) {
			loadDisplay(groupId, screen, data);
		}
	}
	
	private void loadDisplay(Screen screen, List<DataValuePairContainer> data) {
		loadDisplay(currentGroupId, screen, false, data);
	}
	
	private void loadDisplay(Screen screen, boolean orientationChanged, List<DataValuePairContainer> data) {
		loadDisplay(currentGroupId, screen, orientationChanged, data);
	}
	
	private void loadDisplay(Integer newGroupId, Screen screen, List<DataValuePairContainer> data) {
		loadDisplay(newGroupId, screen, false, data);
	}
	
	private void loadDisplay(Integer newGroupId, Screen screen, boolean orientationChanged, List<DataValuePairContainer> data) {
		boolean screenChanged = false;
		boolean groupChanged = false;
		boolean tabBarChanged = false;
		Integer oldScreenId = currentScreenId;
		
		if (screen == null || newGroupId == null) {
			onError(EnumConsoleErrorCode.PANEL_DEFINITION_ERROR);
			return;
		}
		
		int newScreenId = screen.getId();
		
		if (currentScreenId != newScreenId) {
			screenChanged = true;
		}
		if (currentGroupId != newGroupId) {
			groupChanged = true;
		}
		
		if (!screenChanged && !groupChanged) {
			return;
		}
		
		if (screenChanged) {
//			// If old screen is system screen and this is desktop resize console unit to match panel definition
//			if (!BrowserUtils.isMobile) {
//				if (oldScreenId < 0) {
//					PanelSize size = panelService.getPanelSize();
//					resize(size.getWidth(), size.getHeight());
//				}
//			}
			
			// Setting screen should return true at this point
			ScreenViewImpl currentScreenView = consoleDisplay.getScreen();
			try {
				ScreenViewImpl screenView = screenViewService.getScreenView(screen);
				consoleDisplay.setScreenView(screenView, data);
				currentScreenView = screenView;
			} catch (Exception e) {
				consoleDisplay.setScreenView(currentScreenView, data);
				onError(EnumConsoleErrorCode.SCREEN_ERROR, "Screen ID = " + newScreenId);
				return;
			}
			
			// Configure gestures
			setGestureMap(screen.getGesture());
			
			// Stop previous polling
			if (pollingHelperMap.containsKey(oldScreenId)) {
				PollingHelper pollingHelper = pollingHelperMap.get(oldScreenId);
				if (pollingHelper != null) {
					pollingHelper.stopMonitoring();
				}
			}
			// Start new polling
			if (!pollingHelperMap.containsKey(newScreenId)) {
				Set<Integer> sensorIds = currentScreenView.getSensorIds();
				if (sensorIds == null || sensorIds.size() == 0) {
					pollingHelperMap.put(newScreenId, null);
				} else {
					pollingHelperMap.put(newScreenId, new PollingHelper(sensorIds, pollingCallback));
				}
			}
			PollingHelper pollingHelper = pollingHelperMap.get(newScreenId);
			if (pollingHelper != null) {
				pollingHelper.startSensorMonitoring();
			}
			currentScreenId = newScreenId;
		}
		
		if (groupChanged) {
			consoleDisplay.removeTabBar();
			// Get Tab Bar for this group
			TabBar tabBar = panelService.getTabBar(newGroupId);
			if (tabBar != null && tabBar.getItem() != null) {
				try {
					TabBarComponent tabBarComponent = new TabBarComponent(tabBar);
					tabBarChanged = consoleDisplay.setTabBar(tabBarComponent);
					tabBarComponent.onScreenViewChange(new ScreenViewChangeEvent(newScreenId, newGroupId));
				} catch (Exception e) {
					onError(EnumConsoleErrorCode.TABBAR_ERROR);
				}
			}
			// Get Screen ID List and create screenIndicator
			List<Integer> screenIds = panelService.getGroupScreenIdsWithSameOrientation(newScreenId, newGroupId);
			if (screenIds != null && screenIds.size() > 1) {
				ScreenIndicator screenIndicator = new ScreenIndicator(screenIds);
				consoleDisplay.setScreenIndicator(screenIndicator);
				screenIndicator.onScreenViewChange(new ScreenViewChangeEvent(newScreenId, newGroupId));
			} else {
				consoleDisplay.removeScreenIndicator();
			}
			currentGroupId = newGroupId;
		}
		
		if (screenChanged) {
			ConsoleUnitEventManager.getInstance().getEventBus().fireEvent(new ScreenViewChangeEvent(newScreenId, newGroupId));		
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
	
	public ControllerService getControllerService() {
		return this.controllerService;
	}

	public LocalDataService getLocalDataService() {
		return this.dataService;
	}
	
	/*
	 * **********************************************
	 * Event Handlers below here
	 * **********************************************
	 */
	private void registerHandlers() {
		HandlerManager eventBus = ConsoleUnitEventManager.getInstance().getEventBus();
		eventBus.addHandler(RotationEvent.getType(), this);
		eventBus.addHandler(WindowResizeEvent.getType(), this);
		eventBus.addHandler(SwipeEvent.getType(), this);
		eventBus.addHandler(HoldEvent.getType(), this);
		eventBus.addHandler(NavigateEvent.getType(), this);
		eventBus.addHandler(CommandSendEvent.getType(), this);
	}
	
	public void onAdd() {
		ControllerCredentials controllerCreds;
		String panelName = "";
		
		// Set size
		setSize(width, height);
		
		// Configure display
		consoleDisplay.onAdd(width, height);
		
		show();
		
		// TODO: Check for default Controller in Settings
//		controllerCreds = dataService.getDefaultControllerCredentials();
//		if (controllerCreds != null) {
//			panelName = controllerCreds.getDefaultPanel();
//		} else {
//			controllerCreds = AutoBeanService.getInstance().getFactory().create(ControllerCredentials.class).as();
//			controllerCreds.setUrl("http://controller.openremote.org/iphone/controller/");
//			controllerCreds.setDefaultPanel("My Home");
//			dataService.setDefaultControllerCredentials(controllerCreds);
//		}
//		
//		if (panelName != null && !panelName.equals("")) {
//			loadControllerAndPanel(controllerCreds, panelName);
//		} else {
		
		// Display Welcome message
		String welcomeString = dataService.getObjectString(EnumDataMap.WELCOME_FLAG.getDataName());
		AutoBean<?> bean = AutoBeanService.getInstance().fromJsonString(EnumDataMap.WELCOME_FLAG.getClazz(), welcomeString);
		WelcomeFlag flag = (WelcomeFlag)bean.as();
		if (!flag.getWelcomeDone()) {
			Window.alert("Welcome to the latest Web Console!\n\nUnfortunately it's still not 100% complete!\n\nMost things should work but please check the forums " +
							"regularly for information about updates, to request features and report bugs.\n\n" +
							"Some Pointers: - \n\n" +
							"- Use the Search button or manually add Controllers (at present you have to manually specify the name of the Panel to load).\n" +
							"- Hold down on the background at any time to go back to the Controller List screen.\n" +
							"- Unfortunately security is not supported in this version");
			flag.setWelcomeDone(true);
			dataService.setObject(EnumDataMap.WELCOME_FLAG.getDataName(), AutoBeanService.getInstance().toJsonString(flag));
		}
		
			// Check for Last Controller and Panel in Cache
			controllerCreds = dataService.getLastControllerCredentials();
			if (controllerCreds != null && controllerCreds.getUrl() != null) {
				loadController(controllerCreds);
			} else {
				// No controller to load so go to settings
				loadSettings(EnumSystemScreen.CONTROLLER_LIST, null);
			}
//		}
	}
	
	@Override
	public void onRotate(RotationEvent event) {
		String orientation = event.getOrientation();
		
		// Rotate the console unit if fullscreen or mobile but if desktop and fullscreen just rotate the display
		if (BrowserUtils.isMobile || (!BrowserUtils.isMobile && !isFullscreen)) {
			setOrientation(orientation);
		}
		
		// Load in the inverse screen to what is currently loaded if screen orientation doesn't match console orientation
		if (panelService.isInitialized()) {
			if (!orientation.equalsIgnoreCase(consoleDisplay.getOrientation()) || (!BrowserUtils.isMobile && isFullscreen)) {
				Screen inverseScreen = panelService.getInverseScreen(currentScreenId);
				if (inverseScreen != null) {
					loadDisplay(inverseScreen, true, null);
					consoleDisplay.updateTabBar();
				}
			}
		}
	}
	
	@Override
	public void onWindowResize(WindowResizeEvent event) {
		// If fullscreen unit then we update the console unit size
		if (BrowserUtils.isMobile || isFullscreen) {
			if (getOrientation().equalsIgnoreCase("portrait")) {
				setSize(event.getWindowWidth(), event.getWindowHeight());
			} else {
				setSize(event.getWindowHeight(), event.getWindowWidth());
			}
		} else {
			setPosition(event.getWindowWidth(), event.getWindowHeight());
		}
	}
	
	@Override
	public void onHold(HoldEvent event) {
		if (event.getSource() == consoleDisplay) {
			loadSettings(EnumSystemScreen.CONTROLLER_LIST, null);
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
				if (navigate.getTo() != null) {
					if (navigate.getTo().equalsIgnoreCase("previousscreen")) {
						gestureHandled = true;
						Screen prevScreen = panelService.getPreviousScreen(currentGroupId, currentScreenId);
						if (prevScreen != null) {
							loadDisplay(prevScreen, null);
						}
					} else if (navigate.getTo().equalsIgnoreCase("nextscreen")) {
						gestureHandled = true;
						Screen nextScreen = panelService.getNextScreen(currentGroupId, currentScreenId);
						if (nextScreen != null) {
							loadDisplay(nextScreen, null);
						}
					}					
				}
				if (!gestureHandled && (navigate.getToGroup() != currentGroupId || navigate.getToScreen() != currentScreenId)) {
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
				if (nextScreen != null) {
					loadDisplay(nextScreen, null);
				}
				break;
			case RIGHT:
				Screen prevScreen = panelService.getPreviousScreen(currentGroupId, currentScreenId);
				if (prevScreen != null) {
					loadDisplay(prevScreen, null);
				}
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
			List<DataValuePairContainer> data = navigate.getData();
			
			if (to != null && !to.equals("")) {
				EnumSystemScreen screen = EnumSystemScreen.getSystemScreen(to);
				if (screen != null) {
					loadSettings(screen, data);
				}
			} else if(toGroupId != null) {
				Screen screen = null;
				if (toScreenId == null) {
					screen = panelService.getDefaultScreen(toGroupId);
				} else {
					screen = panelService.getScreenById(toScreenId);
				}
				loadDisplay(toGroupId, screen, data);
			}
		}
	}

	@Override
	public void onCommandSend(CommandSendEvent event) {
		if (event != null) {
			switch (event.getCommandId()) {
			case -1: //Controller Discovery
				// Change tab bar search item image
				String imageSrc = BrowserUtils.getSystemImageDir() + "/" + "controller_searching.gif";
				consoleDisplay.getTabBar().getItems().get(0).setImageSrc(imageSrc);
				
				// Do RPC
				AutoDiscoveryRPCServiceAsync discoveryService = (AutoDiscoveryRPCServiceAsync) GWT.create(AutoDiscoveryRPCService.class);
				AsyncControllerCallback<List<String>> callback = new AsyncControllerCallback<List<String>>() {
					@Override
					public void onSuccess(List<String> discoveredUrls) {
						ControllerCredentialsList credsListObj = dataService.getControllerCredentialsList();
						List<ControllerCredentials> credsList = credsListObj.getControllerCredentials();
						for (String discoveredUrl : discoveredUrls) {
							boolean alreadyExists = false;
							for (ControllerCredentials creds : credsList) {
								if (creds.getUrl().equalsIgnoreCase(discoveredUrl)) {
									alreadyExists = true;
									break;
								}
							}
							if (!alreadyExists) {
								AutoBean<ControllerCredentials> credentialsBean = AutoBeanService.getInstance().getFactory().controllerCredentials();
								credentialsBean.as().setUrl(discoveredUrl);
								credsList.add(credentialsBean.as());
							}
						}
						credsListObj.setControllerCredentials(credsList);
						dataService.setControllerCredentialsList(credsListObj);
						resetTabItemImage();
					}
					@Override
					public void onFailure(Throwable exception) {
						resetTabItemImage();
						super.onFailure(exception);
					}
					
					private void resetTabItemImage() {
						String imageSrc = BrowserUtils.getSystemImageDir() + "/" + "controller_search.png";
						consoleDisplay.getTabBar().getItems().get(0).setImageSrc(imageSrc);
					}
				};
				discoveryService.getAutoDiscoveryServers(callback);
				break;
			case -2: // Load Controller
				String loadUrl = event.getCommand();
				ControllerCredentialsList credsList =  dataService.getControllerCredentialsList();
				for (ControllerCredentials creds : credsList.getControllerCredentials()) {
					if (creds.getUrl().equalsIgnoreCase(loadUrl)) {
						loadController(creds);
						break;
					}
				}
				break;
			case -3: // Clear Cache
				dataService.clearAllData();
				loadSettings(EnumSystemScreen.CONTROLLER_LIST, null);
				break;
			default:
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
	}
	
	public void onError(EnumControllerResponseCode errorCode) {
		switch (errorCode) {
			case XML_CHANGED:
				// Try and reload the panel
				reloadControllerAndPanel();
				break;
			case COMPONENT_INVALID:
				// TODO: display message in alert popup
				if (!invalidWarningDisplayed) {
					Window.alert("Polling Failed\n\nAt least one command ID has not been recognised.");
					invalidWarningDisplayed = true;
				}
				break;
			default:
				loadSettings(EnumSystemScreen.CONTROLLER_LIST, null);
		}
	}
	
	public void onError(EnumConsoleErrorCode errorCode) {
		onError(errorCode, null);
	}
	
	public void onError(EnumConsoleErrorCode errorCode, String additionalInfo) {
		//TODO: Console unit error handling
		String errorStr = errorCode.getDescription();
		
		if (additionalInfo != null && !additionalInfo.equals("")) {
			errorStr += "\n\nAdditional Info: " + additionalInfo;
		}
		switch(errorCode) {
		default:
			Window.alert("Console Error: " + errorStr);
			currentScreenId = 0;
		}
	}
}
