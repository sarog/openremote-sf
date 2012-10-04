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
package org.openremote.controller.protocol.socket;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Set;

import org.openremote.controller.Constants;
import org.openremote.controller.command.Command;
import org.openremote.controller.protocol.DeviceProtocol;
import org.openremote.controller.protocol.bus.Message;
import org.openremote.controller.protocol.ip.IPConnection;
import org.openremote.controller.protocol.ip.TCPIPConnection;
import org.openremote.controller.utils.Logger;


/**
 *
 * @author Marcus 2009-4-26
 */
public class TCPSocketCommandBuilder extends TCPIPConnection
{
  protected static Logger log = Logger.getLogger(Constants.CONTROLLER_PROTOCOL_LOG_CATEGORY + "tcp");


  static Set<DeviceProtocol.Option> getProtocolOptions()
  {
    Set<DeviceProtocol.Option> options = new HashSet<DeviceProtocol.Option>(3);

    options.add(DeviceProtocol.Option.ENABLE_PIPED_MESSAGES);
    options.add(DeviceProtocol.Option.INCLUDE_CR_AT_END_OF_MESSAGE);
    options.add(DeviceProtocol.Option.ENABLE_HEX_STRING_MESSAGES);

    return options;
  }

  static Set<IPConnection.Option> getIPConnectionOptions()
  {
    Set<IPConnection.Option> options = new HashSet<IPConnection.Option>(3);

    return options;
  }


  // Constructors ---------------------------------------------------------------------------------

  TCPSocketCommandBuilder()
  {
    super(getIPConnectionOptions(), getProtocolOptions());
  }


  // Implements DeviceProtocol --------------------------------------------------------------------

  @Override public Command createIPCommand(Properties properties, Set<Message> messages,
                                           IPConnection.Option responsePolicy)
  {
    String commandName = properties.getMandatoryProperty("name");

    int socketPort = properties.getMandatoryNumber("port").intValue();

    InetAddress ipAddress = properties.getMandatoryInetAddress("ipAddress");

    InetSocketAddress socketAddress = new InetSocketAddress(ipAddress, socketPort);

    TCPIPConnection connection = getConnection(socketAddress);

    return new TCPSocketCommand(commandName, messages, connection, responsePolicy);
  }

}
