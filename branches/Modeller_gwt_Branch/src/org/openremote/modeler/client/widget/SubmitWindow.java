/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2009, OpenRemote Inc.
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
package org.openremote.modeler.client.widget;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.widget.Window;

// TODO: Auto-generated Javadoc
/**
 * The Class SubmitForm.
 */
public class SubmitWindow extends Window {
   
   /** The submit listeners. */
   private List<Listener<AppEvent>> submitListeners = new ArrayList<Listener<AppEvent>>();

   /**
    * Adds the submit listener.
    * 
    * @param listener the listener
    */
   public void addSubmitListener(Listener<AppEvent> listener) {
      submitListeners.add(listener);
   }

   /**
    * Remote submit listener.
    * 
    * @param listener the listener
    */
   public void remoteSubmitListener(Listener<AppEvent> listener) {
      submitListeners.remove(listener);
   }

   /**
    * Fire submit listener.
    * 
    * @param event the event
    */
   protected void fireSubmitListener(AppEvent event) {
      for (Listener<AppEvent> listener : submitListeners) {
         listener.handleEvent(event);
      }
   }
}
