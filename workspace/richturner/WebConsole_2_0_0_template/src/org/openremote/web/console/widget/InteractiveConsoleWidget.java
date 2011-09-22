package org.openremote.web.console.widget;

import org.openremote.web.console.event.ConsoleUnitEventManager;
import org.openremote.web.console.event.press.PressEndEvent;
import org.openremote.web.console.event.press.PressMoveEvent;
import org.openremote.web.console.event.press.PressStartEvent;
import org.openremote.web.console.util.BrowserUtils;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.user.client.ui.Widget;

public abstract class InteractiveConsoleWidget extends ConsoleWidgetImpl implements Interactive {
	protected boolean handlersRegistered = false;
	PressStartEvent startEvent = null;
	protected PressMoveEvent lastMoveEvent = null;
	ConsoleUnitEventManager eventManager = ConsoleUnitEventManager.getInstance();
	
	public InteractiveConsoleWidget() {
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
			component.addDomHandler(this, TouchStartEvent.getType());
			component.addDomHandler(this, TouchEndEvent.getType());
		} else {
			component.addDomHandler(this, MouseDownEvent.getType());
			component.addDomHandler(this, MouseUpEvent.getType());
		}
		handlersRegistered = true;
	}
}
