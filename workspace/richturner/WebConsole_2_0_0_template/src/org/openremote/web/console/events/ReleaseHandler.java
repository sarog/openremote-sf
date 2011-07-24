package org.openremote.web.console.events;

import com.google.gwt.event.shared.EventHandler;

public interface ReleaseHandler extends EventHandler {
	void onRelease(ReleaseEvent event);
}
