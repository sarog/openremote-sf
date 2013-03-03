/* OpenRemote, the Home of the Digital Home.
 * Copyright 2008, OpenRemote Inc.
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
package org.openremote.irbuilder.domain;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.util.ArrayList;

/**
 * @author allen.wei
 */
@XStreamAlias("events")
public class EventsWrapper {

   ArrayList<KNXEvent> knxEvents = new ArrayList<KNXEvent>();
   ArrayList<X10Event> x10Events = new ArrayList<X10Event>();
   ArrayList<IREvent> irEvents = new ArrayList<IREvent>();

   public ArrayList<KNXEvent> getKnxEvents() {
      return knxEvents;
   }

   public void setKnxEvents(ArrayList<KNXEvent> knxEvents) {
      this.knxEvents = knxEvents;
   }

   public ArrayList<X10Event> getX10Events() {
      return x10Events;
   }

   public void setX10Events(ArrayList<X10Event> x10Events) {
      this.x10Events = x10Events;
   }

   public ArrayList<IREvent> getIrEvents() {
      return irEvents;
   }

   public void setIrEvents(ArrayList<IREvent> irEvents) {
      this.irEvents = irEvents;
   }
}
