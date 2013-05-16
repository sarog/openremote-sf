/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2013, OpenRemote Inc.
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
package org.openremote.controller.protocol.port;

import java.io.IOException;
import java.util.Map;

/**
 * The abstraction of a physical bus.
 * <p>
 * This interface describes the entry points of all the physical buses OpenRemote controller uses.
 * </p>
 */
public interface Port {
   /**
    * @param configuration
    */
   void configure(Map<String, Object> configuration) throws IOException, PortException;
   
   /**
    * Start the physical bus.
    * 
    */
   void start() throws IOException, PortException;

   /**
    * Stop the physical bus.
    * 
    * @throws InterruptedException
    */
   void stop() throws IOException, PortException;

   /**
    * Send a message to device(s) through the physical bus.
    * 
    * @param message
    *           The message to send.
    * @throws IOException
    */
   void send(Message message) throws IOException, PortException;

   /**
    * Receive a message from the physical bus. This method blocks until a message is received.
    * 
    * @return Expected message.
    * @throws IOException
    *            Message could not be received.
    */
   Message receive() throws IOException;
}
