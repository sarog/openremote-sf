package org.openremote.web.console.event.drag;

import com.google.gwt.event.shared.EventHandler;

public interface DragStartHandler extends EventHandler {
	void onDragStart(DragStartEvent event);
}
