package org.openremote.web.console.event.drag;

import org.openremote.web.console.event.press.PressCancelHandler;
import org.openremote.web.console.event.press.PressEvent;

/**
 * This event defines a drag cancel event
 * @author rich
 *
 */
public class DragCancelEvent extends DragEvent<DragCancelHandler> {
	private static final Type<DragCancelHandler> TYPE = new Type<DragCancelHandler>();

	public DragCancelEvent(PressEvent<PressCancelHandler> sourceEvent) {
		xPos = sourceEvent.getClientX();
		yPos = sourceEvent.getClientY();
		source = sourceEvent.getSource();
	}
	
	@Override
	public Type<DragCancelHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(DragCancelHandler handler) {
		handler.onDragCancel(this);
	}

	public static Type<DragCancelHandler> getType() {
		return TYPE;
	}
}
