package org.openremote.web.console.event.value;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.ui.Widget;

/**
 * This event provides a high level semantic event to indicate a quick press
 * and release event on a component with restricted X, Y movement during event
 * @author rich
 *
 */
public class ControllerValueChangeEvent extends GwtEvent<ControllerValueChangeHandler> {
	private static final Type<ControllerValueChangeHandler> TYPE = new Type<ControllerValueChangeHandler>();
	
	public ControllerValueChangeEvent() {

	}
	
	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<ControllerValueChangeHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ControllerValueChangeHandler handler) {
		handler.onControllerValueChange(this);
	}

	public static Type<ControllerValueChangeHandler> getType() {
		return TYPE;
	}
}
