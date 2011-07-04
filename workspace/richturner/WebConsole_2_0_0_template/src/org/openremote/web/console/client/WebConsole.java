package org.openremote.web.console.client;


import org.openremote.web.console.types.FullScreenUnit;
import org.openremote.web.console.types.ResizableUnit;
import org.openremote.web.console.utils.BrowserUtils;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
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
	
	VerticalPanel mainPanel;
	ConsoleUnit consoleUnit;
	int windowHeight = 0;
	int windowWidth = 0;
	public boolean isInitialised = false;
	String windowOrientation = "portrait";
	int topMargin;
	int bottomMargin;
	HandlerRegistration scrollHandler;
	Timer addressBarMonitor;
	
	public void onModuleLoad() {
		// Get Window information
		getWindowInfo();
		
		// Create a timer to wait for window to be initialised
		Timer initialisationTimer = new Timer() {
			@Override
			public void run() {
				// if window initialised then initialise console
				if (isInitialised) {
					// Initialise the DOM
					initialiseDom();

					initialiseConsole();
					this.cancel();
				}
			}
		};
		initialisationTimer.scheduleRepeating(100);
		
		/*
		 * If mobile device then hide address bar and determine true window size
		 * and lock the window in place. User can still get to the address bar if
		 * they wish by touching the status bar on iphone or pressing menu on android
		 */		
		if (BrowserUtils.isMobile) {
			// Will initialise window and set initialised flag when done
			BrowserUtils.initWindow(this);
		} else {
			// No initialising to do so set the flag now
			isInitialised = true;
		}
	}
	
	/**
	 * Method to size body to window dynamically as window changes.
	 * Window change on mobile devices will be an orientation
	 * change
	 */
	private void initialiseDom() {
		// Size body
		if("portrait".equals(windowOrientation)) {
			BrowserUtils.setBodySize(windowWidth, windowHeight);
		} else {
			BrowserUtils.setBodySize(windowHeight, windowWidth);
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
				/**
				 * For desktops we just update window dimensions
				 * For mobiles we switch width and height depending on orientation
				 */
				
				if (BrowserUtils.isMobile) {
					Window.scrollTo(0,1);
					
					String prevOrientation = windowOrientation;
					
					setWindowOrientation();
					
					if (prevOrientation.equals(windowOrientation)) {
						return;
					}
					consoleUnit.setOrientation(windowOrientation);
				}
				
				resizeBodyElement();
				
				// Do scroll if address bar has re-appeared 
				if (BrowserUtils.isMobile && ((Window.getClientHeight() != windowWidth || Window.getClientHeight() != windowHeight) || (Window.getClientWidth() != windowWidth || Window.getClientWidth() != windowHeight))) {
					Window.scrollTo(0,1);
				}
				
//				// Change console type if necessary
//				if(consoleUnit instanceof ResizableUnit && (event.getWidth() < consoleUnit.getWidth() || event.getHeight() < consoleUnit.getHeight())) {
//					redrawConsoleUnit();
//				} else if(consoleUnit instanceof FullScreenUnit && (event.getWidth() > consoleUnit.getWidth() && event.getHeight() > consoleUnit.getHeight())) {
//					redrawConsoleUnit();
//				}
			}
		});
	}
	
	/**
	 * Resize the body element to match current window size
	 */
	public void resizeBodyElement() {
		int newWidth;
		int newHeight;
		
		// If mobile update orientation info
		if (BrowserUtils.isMobile) {
			// Get new window size if desktop
			if("portrait".equals(windowOrientation)) {
					newWidth = windowWidth;
					newHeight = windowHeight;						
			} else {
					newWidth = windowHeight;
					newHeight = windowWidth;
			}
		} else {
			newWidth = Window.getClientWidth();
			newHeight = Window.getClientHeight();
		}
		
		// Resize body to new window size
		BrowserUtils.setBodySize(newWidth, newHeight);
	}
	
	/**
	 * Create the console unit and add it to the page
	 * Vertically align the console unit in the middle
	 * Horizontally align the console in the centre
	 */
	private void initialiseConsole() {
		consoleUnit = ConsoleUnit.create(windowWidth, windowHeight);
		
		// Add Console Unit to the screen and position vertically
		addAndPositionConsole();
		
		// Orient the console to match the window
		consoleUnit.setOrientation(windowOrientation);
	}
	
	// This is our window height i.e. the longest window dimension
	public int getWindowHeight() {
		return windowHeight;
	}
	
	public int getWindowWidth() {
		return windowWidth;
	}
	
	public void getWindowInfo() {
		/**
		 * Only set window orientation the first time as desktops will
		 * be static but mobile device orientation will be picked up
		 * by the resize handler
		 */
		if (BrowserUtils.isMobile) {
			setWindowOrientation();
		}
		
		// Get window sizes independent of screen orientation
		// largest dimension is always the height
		if ("portrait".equals(windowOrientation)) {
			if (Window.getClientHeight() > windowHeight) {
				windowHeight = Window.getClientHeight();
			}
			if (Window.getClientWidth() > windowWidth) {
				windowWidth = Window.getClientWidth();
			}
		} else {
			if (Window.getClientHeight() > windowWidth) {
				windowWidth = Window.getClientHeight();
			}
			if (Window.getClientWidth() > windowHeight) {
				windowHeight = Window.getClientWidth();
			}
		}
	}
	
	public void setWindowOrientation() {
		if (Window.getClientHeight() >= Window.getClientWidth()) {
			windowOrientation = "portrait";
		} else {
			windowOrientation = "landscape";
		}
	}
	
	public void addAndPositionConsole() {
		// Use a vertical panel to position the console vertically
		VerticalPanel consoleUnitWrapper = new VerticalPanel();
		consoleUnitWrapper.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		consoleUnitWrapper.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		consoleUnitWrapper.setStylePrimaryName("consoleUnitWrapper");
		consoleUnitWrapper.add(consoleUnit);
		
		// Add console to page
		RootPanel.get().add(consoleUnitWrapper);

		RootPanel.get().setStylePrimaryName("consoleUnitContainer");
		
//		// Add margin to top and bottom for vertical align of console unit
//		topMargin = ((windowHeight - consoleUnit.getHeight()) / 2) - 2;
//		bottomMargin = topMargin;
//		DOM.setStyleAttribute(consoleUnit.getElement(), "marginTop", topMargin + "px");
//		DOM.setStyleAttribute(consoleUnit.getElement(), "marginBottom", bottomMargin + "px");		
	}
	
	public void redrawConsoleUnit() {
		ConsoleUnit newConsole = consoleUnit.redraw(windowWidth, windowHeight);
		RootPanel.get("consoleUnitContainer").remove(consoleUnit);
		consoleUnit = newConsole;
		addAndPositionConsole();
	}
}
