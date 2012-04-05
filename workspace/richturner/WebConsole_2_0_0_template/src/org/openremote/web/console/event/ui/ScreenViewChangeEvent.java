package org.openremote.web.console.event.ui;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Indicates that the screen view has changed on the controller
 *  
 * @author rich
 */
public class ScreenViewChangeEvent extends GwtEvent<ScreenViewChangeHandler> {
	private static final Type<ScreenViewChangeHandler> TYPE = new Type<ScreenViewChangeHandler>();
	private int screenId;
	private int groupId;
	
	public ScreenViewChangeEvent(int screenId, int groupId) {
		this.screenId = screenId;
		this.groupId = groupId;
	}
	
	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<ScreenViewChangeHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ScreenViewChangeHandler handler) {
		handler.onScreenViewChange(this);
	}

	public static Type<ScreenViewChangeHandler> getType() {
		return TYPE;
	}
	
	public int getNewScreenId() {
		return screenId;
	}
	
	public int getNewGroupId() {
		return groupId;
	}
}
