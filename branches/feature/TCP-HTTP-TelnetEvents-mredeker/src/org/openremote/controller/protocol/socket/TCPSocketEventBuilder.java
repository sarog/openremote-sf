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
package org.openremote.controller.protocol.socket;

import org.openremote.controller.event.Event;
import org.openremote.controller.event.EventBuilder;
import org.w3c.dom.Element;


/**
 * The Class SocketEventBuilder.
 *
 * @author Marcus 2009-4-26
 */
public class TCPSocketEventBuilder implements EventBuilder {

   /**
    * {@inheritDoc}
    */
   public Event build(Element element) {
      TCPSocketEvent irEvent = new TCPSocketEvent();
      irEvent.setCommand(element.getAttribute("command"));
      irEvent.setName(element.getAttribute("name"));
      irEvent.setIp(element.getAttribute("ip"));
      irEvent.setPort(element.getAttribute("port"));
      return irEvent;
   }

}
