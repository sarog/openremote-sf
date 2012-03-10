package org.openremote.web.console.event.ui;

import org.openremote.web.console.widget.InteractiveConsoleComponent;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Indicates that the screen view has changed on the controller
 *  
 * @author rich
 */
public class AnimationEndEvent extends GwtEvent<AnimationEndHandler> {
	private static final Type<AnimationEndHandler> TYPE = new Type<AnimationEndHandler>();
	
	public AnimationEndEvent() {}
	
	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<AnimationEndHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(AnimationEndHandler handler) {
		handler.onAnimationEnd();
	}

	public static Type<AnimationEndHandler> getType() {
		return TYPE;
	}
}
