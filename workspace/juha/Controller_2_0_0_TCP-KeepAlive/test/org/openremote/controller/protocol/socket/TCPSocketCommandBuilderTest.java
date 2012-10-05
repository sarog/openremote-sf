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
import java.net.SocketException;
import java.io.IOException;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.util.StringTokenizer;
import java.nio.ByteBuffer;

import junit.framework.Assert;

import org.jdom.Element;
import org.junit.Test;
import org.openremote.controller.command.Command;
import org.openremote.controller.command.ExecutableCommand;
import org.openremote.controller.protocol.socket.TCPSocketCommandBuilder;
import org.openremote.controller.protocol.ReadCommand;


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

      ExecutableCommand cmd = getOneWayKeepAliveCommand("name1", "localhost", "11110", "123");

      // send keep-alive command...

      cmd.send();

      waitForResponse(server);

      Assert.assertTrue(server.getReceivedPayload().equals("123"));
      Assert.assertTrue(server.isSocketOpen());




      cmd = getOneWayStickyKeepAliveCommand("sticky", "localhost", "11110", "sticky");

      // send keep-alive command...

      cmd.send();

      waitForResponse(server);

      Assert.assertTrue(server.getReceivedPayload().equals("sticky"));
      Assert.assertTrue(server.isSocketOpen());



      // send connection close command...

      cmd = getOneWayCommand("name2", "localhost", "11110", "98765");

      cmd.send();

      waitForResponse(server);

      Assert.assertTrue(server.getReceivedPayload().equals("98765"));

      Thread.sleep(100);

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

      ExecutableCommand cmd = getOneWayKeepAliveCommand("name1", "localhost", "11111", "123");

      cmd.send();

      waitForResponse(server);

      Assert.assertTrue(server.getReceivedPayload().equals("123"));
      Assert.assertTrue(server.isSocketOpen());


      cmd = getOneWayKeepAliveCommand("name2", "localhost", "11111", "987");

      cmd.send();

      waitForResponse(server);

      Assert.assertTrue(server.getReceivedPayload().equals("987"));

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

      ExecutableCommand cmd = getOneWayCommand("name1", "localhost", "11115", "523");

      cmd.send();

      waitForResponse(server);

      Assert.assertTrue(server.getReceivedPayload().equals("523"));

      Thread.sleep(100);

      Assert.assertTrue(!server.isSocketOpen());


      cmd = getOneWayCommand("name2", "localhost", "11115", "985");

      cmd.send();

      waitForResponse(server);

      Assert.assertTrue(server.getReceivedPayload().equals("985"));

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

      ExecutableCommand cmd = getOneWayCommand("hex", "localhost", "11116", "0x 01");

      cmd.send();

      waitForResponse(server);

      Assert.assertTrue(server.getReceivedBytes() [0] == 1);

      Thread.sleep(100);

      Assert.assertTrue(!server.isSocketOpen());
    }
    finally
    {
      server.stop();
    }

  }



  /**
   * Tests send and receive on text based server that expects '\r' delimited messages
   * and will close connection after responding.
   *
   * @throws Exception  if there's an error
   */
  @Test public void testReadUntilCloseOnSocketClose() throws Exception
  {
    RespondingStringTCPServer server = new RespondingStringTCPServer(
        11117, RespondingStringTCPServer.SocketPolicy.CLOSE_AFTER_RESPONSE
    );

    try
    {
      server.start();

      ReadCommand cmd = getReadUntilCloseResponseCommand("resp1", "localhost", "11117", "GET 1");

      String response = cmd.read(null);

      Assert.assertTrue("GOT " + response, response.equals("1"));

      Thread.sleep(100);

      Assert.assertTrue(!server.isSocketOpen());

      cmd = getReadUntilCloseResponseCommand("resp2", "localhost", "11117", "GET 123456789");

      response = cmd.read(null);

      Assert.assertTrue("GOT " + response, response.equals("123456789"));

      Thread.sleep(100);

      Assert.assertTrue(!server.isSocketOpen());

    }
    
    finally
    {
      server.stop();
    }
  }

  /**
   * Tests send and receive with keepalive on text based server that expects '\r' delimited messages
   * and will close connection after responding.
   *
   * @throws Exception  if there's an error
   */
  @Test public void testKeepAliveReadUntilCloseOnSocketClose() throws Exception
  {
    RespondingStringTCPServer server = new RespondingStringTCPServer(
        11118, RespondingStringTCPServer.SocketPolicy.CLOSE_AFTER_RESPONSE
    );

    try
    {
      server.start();

      ReadCommand cmd = getKeepAliveReadUntilCloseResponseCommand("resp1", "localhost", "11118", "GET 1");

      String response = cmd.read(null);

      Assert.assertTrue("GOT " + response, response.equals("1"));

      Thread.sleep(100);

      Assert.assertTrue(!server.isSocketOpen());

      cmd = getKeepAliveReadUntilCloseResponseCommand("resp2", "localhost", "11118", "GET 123456789");

      response = cmd.read(null);

      Assert.assertTrue("GOT " + response, response.equals("123456789"));

      Thread.sleep(100);

      Assert.assertTrue(!server.isSocketOpen());

    }

    finally
    {
      server.stop();
    }
  }

  /**
   * Tests send and receive with keepalive on text based server that expects '\r' delimited messages
   * and will not signal on response end-of-message (read available).
   *
   * @throws Exception  if there's an error
   */
  @Test public void testKeepAliveReadAvailable() throws Exception
  {
    RespondingStringTCPServer server = new RespondingStringTCPServer(
        11119, RespondingStringTCPServer.SocketPolicy.NO_INDICATOR
    );

    try
    {
      server.start();

      ReadCommand cmd = getKeepAliveReadAvailableResponseCommand("resp1", "localhost", "11119", "GET 9");

      String response = cmd.read(null);

      Assert.assertTrue("GOT " + response, response.equals("9"));

      Thread.sleep(100);

      Assert.assertTrue(server.isSocketOpen());

      cmd = getKeepAliveReadAvailableResponseCommand("resp2", "localhost", "11119", "GET 123456789");

      response = cmd.read(null);

      Assert.assertTrue("GOT " + response, response.equals("123456789"));

      Thread.sleep(100);

      Assert.assertTrue(server.isSocketOpen());

    }

    finally
    {
      server.stop();
    }

  }


  /**
   * Tests send and receive with no keep-alive (default) on text based server that expects '\r'
   * delimited messages and will not signal on response end-of-message (read available).
   *
   * @throws Exception  if there's an error
   */
  @Test public void testReadAvailable() throws Exception
  {
    RespondingStringTCPServer server = new RespondingStringTCPServer(
        11120, RespondingStringTCPServer.SocketPolicy.NO_INDICATOR
    );

    try
    {
      server.start();

      ReadCommand cmd = getReadAvailableResponseCommand("resp1", "localhost", "11120", "GET 44");

      String response = cmd.read(null);

      Assert.assertTrue("GOT " + response, response.equals("44"));

      Thread.sleep(100);

      Assert.assertTrue(server.isSocketOpen());

      cmd = getReadAvailableResponseCommand("resp2", "localhost", "11120", "GET 123456789");

      response = cmd.read(null);

      Assert.assertTrue("GOT " + response, response.equals("123456789"));

      Thread.sleep(100);

      Assert.assertTrue(server.isSocketOpen());

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
    synchronized (server.getReceiveMonitor())
    {
      server.getReceiveMonitor().wait();
    }
  }



  /**
   * Constructs a send-and-receive command with no keep-alive (default, connection sticky) and
   * read-available response handling.
   *
   * @param name      command name
   * @param address   ip address
   * @param port      server port
   * @param command   command payload
   *
   * @return    command ready to send
   */
  private ReadCommand getReadAvailableResponseCommand(String name, String address,
                                                                     String port, String command)
  {
    Element el = getElement(name, address, port, command, true);

    addReadAvailable(el);

    return (ReadCommand) builder.build(el);
  }



  /**
   * Constructs a send-and-receive command with default keep-alive (connection sticky) and
   * read-until-close response handling.
   *
   * @param name      command name
   * @param address   ip address
   * @param port      server port
   * @param command   command payload
   *
   * @return    command ready to send
   */
  private ReadCommand getKeepAliveReadUntilCloseResponseCommand(String name, String address,
                                                                     String port, String command)
  {
    Element el = getElement(name, address, port, command, true);

    addReadUntilClose(el);

    return (ReadCommand) builder.build(el);
  }

  /**
   * Constructs a send-and-receive command with keep-alive and read-available response handling.
   *
   * @param name      command name
   * @param address   ip address
   * @param port      server port
   * @param command   command payload
   *
   * @return    command ready to send
   */
  private ReadCommand getKeepAliveReadAvailableResponseCommand(String name, String address,
                                                                     String port, String command)
  {
    Element el = getElement(name, address, port, command, true);

    addKeepAlive(el);
    addReadAvailable(el);

    return (ReadCommand) builder.build(el);
  }


  /**
   * Adds keep-alive property
   *
   * @param el    parent XML element
   *
   * @return  modified parent XML element
   */
  private Element addKeepAlive(Element el)
  {
    return addKeepAlive(el, true);
  }

  /**
   * Adds keep-alive property with given value
   *
   * @param el    parent XML element
   * @param b     keep-alive value
   *
   * @return    modified parent XML element
   */
  private Element addKeepAlive(Element el, Boolean b)
  {
    Element keepAlive = new Element("property");
    keepAlive.setAttribute("name", "keepAlive");
    keepAlive.setAttribute("value", b.toString());

    el.addContent(keepAlive);

    return el;
  }


  private Element addReadUntilClose(Element el)
  {
    Element readUntilClose = new Element("property");
    readUntilClose.setAttribute("name", "responsePolicy");
    readUntilClose.setAttribute("value", "read_until_close");

    el.addContent(readUntilClose);

    return el;
  }

  private Element addNoReadResponse(Element el)
  {
    Element readUntilClose = new Element("property");
    readUntilClose.setAttribute("name", "responsePolicy");
    readUntilClose.setAttribute("value", "read_nothing");

    el.addContent(readUntilClose);

    return el;
  }


  private Element addReadAvailable(Element el)
  {
    Element readUntilClose = new Element("property");
    readUntilClose.setAttribute("name", "responsePolicy");
    readUntilClose.setAttribute("value", "read_available");

    el.addContent(readUntilClose);

    return el;
  }


  private Element getElement(String name, String address, String port, String command, Boolean pollReader)
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

    Element propPollingReader = new Element("property");
    propPollingReader.setAttribute("name", "pollingReader");
    propPollingReader.setAttribute("value", pollReader.toString());

    ele.addContent(propName);
    ele.addContent(propAddr);
    ele.addContent(propPort);
    ele.addContent(propCommand);
    ele.addContent(propPollingReader);

    return ele;
  }

  /**
   * Constructs a send-and-receive command with read-until-close response policy.
   *
   * @param name      command name
   * @param address   ip address
   * @param port      server port
   * @param command   command payload
   *
   * @return    command ready to send
   */
  private ReadCommand getReadUntilCloseResponseCommand(String name, String address, String port, String command)
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

    Element propPollingReader = new Element("property");
    propPollingReader.setAttribute("name", "pollingReader");
    propPollingReader.setAttribute("value", "true");

    Element keepAlive = new Element("property");
    keepAlive.setAttribute("name", "responsePolicy");
    keepAlive.setAttribute("value", "read until close");

    ele.addContent(propName);
    ele.addContent(propAddr);
    ele.addContent(propPort);
    ele.addContent(propCommand);
    ele.addContent(keepAlive);
    ele.addContent(propPollingReader);

    return (ReadCommand) builder.build(ele);

  }
  /**
   * Constructs a one-way command definition that will not expect a response back from the server
   * and has keep-alive set explicitly to false (forcing close of connection after send).
   *
   * @param name      command name
   * @param address   ip address
   * @param port      server port
   * @param command   command payload
   *
   * @return    command ready to send
   */
  private ExecutableCommand getOneWayCommand(String name, String address, String port, String command)
  {
     return getCommand(name, address, port, command, false, false);
  }

  /**
   * Constructs a one-way command definition that will not expect a response back from the server
   * and has no keep-alive bit set (respecting connection's current sticky setting).
   *
   * @param name      command name
   * @param address   ip address
   * @param port      server port
   * @param command   command payload
   *
   * @return    command ready to send
   */
  private ExecutableCommand getOneWayStickyKeepAliveCommand(String name, String address, String port, String command)
  {
     return getCommand(name, address, port, command, false);
  }


  /**
   * Constructs a command definition which either consumes or ignores server response.
   *
   * @param name      command name
   * @param address   ip address
   * @param port      server port
   * @param payload   command payload
   * @param waitForResponse  whether command should consume response from server before continuing
   *
   * @return    command ready to send
   */
  private ExecutableCommand getCommand(String name, String address, String port, String payload,
                                      Boolean waitForResponse)
  {
    Element el = getElement(name, address, port, payload, false);

    if (!waitForResponse)
    {
      addNoReadResponse(el);
    }

    return (ExecutableCommand) builder.build(el);
  }



  /**
   * Constructs a command definition which either consumes or ignores server response and
   * has a given keep-alive bit.
   *
   * @param name      command name
   * @param address   ip address
   * @param port      server port
   * @param payload   command payload
   * @param waitForResponse  whether command should consume response from server before continuing
   * @param keepAlive   whether to close connection after the command is finished
   *
   * @return    command ready to send
   */
  private ExecutableCommand getCommand(String name, String address, String port, String payload,
                                      Boolean waitForResponse, Boolean keepAlive)
  {
    Element el = getElement(name, address, port, payload, false);

    addKeepAlive(el, keepAlive);

    if (!waitForResponse)
    {
      addNoReadResponse(el);
    }

    return (ExecutableCommand) builder.build(el);
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
  private ExecutableCommand getOneWayKeepAliveCommand(String name, String address, String port, String command)
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

    Element propPollingReader = new Element("property");
    propPollingReader.setAttribute("name", "pollingReader");
    propPollingReader.setAttribute("value", "false");

    Element keepAlive = new Element("property");
    keepAlive.setAttribute("name", "keepAlive");
    keepAlive.setAttribute("value", "true");

    Element responsePolicy = new Element("property");
    responsePolicy.setAttribute("name", "responsePolicy");
    responsePolicy.setAttribute("value", "read_nothing");


    ele.addContent(propName);
    ele.addContent(propAddr);
    ele.addContent(propPort);
    ele.addContent(propCommand);
    ele.addContent(keepAlive);
    ele.addContent(responsePolicy);
    ele.addContent(propPollingReader);

    return (ExecutableCommand) builder.build(ele);
  }



  // Nested Classes -------------------------------------------------------------------------------


  /**
   * TCP/IP Server that expects text payloads delimited by '\r'. Responds to each payload and
   * closes the client socket after.
   */
  private static class RespondingStringTCPServer extends StringTCPServer
  {
    enum SocketPolicy
    {
      CLOSE_AFTER_RESPONSE,
      NO_INDICATOR
    }

    private SocketPolicy policy;


    RespondingStringTCPServer(int port, SocketPolicy policy)
    {
      super(port);

      this.policy = policy;
    }

    @Override protected void start() throws Exception
    {
      server = new ServerSocket(port);

      reader = new ResponseTextReader();

      t = new Thread(reader);
      t.start();
    }


    private class ResponseTextReader extends SocketReader
    {
      @Override public void run()
      {
        while (running)
        {
          try
          {
            // block and wait for connecting client socket...

            Socket s = null;
            
            try
            {
              s = server.accept();
            }
            catch (SocketException e)
            {
              running = false;
              break;
            }

            BufferedInputStream bin = new BufferedInputStream(s.getInputStream());
            DataOutputStream out = new DataOutputStream(new BufferedOutputStream(s.getOutputStream()));

            isSocketOpen = s.isBound() && s.isConnected();

            boolean endofstream = false;

            while (!endofstream)
            {
              StringBuffer sbuf = new StringBuffer();

              while (true)
              {
                int b = -1;

                try
                {
                  b = bin.read();
                }

                catch (SocketException e)
                {
                  System.out.println("Closing client socket at " + port);
                }

                if (b == '\r')
                  break;

                if (b == -1)
                {
                  endofstream = true;

                  break;
                }

                sbuf.append((char)b);
              }

              String payload = sbuf.toString();

              if (payload.startsWith("GET "))
              {
                out.writeChars(payload.substring(4, payload.length()));

                switch (policy)
                {
                  case CLOSE_AFTER_RESPONSE:

                    out.close();

                    endofstream = true;

                    System.out.println("CLOSED CLIENT SOCKET AFTER RESPONSE AT " + port);

                    break;

                  case NO_INDICATOR:

                    out.flush();
                    break;
                }
              }
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
   * TCP/IP Server that expects binary payloads. Does not acknowledge or respond to incoming
   * connections. Keeps reading until client closes the socket.
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

    byte[] receivedBytes;

    byte[] getReceivedBytes()
    {
      return receivedBytes;
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

            Socket s = null;

            try
            {
              s = server.accept();
            }
            catch (Throwable t)
            {
              running = false;
              break;
            }

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

            receivedBytes = payload.array();

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
   * connections. Continues reading until client socket is closed. Expects payloads to be
   * delimited by '\r'.
   */
  private static class StringTCPServer
  {
    protected int port;
    protected Thread t;
    protected SocketReader reader;
    protected ServerSocket server;

    private String receivedPayload;

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

    private Object getReceiveMonitor()
    {
      return RESPONSE_MONITOR;
    }

    private String getReceivedPayload()
    {
      return (receivedPayload == null) ? "<null>" : receivedPayload;
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

            Socket s = null;

            try
            {
              s = server.accept();
            }
            catch (Throwable t)
            {
              running = false;
              break;
            }

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

                  receivedPayload = msg;

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

                  receivedPayload = msg;

                  RESPONSE_MONITOR.notify();
                }

                sbuf = new StringBuffer();
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
