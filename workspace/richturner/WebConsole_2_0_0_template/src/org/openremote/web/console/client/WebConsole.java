package org.openremote.web.console.client;

import org.openremote.web.console.client.unit.ConsoleUnit;
import org.openremote.web.console.client.unit.type.FullScreenUnit;
import org.openremote.web.console.client.unit.type.ResizableUnit;
import org.openremote.web.console.event.WindowResizeHandlerImpl;
import org.openremote.web.console.util.BrowserUtils;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 * This class configures the Window for mobile and desktop environments
 * and determines the size of the window we have to work with:
 * windowHeight = Longest window dimension (i.e. portrait)
 * windowWidth = Shortest window dimension
 */
public class WebConsole implements EntryPoint {
	public static final String CONSOLE_UNIT_CONTAINER_ID = "consoleContainer";
	VerticalPanel mainPanel;
	private static ConsoleUnit consoleUnit;
	private WindowResizeHandlerImpl resizeHandler;
	int windowHeightPortrait = 2000;
	int windowWidthPortrait = 2000;
	int windowHeightLandscape = 2000;
	int windowWidthLandscape = 2000;
	public boolean isInitialised = false;
	String windowOrientation = "portrait";
	int topMargin;
	int bottomMargin;
	HandlerRegistration scrollHandler;
	Timer addressBarMonitor;
	boolean isPortraitInitialised = false;
	boolean isLandscapeInitialised = false;
	
	public void onModuleLoad() {
		// Fix body size as square to aid with orientation changes
		RootPanel.getBodyElement().getStyle().setHeight(2000, Unit.PX);
		RootPanel.getBodyElement().getStyle().setWidth(2000, Unit.PX);
		
		// Create Window Resize handler
		resizeHandler = new WindowResizeHandlerImpl(this);
		
		// Create a timer to wait for window to be initialised
		Timer initialisationTimer = new Timer() {
			@Override
			public void run() {
				// if window initialised then initialise console
				if (isInitialised) {					
					// Cancel this timer
					this.cancel();
					
					// Update window info
					updateWindowInfo();				
					
					// Initialise the Console Unit
					createConsoleUnit();
				}
			}
		};
		initialisationTimer.scheduleRepeating(100);
		
		// Get window sizes for mobile and desktop, also set window orientation for mobile
		if (BrowserUtils.isMobile) {
			// Initialise window and set initialised flag when done
			initMobileWindow();
		} else {
			// Prevent scrollbars from being shown on desktops
			Window.enableScrolling(false);
			
			// No initialising to do so set the flag now
			isInitialised = true;
		}
	}
	
	public void initMobileWindow() {
		if (isMobileWindowFullyInitialised()) {
			return;
		}
		
		// Determine window orientation
		updateWindowOrientation();
		
		Timer addressBarMonitor = new Timer() {
			public void run() {
				// Attempt scroll again just in case missed first time
				Window.scrollTo(0, 1);
				
				// Get Window information
				updateWindowSizeInfo();
				
				// Set initialised flag
				isInitialised = true;
				
				// Call resize event if window now fully initialised
				if (isMobileWindowFullyInitialised()) {
					resizeHandler.processResize();
				}
		  }
		};

	   // Scroll Window to hide address bar
	   Window.scrollTo(0, 1);

		// Wait 1s for first run as some browsers take a while to do the scroll
	   addressBarMonitor.schedule(1000);
	}
	
	/**
	 * Create the console unit and add it to the page
	 * Vertically align the console unit in the middle
	 * Horizontally align the console in the centre
	 */
	private void createConsoleUnit() {
		ConsoleUnit console;
		
		if(BrowserUtils.isMobile || windowWidthPortrait < ResizableUnit.requiredConsoleWidth() || windowHeightPortrait < ResizableUnit.requiredConsoleHeight()) {
			console = new FullScreenUnit(getWindowWidth("portrait"), getWindowHeight("portrait"));
		} else {
			console = new ResizableUnit();
		}
		consoleUnit = console;
		
		// Set initial console orientation and position
		consoleUnit.setOrientation(windowOrientation);
		consoleUnit.setPosition(getWindowWidth(), getWindowHeight());
	}
	
	/*
	 *  Look at window size to determine console unit type for mobile devices always use fullscreen
	 */ 
	
	public void setContainerWidgetSize(int width, int height) {
		RootPanel.get("consoleContainer").setWidth(width + "px");
		RootPanel.get("consoleContainer").setHeight(height + "px");
	}
	
	public int getWindowHeight() {
		return getWindowHeight(windowOrientation);
	}
	
	public int getWindowHeight(String orientation) {
		if("portrait".equals(orientation)) {
			return windowHeightPortrait;
		} else {
			return windowHeightLandscape;
		}
	}
	
	public void setWindowHeight(int windowHeight) {
		if("portrait".equals(windowOrientation)) {
			this.windowHeightPortrait = windowHeight;
		} else {
			this.windowHeightLandscape = windowHeight;
		}
	}
	
	public int getWindowWidth() {
		return getWindowWidth(windowOrientation);
	}
	
	public int getWindowWidth(String orientation) {
		if("portrait".equals(orientation)) {
			return windowWidthPortrait;
		} else {
			return windowWidthLandscape;
		}
	}

	public void setWindowWidth(int windowWidth) {
		if("portrait".equals(windowOrientation)) {
			this.windowWidthPortrait = windowWidth;
		} else {
			this.windowWidthLandscape = windowWidth;
		}
	}
	
	public String getWindowOrientation() {
		return windowOrientation;
	}
	
	public void updateWindowInfo() {
		// Determine window orientation on mobile devices
		if (BrowserUtils.isMobile) {
			updateWindowOrientation();
		} else {
			// Update window size
			updateWindowSizeInfo();
		}
		
		// Resize the console container size to match window				
		if("portrait".equals(windowOrientation)) {
			setContainerWidgetSize(windowWidthPortrait, windowHeightPortrait);
		} else {
			setContainerWidgetSize(windowWidthLandscape, windowHeightLandscape);
		}
		
		Window.scrollTo(0, 1);
	}

	public void updateWindowSizeInfo() {
		int winWidth = (Window.getClientWidth() > BrowserUtils.getNativeWidth() || !BrowserUtils.isMobile) ? Window.getClientWidth() : BrowserUtils.getNativeWidth();
   	int winHeight = (Window.getClientHeight() > BrowserUtils.getNativeHeight() || !BrowserUtils.isMobile) ? Window.getClientHeight() : BrowserUtils.getNativeHeight();
		if ("portrait".equals(windowOrientation)) {
			if (!isPortraitInitialised  || !BrowserUtils.isMobile) {
				windowWidthPortrait = winWidth;
				windowHeightPortrait = winHeight;
				if (!isLandscapeInitialised) {
					windowWidthLandscape = winHeight;
					windowHeightLandscape = winWidth;
				}
			}
			isPortraitInitialised = true;
		} else {
			if (!isLandscapeInitialised  || !BrowserUtils.isMobile) {
				windowWidthLandscape = winWidth;
				windowHeightLandscape = winHeight;
				if (!isPortraitInitialised) {
					windowWidthPortrait = winHeight;
					windowHeightPortrait = winWidth;
				}
			}
			isLandscapeInitialised = true;
		}
	}
	
	public void updateWindowOrientation() {
		if (Window.getClientHeight() >= Window.getClientWidth()) {
			windowOrientation = "portrait";
		} else {
			windowOrientation = "landscape";
		}
	}
	
	public static ConsoleUnit getConsoleUnit() {
		return consoleUnit;
	}
	
	public boolean isMobileWindowFullyInitialised() {
		return (isPortraitInitialised && isLandscapeInitialised);
	}
}
