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
package org.openremote.controller.commander;

import java.util.Properties;

import org.openremote.controller.spring.SpringContext;
import org.openremote.irbuilder.domain.Event;



/**
 * A factory for creating EventCommander objects.
 * 
 * @author Dan 2009-4-3
 */
public class EventCommanderFactory {
   
   /** The event commanders. */
   private Properties eventCommanders;
   
   /**
    * Gets the commander.
    * 
    * @param name the name
    * @param event the event
    * 
    * @return the commander
    */
   public EventCommander getCommander(String name,Event event){
      String builder = eventCommanders.getProperty(name);
      EventCommanderBuilder eventCommanderBuilder = (EventCommanderBuilder) SpringContext.getInstance().getBean(builder);
      return eventCommanderBuilder.build(event);
   }

   /**
    * Sets the event commanders.
    * 
    * @param eventCommanders the new event commanders
    */
   public void setEventCommanders(Properties eventCommanders) {
      this.eventCommanders = eventCommanders;
   }
   
   

}
