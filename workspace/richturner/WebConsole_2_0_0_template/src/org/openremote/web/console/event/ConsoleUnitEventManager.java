package org.openremote.web.console.event;

import org.openremote.web.console.client.WebConsole;
import org.openremote.web.console.event.press.PressCancelEvent;
import org.openremote.web.console.event.press.PressEndEvent;
import org.openremote.web.console.event.press.PressMoveEvent;
import org.openremote.web.console.event.press.PressMoveReleaseHandlerImpl;
import org.openremote.web.console.event.press.PressStartEvent;
import com.google.gwt.event.shared.HandlerManager;

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
