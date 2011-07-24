package org.openremote.web.console.events;

import com.google.gwt.event.shared.GwtEvent;

/**
 * This event provides an amalgamation of touchstart and mousedown events
 * so it can be used for mobile and desktop human interaction detection
 * @author rich
 *
 */
public class RotationEvent extends GwtEvent<RotationHandler> {
	private static final Type<RotationHandler> TYPE = new Type<RotationHandler>();
	String orientation;
	
	public RotationEvent(String orientation) {
			this.orientation = orientation;
	}
	
	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<RotationHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(RotationHandler handler) {
		handler.onRotate(this);
	}

	public static Type<RotationHandler> getType() {
		return TYPE;
	}

	public String getOrientation() {
		return orientation;
	}
}
