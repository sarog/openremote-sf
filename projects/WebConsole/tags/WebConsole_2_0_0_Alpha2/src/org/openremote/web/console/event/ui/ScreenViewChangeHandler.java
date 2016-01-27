package org.openremote.web.console.event.ui;

import com.google.gwt.event.shared.EventHandler;

public interface ScreenViewChangeHandler extends EventHandler {
	void onScreenViewChange(ScreenViewChangeEvent event);
}
