package org.openremote.web.console.events;

import org.openremote.web.console.client.WebConsole;
import org.openremote.web.console.utils.BrowserUtils;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;

public class ResizeHandlerImpl implements ResizeHandler {
	boolean resizeOccurred = false;
	ResizeEvent event = null;
	private ConsoleUnitEventManager eventManager;
	private WebConsole consoleModule;
	
	public ResizeHandlerImpl(ConsoleUnitEventManager eventManager) {
		this.eventManager = eventManager;
		consoleModule = this.eventManager.getConsoleModule();
	}
	
	@Override
	public void onResize(ResizeEvent event) {
		if (!resizeOccurred) {
			// If mobile make sure orientation has changed
			if (BrowserUtils.isMobile) {
				String oldOrientation = consoleModule.getWindowOrientation();
				consoleModule.updateWindowOrientation();
				String newOrientation = consoleModule.getWindowOrientation();
				if (oldOrientation.equals(newOrientation)) {
					return;
				}
			}
			
			// Hide the console unit
			consoleModule.getConsoleUnit().hide();
			
			// If mobile then check window is fully initialised
			if (BrowserUtils.isMobile && !consoleModule.isMobileWindowFullyInitialised()) {
				consoleModule.initMobileWindow();
				return;
			}
			
			resizeOccurred = true;
			this.event = event;
			
			processResize();
		}
	}
	
	public void onResize() {
		onResize(null);
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
	public void processResize() {	
		// Update window
		consoleModule.updateWindow();
	
		// If mobile then this is actually a rotation event so fire console unit rotation event
		if (BrowserUtils.isMobile) {
			consoleModule.getConsoleUnit().fireEvent(new RotationEvent(consoleModule.getWindowOrientation(), consoleModule.getWindowWidth(), consoleModule.getWindowHeight()));
		} else {
			// Reposition the console unit
			consoleModule.getConsoleUnit().setPosition(consoleModule.getWindowWidth(), consoleModule.getWindowHeight());
		}
		
		// Show the console unit again
		consoleModule.getConsoleUnit().show();
		
		reset();
	}
	
	public void reset() {
		event = null;
		resizeOccurred = false;
	}
}
