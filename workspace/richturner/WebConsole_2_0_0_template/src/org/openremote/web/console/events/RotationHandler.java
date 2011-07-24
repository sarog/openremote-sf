package org.openremote.web.console.events;

import com.google.gwt.event.shared.EventHandler;

public interface RotationHandler extends EventHandler {
	void onRotate(RotationEvent event);
}
