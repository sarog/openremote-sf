package org.openremote.web.console.events;

import com.google.gwt.event.shared.EventHandler;

public interface SwipeHandler extends EventHandler {
	void onSwipe(SwipeEvent event);
}
