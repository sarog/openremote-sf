package org.openremote.web.console.event.value;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.ui.Widget;

/**
 * This event provides a high level semantic event to indicate a quick press
 * and release event on a component with restricted X, Y movement during event
 * @author rich
 *
 */
public class UiValueChangeEvent extends GwtEvent<UiValueChangeHandler> {
	private static final Type<UiValueChangeHandler> TYPE = new Type<UiValueChangeHandler>();
	
	public UiValueChangeEvent() {

	}
	
	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<UiValueChangeHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(UiValueChangeHandler handler) {
		handler.onUiValueChange(this);
	}

	public static Type<UiValueChangeHandler> getType() {
		return TYPE;
	}
}
