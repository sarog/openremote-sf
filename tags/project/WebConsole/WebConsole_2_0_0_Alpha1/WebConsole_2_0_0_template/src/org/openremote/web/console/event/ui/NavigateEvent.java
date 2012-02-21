package org.openremote.web.console.event.ui;

import org.openremote.web.console.panel.entity.Navigate;
import com.google.gwt.event.shared.GwtEvent;

/**
 * Indicates that the screen view has changed on the controller
 *  
 * @author rich
 */
public class NavigateEvent extends GwtEvent<NavigateHandler> {
	private static final Type<NavigateHandler> TYPE = new Type<NavigateHandler>();
	private Navigate navigate;
	
	public NavigateEvent(Navigate navigate) {
		this.navigate = navigate;
	}
	
	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<NavigateHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(NavigateHandler handler) {
		handler.onNavigate(this);
	}

	public static Type<NavigateHandler> getType() {
		return TYPE;
	}
	
	public Navigate getNavigate() {
		return navigate;
	}
}
