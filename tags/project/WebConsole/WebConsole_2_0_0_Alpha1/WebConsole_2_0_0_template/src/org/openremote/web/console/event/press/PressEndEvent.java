package org.openremote.web.console.event.press;

import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.TouchEvent;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * This event defines the end of a press event
 * @author rich
 *
 */
public class PressEndEvent extends PressEvent<PressEndHandler> {
	private static final Type<PressEndHandler> TYPE = new Type<PressEndHandler>();

	public PressEndEvent(GwtEvent<? extends EventHandler> sourceEvent) {
		super(sourceEvent);
		
		if (sourceEvent.getClass().equals(MouseUpEvent.class)) {
			MouseUpEvent event = (MouseUpEvent)sourceEvent;
			clientXPos = event.getClientX();
			clientYPos = event.getClientY();
			screenXPos = event.getScreenX();
			screenYPos = event.getScreenY();
		} else if (sourceEvent.getClass().equals(TouchStartEvent.class) || sourceEvent.getClass().equals(TouchMoveEvent.class)) {
			TouchEvent<? extends EventHandler> event = (TouchEvent<? extends EventHandler>) sourceEvent;
			Touch touch = event.getTouches().get(0);
			clientXPos = touch.getClientX();
			clientYPos = touch.getClientY();
			screenXPos = touch.getScreenX();
			screenYPos = touch.getScreenY();
		}
	}
	
	@Override
	public Type<PressEndHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(PressEndHandler handler) {
		handler.onPressEnd(this);
	}

	public static Type<PressEndHandler> getType() {
		return TYPE;
	}
}
