package org.openremote.web.console.event.press;

import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.HumanInputEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
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
public class PressCancelEvent extends PressEvent<PressCancelHandler> {
	private static final Type<PressCancelHandler> TYPE = new Type<PressCancelHandler>();

	public PressCancelEvent(MouseEvent<MouseOutHandler> sourceEvent) {
		super(sourceEvent);

		clientXPos = sourceEvent.getClientX();
		clientYPos = sourceEvent.getClientY();
		screenXPos = sourceEvent.getScreenX();
		screenYPos = sourceEvent.getScreenY();
	}
	
	@Override
	public Type<PressCancelHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(PressCancelHandler handler) {
		handler.onPressCancel(this);
	}

	public static Type<PressCancelHandler> getType() {
		return TYPE;
	}
}
