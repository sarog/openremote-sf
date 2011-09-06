package org.openremote.web.console.event.value;

import com.google.gwt.event.shared.EventHandler;

public interface ControllerValueChangeHandler extends EventHandler {
	void onControllerValueChange(ControllerValueChangeEvent event);
}
