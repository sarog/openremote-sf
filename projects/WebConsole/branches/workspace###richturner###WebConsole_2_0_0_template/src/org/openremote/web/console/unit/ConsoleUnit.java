/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2014, OpenRemote Inc.
*
* See the contributors.txt file in the distribution for a
* full listing of individual contributors.
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as
* published by the Free Software Foundation, either version 3 of the
* License, or (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU Affero General Public License for more details.
*
* You should have received a copy of the GNU Affero General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*/
package org.openremote.web.console.unit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.openremote.web.console.controller.Controller;
import org.openremote.web.console.controller.ControllerCredentials;
import org.openremote.web.console.controller.ControllerCredentialsList;
import org.openremote.web.console.controller.EnumControllerResponseCode;
import org.openremote.web.console.event.ConsoleUnitEventManager;
import org.openremote.web.console.event.hold.HoldEvent;
import org.openremote.web.console.event.hold.HoldHandler;
import org.openremote.web.console.event.rotate.RotationEvent;
import org.openremote.web.console.event.rotate.RotationHandler;
import org.openremote.web.console.event.sensor.SensorChangeEvent;
import org.openremote.web.console.event.swipe.SwipeEvent;
import org.openremote.web.console.event.swipe.SwipeEvent.SwipeDirection;
import org.openremote.web.console.event.swipe.SwipeHandler;
import org.openremote.web.console.event.ui.CommandSendEvent;
import org.openremote.web.console.event.ui.CommandSendHandler;
import org.openremote.web.console.event.ui.NavigateEvent;
import org.openremote.web.console.event.ui.NavigateHandler;
import org.openremote.web.console.event.ui.ScreenViewChangeEvent;
import org.openremote.web.console.event.ui.WindowResizeEvent;
import org.openremote.web.console.event.ui.WindowResizeHandler;
import org.openremote.web.console.panel.Panel;
import org.openremote.web.console.panel.PanelIdentityList;
import org.openremote.web.console.panel.entity.DataValuePair;
import org.openremote.web.console.panel.entity.DataValuePairContainer;
import org.openremote.web.console.panel.entity.Gesture;
import org.openremote.web.console.panel.entity.Navigate;
import org.openremote.web.console.panel.entity.Screen;
import org.openremote.web.console.panel.entity.TabBar;
import org.openremote.web.console.service.AsyncControllerCallback;
import org.openremote.web.console.service.AutoBeanService;
import org.openremote.web.console.service.AutoDiscoveryRPCService;
import org.openremote.web.console.service.AutoDiscoveryRPCServiceAsync;
import org.openremote.web.console.service.ControllerService;
import org.openremote.web.console.service.JSONPControllerConnector;
import org.openremote.web.console.service.JSONControllerConnector;
import org.openremote.web.console.service.LocalDataService;
import org.openremote.web.console.service.LocalDataServiceImpl;
import org.openremote.web.console.service.PanelService;
import org.openremote.web.console.service.PanelServiceImpl;
import org.openremote.web.console.service.ScreenViewService;
import org.openremote.web.console.util.BrowserUtils;
import org.openremote.web.console.util.ImageContainer;
import org.openremote.web.console.util.PollingHelper;
import org.openremote.web.console.view.ScreenViewImpl;
import org.openremote.web.console.widget.ScreenIndicator;
import org.openremote.web.console.widget.TabBarComponent;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.web.bindery.autobean.shared.AutoBean;
/**
 * 
 *  
 * @author <a href="mailto:richard@openremote.org">Richard Turner</a>
 */
public class ConsoleUnit extends VerticalPanel implements RotationHandler, WindowResizeHandler, SwipeHandler, HoldHandler, NavigateHandler, CommandSendHandler {
	public static final int MIN_WIDTH = 320;
	public static final int MIN_HEIGHT = 460;
	public static final int DEFAULT_DISPLAY_WIDTH = 320;
	public static final int DEFAULT_DISPLAY_HEIGHT = 460;
	public static final String CONSOLE_HTML_ELEMENT_ID = "consoleUnit";
	public static int FRAME_WIDTH_TOP = 20;
	public static int FRAME_WIDTH_BOTTOM = 50;
	public static int FRAME_WIDTH_LEFT = 20;
	public static int FRAME_WIDTH_RIGHT = 20;
	public static int BOSS_WIDTH = 2;
	public static final String LOGO_TEXT_LEFT = "Open";
	public static final String LOGO_TEXT_RIGHT = "Remote";
	public static final int IMAGE_CACHE_LOAD_TIMEOUT_SECONDS = 10;
	protected ConsoleDisplay consoleDisplay;
	private Boolean isFullscreen = true;
	protected int width;
	protected int height;
	private String orientation = "portrait";
	private ControllerService controllerService = ControllerService.getInstance();
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
	private boolean invalidWarningDisplayed = false;
	private Map<String, ImageContainer> imageCache = new HashMap<String, ImageContainer>();
	
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
		LOGIN(52, 4, "login"),
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
		this(false);
	}
	public ConsoleUnit(boolean fullscreen) {
		this(DEFAULT_DISPLAY_WIDTH, DEFAULT_DISPLAY_HEIGHT, fullscreen);
	}
	
	public ConsoleUnit(int width, int height) {
		this(width, height, false);
	}
	
	public ConsoleUnit(int width, int height, boolean fullscreen) {
//		if (fullscreen) {
//			width = BrowserUtils.getWindowWidth();
//			height = BrowserUtils.getWindowHeight();
//		}
////		if (width > height) {
////			int tempWidth = height;
////			height = width;
////			width = tempWidth;
////		}
//
//		this.width = width;
//		this.height = height;
		
		// Create a display and add to console container
		consoleDisplay = new ConsoleDisplay();
//		DOM.setStyleAttribute(consoleDisplay.getElement(), "borderStyle", "solid ");
//		DOM.setStyleAttribute(consoleDisplay.getElement(), "borderWidth", BOSS_WIDTH + "px");
		add(consoleDisplay);
		
		// Create Logo
		SimplePanel logoPanel = new SimplePanel();
		logoPanel.getElement().setId("consoleFrameLogo");
		logoPanel.setHeight(FRAME_WIDTH_BOTTOM - BOSS_WIDTH + "px");
		logoPanel.getElement().setAttribute("onselect", "return false;");
		add(logoPanel);
		
		// Set static console unit styling
		setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		getElement().setId(CONSOLE_HTML_ELEMENT_ID);
		addStyleName("portrait");
		getElement().setAttribute("unselectable", "on");
		BrowserUtils.setStyleAttributeAllBrowsers(getElement(), "userSelect", "none");
		consoleDisplay.getElement().setAttribute("unselectable", "on");
		
		// Set Size
		setSize(width, height, fullscreen);
		
		// Register gesture and controller message handlers
		registerHandlers();
	}
	
	public void setSize(boolean fullscreen) {
		setSize(0,0,true);
	}
	
	public void setSize(int width, int height) {
		setSize(width,height, isFullscreen);
	}
	
	public void setSize(int width, int height, boolean setFullscreen) {
		int winWidth = BrowserUtils.getWindowWidth();
		int winHeight = BrowserUtils.getWindowHeight();
		int maxDim = 0;
		String winOrientation = BrowserUtils.getWindowOrientation();
		
		if (BrowserUtils.isMobile) {
			setFullscreen = true;
		}
		if (!setFullscreen) {
			if (width > height) {
				int tempWidth = height;
				height = width;
				width = tempWidth;
			}

//			if (maxDim >= winWidth || maxDim >= winHeight) {
//				 setFullscreen = true;
//			}
		}
		
		if (setFullscreen) {
			if (winOrientation.equals("portrait")) {
				width = winWidth;
				height = winHeight;
			} else {
				width = winHeight;
				height = winWidth;
			}
		}
		
		
		maxDim = width > height ? width : height; 
	
		width = width < MIN_WIDTH ? MIN_WIDTH : width;
		height = height < MIN_HEIGHT ? MIN_HEIGHT : height;	
		
		
		toggleFrame(!setFullscreen);
		
		this.width = width;
		this.height = height;
		isFullscreen = setFullscreen;

		if (setFullscreen || maxDim >= winWidth || maxDim >= winHeight) {
			
			setOrientation(winOrientation);
			
			if (!winOrientation.equals(getOrientation())) {
				// Load in the inverse screen to what is currently loaded if screen orientation doesn't match console orientation
				if (panelService.isInitialized()) {
					if (!orientation.equalsIgnoreCase(consoleDisplay.getOrientation()) || (!BrowserUtils.isMobile && isFullscreen)) {
						Screen inverseScreen = panelService.getInverseScreen(currentScreenId);
						if (inverseScreen != null) {
							loadDisplay(inverseScreen, true, null);
						}
					}
				}
			}
		}
		
		consoleDisplay.setSize(width, height);
	}
		
	private void toggleFrame(boolean showFrame) {
		if (showFrame) {
			addStyleName("framed");
			removeStyleName("fullscreen");
			RootPanel.getBodyElement().removeClassName("fullscreen");
			DOM.setStyleAttribute(consoleDisplay.getElement(), "margin", (FRAME_WIDTH_TOP - BOSS_WIDTH) + "px " + (FRAME_WIDTH_RIGHT - BOSS_WIDTH) + "px 0 " + (FRAME_WIDTH_LEFT - BOSS_WIDTH) + "px");
			DOM.setStyleAttribute(getElement(), "position", "absolute");
		} else {
			RootPanel.getBodyElement().addClassName("fullscreen");
			addStyleName("fullscreen");
			removeStyleName("framed");
			DOM.setStyleAttribute(consoleDisplay.getElement(), "margin", "0");
			DOM.setStyleAttribute(getElement(), "position", "static");
		}
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
	public void setPosition() {
		int winWidth = BrowserUtils.getWindowWidth();
		int winHeight = BrowserUtils.getWindowHeight();
//		int xPos = 0;
//		int yPos = 0;
//		if (BrowserUtils.isIE && orientation.equalsIgnoreCase("landscape")) {
//			xPos = (int)Math.round(((double)winWidth/2)-(getHeight()/2));
//			yPos = (int)Math.round(((double)winHeight/2)-(getWidth()/2));
//		} else {
		if (!isFullscreen) {
			int xPos = (int)Math.round(((double)winWidth/2)-(getWidth()/2));
			int yPos = (int)Math.round(((double)winHeight/2)-(getHeight()/2));
			DOM.setStyleAttribute(this.getElement(), "position", "absolute");
			DOM.setStyleAttribute(this.getElement(), "top", yPos + "px");
			DOM.setStyleAttribute(this.getElement(), "left", xPos + "px");
		} else {
			DOM.setStyleAttribute(this.getElement(), "position", "static");	
		}
//		}
//		BrowserUtils.getConsoleContainer().setWidgetPosition(this, xPos, yPos);
	}

	/**
	 * Adjusts the CSS class to either landscape or portrait
	 * @param orientation
	 */
	public void setOrientation(String orientation) {
		int halfOuterWidth = (width + FRAME_WIDTH_LEFT + FRAME_WIDTH_RIGHT) / 2;
		int halfOuterHeight = (height + FRAME_WIDTH_TOP + FRAME_WIDTH_BOTTOM) / 2;
	
		if ("portrait".equals(orientation)) {
			BrowserUtils.setStyleAttributeAllBrowsers(this.getElement(), "transform", "rotate(0deg) translate(0,0)");
			this.addStyleName("portrait");
			this.removeStyleName("landscape");
		} else if ("landscape".equals(orientation)) {
			if (!isFullscreen) {
				BrowserUtils.setStyleAttributeAllBrowsers(this.getElement(), "transform", "rotate(-90deg) translate( -" + (halfOuterHeight + halfOuterWidth) + "px,-" + (halfOuterHeight - halfOuterWidth) + "px)");
			} else {
				BrowserUtils.setStyleAttributeAllBrowsers(this.getElement(), "transform", "rotate(-90deg) translate( -" + width + "px,0)");
			}
			this.addStyleName("landscape");
			this.removeStyleName("portrait");
		}
		
		if (BrowserUtils.isMobile) {
			Window.scrollTo(0, 1);
		}
		
		this.orientation = orientation;
//		setPosition(BrowserUtils.getWindowWidth(), BrowserUtils.getWindowHeight());
	}

	public boolean getIsFullscreen() {
		return isFullscreen;
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
	
	private void loadController(ControllerCredentials controllerCreds) {
		// Unload current panel
		unloadPanel();
		
		BrowserUtils.showLoadingMsg("Loading Controller");
		
		if (controllerCreds == null) {
			loadSettings(EnumSystemScreen.CONTROLLER_LIST, null);
		} else {
			currentControllerCredentials = controllerCreds;
			
			final Controller controller = new Controller(controllerCreds);

			controller.initialise(new AsyncControllerCallback<Boolean>(){
				@Override
				public void onSuccess(Boolean result) {
					// Controller is fully initialised let's see what state it's in
					if (!controller.isValid()) {
						// Alert the user
						BrowserUtils.showAlert("Controller Error!<br /><br />Controller '" + controller.getUrl() + "' can't be reached. Make sure controller is running and if it is secured check your browser supports HTML5 CORS.");
						loadSettings(EnumSystemScreen.CONTROLLER_LIST, null);
						return;
					}

						// Set the appropriate controller connector
						if (!controller.isSameOrigin()) {
							controllerService.setConnector(new JSONPControllerConnector());
						} else {
							controllerService.setConnector(new JSONControllerConnector());
						}
						
						// Load the controller
						controllerService.setController(controller);
						currentPanelName = currentControllerCredentials.getDefaultPanel();
						
						// If current panel name set try and load it otherwise prompt for panel to load
						if (currentPanelName != null && !currentPanelName.equalsIgnoreCase("")) {
							dataService.setLastControllerCredentials(currentControllerCredentials);
							loadPanel(currentPanelName);
						} else {
							loadPanelSelection();
						}
				}
			});
		}
	}
	
	private void loadPanelSelection() {
		// Retrieve panel identity list from controller and display panel selection screen
		controllerService.getPanelIdentities(new AsyncControllerCallback<PanelIdentityList>() {
			@Override
			public void onSuccess(PanelIdentityList panelIdentities) {
				if (panelIdentities != null) {
					String dataValue = AutoBeanService.getInstance().toJsonString(panelIdentities);
					DataValuePairContainer dvpC = AutoBeanService.getInstance().getFactory().create(DataValuePairContainer.class).as();
					DataValuePair dvp = AutoBeanService.getInstance().getFactory().create(DataValuePair.class).as();
					dvp.setName("panelIdentityList");
					dvp.setValue(dataValue);
					dvpC.setDataValuePair(dvp);
					List<DataValuePairContainer> data = new ArrayList<DataValuePairContainer>();
					data.add(dvpC);
					
					loadSettings(EnumSystemScreen.PANEL_SELECTION, data);
				} else {
					onError(EnumConsoleErrorCode.PANEL_LIST_ERROR);
				}									
			}
			
			@Override
			public void onFailure(EnumControllerResponseCode code) {
				if (code == EnumControllerResponseCode.NOT_AUTHORIZED) {
					// Take them to the login screen
					loadSettings(EnumSystemScreen.LOGIN, null);
				} else {
					onError(code);
				}
			}
			
			@Override
			public void onFailure(Throwable error) {
				onError(EnumConsoleErrorCode.PANEL_LIST_ERROR);
			}								
		});
	}
	
	private void loadPanel(final String panelName) {
		unloadPanel();
		BrowserUtils.showLoadingMsg("Loading Panel");
		controllerService.getPanel(panelName, new AsyncControllerCallback<Panel>() {
			@Override
			public void onFailure(EnumControllerResponseCode response) {
				BrowserUtils.hideLoadingMsg();
				if (response == EnumControllerResponseCode.NOT_AUTHORIZED || response == EnumControllerResponseCode.FORBIDDEN) {
					// Take them to the login screen
					loadSettings(EnumSystemScreen.LOGIN, null);
				} else {
					onError(response);
				}
			}
			
			@Override
			public void onSuccess(Panel result) {
				BrowserUtils.hideLoadingMsg();
				if (result != null) {
					try {
						// Update current controller credentials
						currentControllerCredentials.setDefaultPanel(panelName);
						dataService.setLastControllerCredentials(currentControllerCredentials);
						
						if (!setPanel(result)) {
								if (result != systemPanel) {
									// Display error and go to system screen
									onError(EnumConsoleErrorCode.PANEL_DEFINITION_ERROR);
									return;
								} else {
										// Big problem if we can't load system screens so let's exit
										Window.alert("Failed to load a system screen!\n\nThis is a big problem and we can't continue.");
										return;
								}
						}
					} catch (Exception e) {
						onError(EnumConsoleErrorCode.PANEL_DEFINITION_ERROR, e.getMessage());
					}
				} else {
					onError(EnumConsoleErrorCode.UNKNOWN_ERROR, "Load Panel Response Invalid");
				}
			}			
		});
	}
	
	private boolean setPanel(Panel result) {
		boolean success = false;
		
		if (result != null) {
			// Unload current panel
			unloadPanel();
			
			// Set new panel and prefetch image resources
			panelService.setCurrentPanel(result);
			
			BrowserUtils.showLoadingMsg("Retrieving Resources..");
			
			try {
//				List<String> imageUrls = panelService.getImageResourceUrls();
				
//				for(String imageUrl : imageUrls ) {
//					addImageToCache(imageUrl);
//					Thread.sleep(100);
//				}
				
				Timer imageCacher = new Timer() {
					int i=0;
					String[] imageUrls = panelService.getPanelImageUrls();
					
					@Override
					public void run() {
						if (i < imageUrls.length) {
							addImageToCache(imageUrls[i]);
							i++;
							this.schedule(100);
						}	else {
							// Prefetch All Images Resources before proceeding
							loadCache(new AsyncControllerCallback() {

								@Override
								public void onSuccess(Object result) {
									initialisePanel();
								}
								
								@Override
								public void onFailure(Throwable e) {
									initialisePanel();
								}
							});
						}
					}};
				
				imageCacher.run();	
			} catch (Exception e) {
				return false;
			}
		}
		return true;
	}
	
	private void loadCache(final AsyncControllerCallback callback) {
		Timer imageLoader = new Timer() {
			private int secondCount = 0;
			private boolean allImagesLoaded = false;
			public void run() {
				boolean allImagesLoaded = true;
				for (Iterator<String> it = imageCache.keySet().iterator(); it.hasNext();) {
					String url = it.next();
					ImageContainer image = imageCache.get(url);
					if (!image.getLoadAttempted()) {
						allImagesLoaded = false;
						break;
					}
				}
				
				if (secondCount > IMAGE_CACHE_LOAD_TIMEOUT_SECONDS) {
					callback.onFailure(new Exception("Timeout during image loading"));
				} else if (allImagesLoaded) {
					callback.onSuccess(null);
				} else {
					secondCount++;
					this.schedule(1000);
				}
			}
		};
		imageLoader.run();
	}
	
	private void initialisePanel() {
		if (panelService.isInitialized()) {
			// Reset warning flag
			invalidWarningDisplayed = false;
			
			// Get default group ID
			Integer defaultGroupId = panelService.getDefaultGroupId();
			Screen defaultScreen = panelService.getDefaultScreen(defaultGroupId);
			
			if (defaultScreen != null) {
				String screenOrientation = (defaultScreen.getLandscape() != null && defaultScreen.getLandscape()) ? "landscape" : "portrait";
				
				// Load in the inverse screen to what is currently loaded if screen orientation doesn't match console orientation
				if (panelService.isInitialized()) {
					if (!screenOrientation.equalsIgnoreCase(consoleDisplay.getOrientation())) {
						Screen inverseScreen = panelService.getInverseScreen(defaultScreen.getId());
						if (inverseScreen != null) {
							defaultScreen = inverseScreen;
						}
					}
				}
				
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
		// Clear last controller credentials
		dataService.setLastControllerCredentials(null);
		
		// Logout of controller
		controllerService.logout(new AsyncControllerCallback<Boolean>() {
			@Override
			public void onSuccess(Boolean result) {}
			@Override
			public void onFailure(Throwable error) {}
			
			@Override
			public void onFailure(EnumControllerResponseCode response) {}
		});
		
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
		reloadTimer.schedule(5000);
	}
	
	public void restart() {
		unloadControllerAndPanel();
		onAdd();
	}
	
	private void loadSettings(EnumSystemScreen systemScreen, List<DataValuePairContainer> data) {
		if (systemScreen == EnumSystemScreen.CONTROLLER_LIST || systemScreen == EnumSystemScreen.LOGOUT) {
			unloadController();
		}
		
		if (panelService.getCurrentPanel() != systemPanel) {
			unloadPanel();

			panelService.setCurrentPanel(systemPanel);
		}
		
		int groupId = systemScreen.getGroupId();
		int screenId = systemScreen.getId();
		
		if (this.getOrientation().equalsIgnoreCase("landscape") && screenId < 1000) {
			screenId += 1000;
		}
		
		Screen screen = panelService.getScreenById(screenId);
		
		if (screen == null || !loadDisplay(groupId, screen, data)) {
			// Big problem if we can't load system screens so let's exit
			Window.alert("Failed to load a system screen!\n\nThis is a big problem and we can't continue.");
			return;
		}
	}
	
	private boolean loadDisplay(Screen screen, List<DataValuePairContainer> data) {
		return loadDisplay(currentGroupId, screen, data);
	}
	
	private boolean loadDisplay(Screen screen, boolean orientationChanged, List<DataValuePairContainer> data) {
		return loadDisplay(currentGroupId, screen, data);
	}
	
	private boolean loadDisplay(Integer newGroupId, Screen screen, List<DataValuePairContainer> data) {
		boolean screenChanged = false;
		boolean groupChanged = false;
		boolean screenOrientationChanged = false;
		ScreenViewImpl currentScreenView = consoleDisplay.getScreen();
		Integer oldScreenId = currentScreenId;
		
		if (screen == null || newGroupId == null) {
			onError(EnumConsoleErrorCode.PANEL_DEFINITION_ERROR);
			return false;
		}
		
		int newScreenId = screen.getId();
		
		if (currentScreenId != newScreenId) {
			screenChanged = true;
			screenOrientationChanged = (currentScreenView == null || ((currentScreenView.isLandscape() != null && currentScreenView.isLandscape()) != (screen.getLandscape() != null && screen.getLandscape()))) ? true : false;
		}
		if (currentGroupId != newGroupId) {
			groupChanged = true;
		}
		
		if (!screenChanged && !groupChanged) {
			return true;
		}
		
		if (screenChanged) {
			// Try setting new screen fall back to previous screen on failure only if this isn't the systemPanel
			try {
				ScreenViewImpl screenView = screenViewService.getScreenView(screen);
				if (screenView == null) {
					throw new Exception("Failed to retrieve screen view for screen ID: " + screen.getId());
				}
				consoleDisplay.setScreenView(screenView, data);
				currentScreenView = screenView;
			} catch (Exception e) {
				if (panelService.getCurrentPanel() == systemPanel) {
					// Just return
					return false;
				}
				try {
					// Put screen back to previous one
					consoleDisplay.setScreenView(currentScreenView, data);
					onError(EnumConsoleErrorCode.SCREEN_ERROR, "Screen ID = " + newScreenId);
				} catch (Exception ex) {
					onError(EnumConsoleErrorCode.UNKNOWN_ERROR, ex.getMessage());
				}
				return false;
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
					consoleDisplay.setTabBar(tabBarComponent);
					tabBarComponent.onScreenViewChange(new ScreenViewChangeEvent(newScreenId, newGroupId)); // Problem with event bus means tab bar doesn't receive event notification when first added
				} catch (Exception e) {
					onError(EnumConsoleErrorCode.TABBAR_ERROR);
				}
			}
			currentGroupId = newGroupId;
		}
		if (groupChanged || (screenChanged && screenOrientationChanged)) {
			// Get Screen ID List and create screenIndicator
			List<Integer> screenIds = panelService.getGroupScreenIdsWithSameOrientation(newScreenId, newGroupId);
			if (screenIds != null && screenIds.size() > 1) {
				ScreenIndicator screenIndicator = new ScreenIndicator(screenIds);
				consoleDisplay.setScreenIndicator(screenIndicator);
				screenIndicator.onScreenViewChange(new ScreenViewChangeEvent(newScreenId, newGroupId)); // Problem with event bus means indicator doesn't receive event notification when first added
			} else {
				consoleDisplay.removeScreenIndicator();
			}
			consoleDisplay.updateScreenIndicator();
			consoleDisplay.updateTabBar();
		}
		
		if (screenChanged) {
			ConsoleUnitEventManager.getInstance().getEventBus().fireEvent(new ScreenViewChangeEvent(newScreenId, newGroupId));		
		}
		return true;
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
	
	public void addImageToCache(String url) {
		if (imageCache.containsKey(url)) return;
		
		String imageUrl = url;
		Controller controller = controllerService.getController();
		if (controller != null && controller.isProxied()) {
			ControllerCredentials creds = controller.getCredentials();
			imageUrl = BrowserUtils.getImageProxyURL(creds.getUsername(), creds.getPassword(), url);
		}
		
		imageCache.put(url, new ImageContainer(imageUrl));
	}
	
	public ImageContainer getImageFromCache(String url) {
		ImageContainer container = null;
		container = imageCache.get(url);
		if (container != null) {
			container = container.clone();
		} else {
			container = new ImageContainer("");
		}
		return container;
	}
	
	private void initialiseConsole() {
    // Check for Last Controller and Panel in Cache
		ControllerCredentials controllerCreds = dataService.getLastControllerCredentials();
		if (controllerCreds != null && controllerCreds.getUrl() != null) {
			loadController(controllerCreds);
		} else {
			// No controller to load so go to settings
			loadSettings(EnumSystemScreen.CONTROLLER_LIST, null);
		}
		
//		// TODO: Check for default Controller in Settings
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


//		}
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
		// Set Position
		setPosition();
		
		// Configure display
		consoleDisplay.onAdd(width, height);
		
		show();
		
		// Initialise the system panel
//		String systemPanelStr = dataService.getObjectString(EnumDataMap.SYSTEM_PANEL.getDataName());
//		if (systemPanelStr == null) {
			// Load from Server
			BrowserUtils.showLoadingMsg("Retreiving System Panel Definition");

			try {
				int displayDensity = BrowserUtils.getDisplayDensityValue();
				new RequestBuilder(RequestBuilder.GET, "resources/systempanel" + displayDensity + ".json").sendRequest("", new RequestCallback() {
					@Override
					public void onResponseReceived(Request request, Response response) {
						String systemPanelStr = response.getText();
						systemPanel = AutoBeanService.getInstance().fromJsonString(Panel.class, systemPanelStr).as();
						
						// Initialise the system panel
						panelService.setCurrentPanel(systemPanel);
						
						BrowserUtils.showLoadingMsg("Retrieving Resources..");
						
						try {
//							List<String> imageUrls = panelService.getImageResourceUrls();
							
//							for(String imageUrl : imageUrls ) {
//								addImageToCache(imageUrl);
//								Thread.sleep(100);
//							}
							
							Timer imageCacher = new Timer() {
								int i=0;
								String[] imageUrls = panelService.getPanelImageUrls();
								
								@Override
								public void run() {
									if (i < imageUrls.length) {
										addImageToCache(imageUrls[i]);
										i++;
										this.schedule(100);
									}					
								}};
							
							imageCacher.run();	
								
							// Prefetch All Images Resources before proceeding
							loadCache(new AsyncControllerCallback() {

								@Override
								public void onSuccess(Object result) {
									initialiseConsole();
								}
								
								@Override
								public void onFailure(Throwable e) {
									BrowserUtils.hideLoadingMsg();
									BrowserUtils.showAlert("FATAL Error: Failed to retrieve System Panel Definition.");
								}
							});							
						} catch (Exception e) {
							BrowserUtils.hideLoadingMsg();
							BrowserUtils.showAlert("FATAL Error: Failed to retrieve System Panel Definition.");
						}
					}

					@Override
					public void onError(Request request, Throwable exception) {
						BrowserUtils.hideLoadingMsg();
						BrowserUtils.showAlert("FATAL Error: Failed to retrieve System Panel Definition.");
					}
					});
			} catch (RequestException e) {
				BrowserUtils.hideLoadingMsg();
				BrowserUtils.showAlert("FATAL Error: Failed to retrieve System Panel Definition.");
			}
//		}
//		else {
//			systemPanel = AutoBeanService.getInstance().fromJsonString(Panel.class, systemPanelStr).as();
//			initialiseConsole();
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
//			setPosition(event.getWindowWidth(), event.getWindowHeight());
			setPosition();
		}
	}
	
	@Override
	public void onHold(HoldEvent event) {
		if (event.getSource() == consoleDisplay && !BrowserUtils.disableFailsafe()) {
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
			} else if (hasControlCommand != null && hasControlCommand) {
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
					screen = panelService.getScreenById(toScreenId, toGroupId);
				}
				loadDisplay(toGroupId, screen, data);
			}
		}
	}

	@Override
	public void onCommandSend(CommandSendEvent event) {
		if (event != null) {
			switch (event.getCommandId()) {
			
			case -1:
			case -1001: //Controller Discovery
				// Change tab bar search item image
				String imageSrc = BrowserUtils.getSystemImageDir() + "controller_searching.gif";
				consoleDisplay.getTabBar().getItems().get(0).setImageSrc(imageSrc);
				
				// Do RPC
				AutoDiscoveryRPCServiceAsync discoveryService = (AutoDiscoveryRPCServiceAsync) GWT.create(AutoDiscoveryRPCService.class);
				AsyncControllerCallback<List<String>> callback = new AsyncControllerCallback<List<String>>() {
					@Override
					public void onSuccess(List<String> discoveredUrls) {
						ControllerCredentialsList credsListObj = dataService.getControllerCredentialsList();
						List<ControllerCredentials> credsList = new ArrayList<ControllerCredentials>();
						
						if (credsListObj != null)
						{
							credsList = credsListObj.getControllerCredentials();
						}
						
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
						
						if (credsListObj != null)
						{
							credsListObj.setControllerCredentials(credsList);
							dataService.setControllerCredentialsList(credsListObj);
						}
						
						resetTabItemImage();
					}
					@Override
					public void onFailure(Throwable exception) {
						resetTabItemImage();
						super.onFailure(exception);
					}
					
					private void resetTabItemImage() {
						String imageSrc = BrowserUtils.getSystemImageDir() + "controller_search.png";
						consoleDisplay.getTabBar().getItems().get(0).setImageSrc(imageSrc);
					}
				};
				discoveryService.getAutoDiscoveryServers(callback);
				break;
			case -2:
			case -1002: // Load Controller
				String loadUrl = event.getCommand();
				ControllerCredentialsList credsList =  dataService.getControllerCredentialsList();
				for (ControllerCredentials creds : credsList.getControllerCredentials()) {
					if (creds.getUrl().equalsIgnoreCase(loadUrl)) {
						loadController(creds);
						break;
					}
				}
				break;
			case -3:
			case -1003: // Clear Cache
				dataService.clearAllData();
				loadSettings(EnumSystemScreen.CONTROLLER_LIST, null);
				break;
			case -4:
			case -1004: // Load Panel
				String panelName = event.getCommand();
				loadPanel(panelName);
				break;
			case -5:
			case -1005: // Login
				InputElement userElem = DOM.getElementById("loginuserid").cast();
				InputElement passElem = DOM.getElementById("loginpassword").cast();
				
				// If we get here user is submitting login form
				Controller controller = controllerService.getController();
				String username = userElem.getValue().trim();
				controller.setUsername(username);
				String password = passElem.getValue().trim();
				controller.setPassword(password);

				//TODO: Test login credentials and notify on failure
				
				loadPanelSelection();
				break;
			case -6:
			case -1006: // Logout
				loadSettings(EnumSystemScreen.CONTROLLER_LIST, null);
				break;
			default:
				controllerService.sendCommand(event.getCommandId() + "/" + event.getCommand(), new AsyncControllerCallback<Boolean>() {
					@Override
					public void onSuccess(Boolean result) {
						if (!result) {
							BrowserUtils.showAlert("Command Send Failed!");
						}
					}
					public void onFailure(Throwable exception) {
						// DO NOTHING HERE AS CONTROLLER MAY BE ALIVE BUT NOT AGREE WITH THE COMMAND REQUEST BUT DOESN'T EXPLICITLY TELL US THAT
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
					BrowserUtils.showAlert("Polling Failed!<br /><br />At least one command ID has not been recognised.");
					invalidWarningDisplayed = true;
				}
				break;
			case PANEL_NOT_FOUND:
					loadPanelSelection();
					break;
			case FORBIDDEN:
					BrowserUtils.showAlert("Security Error!<br /><br />Controller is secured and supplied credentials are invalid.");
			default:
				BrowserUtils.showAlert("Error!<br /><br />The following error occurred: -<br /><br />" + errorCode.getDescription());
				loadSettings(EnumSystemScreen.CONTROLLER_LIST, null);
		}
	}
	
	public void onError(EnumConsoleErrorCode errorCode) {
		onError(errorCode, null);
	}
	
	public void onError(EnumConsoleErrorCode errorCode, String additionalInfo) {
		String errorStr = errorCode.getDescription();
		
		if (additionalInfo != null && !additionalInfo.equals("")) {
			errorStr += "\n\nAdditional Info: " + additionalInfo;
		}
		switch(errorCode) {
		default:
			BrowserUtils.showAlert("Console Error:\n\n" + errorStr);
			currentScreenId = 0;
		}
		loadSettings(EnumSystemScreen.CONTROLLER_LIST, null);
	}
}
