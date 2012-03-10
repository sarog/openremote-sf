package org.openremote.web.console.event.swipe;

import com.google.gwt.event.shared.EventHandler;

public interface SwipeHandler extends EventHandler {
	void onSwipe(SwipeEvent event);
}
