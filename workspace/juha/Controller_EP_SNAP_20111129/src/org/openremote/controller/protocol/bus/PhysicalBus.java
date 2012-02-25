/*
 * OpenRemote, the Home of the Digital Home.
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
package org.openremote.controller.protocol.bus;

import java.io.IOException;


/**
 * The abstraction of a physical bus/gateway. <p>
 *
 * This interface describes the entry points of all the physical buses OpenRemote controller uses.
 */
public interface PhysicalBus
{
  /**
   * Start the physical bus.
   *
   * @param  inChannel      // TODO : should be in ctor
   * @param  outChannel     // TODO : should be in ctor
   */
  void start(Object inChannel, Object outChannel);

  /**
   * Stop the physical bus.
   */
  void stop();

  /**
   * Send a message to device(s) through the physical bus.
   *
   * @param   message  message to send to gateway
   *
   * @throws  IOException   if there was an I/O error when sending message
   *
   */
  void send(Message message) throws IOException;

  /**
   * Receive a message from the physical bus. This method blocks until a message is received.
   *
   * @return received message.
   *
   * @throws IOException    if there was an I/O error while receiving the message
   */
  Message receive() throws IOException;
  
}
