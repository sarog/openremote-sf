package org.openremote.web.console.unit;

import java.util.List;

import org.openremote.web.console.controller.Controller;
import org.openremote.web.console.event.ConsoleUnitEventManager;
import org.openremote.web.console.event.hold.HoldEvent;
import org.openremote.web.console.event.hold.HoldHandler;
import org.openremote.web.console.event.rotate.RotationEvent;
import org.openremote.web.console.event.rotate.RotationHandler;
import org.openremote.web.console.event.swipe.SwipeEvent;
import org.openremote.web.console.event.swipe.SwipeEvent.SwipeDirection;
import org.openremote.web.console.event.swipe.SwipeHandler;
import org.openremote.web.console.event.tap.DoubleTapEvent;
import org.openremote.web.console.event.tap.DoubleTapHandler;
import org.openremote.web.console.event.tap.TapEvent;
import org.openremote.web.console.event.tap.TapHandler;
import org.openremote.web.console.event.ui.NavigateEvent;
import org.openremote.web.console.event.ui.NavigateHandler;
import org.openremote.web.console.event.ui.ScreenViewChangeEvent;
import org.openremote.web.console.panel.Panel;
import org.openremote.web.console.panel.PanelCredentials;
import org.openremote.web.console.panel.PanelCredentialsImpl;
import org.openremote.web.console.panel.PanelIdentity;
import org.openremote.web.console.panel.entity.Gesture;
import org.openremote.web.console.panel.entity.Navigate;
import org.openremote.web.console.panel.entity.Screen;
import org.openremote.web.console.panel.entity.TabBar;
import org.openremote.web.console.rpc.json.JSONPControllerService;
import org.openremote.web.console.service.AsyncControllerCallback;
import org.openremote.web.console.service.ControllerService;
import org.openremote.web.console.service.LocalDataService;
import org.openremote.web.console.service.LocalDataServiceImpl;
import org.openremote.web.console.service.PanelService;
import org.openremote.web.console.service.PanelServiceImpl;
import org.openremote.web.console.service.ScreenViewService;
import org.openremote.web.console.view.ScreenView;
import org.openremote.web.console.widget.TabBarComponent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ConsoleUnit extends SimplePanel implements RotationHandler, SwipeHandler, HoldHandler, TapHandler, DoubleTapHandler, NavigateHandler {
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
	private PanelCredentials currentPanelCredentials;
	private PanelIdentity currentPanelIdentity;
	private Integer currentGroupId;
	private Integer currentScreenId;
	
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
	
	public void initialise() {
		// Create and load loading screen
		ScreenView loadingScreen = screenViewService.getScreenView(ScreenViewService.LOADING_SCREEN_ID);
		setScreenView(loadingScreen);
		
		// Check for Last Panel in Cache
		PanelCredentialsImpl panelCred = new PanelCredentialsImpl("http://192.168.1.71:8080/controller", 30, "Mobile");
		dataService.setLastPanelCredentials(panelCred);
		PanelCredentials lastPanelCredentials = dataService.getLastPanelCredentials();
		
		
		// If Panel Credentials found look for Controller and Panel if found then load that panel
		// Otherwise go to the settings screen view
		if (lastPanelCredentials != null) {
			currentPanelCredentials = lastPanelCredentials;
			
			// Get Panel list and look for last panel name in list
			Controller controller = new Controller();
			controller.setUrl(lastPanelCredentials.getControllerUrl());
			controllerService.setController(controller);
			getPanelIdentities();
		}
	}
	
	public void getPanelIdentities() {
		controllerService.getPanelIdentities(new AsyncControllerCallback<List<PanelIdentity>>() {

			@Override
			public void onSuccess(List<PanelIdentity> result) {
				boolean panelFound = false;
				for (PanelIdentity identity : result) {
					if (currentPanelCredentials.getName().equalsIgnoreCase(identity.getName())) {
						getPanel(identity);
						panelFound = true;
						break;
					}
				}
				if (!panelFound) {
					loadSettings();
				}
			}
			
		});
	}
	
	public void getIsSecure() {
		controllerService.isSecure(new AsyncControllerCallback<Boolean>() {

			@Override
			public void onSuccess(Boolean result) {
				if (result) {
					Window.alert("I'm Secure!");
				} else {
					Window.alert("I'm not Secure");
				}
			}
			
		});
	}
	
	public void getPanel(PanelIdentity panelIdentity) {
		controllerService.getPanel(panelIdentity.getName(), new AsyncControllerCallback<Panel>() {

			@Override
			public void onSuccess(Panel result) {
				panelService.setCurrentPanel(result);
				loadPanel(result);
			}			
		});
	}
	
	public void loadPanel(Panel panel) {
		// Get default group ID
		Integer defaultGroupId = panelService.getDefaultGroupId();
		Screen screen = panelService.getDefaultScreen(defaultGroupId);
		
		if (screen != null) {
			// Load default Screen for default group
			loadScreen(defaultGroupId, screen);
		} else {
			loadSettings();
		}
	}
	
	public void loadScreen(Integer groupId, Screen screen) {
		boolean screenChanged = false;
		boolean groupChanged = false;
		
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
			ScreenView screenView = screenViewService.getScreenView(screen);
		
			if (screenView != null) {
				setScreenView(screenView);
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
				setTabBar(tabBarComponent);
			}
		}
		
		// Fire Screen View Change event
		if (screenChanged) {
			HandlerManager eventBus = ConsoleUnitEventManager.getInstance().getEventBus();
			eventBus.fireEvent(new ScreenViewChangeEvent(currentScreenId));
		}
	}
	
	public void loadSettings() {
		// TODO Load the settings screen
		Window.alert("LOAD SETTINGS");
	}
	
	public void setScreenView(ScreenView screen) {
		consoleDisplay.setScreenView(screen);
	}
	
	public void setTabBar(TabBarComponent tabBar) {
		consoleDisplay.setTabBar(tabBar);
	}
	
	public ControllerService getControllerService() {
		return this.controllerService;
	}
	
	/*
	 * **********************************************
	 * Event Handlers below here
	 * **********************************************
	 */
	public void registerHandlers() {
		HandlerManager eventBus = ConsoleUnitEventManager.getInstance().getEventBus();
		eventBus.addHandler(RotationEvent.getType(), this);
		eventBus.addHandler(SwipeEvent.getType(), this);
		eventBus.addHandler(HoldEvent.getType(), this);
		eventBus.addHandler(TapEvent.getType(), this);
		eventBus.addHandler(DoubleTapEvent.getType(), this);
		eventBus.addHandler(NavigateEvent.getType(), this);
	}
	
	@Override
	public void onRotate(RotationEvent event) {
		boolean rotateDisplay = false;
		
		setOrientation(event);
		setPosition(event.getWindowWidth(), event.getWindowHeight());
		
		// Load in the inverse screen to what is currently loaded
		if (panelService.isInitialized()) {
			Screen inverseScreen = panelService.getInverseScreen(currentScreenId);
			if (inverseScreen != null) {
				loadScreen(currentGroupId, inverseScreen);
				rotateDisplay = true;
			}
		}
		
		// Adjust console display
		consoleDisplay.setOrientation(event, rotateDisplay);
	}
	
	@Override
	public void onHold(HoldEvent event) {
		loadSettings();
	}

	@Override
	public void onSwipe(SwipeEvent event) {
		switch (event.getDirection()) {
			case LEFT:
				
				break;
			case RIGHT:
				break;
			case DOWN:
				break;
			case UP:
				break;
		}
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
	public void onNavigate(NavigateEvent event) {
		Navigate navigate = event.getNavigate();
		if (navigate != null) {
			String to = navigate.getTo();
			Integer toGroupId = navigate.getToGroup();
			Integer toScreenId = navigate.getToScreen();
			
			if (to != null && !to.equals("")) {
				// Force portrait display
				consoleDisplay.setDisplayOrientation("portrait");
				// TODO Load System Screen
				Window.alert("Load System Screen");
			} else if(toGroupId != null && toScreenId != null) {
				Screen screen = panelService.getScreenById(toScreenId);
				loadScreen(toGroupId, screen);
				Boolean isLandscape =screen.getLandscape();  
				if (isLandscape != null && isLandscape) {
					consoleDisplay.setDisplayOrientation("landscape");
				} else {
					consoleDisplay.setDisplayOrientation("portrait");
				}
			}
		}
	}
}
