package org.openremote.web.console.event.press;

import com.google.gwt.event.shared.EventHandler;

public interface PressMoveHandler extends EventHandler {
	void onPressMove(PressMoveEvent event);
}
