package org.openremote.web.console.event;

import org.openremote.web.console.client.WebConsole;
import org.openremote.web.console.client.unit.ConsoleUnit;
import org.openremote.web.console.event.press.PressMoveReleaseHandlerImpl;
import org.openremote.web.console.util.BrowserUtils;

import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;

public class ConsoleUnitEventManager {
	private final HandlerManager eventBus;
	private PressMoveReleaseHandlerImpl pressMoveReleaseHandler;
	private WindowResizeHandlerImpl resizeHandler;
	private WebConsole consoleModule;
	private ConsoleUnit consoleUnit;
	
	public ConsoleUnitEventManager(WebConsole consoleModule) {
		this.consoleModule = consoleModule;
		consoleUnit = consoleModule.getConsoleUnit();
		eventBus = new HandlerManager(consoleUnit);
		pressMoveReleaseHandler = new PressMoveReleaseHandlerImpl(this);
		resizeHandler = new WindowResizeHandlerImpl(this);
		attachHandlers();
	}
	
	private void attachHandlers() {
//		eventBus.addHandler(PressStartEvent.getType(),pressMoveReleaseHandler);
//		eventBus.addHandler(PressMoveEvent.getType(),pressMoveReleaseHandler);
//		eventBus.addHandler(PressEndEvent.getType(),pressMoveReleaseHandler);
		
		// Add window resize handler
		Window.addResizeHandler(resizeHandler);
		
		// Add native orientation handler for mobiles
		if (BrowserUtils.isMobile) {
			addNativeOrientationHandler(resizeHandler);
		}
		
		// Disable scrolling on mobile devices
		if (BrowserUtils.isMobile) {
			// Prevent touch moves
			RootPanel.get().addDomHandler(new TouchMoveHandler() {
				public void onTouchMove(TouchMoveEvent e) {
						e.preventDefault();
				}
			}, TouchMoveEvent.getType());
		}
	}
	
	// Create a native orientation change handler as resize handler isn't reliable on iOS 3.x
   public native void addNativeOrientationHandler(WindowResizeHandlerImpl resizeHandler) /*-{
   	if (typeof window.onorientationchange != 'undefined') {
	   	function eventHandler(e) {
				resizeHandler.@org.openremote.web.console.event.WindowResizeHandlerImpl::onResize()();
	   	}
	   	$wnd.addEventListener("orientationchange", eventHandler, false);
   	}
	}-*/;
   
   public WebConsole getConsoleModule() {
   	return consoleModule;
   }
   
   public WindowResizeHandlerImpl getResizeHandler() {
   	return resizeHandler;
   }
   
   public HandlerManager getEventBus() {
   	return eventBus;
   }
   
   public PressMoveReleaseHandlerImpl getPressMoveReleaseHandler() {
   	return pressMoveReleaseHandler;
   }
}
