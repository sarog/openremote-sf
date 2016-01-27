package org.openremote.web.console.event.press;

import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.HumanInputEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.shared.EventHandler;

/**
 * This event defines the start of a press event
 * @author rich
 *
 */
public class PressStartEvent extends PressEvent<PressStartHandler> {
	private static final Type<PressStartHandler> TYPE = new Type<PressStartHandler>();

	public PressStartEvent(HumanInputEvent<? extends EventHandler> sourceEvent) {
		super(sourceEvent);
		
		if (sourceEvent.getClass().equals(MouseDownEvent.class)) {
			MouseDownEvent event = (MouseDownEvent)sourceEvent;
			clientXPos = event.getClientX();
			clientYPos = event.getClientY();
			screenXPos = event.getScreenX();
			screenYPos = event.getScreenY();
		} else if (sourceEvent.getClass().equals(TouchStartEvent.class)) {
			TouchStartEvent event = (TouchStartEvent)sourceEvent;
			Touch touch = event.getTouches().get(0);
			clientXPos = touch.getClientX();
			clientYPos = touch.getClientY();
			screenXPos = touch.getScreenX();
			screenYPos = touch.getScreenY();
		}
	}
	
	@Override
	public Type<PressStartHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(PressStartHandler handler) {
		handler.onPressStart(this);
	}

	public static Type<PressStartHandler> getType() {
		return TYPE;
	}
}
