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
 * TODO: this class manages the serial socket protocol between the Java process and native I/O daemon.
 *
 * @author <a href = "mailto:juha@juhalindfors.com">Juha Lindfors</a>
 */
public class SerialProtocol
{

  // Constants ------------------------------------------------------------------------------------

  private final static String OPEN_PORT_COMMAND = "OPEN PORT";


  // Enums ----------------------------------------------------------------------------------------

  public enum Parity
  {
    EVEN ("E"),
    ODD  ("O"),
    NONE ("N");

    private String serialFormat;

    private Parity(String serialFormat)
    {
      this.serialFormat = serialFormat;
    }

    private String getSerialFormat()
    {
      return serialFormat;
    }
  }

  public enum StopBits
  {
    ONE ("1"),
    TWO ("2"),
    ONE_HALF ("9");

    private String serialFormat;

    private StopBits(String serialFormat)
    {
      this.serialFormat = serialFormat;
    }

    private String getSerialFormat()
    {
      return serialFormat;
    }
  }

  public enum DataBits
  {
    FIVE  ("5"),
    SIX   ("6"),
    SEVEN ("7"),
    EIGHT ("8");

    private String serialFormat;

    private DataBits(String serialFormat)
    {
      this.serialFormat = serialFormat;
    }

    public String getSerialFormat()
    {
      return serialFormat;
    }
  }

  public enum FlowControl
  {
    SOFTWARE,
    HARDWARE
  }


  // Class Members --------------------------------------------------------------------------------

  public static byte[] createOpenPortMessage(String portID, int baudrate, DataBits databits,
                                             Parity parity, StopBits stopBits)
  {
    byte separator = 0;

    StringBuilder builder = new StringBuilder(1024);

    builder.append(OPEN_PORT_COMMAND);
    builder.append(separator);

    return builder.toString().getBytes();
  }

}
