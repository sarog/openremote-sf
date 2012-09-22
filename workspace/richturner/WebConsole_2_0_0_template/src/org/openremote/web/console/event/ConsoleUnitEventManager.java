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
