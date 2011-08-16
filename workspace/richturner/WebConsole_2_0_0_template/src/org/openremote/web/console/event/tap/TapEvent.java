package org.openremote.web.console.event.tap;

import com.google.gwt.event.shared.GwtEvent;

/**
 * This event provides a high level semantic event to indicate a quick press
 * and release event on a component with restricted X, Y movement during event
 * @author rich
 *
 */
public class TapEvent extends GwtEvent<TapHandler> {
	private static final Type<TapHandler> TYPE = new Type<TapHandler>();
	public static int TAP_X_TOLERANCE = 30;
	public static int TAP_Y_TOLERANCE = 30;
	private static int xPos;
	private static int yPos;
	private static Object source; 
	
	public TapEvent(int xPos, int yPos, Object source) {
			this.xPos = xPos;
			this.yPos = yPos;
			this.source = source;
	}
	
	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<TapHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(TapHandler handler) {
		handler.onTap(this);
	}

	public static Type<TapHandler> getType() {
		return TYPE;
	}

	public int getXPos() {
		return xPos;
	}
	
	public int getYPos() {
		return yPos;
	}
}
