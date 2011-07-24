package org.openremote.web.console.events;

import com.google.gwt.event.shared.EventHandler;

public interface HoldHandler extends EventHandler {
	void onHold(HoldEvent event);
}
