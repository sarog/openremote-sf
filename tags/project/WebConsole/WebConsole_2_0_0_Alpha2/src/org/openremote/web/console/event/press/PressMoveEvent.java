package org.openremote.web.console.event.press;

import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.HumanInputEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.shared.EventHandler;

/**
 * This event defines press move event
 * @author rich
 *
 */
public class PressMoveEvent extends PressEvent<PressMoveHandler> {
	private static final Type<PressMoveHandler> TYPE = new Type<PressMoveHandler>();

	public PressMoveEvent(HumanInputEvent<? extends EventHandler> sourceEvent) {
		super(sourceEvent);
		
		if (sourceEvent.getClass().equals(MouseMoveEvent.class)) {
			MouseMoveEvent event = (MouseMoveEvent)sourceEvent;
			clientXPos = event.getClientX();
			clientYPos = event.getClientY();
			screenXPos = event.getScreenX();
			screenYPos = event.getScreenY();
		} else if (sourceEvent.getClass().equals(TouchMoveEvent.class)) {
			TouchMoveEvent event = (TouchMoveEvent)sourceEvent;
			Touch touch = event.getTouches().get(0);
			clientXPos = touch.getClientX();
			clientYPos = touch.getClientY();
			screenXPos = touch.getScreenX();
			screenYPos = touch.getScreenY();
		}
	}
	
	@Override
	public Type<PressMoveHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(PressMoveHandler handler) {
		handler.onPressMove(this);
	}

	public static Type<PressMoveHandler> getType() {
		return TYPE;
	}
}
