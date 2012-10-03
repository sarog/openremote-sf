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

import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.io.BufferedInputStream;
import java.util.StringTokenizer;
import java.nio.ByteBuffer;

import junit.framework.Assert;

import org.jdom.Element;
import org.junit.Test;
import org.openremote.controller.command.Command;
import org.openremote.controller.protocol.socket.TCPSocketCommand;
import org.openremote.controller.protocol.socket.TCPSocketCommandBuilder;


/**
 * Unit tests for {@link org.openremote.controller.protocol.socket.TCPSocketCommandBuilder}.
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 *
 */
public class TCPSocketCommandBuilderTest
{
  private TCPSocketCommandBuilder builder = new TCPSocketCommandBuilder();


  /**
   * Tests for command based connection property management for keep-alive.
   *
   * @throws Exception  if any error occurs
   */
  @Test public void testMixOfKeepAliveAndCloseConnectionCommands() throws Exception
  {
    StringTCPServer server = new StringTCPServer(11110);

    try
    {
      server.start();

      TCPSocketCommand cmd = getOneWayKeepAliveCommand("name1", "localhost", "11110", "123");

      // send keep-alive command...

      cmd.send();

      waitForResponse(server);

      Assert.assertTrue(server.getResponse().equals("123"));
      Assert.assertTrue(server.isSocketOpen());


      // send connection close command...

      cmd = getOneWayCommand("name2", "localhost", "11110", "987");

      cmd.send();

      waitForResponse(server);

      Assert.assertTrue(server.getResponse().equals("987"));

      Thread.sleep(1000);

      Assert.assertTrue(!server.isSocketOpen());
    }
    
    finally
    {
      server.stop();
    }

  }


  /**
   * Tests keep-alive command sequence.
   *
   * @throws Exception if any error occurs
   */
  @Test public void testKeepAliveCommands() throws Exception
  {
    StringTCPServer server = new StringTCPServer(11111);

    try
    {
      server.start();

      TCPSocketCommand cmd = getOneWayKeepAliveCommand("name1", "localhost", "11111", "123");

      cmd.send();

      waitForResponse(server);

      Assert.assertTrue(server.getResponse().equals("123"));
      Assert.assertTrue(server.isSocketOpen());


      cmd = getOneWayKeepAliveCommand("name2", "localhost", "11111", "987");

      cmd.send();

      waitForResponse(server);

      Assert.assertTrue(server.getResponse().equals("987"));

      Thread.sleep(100);

      Assert.assertTrue(server.isSocketOpen());
    }
    finally
    {
      server.stop();
    }
  }


  /**
   * Tests new socket per request command policy.
   *
   * @throws Exception if any error occurs
   */
  @Test public void testConnectionCloseCommands() throws Exception
  {
    StringTCPServer server = new StringTCPServer(11115);

    try
    {
      server.start();

      TCPSocketCommand cmd = getOneWayCommand("name1", "localhost", "11115", "523");

      cmd.send();

      waitForResponse(server);

      Assert.assertTrue(server.getResponse().equals("523"));

      Thread.sleep(100);

      Assert.assertTrue(!server.isSocketOpen());


      cmd = getOneWayCommand("name2", "localhost", "11115", "985");

      cmd.send();

      waitForResponse(server);

      Assert.assertTrue(server.getResponse().equals("985"));

      Thread.sleep(100);

      Assert.assertTrue(!server.isSocketOpen());
    }
    finally
    {
      server.stop();
    }

  }


  /**
   * Tests the hex sending over ascii sending with 0x prefix.
   *
   * @throws Exception  if there's an error
   */
  @Test public void testHexCommands() throws Exception
  {
    HexTCPServer server = new HexTCPServer(11116);

    try
    {
      server.start();

      TCPSocketCommand cmd = getOneWayCommand("hex", "localhost", "11116", "0x 01");

      cmd.send();

      waitForResponse(server);

      Assert.assertTrue(server.getByteResponse() [0] == 1);

      Thread.sleep(100);

      Assert.assertTrue(!server.isSocketOpen());
    }
    finally
    {
      server.stop();
    }

  }



  /**
   * Test ${param}
   */
  @Test
  public void testSocketCommandWithParam() {
//      TCPSocketCommand cmd = getCommand("name", "192.168.0.1", "9090", "light1_${param}");

//      Assert.assertEquals(cmd.getIp(), "192.168.0.1");
//      Assert.assertEquals(cmd.getPort(), "9090");
//      Assert.assertEquals(cmd.getCommand(), "light1_255");
//      Assert.assertEquals(cmd.getName(), "testName");
  }




  // Helpers --------------------------------------------------------------------------------------

  /**
   * Lets server notify the test that it has received the sent payload
   *
   * @param server      server instance being used in the test
   *
   * @throws Exception  if error occurs
   */
  private void waitForResponse(StringTCPServer server) throws Exception
  {
    synchronized (server.getResponseMonitor())
    {
      server.getResponseMonitor().wait();
    }
  }


  /**
   * Constructs a one-way command definition that will not expect a response back from the server
   * and has no keep-alive bit set.
   *
   * @param name      command name
   * @param address   ip address
   * @param port      server port
   * @param command   command payload
   *
   * @return    command ready to send
   */
  private TCPSocketCommand getOneWayCommand(String name, String address, String port, String command)
  {
    Element ele = new Element("command");
    ele.setAttribute("id", "test");
    ele.setAttribute("protocol", "tcpSocket");
    ele.setAttribute(Command.DYNAMIC_VALUE_ATTR_NAME, "255");

    Element propName = new Element("property");
    propName.setAttribute("name", "name");
    propName.setAttribute("value", name);

    Element propAddr = new Element("property");
    propAddr.setAttribute("name", "ipAddress");
    propAddr.setAttribute("value", address);

    Element propPort = new Element("property");
    propPort.setAttribute("name", "port");
    propPort.setAttribute("value", port);

    Element propCommand = new Element("property");
    propCommand.setAttribute("name", "command");
    propCommand.setAttribute("value", command);

    Element waitForResp = new Element("property");
    waitForResp.setAttribute("name", "waitForResponse");
    waitForResp.setAttribute("value", "false");

    ele.addContent(propName);
    ele.addContent(propAddr);
    ele.addContent(propPort);
    ele.addContent(propCommand);
    ele.addContent(waitForResp);

    return (TCPSocketCommand) builder.build(ele);
  }

  /**
   * Constructs a one-way command definition that will not expect a response back from the server
   * and has keep-alive for the connection.
   *
   * @param name      command name
   * @param address   ip address
   * @param port      server port
   * @param command   command payload
   *
   * @return    command ready to send
   */
  private TCPSocketCommand getOneWayKeepAliveCommand(String name, String address, String port, String command)
  {
    Element ele = new Element("command");
    ele.setAttribute("id", "test");
    ele.setAttribute("protocol", "tcpSocket");
    ele.setAttribute(Command.DYNAMIC_VALUE_ATTR_NAME, "255");

    Element propName = new Element("property");
    propName.setAttribute("name", "name");
    propName.setAttribute("value", name);

    Element propAddr = new Element("property");
    propAddr.setAttribute("name", "ipAddress");
    propAddr.setAttribute("value", address);

    Element propPort = new Element("property");
    propPort.setAttribute("name", "port");
    propPort.setAttribute("value", port);

    Element propCommand = new Element("property");
    propCommand.setAttribute("name", "command");
    propCommand.setAttribute("value", command);

    Element keepAlive = new Element("property");
    keepAlive.setAttribute("name", "keepAlive");
    keepAlive.setAttribute("value", "true");

    Element waitForResp = new Element("property");
    waitForResp.setAttribute("name", "waitForResponse");
    waitForResp.setAttribute("value", "false");

    ele.addContent(propName);
    ele.addContent(propAddr);
    ele.addContent(propPort);
    ele.addContent(propCommand);
    ele.addContent(keepAlive);
    ele.addContent(waitForResp);
    
    return (TCPSocketCommand) builder.build(ele);
  }



  // Nested Classes -------------------------------------------------------------------------------


  /**
   * TCP/IP Server that expects binary payloads. Does not acknowledge or respond to incoming
   * connections.
   */
  private static class HexTCPServer extends StringTCPServer
  {

    HexTCPServer(int port)
    {
      super(port);
    }

    @Override protected void start() throws Exception
    {
      server = new ServerSocket(port);

      reader = new HexSocketReader();
      t = new Thread(reader);
      t.start();

      // start and return...

    }

    byte[] response;

    byte[] getByteResponse()
    {
      return response;
    }


    private class HexSocketReader extends SocketReader
    {
      @Override public void run()
      {
        while (running)
        {
          try
          {
            // block and wait for connecting client socket...

            Socket s = server.accept();

            BufferedInputStream in = new BufferedInputStream(s.getInputStream());

            int len = 0;

            isSocketOpen = s.isBound() && s.isConnected();

            ByteBuffer payload = ByteBuffer.allocate(20000);

            while (len != -1)
            {
              byte[] buffer = new byte[1024];

              len = in.read(buffer, 0, buffer.length);

              payload.put(buffer);
            }

            Thread.sleep(100);

            response = payload.array();

            synchronized (RESPONSE_MONITOR)
            {
              RESPONSE_MONITOR.notify();
            }

            isSocketOpen = false;   // s.isClosed() won't work if/when SO_LINGER is set on the client
          }

          catch (Throwable t)
          {
            t.printStackTrace();
          }
        }
      }
    }
  }



  /**
   * TCP/IP Server that expects text payloads. Does not acknowledge or respond to incoming
   * connections.
   */
  private static class StringTCPServer
  {
    protected int port;
    protected Thread t;
    protected SocketReader reader;
    protected ServerSocket server;

    private String response;

    private StringTCPServer(int port)
    {
      this.port = port;
    }


    protected void start() throws Exception
    {
      server = new ServerSocket(port);

      reader = new SocketReader();
      t = new Thread(reader);
      t.start();

      // start and return...
    }


    protected void stop() throws IOException
    {
      reader.running = false;
      server.close();
    }

    protected final static Object RESPONSE_MONITOR = new Object();

    private Object getResponseMonitor()
    {
      return RESPONSE_MONITOR;
    }

    private String getResponse()
    {
      return (response == null) ? "<null>" : response;
    }

    boolean isSocketOpen = false;

    public boolean isSocketOpen()
    {
      return isSocketOpen;
    }

    protected class SocketReader implements Runnable
    {
      final String PAYLOAD_DELIMITER = "\r";

      protected volatile boolean running = true;

      @Override public void run()
      {
        while (running)
        {
          try
          {
            // block and wait for connecting client socket...

            Socket s = server.accept();

            BufferedInputStream in = new BufferedInputStream(s.getInputStream());

            int len = 0;
            StringBuffer sbuf = new StringBuffer();

            isSocketOpen = s.isBound() && s.isConnected();

            while (len != -1)
            {
              byte[] buffer = new byte[1024];

              len = in.read(buffer, 0, buffer.length);

              String payload = new String(buffer);

              if (!payload.contains(PAYLOAD_DELIMITER))
              {
                sbuf.append(payload);
              }

              else if (!payload.endsWith(PAYLOAD_DELIMITER))
              {
                int lastIndex = payload.lastIndexOf(PAYLOAD_DELIMITER);

                String remainder = payload.substring(payload.lastIndexOf(PAYLOAD_DELIMITER), payload.length());
                String complete = payload.substring(0, lastIndex);

                StringTokenizer tokenizer = new StringTokenizer(complete, PAYLOAD_DELIMITER);

                while (tokenizer.hasMoreTokens())
                {
                  Thread.sleep(100);

                  String msg = tokenizer.nextToken();

                  response = msg;

                  synchronized (RESPONSE_MONITOR)
                  {
                    RESPONSE_MONITOR.notify();
                  }
                }

                sbuf = new StringBuffer(remainder);
              }

              else
              {
                StringTokenizer tokenizer = new StringTokenizer(payload, PAYLOAD_DELIMITER);

                while (tokenizer.hasMoreTokens())
                {
                  Thread.sleep(100);

                  String msg = tokenizer.nextToken();

                  response = msg;

                  RESPONSE_MONITOR.notify();
                }
              }
            }

            isSocketOpen = false;
            
            // isSocketOpen = s.isClosed(); -- won't be closed as long as we have SO_LINGER on client
          }

          catch (Throwable t)
          {
            t.printStackTrace();
          }

        }
      }
    }

    @Override public String toString()
    {
      return "Port " + port;
    }
  }

}
