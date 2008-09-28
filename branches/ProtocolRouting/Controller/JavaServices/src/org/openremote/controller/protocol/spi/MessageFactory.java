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
package org.openremote.controller.protocol.spi;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author <a href = "mailto:juha@juhalindfors.com">Juha Lindfors</a>
 */
public class MessageFactory
{

  // Constants ------------------------------------------------------------------------------------

  public final static String VERSION_1 = "1";

  public final static String HEADER_BLOCK = "header";
  public final static String OPENREMOTE_COMPONENT_BLOCK = "OpenRemote Component";
  public final static String PROTOCOL_GATEWAY_BLOCK = "Control Protocol Gateway";

  
  public final static String GATEWAY_REGISTRATION_MESSAGE_CLASS = "Registration.Gateway";


  public final static String X10_CONTROL_PROTOCOL = "X10";
  public final static String INSTEON_CONTROL_PROTOCOL = "INSTEON";
  public final static String KNX_CONTROL_PROTOCOL = "KNX";
  

  public final static String COMPONENT_NAME_PROPERTY = "ComponentName";

  public final static String PROTOCOL_GATEWAY_MODE_PROPERTY = "Mode";
  public final static String IPC_PROTOCOL_GATEWAY_IPADDRESS_PROPERTY = "IPAddress";
  public final static String IPC_PROTOCOL_GATEWAY_PORT_PROPERTY = "Port";

  public final static String X10_INVM_PROTOCOL_GATEWAY_COMMS_PROPERTY = "Port";
  public final static String X10_INVM_PROTOCOL_GATEWAY_MODULE_PROPERTY = "Module";
  

  // Class Members --------------------------------------------------------------------------------

  public static List<MandatoryHeader> getMandatoryHeaders(String serializationVersion)
  {
    ArrayList<MandatoryHeader> list = new ArrayList<MandatoryHeader>(5);
    list.add(MandatoryHeader.VERSION);
    list.add(MandatoryHeader.HOP);
    list.add(MandatoryHeader.UID);
    list.add(MandatoryHeader.SOURCE);
    list.add(MandatoryHeader.CLASS);

    return list;
  }
}
