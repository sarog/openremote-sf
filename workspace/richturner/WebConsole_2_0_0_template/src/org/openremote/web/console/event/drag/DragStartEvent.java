package org.openremote.web.console.event.drag;

import org.openremote.web.console.event.press.PressEvent;
import org.openremote.web.console.event.press.PressStartHandler;

/**
 * This event defines press move event
 * @author rich
 *
 */
public class DragStartEvent extends DragEvent<DragStartHandler> {
	private static final Type<DragStartHandler> TYPE = new Type<DragStartHandler>();

	public DragStartEvent(PressEvent<PressStartHandler> sourceEvent) {
		xPos = sourceEvent.getClientX();
		yPos = sourceEvent.getClientY();
		source = sourceEvent.getSource();
	}
	
	@Override
	public Type<DragStartHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(DragStartHandler handler) {
		handler.onDragStart(this);
	}

	public static Type<DragStartHandler> getType() {
		return TYPE;
	}
}
