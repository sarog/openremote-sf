package org.openremote.web.console.event;

import org.openremote.web.console.client.WebConsole;
import org.openremote.web.console.event.press.PressCancelEvent;
import org.openremote.web.console.event.press.PressEndEvent;
import org.openremote.web.console.event.press.PressMoveEvent;
import org.openremote.web.console.event.press.PressMoveReleaseHandlerImpl;
import org.openremote.web.console.event.press.PressStartEvent;
import org.openremote.web.console.util.BrowserUtils;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.RootPanel;

public class ConsoleUnitEventManager {
	private static ConsoleUnitEventManager instance;
	private HandlerManager eventBus;
	private PressMoveReleaseHandlerImpl pressMoveReleaseHandler;
	
	private ConsoleUnitEventManager() {
		eventBus = new HandlerManager(WebConsole.getConsoleUnit());
		pressMoveReleaseHandler = new PressMoveReleaseHandlerImpl();
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
		
		// Add Press handlers to capture Press Events
		eventBus.addHandler(PressStartEvent.getType(), pressMoveReleaseHandler);
		eventBus.addHandler(PressMoveEvent.getType(), pressMoveReleaseHandler);
		eventBus.addHandler(PressEndEvent.getType(), pressMoveReleaseHandler);
		eventBus.addHandler(PressCancelEvent.getType(), pressMoveReleaseHandler);
	}
   
   public HandlerManager getEventBus() {
   	return eventBus;
   }
   
   public PressMoveReleaseHandlerImpl getPressMoveReleaseHandler() {
   	return pressMoveReleaseHandler;
   }
   
   public static synchronized ConsoleUnitEventManager getInstance() {
      if ( instance == null) {
         instance = new ConsoleUnitEventManager();
      }
      return instance;
   }
}
