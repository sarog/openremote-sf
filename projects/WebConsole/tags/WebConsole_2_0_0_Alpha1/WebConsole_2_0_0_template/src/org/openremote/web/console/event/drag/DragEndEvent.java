package org.openremote.web.console.event.drag;

import org.openremote.web.console.event.press.PressEndEvent;

/**
 * This event defines a drag cancel event
 * @author rich
 *
 */
public class DragEndEvent extends DragEvent<DragEndHandler> {
	private static final Type<DragEndHandler> TYPE = new Type<DragEndHandler>();

	public DragEndEvent(PressEndEvent event) {
		xPos = event.getClientX();
		yPos = event.getClientY();
		source = event.getSource();
	}
	
	@Override
	public Type<DragEndHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(DragEndHandler handler) {
		handler.onDragEnd(this);
	}

	public static Type<DragEndHandler> getType() {
		return TYPE;
	}
}
