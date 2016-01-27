package org.openremote.web.console.event.ui;

import com.google.gwt.event.shared.GwtEvent;

/**
 * @author rich
 *
 */
public class WindowResizeEvent extends GwtEvent<WindowResizeHandler> {
	private static final Type<WindowResizeHandler> TYPE = new Type<WindowResizeHandler>();
	int winWidth;
	int winHeight;
	
	public WindowResizeEvent(int winWidth, int winHeight) {
			this.winWidth = winWidth;
			this.winHeight = winHeight;
	}
	
	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<WindowResizeHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(WindowResizeHandler handler) {
		handler.onWindowResize(this);
	}

	public static Type<WindowResizeHandler> getType() {
		return TYPE;
	}
	
	public int getWindowWidth() {
		return winWidth;
	}
	
	public int getWindowHeight() {
		return winHeight;
	}
}