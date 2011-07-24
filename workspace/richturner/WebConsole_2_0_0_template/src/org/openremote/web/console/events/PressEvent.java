package org.openremote.web.console.events;

import java.util.Date;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.HumanInputEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.Window;

/**
 * This event provides an amalgamation of touchstart and mousedown events
 * so it can be used for mobile and desktop human interaction detection
 * @author rich
 *
 */
public class PressEvent extends GwtEvent<PressHandler> {
	private static final Type<PressHandler> TYPE = new Type<PressHandler>();
	private int clientXPos;
	private int clientYPos;
	private int screenXPos;
	private int screenYPos;
	private long time;
	private Object source;
	
	public PressEvent(HumanInputEvent<? extends EventHandler> sourceEvent) {
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
		time = new Date().getTime();
		source = sourceEvent.getSource();
	}
	
	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<PressHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(PressHandler handler) {
		handler.onPress(this);
	}

	public static Type<PressHandler> getType() {
		return TYPE;
	}
	
	public int getClientX() {
		return clientXPos;
	}
	
	public int getClientY() {
		return clientYPos;
	}
	
	public int getScreenX() {
		return screenXPos;
	}
	
	public int getScreenY() {
		return screenYPos;
	}
	
	public Object getSource() {
		return source;
	}
	
	public long getTime() {
		return time;
	}
}
