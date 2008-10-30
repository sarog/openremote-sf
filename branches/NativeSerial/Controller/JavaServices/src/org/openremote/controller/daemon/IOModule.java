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
  RAW_SERIAL  ("R_SERIAL"),
  CONTROL     ("_CONTROL");


  // Class Members ------------------------------------------------------------------------------

  /**
   * Returns message length field for message header as specified in the protocol -- an
   * uppercase hex value with leading '0X' including leading zeroes up to string of length 10.
   *
   * @param value integer value to translate
   *
   * @return a ten character long hex string in uppercase with leading zeroes, such as
   *         '0X0000DEAD', '0XCAFEBABE' or '0X00000005'
   */
  protected static String getMessageLength(int value)
  {
    String hexValue = Integer.toHexString(value).toUpperCase();

    // add leading zeroes....

    if (value <= 0xF)
      return "0X0000000" + hexValue;
    if (value <= 0xFF)
      return "0X000000" + hexValue;
    if (value <= 0xFFF)
      return "0X00000" + hexValue;
    if (value <= 0xFFFF)
      return "0X0000" + hexValue;
    if (value <= 0xFFFFF)
      return "0X000" + hexValue;
    if (value <= 0xFFFFFF)
      return "0X00" + hexValue;
    if (value <= 0xFFFFFFF)
      return "0X0" + hexValue;

    return "0X" + hexValue;
  }

  // Instance Fields ------------------------------------------------------------------------------

  private String moduleID;


  // Constructors ---------------------------------------------------------------------------------

  private IOModule(String moduleID)
  {
    this.moduleID = moduleID;
  }


  // Instance Methods -----------------------------------------------------------------------------

  public String getModuleID()
  {
    return moduleID;
  }


  // Nested Enums ---------------------------------------------------------------------------------

  private final static String PING_PAYLOAD = "ARE YOU THERE";
  private final static String KILL_PAYLOAD = "D1ED1ED1E";

  protected static enum ControlProtocol
  {
    PING_MESSAGE(CONTROL.getModuleID(), getMessageLength(PING_PAYLOAD.length()), PING_PAYLOAD),
    PING_RESPONSE("I AM HERE"),

    KILL_MESSAGE(CONTROL.getModuleID(), getMessageLength(KILL_PAYLOAD.length()), KILL_PAYLOAD),
    KILL_RESPONSE("GOODBYE CRUEL WORLD");



    // Instance Fields ----------------------------------------------------------------------------

    private String message = "";


    // Constructors -------------------------------------------------------------------------------

    ControlProtocol(String... args)
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
