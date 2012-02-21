package org.openremote.web.console.event.hold;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.ui.Widget;

/**
 * This event provides an amalgamation of touchstart and mousedown events
 * so it can be used for mobile and desktop human interaction detection
 * @author rich
 *
 */
public class HoldEvent extends GwtEvent<HoldHandler> {
	private static final Type<HoldHandler> TYPE = new Type<HoldHandler>();
	public static final int MIN_HOLD_TIME_MILLISECONDS = 1000;
	int xPos;
	int yPos;
	Widget source;
	
	public HoldEvent(int xPos, int yPos, Widget source) {
			this.xPos = xPos;
			this.yPos = yPos;
			this.source = source;
	}
	
	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<HoldHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(HoldHandler handler) {
		handler.onHold(this);
	}

	public static Type<HoldHandler> getType() {
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
