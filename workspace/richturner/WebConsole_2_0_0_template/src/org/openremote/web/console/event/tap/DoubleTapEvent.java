package org.openremote.web.console.event.tap;

import com.google.gwt.event.shared.GwtEvent;

/**
 * This event provides a high level semantic event to indicate a quick press
 * and release event on a component with restricted X, Y movement during event
 * @author rich
 *
 */
public class DoubleTapEvent extends GwtEvent<DoubleTapHandler> {
	private static final Type<DoubleTapHandler> TYPE = new Type<DoubleTapHandler>();
	public static int TAP_X_TOLERANCE = 30;
	public static int TAP_Y_TOLERANCE = 30;
	public static int MAX_TIME_BETWEEN_TAPS_MILLISECONDS = 500;
	private static int xPos;
	private static int yPos;
	private static Object source; 
	
	public DoubleTapEvent(int xPos, int yPos, Object source) {
			this.xPos = xPos;
			this.yPos = yPos;
			this.source = source;
	}
	
	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<DoubleTapHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(DoubleTapHandler handler) {
		handler.onDoubleTap(this);
	}

	public static Type<DoubleTapHandler> getType() {
		return TYPE;
	}

	public int getXPos() {
		return xPos;
	}
	
	public int getYPos() {
		return yPos;
	}
}
