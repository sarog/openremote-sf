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
package org.openremote.controller.daemon;

/**
 * TODO
 *
 * @author <a href = "mailto:juha@juhalindfors.com">Juha Lindfors</a>
 */
public enum IOModule
{
  SERIAL(0x00000101),
  PING(0xFFFFFFFF);




  // Instance Fields ------------------------------------------------------------------------------

  private int moduleID;


  // Constructors ---------------------------------------------------------------------------------

  private IOModule(int moduleID)
  {
    this.moduleID = moduleID;
  }


  // Instance Methods -----------------------------------------------------------------------------

  public String getModuleID()
  {
    return Integer.toHexString(moduleID).toUpperCase();
  }


  // Nested Enums ---------------------------------------------------------------------------------

  private final static String PING_PAYLOAD = "ARE YOU THERE";

  protected static enum PingProtocol
  {
    PING_MESSAGE(PING.getModuleID(), String.valueOf(PING_PAYLOAD.length()), PING_PAYLOAD),
    PING_RESPONSE("I AM HERE");


    // Instance Fields ----------------------------------------------------------------------------

    private String message = "";


    // Constructors -------------------------------------------------------------------------------

    PingProtocol(String... args)
    {
      StringBuilder builder = new StringBuilder(1024);

      for (String arg : args)
        builder.append(arg);

      message = builder.toString();
    }


    // Instance Methods ---------------------------------------------------------------------------

    public String getMessage()
    {
      return message;
    }

    public byte[] getBytes()
    {
      return getMessage().getBytes();
    }

    public int getLength()
    {
      return getMessage().length();
    }
  }
}
