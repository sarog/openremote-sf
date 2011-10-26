package org.openremote.web.console.widget;

import java.util.ArrayList;
import java.util.List;

import org.openremote.web.console.event.ConsoleUnitEventManager;
import org.openremote.web.console.event.press.PressEndEvent;
import org.openremote.web.console.event.press.PressMoveEvent;
import org.openremote.web.console.event.press.PressStartEvent;
import org.openremote.web.console.util.BrowserUtils;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Widget;

public abstract class InteractiveConsoleComponent extends ConsoleComponentImpl implements Interactive {
	private List<HandlerRegistration> handlerRegistrations = new ArrayList<HandlerRegistration>();
	protected boolean handlersRegistered = false;
	PressStartEvent startEvent = null;
	protected PressMoveEvent lastMoveEvent = null;
	ConsoleUnitEventManager eventManager = ConsoleUnitEventManager.getInstance();
	
	protected InteractiveConsoleComponent(Widget container) {
		super(container);
	}
	
	@Override
	public void onTouchStart(TouchStartEvent event) {
		event.stopPropagation();
		startEvent = new PressStartEvent(event);
		eventManager.getEventBus().fireEvent(startEvent);
	}

	@Override
	public void onMouseDown(MouseDownEvent event) {
		event.stopPropagation();
		startEvent = new PressStartEvent(event);
		eventManager.getEventBus().fireEvent(startEvent);
	}

	@Override
	public void onTouchEnd(TouchEndEvent event) {
		event.stopPropagation();
		if (lastMoveEvent != null) {
			eventManager.getEventBus().fireEvent(new PressEndEvent(lastMoveEvent));
		} else if (startEvent != null) {
			eventManager.getEventBus().fireEvent(new PressEndEvent(startEvent));
		}
		reset();
	}

	@Override
	public void onMouseUp(MouseUpEvent event) {
		event.stopPropagation();
		eventManager.getEventBus().fireEvent(new PressEndEvent(event));
		reset();
	}

	protected void reset() {
		startEvent = null;
		lastMoveEvent = null;
	}
	
	
	/**
	 * Add Mouse and Touch Handlers to either entire console component or specified
	 * child widget
	 */
	public void registerMouseAndTouchHandlers() {
		registerMouseAndTouchHandlers(this);
	}
	
	public void registerMouseAndTouchHandlers(Widget component) {
		if(BrowserUtils.isMobile()) {
			storeHandler(component.addDomHandler(this, TouchStartEvent.getType()));
			storeHandler(component.addDomHandler(this, TouchEndEvent.getType()));
		} else {
			storeHandler(component.addDomHandler(this, MouseDownEvent.getType()));
			storeHandler(component.addDomHandler(this, MouseUpEvent.getType()));
		}
		
	}
	
	public void unRegisterMouseAndTouchHandlers() {
		for (HandlerRegistration handler : handlerRegistrations) {
			handler.removeHandler();
		}
		handlersRegistered = false;
	}
	
	public void storeHandler(HandlerRegistration registration) {
		handlerRegistrations.add(registration);
		handlersRegistered = true;
	}
}
