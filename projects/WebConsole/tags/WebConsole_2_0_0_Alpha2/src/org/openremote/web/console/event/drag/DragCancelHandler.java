package org.openremote.web.console.event.drag;

import com.google.gwt.event.shared.EventHandler;

public interface DragCancelHandler extends EventHandler {
	void onDragCancel(DragCancelEvent event);
}
