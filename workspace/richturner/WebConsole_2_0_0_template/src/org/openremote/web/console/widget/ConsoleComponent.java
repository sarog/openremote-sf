package org.openremote.web.console.widget;

import org.openremote.web.console.event.press.PressEndEvent;
import org.openremote.web.console.event.press.PressMoveEvent;
import org.openremote.web.console.event.press.PressStartEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class ConsoleComponent extends Composite implements MouseDownHandler, TouchStartHandler, MouseUpHandler, TouchEndHandler {
	PressStartEvent startEvent = null;
	protected PressMoveEvent lastMoveEvent = null;
	
	public ConsoleComponent() {
	}
	
	@Override
	public void onTouchStart(TouchStartEvent event) {
		event.stopPropagation();
		startEvent = new PressStartEvent(event);
		this.fireEvent(startEvent);
	}

	@Override
	public void onMouseDown(MouseDownEvent event) {
		event.stopPropagation();
		startEvent = new PressStartEvent(event);
		this.fireEvent(startEvent);
	}

	@Override
	public void onTouchEnd(TouchEndEvent event) {
		event.stopPropagation();
		if (lastMoveEvent != null) {
			this.fireEvent(new PressEndEvent(lastMoveEvent));
		} else if (startEvent != null) {
			this.fireEvent(new PressEndEvent(startEvent));
		}
		reset();
	}

	@Override
	public void onMouseUp(MouseUpEvent event) {
		event.stopPropagation();
		this.fireEvent(new PressEndEvent(event));
		reset();
	}

	protected void reset() {
		startEvent = null;
		lastMoveEvent = null;
	}
	
	public void registerPressHandlers() {
		registerPressHandlers(this);
	}
	
	public void registerPressHandlers(Widget component) {
		component.addDomHandler(this, MouseDownEvent.getType());
		component.addDomHandler(this, TouchStartEvent.getType());
		//component.addDomHandler(this, MouseMoveEvent.getType());
		//component.addDomHandler(this, TouchMoveEvent.getType());
		component.addDomHandler(this, MouseUpEvent.getType());
		component.addDomHandler(this, TouchEndEvent.getType());
	}
}
