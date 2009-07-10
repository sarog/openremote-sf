/* OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2009, OpenRemote Inc.
 * 
 * See the contributors.txt file in the distribution for a
 * full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 3.0 of
 * the License, or (at your option) any later version.
 * 
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.openremote.modeler.client.widget;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.widget.Window;

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
