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
import java.net.ServerSocket;
import java.net.SocketException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.IOException;

import org.openremote.controller.Constants;
import org.openremote.controller.model.sensor.Sensor;
import org.openremote.controller.command.Command;
import org.openremote.controller.command.ExecutableCommand;
import org.openremote.controller.protocol.DeviceProtocol;
import org.openremote.controller.protocol.ReadCommand;
import org.openremote.controller.protocol.EventListener;
import org.openremote.controller.protocol.bus.Message;
import org.openremote.controller.protocol.ip.IPConnection;
import org.openremote.controller.protocol.ip.TCPIPConnection;
import org.openremote.controller.utils.Logger;


/**
 * TODO
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
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


    Integer listenerPort = properties.getOptionalInteger("listenerPort");

    Boolean pollingReader = properties.getMandatoryBoolean("pollingReader");

    InetSocketAddress socketAddress = new InetSocketAddress(ipAddress, socketPort);

    TCPIPConnection connection = getConnection(socketAddress);

    if (listenerPort != null)
    {
      return new TCPListener(listenerPort);
    }

    if (pollingReader)
    {
      return new TCPReader(connection, messages);
    }

    else
    {
      return new TCPWriter(connection, messages, responsePolicy);
    }
  }


  // Nested Classes -------------------------------------------------------------------------------

  private static class TCPWriter implements ExecutableCommand
  {

    private TCPIPConnection connection;
    private Set<Message> messages;
    private boolean waitForResponse = true;

    private TCPWriter(TCPIPConnection connection, Set<Message> messages,
                      IPConnection.Option responsePolicy)
    {

      this.connection = connection;
      this.messages = messages;

      waitForResponse = (responsePolicy != IPConnection.Option.RESPONSE_READ_NOTHING);
    }

    @Override public void send()
    {
      if (waitForResponse)
      {
        TCPReader.sendAndReceive(connection, messages);
      }

      else
      {
        try
        {
          for (Message m : messages)
          {
            connection.send(m);
          }
        }

        catch (Exception e)
        {
          e.printStackTrace();
        }
      }
    }
  }

  private static class TCPReader extends ReadCommand
  {

    private static String sendAndReceive(TCPIPConnection connection, Set<Message> messages)
    {
      StringBuffer sbuf = new StringBuffer();

      try
      {
        for (Message m : messages)
        {
          connection.send(m);

          Message response = connection.receive();

          sbuf.append(new String(response.getContent()));
        }
      }

      catch (Exception e)
      {
        e.printStackTrace();
      }

      return sbuf.toString();
    }

    private TCPIPConnection connection;
    private Set<Message> messages;

    private TCPReader(TCPIPConnection connection, Set<Message> messages)
    {
      this.connection = connection;
      this.messages = messages;
    }

    @Override public String read(Sensor s)
    {
      String rawResult = sendAndReceive(connection, messages);

      // Strip response from control characters (non-ASCII)...

      Pattern p = Pattern.compile("\\p{Cntrl}");
      Matcher m = p.matcher(rawResult);
      String regexResult = m.replaceAll("");

      if ("".equals(regexResult))
      {
         return Sensor.UNKNOWN_STATUS;
      }

      return regexResult;
    }
  }

  private static class TCPListener implements EventListener
  {
    private final static int SERVER_BACKLOG_QUEUE_SIZE = 50;

    private ServerSocket listeningServer;
    private InetSocketAddress bindAddress;

    private TCPListener(int port, InetAddress address)
    {
      if (port < 0 || port > 65535)
      {
        throw new Error("Illegal port number : port");
      }

      bindAddress = new InetSocketAddress(address, port);
    }

    private TCPListener(int port)
    {
      this(port, null);
    }

    @Override public void setSensor(Sensor s)
    {

    }

    @Override public void stop(Sensor s)
    {

    }

    private class WorkerThread implements Runnable
    {
      private volatile boolean running = true;

      @Override public void run()
      {
        try
        {
          listeningServer = new ServerSocket();
          listeningServer.bind(bindAddress);
        }

        catch (IOException e)
        {

        }

        catch (SecurityException e)
        {

        }

        catch (Throwable t)
        {

        }

        while (running)
        {
          Socket socket;

          try
          {
            socket = listeningServer.accept();
          }

          catch (SocketException e)
          {

          }

          catch (SocketTimeoutException e)
          {

          }
          
          catch (IOException e)
          {

          }



        }
      }
    }
  }
}
