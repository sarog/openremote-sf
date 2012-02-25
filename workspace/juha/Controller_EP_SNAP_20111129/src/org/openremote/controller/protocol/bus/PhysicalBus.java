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

import org.openremote.controller.protocol.knx.ip.KnxIpException;

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
   * @param srcAddr
   * @return An object that may be used by the caller.
   * @throws KnxIpException
   * @throws IOException
   * @throws InterruptedException
   */
  void start(Object inChannel, Object outChannel);

  /**
   * Stop the physical bus.
   *
   * @throws InterruptedException
   */
  void stop();

  /**
   * Send a message to device(s) through the physical bus.
   *
   * @param message
   *           The message to send.
   * @throws IOException
   */
  void send(Message message) throws IOException;

  /**
   * Receive a message from the physical bus. This method blocks until a message is received.
   *
   * @return Expected message.
   * @throws IOException
   *            Message could not be received.
   */
  Message receive() throws IOException;
  
}
