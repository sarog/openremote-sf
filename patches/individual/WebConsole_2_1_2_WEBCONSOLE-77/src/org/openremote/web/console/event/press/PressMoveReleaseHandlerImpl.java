/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2012, OpenRemote Inc.
*
* See the contributors.txt file in the distribution for a
* full listing of individual contributors.
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as
* published by the Free Software Foundation, either version 3 of the
* License, or (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU Affero General Public License for more details.
*
* You should have received a copy of the GNU Affero General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*/
package org.openremote.web.console.event.press;

import org.openremote.web.console.client.WebConsole;
import org.openremote.web.console.event.ConsoleUnitEventManager;
import org.openremote.web.console.event.drag.DragCancelEvent;
import org.openremote.web.console.event.drag.DragEndEvent;
import org.openremote.web.console.event.drag.DragMoveEvent;
import org.openremote.web.console.event.drag.DragStartEvent;
import org.openremote.web.console.event.drag.Draggable;
import org.openremote.web.console.event.hold.HoldEvent;
import org.openremote.web.console.event.swipe.SwipeEvent;
import org.openremote.web.console.event.swipe.SwipeEvent.SwipeAxis;
import org.openremote.web.console.event.swipe.SwipeEvent.SwipeDirection;
import org.openremote.web.console.event.swipe.SwipeEvent.SwipeLimits;
import org.openremote.web.console.event.tap.DoubleTapEvent;
import org.openremote.web.console.event.tap.TapEvent;
import org.openremote.web.console.unit.ConsoleUnit;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.Widget;
/**
 * 
 *  
 * @author <a href="mailto:richard@openremote.org">Richard Turner</a>
 */
public class PressMoveReleaseHandlerImpl implements PressStartHandler, PressEndHandler, PressMoveHandler, PressCancelHandler { 
	public boolean pressStarted = false;
	private PressStartEvent pressStartEvent;
	private PressMoveEvent pressMoveEvent;
	private boolean eventHandled;
	private long lastTapTime;
	private Widget lastTappedWidget = null;
	private Widget pressedWidget = null;
	
	public PressMoveReleaseHandlerImpl() {
	}

	public void onPressStart(PressStartEvent event) {
		pressStarted = true;
		pressStartEvent = event;
		pressedWidget = event.getSource();
		if (pressedWidget instanceof Draggable) {
			pressedWidget.fireEvent(new DragStartEvent(event));
			eventHandled = true;
		}
	}

	public void onPressMove(PressMoveEvent event) {
		if(!pressStarted) {
			return;
		}
		pressMoveEvent = event;
		if (pressedWidget instanceof Draggable) {
			pressedWidget.fireEvent(new DragMoveEvent(event));
			eventHandled = true;
		}
	}
	
	public void onPressEnd(PressEndEvent event) {
		if (pressStarted) {
			pressedWidget.fireEvent(new DragEndEvent(event));
			if (!eventHandled) {
				processPressRelease(event);
			}
		}
		reset();
	}

	public void onPressCancel(PressCancelEvent event) {
		if (pressStarted) {
			pressedWidget.fireEvent(event);
			pressedWidget.fireEvent(new DragCancelEvent(event));
			if (!eventHandled) {
				processPressRelease(event);
			}
			reset();
		}
	}
	
	/*
	 * Determine what type of interaction has occurred and
	 * fire the appropriate gesture event
	 */
	private void processPressRelease(PressEvent<?> event) {
		HandlerManager eventBus = ConsoleUnitEventManager.getInstance().getEventBus();
		double duration = (event.getTime() - pressStartEvent.getTime());
		int moveDistanceX = 0;
		int moveDistanceY = 0;
		boolean moveOccurred = false;
		Widget releasedWidget = null; 
		boolean tapOccurred = false;
		boolean sameWidgetPressedAndReleased = false;
		
		// Determine what objects were involved
		if (pressStartEvent.getSource() instanceof Widget) {
			pressedWidget = (Widget)pressStartEvent.getSource();
		}
		
		if (event.getSource() instanceof Widget) {
			releasedWidget = (Widget)event.getSource();
		}
		
		if (pressedWidget == releasedWidget) {
			sameWidgetPressedAndReleased = true;
		}
		
		// Check if we have movement
		if (pressMoveEvent != null) {
			moveDistanceX = pressMoveEvent.getClientX() - pressStartEvent.getClientX();
			moveDistanceY = pressMoveEvent.getClientY() - pressStartEvent.getClientY();
			moveOccurred = true;
		}
		
		// If movement occurred determine if there is correct movement for a swipe event
		// and whether swipe was on specific widget or entire display
		if (moveOccurred) {
			SwipeEvent swipeEvent = null;
			
			// Check for swipe gesture on pressed widget
			if (sameWidgetPressedAndReleased && isMovementWithinWidgetBounds(pressedWidget)) {
				swipeEvent = checkAndCreateSwipeEvent(pressedWidget, moveDistanceX, moveDistanceY);				
			}
			//Check for swipe gesture on the console display
			if (swipeEvent == null) {
				ConsoleUnit consoleUnit = WebConsole.getConsoleUnit();
				if (consoleUnit != null) {
					swipeEvent = checkAndCreateSwipeEvent(consoleUnit.getConsoleDisplay(), moveDistanceX, moveDistanceY);
				}
			}
			
			// If a swipe event has been created then fire it
			if (swipeEvent != null) {
				eventBus.fireEvent(swipeEvent);
				return;
			}
		}
		
		// Gestures below can only be performed if same widget pressed and released
		if (!sameWidgetPressedAndReleased) {
			return;
		}
		
		// Check for hold gesture
		if (duration >= HoldEvent.MIN_HOLD_TIME_MILLISECONDS) {
			eventBus.fireEvent(new HoldEvent(pressStartEvent.getClientX(), pressStartEvent.getClientY(), pressedWidget));
			return;
		}

		// Check for tap or double tap gesture
		if (pressMoveEvent != null) {
			if (Math.abs(pressMoveEvent.getClientX() - pressStartEvent.getClientX()) < TapEvent.TAP_X_TOLERANCE || Math.abs(pressMoveEvent.getClientY() - pressStartEvent.getClientY()) < TapEvent.TAP_Y_TOLERANCE) {
				tapOccurred = true;
			}
		} else {
			tapOccurred = true;
		}		
		if (tapOccurred) {
			if (pressedWidget == null) {
				return;
			}
			if (event.getTime() - lastTapTime < DoubleTapEvent.MAX_TIME_BETWEEN_TAPS_MILLISECONDS && lastTappedWidget == pressedWidget) {
				pressedWidget.fireEvent(new DoubleTapEvent(pressStartEvent.getClientX(), pressStartEvent.getClientY(), pressedWidget));
			} else {
				pressedWidget.fireEvent(new TapEvent(pressStartEvent.getClientX(),pressStartEvent.getClientY(), pressedWidget));
			}
			lastTapTime = event.getTime();
			lastTappedWidget = pressedWidget;
			return;
		}
	}
	
	public void reset() {
		pressedWidget = null;
		pressStartEvent = null;
		pressMoveEvent = null;
		pressStarted = false;
		eventHandled = false;
	}
	
	public boolean isMovementWithinWidgetBounds(Widget pressedWidget) {
		boolean result = true;
		
		//Check horizontal limits
		result = pressedWidget.getAbsoluteLeft() > pressStartEvent.getClientX() ? false : true;
		result = (pressedWidget.getAbsoluteLeft() + pressedWidget.getOffsetWidth()) < pressStartEvent.getClientX() ? false : true;
		result = pressedWidget.getAbsoluteLeft() > pressMoveEvent.getClientX() ? false : true;
		result = (pressedWidget.getAbsoluteLeft() + pressedWidget.getOffsetWidth()) < pressMoveEvent.getClientX() ? false : true;
		
		// Check vertical limits
		result = pressedWidget.getAbsoluteTop() > pressStartEvent.getClientY() ? false : true;
		result = (pressedWidget.getAbsoluteTop() + pressedWidget.getOffsetHeight()) < pressStartEvent.getClientY() ? false : true;
		result = pressedWidget.getAbsoluteTop() > pressMoveEvent.getClientY() ? false : true;
		result = (pressedWidget.getAbsoluteTop() + pressedWidget.getOffsetHeight()) < pressMoveEvent.getClientY() ? false : true;
		
		return result;
	}
	
	public SwipeEvent checkAndCreateSwipeEvent(Widget widget, int moveDistanceX, int moveDistanceY) {	
		SwipeDirection direction = null;
		SwipeAxis axis = null;
		boolean swipeOccurred = false;
		SwipeEvent swipeEvent = null;
		
		SwipeLimits horizontalLimits = new SwipeEvent.SwipeLimits(widget, SwipeAxis.HORIZONTAL);
		SwipeLimits verticalLimits = new SwipeEvent.SwipeLimits(widget, SwipeAxis.VERTICAL);
		
		if (Math.abs(moveDistanceX) > horizontalLimits.primaryAxisMinDistance && Math.abs(moveDistanceY) < horizontalLimits.secondaryAxisMaxDistance) {
			// Horizontal swipe occurred
			direction = moveDistanceX > 0 ? SwipeDirection.RIGHT : SwipeDirection.LEFT;
			axis = SwipeAxis.HORIZONTAL;
			swipeOccurred = true;
		} else if (Math.abs(moveDistanceY) > verticalLimits.primaryAxisMinDistance && Math.abs(moveDistanceX) < verticalLimits.secondaryAxisMaxDistance) {
			// Vertical swipe occurred
			direction = moveDistanceX > 0 ? SwipeDirection.DOWN : SwipeDirection.UP;
			axis = SwipeAxis.VERTICAL;
			swipeOccurred = true;
		}
		
		if (swipeOccurred) {
			swipeEvent = new SwipeEvent(axis, direction, widget);
		}
		
		return swipeEvent;
	}
}
