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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.openremote.controller.Constants;
import org.openremote.controller.command.Command;
import org.openremote.controller.protocol.DeviceProtocol;
import org.openremote.controller.protocol.bus.Message;
import org.openremote.controller.protocol.bus.PhysicalBus;
import org.openremote.controller.utils.Logger;


/**
 *
 * @author Marcus 2009-4-26
 */
public class TCPSocketCommandBuilder extends DeviceProtocol
{
  protected static Logger log = Logger.getLogger(Constants.CONTROLLER_PROTOCOL_LOG_CATEGORY + "tcp");


  // Constructors ---------------------------------------------------------------------------------

  TCPSocketCommandBuilder()
  {
    super(
        DeviceProtocol.Option.ENABLE_PIPED_MESSAGES,
        DeviceProtocol.Option.INCLUDE_CR_AT_END_OF_MESSAGE,
        DeviceProtocol.Option.ENABLE_HEX_STRING_MESSAGES
    );
  }


  // Implements DeviceProtocol --------------------------------------------------------------------

  @Override public Command create(Properties properties, Set<Message> messages)
  {
    Boolean keepAlive = false;
    Boolean waitForResponse = true;

    String commandName = properties.getMandatoryProperty("name");
    int socketPort = properties.getMandatoryNumber("port").intValue();
    InetAddress ipAddress = properties.getMandatoryInetAddress("ipAddress");

    Boolean b = properties.getOptionalBoolean("keepAlive");

    if (b != null)
    {
      keepAlive = b;
    }

    b = properties.getOptionalBoolean("waitForResponse");

    if (b != null)
    {
      waitForResponse = b;
    }

    InetSocketAddress socketAddress = new InetSocketAddress(ipAddress, socketPort);

    TCPConnection connection = getConnection(socketAddress, keepAlive);

    return new TCPSocketCommand(commandName, messages, connection, waitForResponse);
  }


  private static Map<InetSocketAddress, TCPConnection> connections = new HashMap<InetSocketAddress, TCPConnection>();

  private final static Object MUTEX = new Object();

  private TCPConnection getConnection(InetSocketAddress socket, boolean keepAlive)
  {
    if (connections.keySet().contains(socket))
    {
      TCPConnection connection = connections.get(socket);

      connection.setKeepAlive(keepAlive);

      return connection;
    }

    else
    {
      log.info("Building connection to ''{0}'', keepAlive : {1}", socket, keepAlive);

      synchronized (MUTEX)
      {
        TCPConnection bus = new TCPConnection(socket, keepAlive);

        connections.put(socket, bus);

        bus.start(null, null);

        return bus;
      }
    }
  }


  // Nested Classes -------------------------------------------------------------------------------
  

  protected static class TCPConnection implements PhysicalBus
  {
    private InetSocketAddress socketAddress;
    private BufferedOutputStream output;
    private BufferedInputStream input;
    private Boolean keepAlive = false;
    private Socket clientSocket = null;

    private TCPConnection(InetSocketAddress socketAddress, Boolean keepAlive)
    {
      this.socketAddress = socketAddress;
      this.keepAlive = keepAlive;
    }

    private void setKeepAlive(Boolean b)
    {
      this.keepAlive = b;
    }

    @Override public void start(Object inSocket, Object outSocket)
    {
      try
      {
        clientSocket = new Socket();
        clientSocket.setSoLinger(true, 30);
        clientSocket.setReuseAddress(true);

        clientSocket.connect(socketAddress);
        
        output = new BufferedOutputStream(clientSocket.getOutputStream());
        input = new BufferedInputStream(clientSocket.getInputStream());
      }

      catch (IOException e)
      {
        e.printStackTrace();
      }
    }

    @Override public void stop()
    {

    }

    @Override public void send(Message m) throws IOException
    {
      byte[] payload = m.getContent();

      output.write(payload);

      output.flush();

      synchronized (MUTEX)
      {
        if (!keepAlive)
        {
          output.close();

          clientSocket.close();

          connections.remove(socketAddress);

          log.info("Closed Connection ''{0}''", socketAddress);
        }
      }

    }

    @Override public Message receive() throws IOException
    {
      int len = 0;
      StringBuffer sbuf = new StringBuffer();

      while (len != -1)
      {
        byte[] buffer = new byte[1024];
        len = input.read(buffer, 0, buffer.length);

        sbuf.append(new String(buffer));
      }

      return new Message(sbuf.toString().getBytes());
    }

    @Override public String toString()
    {
      return "Connection '" + socketAddress + "', keepAlive = " + keepAlive;
    }
  }
}
