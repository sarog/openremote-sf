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
package org.openremote.controller.event;

import java.util.Properties;

import org.openremote.controller.spring.SpringContext;
import org.openremote.irbuilder.domain.Event;
import org.w3c.dom.Element;


/**
 * A factory for creating Event objects.
 * 
 * @author Dan 2009-4-3
 */
public class EventFactory {
   
   /** The event builders. */
   private Properties eventBuilders;
   
   /**
    * Gets the event.
    * 
    * @param element the element
    * 
    * @return the event
    */
   public Event getEvent(Element element) {
      String name = element.getNodeName();
      String builder = eventBuilders.getProperty(name);
      EventBuilder eventBuilder = (EventBuilder) SpringContext.getInstance().getBean(builder);
      return eventBuilder.build(element);
   }

   /**
    * Sets the event builders.
    * 
    * @param eventBuilders the new event builders
    */
   public void setEventBuilders(Properties eventBuilders) {
      this.eventBuilders = eventBuilders;
   }
   

}
