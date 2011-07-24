package org.openremote.web.console.events;

import org.openremote.web.console.client.ConsoleUnit;
import org.openremote.web.console.client.WebConsole;
import org.openremote.web.console.components.ConsoleDisplay;
import org.openremote.web.console.utils.BrowserUtils;

import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;

public class ConsoleUnitEventManager implements MouseDownHandler, TouchStartHandler, MouseMoveHandler, TouchMoveHandler, MouseUpHandler, TouchEndHandler, MouseOutHandler {
	private final HandlerManager handlerManager;
	private PressReleaseHandlerImpl pressReleaseHandler;
	private ResizeHandlerImpl resizeHandler;
	private WebConsole consoleModule;
	private ConsoleUnit consoleUnit;
	
	public ConsoleUnitEventManager(WebConsole consoleModule) {
		this.consoleModule = consoleModule;
		consoleUnit = this.consoleModule.getConsoleUnit();
		handlerManager = new HandlerManager(consoleUnit);
		pressReleaseHandler = new PressReleaseHandlerImpl(this);
		resizeHandler = new ResizeHandlerImpl(this);
		attachHandlers();
	}

	@Override
	public void onMouseDown(MouseDownEvent event) {
		if (!pressReleaseHandler.pressStarted) {
			handlerManager.fireEvent(new PressEvent(event));
		}
		event.preventDefault();
	}

	@Override
	public void onTouchEnd(TouchEndEvent event) {
		if (pressReleaseHandler.pressStarted) {
			handlerManager.fireEvent(new ReleaseEvent(event));
		}
		event.preventDefault();
	}

	@Override
	public void onMouseUp(MouseUpEvent event) {
		if (pressReleaseHandler.pressStarted) {
			handlerManager.fireEvent(new ReleaseEvent(event));
		}
		event.preventDefault();
	}
	
	@Override
	public void onTouchMove(TouchMoveEvent event) {
		if (pressReleaseHandler.pressStarted) {
			handlerManager.fireEvent(new PressMoveEvent(event));
		}
		event.preventDefault();
	}

	@Override
	public void onMouseMove(MouseMoveEvent event) {
		if (pressReleaseHandler.pressStarted) {
			handlerManager.fireEvent(new PressMoveEvent(event));
		}
		event.preventDefault();
	}

	@Override
	public void onTouchStart(TouchStartEvent event) {
		if (!pressReleaseHandler.pressStarted) {
			handlerManager.fireEvent(new PressEvent(event));
		}
		event.preventDefault();
	}

	@Override
	public void onMouseOut(MouseOutEvent event) {
		if (pressReleaseHandler.pressStarted) {
			handlerManager.fireEvent(new ReleaseEvent(event));
		}
		event.preventDefault();
	}
	
	private void attachHandlers() {
		ConsoleDisplay consoleDisplay = consoleUnit.getConsoleDisplay(); 
		consoleDisplay.addDomHandler(this, MouseDownEvent.getType());
		consoleDisplay.addDomHandler(this, TouchStartEvent.getType());
		consoleDisplay.addDomHandler(this, MouseMoveEvent.getType());
		consoleDisplay.addDomHandler(this, TouchMoveEvent.getType());
		consoleDisplay.addDomHandler(this, MouseUpEvent.getType());
		consoleDisplay.addDomHandler(this, TouchEndEvent.getType());
		consoleDisplay.addDomHandler(this, MouseOutEvent.getType());
//		consoleDisplay.addHandler(pressReleaseHandler, PressEvent.getType());
//		consoleDisplay.addHandler(pressReleaseHandler, PressMoveEvent.getType());
//		consoleDisplay.addHandler(pressReleaseHandler, ReleaseEvent.getType());
		handlerManager.addHandler(PressEvent.getType(),pressReleaseHandler);
		handlerManager.addHandler(PressMoveEvent.getType(),pressReleaseHandler);
		handlerManager.addHandler(ReleaseEvent.getType(),pressReleaseHandler);
		
		// Add window resize handler
		Window.addResizeHandler(resizeHandler);
		
		// Add native orientation handler for mobiles
		if (BrowserUtils.isMobile) {
			addNativeOrientationHandler(resizeHandler, consoleUnit);
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
   public native void addNativeOrientationHandler(ResizeHandlerImpl resizeHandler, ConsoleUnit consoleUnit) /*-{
   	if (typeof window.onorientationchange != 'undefined') {
	   	function eventHandler(e) {
				resizeHandler.@org.openremote.web.console.events.ResizeHandlerImpl::onResize()();
	   	}
	   	$wnd.addEventListener("orientationchange", eventHandler, false);
   	}
	}-*/;
   
   public WebConsole getConsoleModule() {
   	return consoleModule;
   }
   
   public ResizeHandlerImpl getResizeHandler() {
   	return resizeHandler;
   }
}
