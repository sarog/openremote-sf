package org.openremote.web.console.event.swipe;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.ui.Widget;

/**
 * This is a Swipe event and has an axis (horizontal, vertical) and direction (up, down, left, right)
 * @author rich
 */
public class SwipeEvent extends GwtEvent<SwipeHandler> {
	private static final Type<SwipeHandler> TYPE = new Type<SwipeHandler>();
	private SwipeDirection direction;
	private SwipeAxis axis;
	private Widget source;
	
	public static enum SwipeAxis {
		HORIZONTAL,
		VERTICAL;
	}
	
	public static enum SwipeDirection {
		LEFT,
		RIGHT,
		UP,
		DOWN;
		
		public static SwipeDirection enumValueOf(String direction) {
			SwipeDirection result = null;
			try {
			   result = SwipeDirection.valueOf(direction.toUpperCase());
			} catch (Exception e) {
				if (direction.equalsIgnoreCase("swipe-left-to-right")) {
					result = SwipeDirection.RIGHT;
				} else if (direction.equalsIgnoreCase("swipe-right-to-left")) {
					result = SwipeDirection.LEFT;
				} else if (direction.equalsIgnoreCase("swipe-bottom-to-top")) {
					result = SwipeDirection.UP;
				} else if (direction.equalsIgnoreCase("swipe-top-to-bottom")) {
					result = SwipeDirection.DOWN;
				}
			}
			return result;
		}
	}
	
	public SwipeEvent(SwipeAxis axis, SwipeDirection direction, Widget source) {
		this.axis = axis;
		this.direction = direction;
	}
	
	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<SwipeHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(SwipeHandler handler) {
		handler.onSwipe(this);
	}

	public static Type<SwipeHandler> getType() {
		return TYPE;
	}

	public SwipeDirection getDirection() {
		return direction;
	}
	
	public SwipeAxis getAxis() {
		return axis;
	}
	
	public Widget getSource() {
		return source;
	}
	
	public static final class SwipeLimits {
		private static final double PRIMARY_DISTANCE_RATIO = 0.7;
		private static final double SECONDARY_DISTANCE_RATIO = 0.3;
		private static final int MIN_SECONDARY_DISTANCE_PIXELS = 30;
		private static final int MAX_SECONDARY_DISTANCE_PIXELS = 80;
		public int primaryAxisMinDistance;
		public int secondaryAxisMaxDistance;
		
		public SwipeLimits(Widget sourceWidget, SwipeAxis axis) {
			int widgetWidth = sourceWidget.getOffsetWidth();
			int widgetHeight = sourceWidget.getOffsetHeight();
			int widgetPrimary = 0;
			int widgetSecondary = 0;
			
			switch(axis) {
				case HORIZONTAL:
					widgetPrimary = widgetWidth;
					widgetSecondary = widgetHeight;
					break;
				case VERTICAL:
					widgetPrimary = widgetHeight;
					widgetSecondary = widgetWidth;
			}
			primaryAxisMinDistance = (int)(PRIMARY_DISTANCE_RATIO * widgetPrimary);
			secondaryAxisMaxDistance = (int)(SECONDARY_DISTANCE_RATIO * widgetSecondary);
			secondaryAxisMaxDistance = secondaryAxisMaxDistance < MIN_SECONDARY_DISTANCE_PIXELS ? MIN_SECONDARY_DISTANCE_PIXELS : secondaryAxisMaxDistance;
			secondaryAxisMaxDistance = secondaryAxisMaxDistance > MAX_SECONDARY_DISTANCE_PIXELS ? MAX_SECONDARY_DISTANCE_PIXELS : secondaryAxisMaxDistance;
		}
	}
}
