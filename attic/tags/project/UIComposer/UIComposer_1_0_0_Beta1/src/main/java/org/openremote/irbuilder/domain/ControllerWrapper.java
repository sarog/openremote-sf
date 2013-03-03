/*
 * OpenRemote, the Home of the Digital Home. Copyright 2008, OpenRemote Inc.
 * 
 * See the contributors.txt file in the distribution for a full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 3.0 of the License, or (at your option) any later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * You should have received a copy of the GNU General Public License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF site:
 * http://www.fsf.org.
 */
package org.openremote.irbuilder.domain;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.util.ArrayList;

/**
 * @author allen.wei
 */
@XStreamAlias("openremote")
public class ControllerWrapper {

   ArrayList<ControllerButton> buttons = new ArrayList<ControllerButton>();
   @XStreamAlias("events")
   EventsWrapper eventsWrapper;

   public EventsWrapper getEventsWrapper() {
      return eventsWrapper;
   }

   public void setEventsWrapper(EventsWrapper eventsWrapper) {
      this.eventsWrapper = eventsWrapper;
   }

   public ArrayList<ControllerButton> getButtons() {
      return buttons;
   }

   public void setButtons(ArrayList<ControllerButton> buttons) {
      this.buttons = buttons;
   }
}
