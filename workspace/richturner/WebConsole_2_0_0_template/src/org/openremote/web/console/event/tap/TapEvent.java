package org.openremote.web.console.event.tap;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.ui.Widget;

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
	private int xPos;
	private int yPos;
	private Widget source;
	
	public TapEvent(int xPos, int yPos, Widget source) {
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
	
	public Widget getSource() {
		return source;
	}
}
