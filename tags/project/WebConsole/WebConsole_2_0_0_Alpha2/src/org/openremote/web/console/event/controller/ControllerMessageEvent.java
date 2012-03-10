package org.openremote.web.console.event.controller;

import org.openremote.web.console.controller.message.ControllerMessage;
import com.google.gwt.event.shared.GwtEvent;

/**
 * This event provides a high level semantic event to indicate a quick press
 * and release event on a component with restricted X, Y movement during event
 * @author rich
 *
 */
public class ControllerMessageEvent extends GwtEvent<ControllerMessageHandler> {
	private static final Type<ControllerMessageHandler> TYPE = new Type<ControllerMessageHandler>();
	private ControllerMessage message;
	
	public ControllerMessageEvent(ControllerMessage message) {
		this.message = message;
	}

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<ControllerMessageHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ControllerMessageHandler handler) {
		handler.onControllerMessage(this);
	}

	public static Type<ControllerMessageHandler> getType() {
		return TYPE;
	}
	
	public ControllerMessage getMessage() {
		return message;
	}
}
