package org.openremote.web.console.events;

import org.openremote.web.console.client.WebConsole;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

public class PressReleaseHandlerImpl implements PressHandler, ReleaseHandler, PressMoveHandler { 
	public boolean pressStarted = false;
	private PressEvent pressEvent;
	private PressMoveEvent pressMoveEvent;
	private ConsoleUnitEventManager eventManager;
	private WebConsole consoleModule;
	
	public PressReleaseHandlerImpl(ConsoleUnitEventManager eventManager) {
		this.eventManager = eventManager;
		consoleModule = this.eventManager.getConsoleModule();
	}
	
	@Override
	public void onPress(PressEvent event) {
		pressStarted = true;
		pressEvent = event;
	}

	@Override
	public void onRelease(ReleaseEvent event) {
		processPressRelease(event);
		reset();
	}

	@Override
	public void onPressMove(PressMoveEvent event) {
		pressMoveEvent = event;
	}
	
	/*
	 * Determine what type of interaction has occurred and
	 * fire the appropriate gesture event
	 */
	private void processPressRelease(ReleaseEvent event) {
		double duration = (event.getTime() - pressEvent.getTime()) / 1000;
		double moveDistanceX = 0;
		double moveDistanceY = 0;
		boolean moveOccurred = false;
		Widget pressedWidget = null;
		Widget releasedWidget = null;
		
		// Determine what objects were involved
		if (pressEvent.getSource() instanceof Widget) {
			pressedWidget = (Widget)pressEvent.getSource();
		}
		if (event.getSource() instanceof Widget) {
			releasedWidget = (Widget)event.getSource();
		}
		
		// Check if we have movement
		if (pressMoveEvent != null) {
			moveDistanceX = pressMoveEvent.getClientX() - pressEvent.getClientX();
			moveDistanceY = pressMoveEvent.getClientY() - pressEvent.getClientY();
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
		if (duration >= HoldEvent.MIN_HOLD_SECONDS) {
			consoleModule.getConsoleUnit().fireEvent(new HoldEvent(pressEvent.getClientX(), pressEvent.getClientY()));
			return;
		}
		
		// Assume tap event only if pressed and released widgets are the same
		Window.alert("TAP");		
	}
	
	private void reset() {
		pressEvent = null;
		pressMoveEvent = null;
		pressStarted = false;
	}
}
