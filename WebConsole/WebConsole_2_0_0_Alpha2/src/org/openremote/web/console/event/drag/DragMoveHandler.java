package org.openremote.web.console.event.drag;

import com.google.gwt.event.shared.EventHandler;

public interface DragMoveHandler extends EventHandler {
	void onDragMove(DragMoveEvent event);
}
