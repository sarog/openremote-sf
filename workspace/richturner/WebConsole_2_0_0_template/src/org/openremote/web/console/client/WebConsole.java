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
import com.google.gwt.user.client.Window.ScrollEvent;
import com.google.gwt.user.client.Window.ScrollHandler;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;


/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class WebConsole implements EntryPoint {
	
	VerticalPanel mainPanel;
	ConsoleUnit consoleUnit;
	int windowHeight = 0;
	int windowWidth = 0;
	boolean isWindowPortrait;
	int topMargin;
	int bottomMargin;
	HandlerRegistration scrollHandler;
	Timer addressBarMonitor;
	Timer initialisationTimer;
	
	public void onModuleLoad() {
		// Get Window information
		getWindowInfo();
		
		// Create a timer to wait for window to be initialised
		initialisationTimer = new Timer() {
			@Override
			public void run() {
				// if window initialised then initialise console
				if (isInitialised) {
					Window.alert("INIT: " + windowWidth +  " x " + windowHeight);
					initialiseConsole();
					this.cancel();
				}
			}
		};
		initialisationTimer.scheduleRepeating(200);
		
		// If mobile device then hide address bar and determine true window size
		if (BrowserUtils.isMobile()) {
			// Address bar monitor to determine when bar is hidden and to
			// keep it hidden unless user wants to see it then display for 5s
			Timer addressBarMonitor = new Timer() {
			  private boolean addressVisible = false;
			  public void run() {
				  	
				  	if(addressVisible) {
				  		// Scroll window
				  		Window.scrollTo(0, 1);
				  		addressVisible = false;
				  	}
					
					getWindowInfo();
					
					//RootPanel.get().remove(0);
					
					// Create the console unit
					initialiseConsole();
				}
			});
			
			/**
			 *  Add 1s timer just in case scroll never happens
			 *  just carry on as normal but with address bar showing
			 */
			Timer t = new Timer() {
			  public void run() {
				  /**
				   *  Check to see if scroll occurred or 1 second passed
				   *  If it has then create a console unit anyway
				   */
				  if (scrollHandler != null) {
					  scrollHandler = null;
					  
					  // Check width and height just in case scroll handler didn't catch
					  getWindowInfo();

					  // Remove the simple panel
					  //RootPanel.get().remove(0);
					  
					  // Create the console unit
					  initialiseConsole();
				  }
			  }
			};
			
		   // Make body twice window height to ensure there's something to scroll
		   BrowserUtils.setBodySize(windowWidth, windowHeight*2);
		   
		   // Scroll Window to hide address bar
			Window.scrollTo(0, 1);
			
			// Wait 1s for first run as some browsers take a while to do scroll
			addressBarMonitor.schedule(1000);
		   
			/*
			 *  Prevent rotation of the window
			 */
//			Window.addResizeHandler(new ResizeHandler() {
//				@Override
//				public void onResize(ResizeEvent event) {
//					getWindowInfo();
					
//					if(consoleUnit instanceof ResizableUnit && (event.getWidth() < consoleUnit.getWidth() || event.getHeight() < consoleUnit.getHeight())) {
//						redrawConsoleUnit();
//					} else if(consoleUnit instanceof FullScreenUnit && (event.getWidth() > consoleUnit.getWidth() && event.getHeight() > consoleUnit.getHeight())) {
//						redrawConsoleUnit();
//					}
//				}
//			});		   
		} else {
			// No initialising to do so set the flag now
			isInitialised = true;
			
			/*
			 *  Monitor window resize to change console unit type if necessary
			 *  only monitor on desktop as mobile size should be fixed
			 */
			SimplePanel simplePanel = new SimplePanel();
			simplePanel.setSize("1px", "10000px");
			simplePanel.setVisible(true);
			RootPanel.get().add(simplePanel);
			Window.scrollTo(0, 1);
			
			t2.schedule(500);
		   t.schedule(1000);
		   
			/*
			 *  Prevent rotation of the window
			 */
//			Window.addResizeHandler(new ResizeHandler() {
//				@Override
//				public void onResize(ResizeEvent event) {
//					getWindowInfo();
					
//					if(consoleUnit instanceof ResizableUnit && (event.getWidth() < consoleUnit.getWidth() || event.getHeight() < consoleUnit.getHeight())) {
//						redrawConsoleUnit();
//					} else if(consoleUnit instanceof FullScreenUnit && (event.getWidth() > consoleUnit.getWidth() && event.getHeight() > consoleUnit.getHeight())) {
//						redrawConsoleUnit();
//					}
//				}
//			});		   
		} else {
			/*
			 *  Monitor window resize to change console unit type if necessary
			 *  only monitor on desktop as mobile size should be fixed
			 */
			Window.addResizeHandler(new ResizeHandler() {
				@Override
				public void onResize(ResizeEvent event) {
					getWindowInfo();
					
					if(consoleUnit instanceof ResizableUnit && (event.getWidth() < consoleUnit.getWidth() || event.getHeight() < consoleUnit.getHeight())) {
						redrawConsoleUnit();
					} else if(consoleUnit instanceof FullScreenUnit && (event.getWidth() > consoleUnit.getWidth() && event.getHeight() > consoleUnit.getHeight())) {
						redrawConsoleUnit();
					}
				}
			});
			
			// Create the console unit
			initialiseConsole();
		}
	}
	
	/**
	 * Create the console unit and add it to the page
	 * Vertically align the console unit in the middle
	 * Horizontally align the console in the centre
	 */
	private void initialiseConsole() {
		return;
//		consoleUnit = ConsoleUnit.create(windowWidth, windowHeight);
//		
//		if (BrowserUtils.isMobile()) {
//			// Prevent touch move unless console display is bigger than window
//			if (windowWidth > consoleUnit.displayWidth && windowHeight > consoleUnit.displayHeight) {
//				RootPanel.get().addDomHandler(new TouchMoveHandler() {
//					public void onTouchMove(TouchMoveEvent e) {
//						e.preventDefault();
//					}
//				}, TouchMoveEvent.getType());
//			}
//		}		
//		
//		// Add Console Unit to the screen and position vertically
//		addAndPositionConsole();
//
//		// Show loading screen
//		consoleUnit.consoleDisplay.showLoadingScreen();
	}
	
	public void getWindowInfo() {
		//if (!BrowserUtils.isMobile() || windowHeight == 0 || windowWidth == 0) {
			windowHeight = Window.getClientHeight();
			windowWidth = Window.getClientWidth();
		//}
		//Window.scrollTo(0, 1);
		if (Window.getClientHeight() >= Window.getClientWidth()) {
			isWindowPortrait = true;
		} else {
			isWindowPortrait = false;
		}
	}
	
	public void addAndPositionConsole() {
		// Add console to page
		RootPanel.get("consoleUnitContainer").add(consoleUnit);

		// Add margin to top and bottom for vertical align of console unit
		topMargin = ((windowHeight - consoleUnit.getHeight()) / 2) - 2;
		bottomMargin = topMargin;
		DOM.setStyleAttribute(consoleUnit.getElement(), "marginTop", topMargin + "px");
		DOM.setStyleAttribute(consoleUnit.getElement(), "marginBottom", bottomMargin + "px");		
	}
	
	public void redrawConsoleUnit() {
		ConsoleUnit newConsole = consoleUnit.redraw(windowWidth, windowHeight);
		RootPanel.get("consoleUnitContainer").remove(consoleUnit);
		consoleUnit = newConsole;
		addAndPositionConsole();
	}
}
