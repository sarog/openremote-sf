package org.openremote.web.console.events;

import com.google.gwt.event.shared.GwtEvent;

/**
 * This event provides an amalgamation of touchstart and mousedown events
 * so it can be used for mobile and desktop human interaction detection
 * @author rich
 *
 */
public class SwipeEvent extends GwtEvent<SwipeHandler> {
	private static final Type<SwipeHandler> TYPE = new Type<SwipeHandler>();
	String direction;
	
	public SwipeEvent(String direction) {
			this.direction = direction;
	}
	
	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<SwipeHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(SwipeHandler handler) {
		handler.onSwipe(this);
	}

	public static Type<SwipeHandler> getType() {
		return TYPE;
	}

	public String getDirection() {
		return direction;
	}
}
