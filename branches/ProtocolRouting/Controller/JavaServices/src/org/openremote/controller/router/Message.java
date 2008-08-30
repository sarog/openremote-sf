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

/**
 *
 * @author <a href="mailto:juha@juhalindfors.com">Juha Lindfors</a>
 */
public class Message
{

  // Instance Fields ------------------------------------------------------------------------------

  private boolean isDeviceRegistrationMessage = false;


  // Constructors ---------------------------------------------------------------------------------

  public Message(String messageFormat)
  {
    if (messageFormat.contains("class = DeviceRegistration."))
    {
      isDeviceRegistrationMessage = true;

    }

  }


  // Instance Methods -----------------------------------------------------------------------------

  public boolean isDeviceRegistrationMessage()
  {
    return isDeviceRegistrationMessage;
  }

  
  public Address getAddress()
  {

    return null;
  }

  public void setAddress(Address destinationAddress)
  {


  }

  public void send()
  {
    
  }
}
