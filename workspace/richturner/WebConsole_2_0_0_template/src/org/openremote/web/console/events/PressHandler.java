package org.openremote.web.console.events;

import com.google.gwt.event.shared.EventHandler;

public interface PressHandler extends EventHandler {
	void onPress(PressEvent event);
}
