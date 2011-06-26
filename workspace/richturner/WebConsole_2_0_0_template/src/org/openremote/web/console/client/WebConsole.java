package org.openremote.web.console.client;


import org.openremote.web.console.types.FullScreenUnit;
import org.openremote.web.console.utils.BrowserUtils;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Element;
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
	int windowHeight;
	int windowWidth;
	int addressBarHeight;
	int topMargin;
	int bottomMargin;
	HandlerRegistration scrollHandler;
	
	public void onModuleLoad() {
		// Set default display and address bar heights
		windowHeight = Window.getClientHeight();
		windowWidth = Window.getClientWidth();
		
		// If mobile get address bar size and actual display size
		if (BrowserUtils.isMobile()) {
			// Add scroll listener
			scrollHandler = Window.addWindowScrollHandler(new ScrollHandler() {
				@Override
				public void onWindowScroll(ScrollEvent event) {
					scrollHandler.removeHandler();
					scrollHandler = null;
					
					windowHeight = Window.getClientHeight();
					windowWidth = Window.getClientWidth();
					
					RootPanel.get().remove(0);
					
					// Create the console unit
					createConsole();
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
					  windowHeight = Window.getClientHeight();
					  windowWidth = Window.getClientWidth();

					  // Remove the simple panel
					  RootPanel.get().remove(0);
					  
					  // Create the console unit
					  createConsole();
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
			RootPanel.get().add(simplePanel);
			Window.scrollTo(0, 1);
			
			t2.schedule(500);
		   t.schedule(1000);
		} else {
			// Create the console unit
			createConsole();
		}
	}
	
	/**
	 * Create the console unit and add it to the page
	 * Vertically align the console unit in the middle
	 * Horizontally align the console in the centre
	 */
	private void createConsole() {
		consoleUnit = ConsoleUnit.create(windowWidth, windowHeight);
		 
		// Add console to page
		RootPanel.get("consoleUnitContainer").add(consoleUnit);
		
		// Add margin to top and bottom for vertical align
		topMargin = ((Window.getClientHeight() - consoleUnit.getHeight()) / 2) - 2;
		bottomMargin = topMargin;
		
		DOM.setStyleAttribute(DOM.getElementById(ConsoleUnit.CONSOLE_HTML_ELEMENT_ID), "marginTop", topMargin + "px");
		DOM.setStyleAttribute(DOM.getElementById(ConsoleUnit.CONSOLE_HTML_ELEMENT_ID), "marginBottom", bottomMargin + "px");
		Element elem = DOM.getElementById(ConsoleUnit.CONSOLE_HTML_ELEMENT_ID);

		// Disable scrolling if full screen console or mobile
		if (BrowserUtils.isMobile() || consoleUnit instanceof FullScreenUnit) {
			//able scrollNot reliable so will have to leave scrolling active
			//Window.enableScrolling(false);
		}
		
		// Show loading screen
		consoleUnit.consoleDisplay.showLoadingScreen();
	}
}
