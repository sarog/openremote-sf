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
	
	public void onModuleLoad() {
		// Get Window information
		getWindowInfo();
		
		// If mobile get address bar size and actual display size
		if (BrowserUtils.isMobile()) {
			// Add scroll listener
			scrollHandler = Window.addWindowScrollHandler(new ScrollHandler() {
				@Override
				public void onWindowScroll(ScrollEvent event) {
					scrollHandler.removeHandler();
					scrollHandler = null;
					
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
			
			/**
			 * Special iphone timer to delay scroll; doesn't seem
			 * to scroll otherwise
			 */
			Timer t2 = new Timer() {
				  public void run() {
					  Window.scrollTo(0, 1);
				  }
				};
			
			/**
			 *  Add simple panel which is taller than the window height
			 *  so we can do a scroll event to hide address bar only then
			 *  can we determine the true window size
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
