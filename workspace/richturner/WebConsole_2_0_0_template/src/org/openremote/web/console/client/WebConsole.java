package org.openremote.web.console.client;


import org.openremote.web.console.components.ConsoleDisplay;
import org.openremote.web.console.types.FullScreenUnit;
import org.openremote.web.console.types.ResizableUnit;
import org.openremote.web.console.utils.BrowserUtils;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;


/**
 * Entry point classes define <code>onModuleLoad()</code>.
 * This class configures the Window for mobile and desktop environments
 * and determines the size of the window we have to work with:
 * windowHeight = Longest window dimension (i.e. portrait)
 * windowWidth = Shortest window dimension
 */
public class WebConsole implements EntryPoint {
	
	VerticalPanel mainPanel;
	ConsoleUnit consoleUnit;
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
	Timer resizeComplete;
	boolean resizeOccurred = false;
	
	public void onModuleLoad() {
		// Create a timer to wait for window to be initialised
		Timer initialisationTimer = new Timer() {
			@Override
			public void run() {
				// if window initialised then initialise console
				if (isInitialised) {
					// Initialise the DOM and Console
					initialiseDom();
					initialiseConsole();
					this.cancel();
				}
			}
		};
		initialisationTimer.scheduleRepeating(100);
		
		// Create a resize complete timer to avoid multiple resize events being generated
		resizeComplete = new Timer() {
			public void run() {
				// Do resize actions
				doResize();
				
				// Clear flag
				resizeOccurred = false;
		  }
		};
		
		// Get window sizes for mobile and desktop, also set window orientation for mobile
		if (BrowserUtils.isMobile) {
			// Fix body size as square to aid with orientation changes
			RootPanel.getBodyElement().getStyle().setHeight(2000, Unit.PX);
			RootPanel.getBodyElement().getStyle().setWidth(2000, Unit.PX);
			
			// Add native orientation handler for ipod as resize isn't reliable
			BrowserUtils.addNativeOrientationHandler();
			
			// Determine window orientation
			setWindowOrientation();
			
			// Initialise window and set initialised flag when done
			BrowserUtils.initWindow(this);
		} else {
			// Prevent scrollbars from being shown on desktops
			Window.enableScrolling(false);
			
			// Get Window information
			getWindowSize();
			
			// No initialising to do so set the flag now
			isInitialised = true;
		}
	}

	/**
	 * Configure DOM event listeners and set element sizes
	 */
	private void initialiseDom() {
		// Size body
		if("portrait".equals(windowOrientation)) {
			setContainerSize(windowWidthPortrait, windowHeightPortrait);
		} else {
			setContainerSize(windowWidthLandscape, windowHeightLandscape);
		}
		
		// Prevent touch moves for mobile devices(won't affect desktop)
		RootPanel.get().addDomHandler(new TouchMoveHandler() {
			public void onTouchMove(TouchMoveEvent e) {
					e.preventDefault();
			}
		}, TouchMoveEvent.getType());
		
		// Add resize handler
		Window.addResizeHandler(new ResizeHandler() {
			@Override
			public void onResize(ResizeEvent event) {
				resizeHandler();
			}
		});
	}
	
	public void resizeHandler() {
		if (!resizeOccurred) {
			if (BrowserUtils.isMobile) {
				String prevOrientation = windowOrientation;
				setWindowOrientation();
				
				if (prevOrientation.equals(windowOrientation)) {
					return;
				}
				
				if (!isPortraitInitialised || !isLandscapeInitialised) {
					   BrowserUtils.initWindow();
					   return;
				}
				
				// Hide the console unit
				consoleUnit.setVisible(false);
				
				// Attempt immediate scroll
				Window.scrollTo(0, 1);
			}
			
			resizeComplete.schedule(100);
		}
	}
	
	/**
	 * Create the console unit and add it to the page
	 * Vertically align the console unit in the middle
	 * Horizontally align the console in the centre
	 */
	private void initialiseConsole() {
		createConsoleUnit();
		
		// Add Console Unit to the screen and position vertically
		addConsole();
		
		// Set Console Orientation
		consoleUnit.setOrientationAndPosition(windowOrientation, getWindowWidth(), getWindowHeight());
	}

	/**
	 * Create new console unit with type based on window size and requested display size
	 * @return
	 */
//	public static ConsoleUnit create(int windowWidth, int windowHeight) {
//		return create(windowWidth, windowHeight, ConsoleUnit.DEFAULT_DISPLAY_WIDTH, ConsoleUnit.DEFAULT_DISPLAY_HEIGHT, null);
//	}
	
	public void createConsoleUnit() {
		ConsoleUnit console;
		
		// Look at window size to determine console unit type for mobiles always use fullscreen
		if(BrowserUtils.isMobile || windowWidthPortrait < ResizableUnit.requiredConsoleWidth() || windowHeightPortrait < ResizableUnit.requiredConsoleHeight()) {
			console = new FullScreenUnit();
		} else {
			console = new ResizableUnit();
		}
		consoleUnit = console;
	}
	
	/**
	 * For desktops we just update window dimensions
	 * For mobiles we switch width and height depending on orientation
	 *	Update window dimensions again if haven't done so for
	 * Both device orientations, window width isn't the same
	 * As window height when device is rotated on devices that
	 * have on screen elements that can't be hidden i.e. status
	 * bars and menu bars
	 */
	public void doResize() {
		if (!BrowserUtils.isMobile) {
			getWindowSize();
		}
		
		// Resize the body to match window				
		resizeContainerElement();
		
		Window.scrollTo(0, 1);

		if ("portrait".equals(windowOrientation)) {
			consoleUnit.setOrientationAndPosition(windowOrientation, windowWidthPortrait, windowHeightPortrait);
		} else {
			consoleUnit.setOrientationAndPosition(windowOrientation, windowWidthLandscape, windowHeightLandscape);
		}
		
		consoleUnit.setVisible(true);		
//		// Change console type if necessary
//		if(consoleUnit instanceof ResizableUnit && (event.getWidth() < consoleUnit.getWidth() || event.getHeight() < consoleUnit.getHeight())) {
//			redrawConsoleUnit();
//		} else if(consoleUnit instanceof FullScreenUnit && (event.getWidth() > consoleUnit.getWidth() && event.getHeight() > consoleUnit.getHeight())) {
//			redrawConsoleUnit();
//		}
	}
	
	/**
	 * Resize the body element to match current window size
	 */
	public void resizeContainerElement() {
		int newWidth;
		int newHeight;
		
		// Get new window size if desktop
		if("portrait".equals(windowOrientation)) {
				newWidth = windowWidthPortrait;
				newHeight = windowHeightPortrait;						
		} else {
				newWidth = windowWidthLandscape;
				newHeight = windowHeightLandscape;
		}
		
		// Resize body to new window size
		setContainerSize(newWidth, newHeight);
	}
	
	public void setContainerSize(int width, int height) {
		RootPanel.get("consoleContainer").setWidth(width + "px");
		RootPanel.get("consoleContainer").setHeight(height + "px");
	}
	
	// This is our window height i.e. the longest window dimension
	public int getWindowHeight() {
		if("portrait".equals(windowOrientation)) {
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
		if("portrait".equals(windowOrientation)) {
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

	public void getWindowSize() {
		int winWidth = (Window.getClientWidth() > BrowserUtils.getNativeWidth() || !BrowserUtils.isMobile) ? Window.getClientWidth() : BrowserUtils.getNativeWidth();
   	int winHeight = (Window.getClientHeight() > BrowserUtils.getNativeHeight() || !BrowserUtils.isMobile) ? Window.getClientHeight() : BrowserUtils.getNativeHeight();
		if ("portrait".equals(windowOrientation)) {
			if (!isPortraitInitialised  || !BrowserUtils.isMobile) {
				windowWidthPortrait = winWidth;
				windowHeightPortrait = winHeight;
			}
			isPortraitInitialised = true;
		} else {
			if (!isLandscapeInitialised  || !BrowserUtils.isMobile) {
				windowWidthLandscape = winWidth;
				windowHeightLandscape = winHeight;
			}
			isLandscapeInitialised = true;
		}
	}
	
	public void setWindowOrientation() {
		if (Window.getClientHeight() >= Window.getClientWidth()) {
			windowOrientation = "portrait";
		} else {
			windowOrientation = "landscape";
		}
	}
	
	public void addConsole() {
		AbsolutePanel containerInner = new AbsolutePanel();
		containerInner.setWidth("100%");
		containerInner.setHeight("100%");
		containerInner.add(consoleUnit);
		RootPanel.get("consoleContainer").add(containerInner);
	}
	
//	public void redrawConsoleUnit() {
//		ConsoleUnit newConsole = consoleUnit.redraw(windowWidthPortrait, windowHeightPortrait);
//		RootPanel.get("consoleUnitContainer").remove(consoleUnit);
//		consoleUnit = newConsole;
//		addConsole();
//	}
}
