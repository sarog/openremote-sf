/*
* OpenRemote, the Home of the Digital Home.
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
package org.openremote.controller.router;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.HashMap;

/**
 *
 * @author <a href="mailto:juha@juhalindfors.com">Juha Lindfors</a>
 */
public class AddressTable
{


  // Instance Fields ------------------------------------------------------------------------------

  private AtomicInteger deviceIdSequence = new AtomicInteger(1);

  private AtomicInteger addressSequence = new AtomicInteger(1);

  private Map<Integer, Message> addressTable = new ConcurrentHashMap<Integer, Message>();


  // Public Instance Methods ----------------------------------------------------------------------

  public void start()
  {
    System.out.println("Control Protocol Address Table Available.");
  }
  
  public String assignDeviceID()
  {
    int id = deviceIdSequence.getAndIncrement();

    if (id > 0xFFFF)
      throw new Error("Out of device IDs");
    
    String prefix = "";

    if (id <= 0xF)
      prefix = "000";
    else if (id <= 0xFF)
      prefix = "00";
    else if (id <= 0xFFF)
      prefix = "0";

    return prefix + Integer.toHexString(id).toUpperCase();
  }

  public void addDevice(Message msg)
  {
    int address = addressSequence.getAndIncrement();

    addressTable.put(address, msg);

    System.out.println("Device registered!");
  }
}
