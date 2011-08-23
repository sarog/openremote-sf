package org.openremote.web.console.event.drag;

import org.openremote.web.console.event.press.PressEvent;
import org.openremote.web.console.event.press.PressMoveHandler;

/**
 * This event defines press move event
 * @author rich
 *
 */
public class DragMoveEvent extends DragEvent<DragMoveHandler> {
	private static final Type<DragMoveHandler> TYPE = new Type<DragMoveHandler>();

	public DragMoveEvent(PressEvent<PressMoveHandler> sourceEvent) {
		xPos = sourceEvent.getClientX();
		yPos = sourceEvent.getClientY();
		source = sourceEvent.getSource();
	}
	
	@Override
	public Type<DragMoveHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(DragMoveHandler handler) {
		handler.onDragMove(this);
	}

	public static Type<DragMoveHandler> getType() {
		return TYPE;
	}
}
