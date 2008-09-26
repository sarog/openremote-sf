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

import org.jboss.logging.Logger;
import org.openremote.controller.core.Bootstrap;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * TODO: Does various addess assignment and translation services.
 *
 * @author <a href="mailto:juha@juhalindfors.com">Juha Lindfors</a>
 */
public class AddressTable
{

  // Constants ------------------------------------------------------------------------------------

  public final static String LOG_CATEGORY = "CONTROL PROTOCOL ROUTING TABLE";

  public final static String GLOBAL_ADDRESS_PREFIX = "OpenRemote.GlobalAddress";


  // Class Members --------------------------------------------------------------------------------

  private final static Logger log = Logger.getLogger(Bootstrap.ROOT_LOG_CATEGORY + "." + LOG_CATEGORY);


  // Instance Fields ------------------------------------------------------------------------------

  private AtomicInteger deviceIdSequence = new AtomicInteger(1);

  private AtomicInteger addressSequence = new AtomicInteger(1);

  private Map<String, Message> addressTable = new ConcurrentHashMap<String, Message>();


  // MC Component Methods -------------------------------------------------------------------------

  public void start()
  {
    log.info("Control Protocol Routing Table Available.");
  }


  // Public Instance Methods ----------------------------------------------------------------------

  
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

    String globalAddress = GLOBAL_ADDRESS_PREFIX + "." + Integer.toString(address);

    addressTable.put(globalAddress, msg);

    log.info("Registered device at address '" + globalAddress + "': \n " + msg);
  }

  public String lookup(String domainAddress)
  {
    if (!addressTable.containsKey(domainAddress))
    {
      log.warn("Domain address '" + domainAddress + "' not found in address table.");

      return "/dev/null";
    }

    else
      return addressTable.get(domainAddress).getAddress();
  }
}
