package org.openremote.web.console.event.drag;

import com.google.gwt.event.shared.EventHandler;

public interface DragEndHandler extends EventHandler {
	void onDragEnd(DragEndEvent event);
}
