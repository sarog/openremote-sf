package org.openremote.web.console.event.rotate;

import com.google.gwt.event.shared.GwtEvent;

/**
 * @author rich
 *
 */
public class RotationEvent extends GwtEvent<RotationHandler> {
	private static final Type<RotationHandler> TYPE = new Type<RotationHandler>();
	String orientation;
	int winWidth;
	int winHeight;
	
	public RotationEvent(String orientation, int winWidth, int winHeight) {
			this.orientation = orientation;
			this.winWidth = winWidth;
			this.winHeight = winHeight;
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
	
	public int getWindowWidth() {
		return winWidth;
	}
	
	public int getWindowHeight() {
		return winHeight;
	}
}