package org.openremote.web.console.event.press;

import java.util.Date;

import org.openremote.web.console.client.WebConsole;
import org.openremote.web.console.client.unit.ConsoleDisplay;
import org.openremote.web.console.event.ConsoleUnitEventManager;
import org.openremote.web.console.event.drag.DragStartEvent;
import org.openremote.web.console.event.hold.HoldEvent;
import org.openremote.web.console.event.swipe.SwipeEvent;
import org.openremote.web.console.event.tap.TapEvent;
import org.openremote.web.console.widget.ConsoleComponent;
import org.openremote.web.console.widget.Draggable;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

public class PressMoveReleaseHandlerImpl implements PressStartHandler, PressEndHandler, PressMoveHandler, PressCancelHandler { 
	public boolean pressStarted = false;
	private PressStartEvent pressStartEvent;
	private PressMoveEvent pressMoveEvent;
	private ConsoleUnitEventManager eventManager;
	private WebConsole consoleModule;
	private Widget sourceComponent;
	private boolean eventHandled;
	private long lastPressTime;
	
	public PressMoveReleaseHandlerImpl(ConsoleUnitEventManager eventManager) {
		this.eventManager = eventManager;
		consoleModule = this.eventManager.getConsoleModule();
	}

	public void onPressStart(PressStartEvent event) {
		pressStarted = true;
		pressStartEvent = event;
		sourceComponent = event.getSource();
	}
	
	public void onPressEnd(PressEndEvent event) {
		if (!eventHandled) {
			processPressRelease(event);
		}
		reset();
	}

	public void onPressMove(PressMoveEvent event) {
		if(!pressStarted) {
			return;
		}
		pressMoveEvent = event;
		if (sourceComponent instanceof Draggable) {
			eventHandled = true;
			sourceComponent.fireEvent(new DragStartEvent(event));
		}
	}

	public void onPressCancel(PressCancelEvent event) {
		// TODO Auto-generated method stub
	}
	
	/*
	 * Determine what type of interaction has occurred and
	 * fire the appropriate gesture event
	 */
	private void processPressRelease(PressEndEvent event) {
		double duration = (event.getTime() - pressStartEvent.getTime()) / 1000;
		double moveDistanceX = 0;
		double moveDistanceY = 0;
		boolean moveOccurred = false;
		Widget pressedWidget = null;
		Widget releasedWidget = null;
		
		// Determine what objects were involved
		if (pressStartEvent.getSource() instanceof Widget) {
			pressedWidget = (Widget)pressStartEvent.getSource();
		}
		if (event.getSource() instanceof Widget) {
			releasedWidget = (Widget)event.getSource();
		}
		
		// Check if we have movement
		if (pressMoveEvent != null) {
			moveDistanceX = pressMoveEvent.getClientX() - pressStartEvent.getClientX();
			moveDistanceY = pressMoveEvent.getClientY() - pressStartEvent.getClientY();
			moveOccurred = true;
		}
		
		// Check for left right swipe gesture
		if (moveOccurred) {
			if (Math.abs(moveDistanceX) >= SwipeEvent.MIN_X_TOLERANCE && Math.abs(moveDistanceY) < SwipeEvent.MAX_Y_TOLERANCE) {
				String swipeDir = "RIGHT";
				if (moveDistanceX < 0) {
					swipeDir = "LEFT";
				}
				consoleModule.getConsoleUnit().fireEvent(new SwipeEvent(swipeDir));
				return;
			}
		}
		
		// Check for hold gesture
		if (duration >= HoldEvent.HOLD_TIME_TOLERANCE) {
			consoleModule.getConsoleUnit().fireEvent(new HoldEvent(pressStartEvent.getClientX(), pressStartEvent.getClientY()));
			return;
		}

		// Check for tap gesture
		boolean tapOccurred = false;
		if (pressMoveEvent != null) {
			if (Math.abs(pressMoveEvent.getClientX() - pressStartEvent.getClientX()) < TapEvent.TAP_X_TOLERANCE || Math.abs(pressMoveEvent.getClientY() - pressStartEvent.getClientY()) < TapEvent.TAP_Y_TOLERANCE) {
				tapOccurred = true;
			}
		} else {
			tapOccurred = true;
		}
		
		if (tapOccurred) { 
			// TODO Do something with tap event
		}
	}
	
	public void reset() {
		pressStartEvent = null;
		pressMoveEvent = null;
		sourceComponent = null;
		pressStarted = false;
		eventHandled = false;
	}
}
