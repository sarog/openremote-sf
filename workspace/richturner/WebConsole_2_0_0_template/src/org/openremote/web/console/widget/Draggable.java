package org.openremote.web.console.widget;

import org.openremote.web.console.event.drag.DragCancelHandler;
import org.openremote.web.console.event.drag.DragEndHandler;
import org.openremote.web.console.event.drag.DragMoveHandler;
import org.openremote.web.console.event.drag.DragStartHandler;

public interface Draggable extends DragStartHandler, DragMoveHandler, DragEndHandler, DragCancelHandler {

}
