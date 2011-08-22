package org.openremote.web.console.event;

import org.openremote.web.console.client.unit.ConsoleUnit;
import org.openremote.web.console.event.press.PressMoveReleaseHandlerImpl;
import org.openremote.web.console.util.BrowserUtils;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.RootPanel;

public class ConsoleUnitEventManager {
	private final HandlerManager eventBus;
	private PressMoveReleaseHandlerImpl pressMoveReleaseHandler;
	
	public ConsoleUnitEventManager(ConsoleUnit consoleUnit) {
		eventBus = new HandlerManager(consoleUnit);
		pressMoveReleaseHandler = new PressMoveReleaseHandlerImpl(consoleUnit);
		attachHandlers();
	}
	
	private void attachHandlers() {
		// Disable scrolling on mobile devices
		if (BrowserUtils.isMobile) {
			// Prevent touch moves
			RootPanel.get().addDomHandler(new TouchMoveHandler() {
				public void onTouchMove(TouchMoveEvent e) {
						e.preventDefault();
				}
			}, TouchMoveEvent.getType());
		}
	}
   
   public HandlerManager getEventBus() {
   	return eventBus;
   }
   
   public PressMoveReleaseHandlerImpl getPressMoveReleaseHandler() {
   	return pressMoveReleaseHandler;
   }
}
