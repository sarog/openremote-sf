/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2013, OpenRemote Inc.
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
package org.openremote.controller.protocol.telnet;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import junit.framework.Assert;

import org.jdom.Element;
import org.junit.Before;
import org.junit.Test;
import org.openremote.controller.command.Command;
import org.openremote.controller.model.sensor.SwitchSensor;
import org.openremote.controller.protocol.ReadCommand;
import org.openremote.controller.statuscache.ChangedStatusTable;
import org.openremote.controller.statuscache.EventProcessorChain;
import org.openremote.controller.statuscache.StatusCache;
import org.openremote.controller.utils.Logger;

/**
 * Tests for {@link org.openremote.controller.protocol.telnet.TelnetCommandBuilder} and
 * {@link org.openremote.controller.protocol.telnet.TelnetCommand} classes
 * 
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class TelnetCommandBuilderTest
{
  private TelnetCommandBuilder builder = null;
  private StatusCache cache = null;


  // Test Lifecycle -------------------------------------------------------------------------------

  @Before public void setUp()
  {
    // sets up a shared telnet command builder and a status cache for sensor testing

    builder = new TelnetCommandBuilder();

    ChangedStatusTable cst = new ChangedStatusTable();
    EventProcessorChain echain = new EventProcessorChain();

    cache = new StatusCache(cst, echain);
  }

  /**
   * This is a regression test for ORCJAVA-326 (http://jira.openremote.org/browse/ORCJAVA-326).
   *
   * Simple telnet (TCP really) server is started which responds with 'on', should map the
   * configured switch sensor to on state.
   *
   * @throws Exception  if test fails
   */
  @Test public void testSwitchOnResponse_ORCJAVA_326() throws Exception
  {
    Server server = null;
    final int SENSOR_ID = 1;

    try
    {
      server = new Server(4444);
      Map<String, String> requestResponse = new HashMap<String, String>();
      requestResponse.put("sendtest1", "on");

      server.start(requestResponse);

      TelnetCommand cmd = getCommand("localhost", "4444", "null|sendtest1");

      SwitchSensor s1 = new SwitchSensor("switch on", SENSOR_ID, cache, cmd);

      cache.registerSensor(s1);
      s1.start();

      Assert.assertTrue(
          "Expected 'on', got '" + getSensorValueFromCache(SENSOR_ID) + "'",
          getSensorValueFromCache(SENSOR_ID).equals("on")
      );
    }

    finally
    {
      if (server != null)
      {
        try
        {
          server.stop();
        }

        catch (Exception e)
        {
          System.err.println("test server failed: " + e);
        }
      }
    }
  }
   @Test public void testTelnet()
   {
     TelnetCommand cmd = getCommand("192.168.1.1", "23", "test");
     Assert.assertEquals("192.168.1.1", cmd.getIp());
     Assert.assertEquals(23, cmd.getPort().intValue());
     Assert.assertEquals("test", cmd.getCommand());
   }
   
   @Test public void testTelnetWithParam()
   {
     TelnetCommand cmd = getCommand("192.168.1.1", "23", "light1_${param}");
     Assert.assertEquals("192.168.1.1", cmd.getIp());
     Assert.assertEquals(23, cmd.getPort().intValue());
     Assert.assertEquals("light1_255", cmd.getCommand());
   }


  // Helpers --------------------------------------------------------------------------------------

  private String getSensorValueFromCache(int sensorID) throws Exception
  {
    // sleep here to give the polling mechanism enough time to push the event value to cache...

    Thread.sleep(ReadCommand.POLLING_INTERVAL * 2);

    return cache.queryStatus(sensorID);
  }

  private TelnetCommand getCommand(String address, String port, String cmd)
  {
    Element ele = new Element("command");
    ele.setAttribute("id", "test");
    ele.setAttribute("protocol", "telnet");
    ele.setAttribute(Command.DYNAMIC_VALUE_ATTR_NAME, "255");

    Element propName = new Element("property");
    propName.setAttribute("name", "name");
    propName.setAttribute("value", "testName");

    Element propAddr = new Element("property");
    propAddr.setAttribute("name", "ipAddress");
    propAddr.setAttribute("value", address);

    Element propPort = new Element("property");
    propPort.setAttribute("name", "port");
    propPort.setAttribute("value", port);

    Element propCommand = new Element("property");
    propCommand.setAttribute("name", "command");
    propCommand.setAttribute("value", cmd);

    ele.addContent(propName);
    ele.addContent(propAddr);
    ele.addContent(propPort);
    ele.addContent(propCommand);

    return (TelnetCommand) builder.build(ele);
  }


  // Nested Classes -------------------------------------------------------------------------------


  /**
   * A simple TCP server that can be used for telnet testing.
   */
  private static class Server
  {
    private int port;
    private ServerSocket server;
    private ServerThread serverListeningThread = null;
    private Thread t;

    private Server(int port)
    {
      this.port = port;
    }


    /**
     * Starts the server on configured port.
     *
     * @param requestResponse mapping of incoming requests to responses.
     *
     * @throws IOException  if things break
     */
    private void start(final Map<String, String> requestResponse) throws IOException
    {
      server = new ServerSocket(port);

      serverListeningThread = new ServerThread(server, requestResponse);
      t = new Thread(serverListeningThread);
      t.start();
    }

    /**
     * Try to do a clean stop of the server.
     *
     * @throws Exception  if things break
     */
    private void stop() throws Exception
    {
      Logger log = Logger.getLogger("Telnet");

      log.info("Telnet server closing.");

      serverListeningThread.stop();

      // give server listening thread a little while to clean up before attempting to interrupt...

      Thread.sleep(250);

      // interrupt the server listening thread...

      t.interrupt();

      // give the server listening thread a little while to manage interrupt before forcing
      // server socket close...

      Thread.sleep(250);

      server.close();
    }
  }


  /**
   * The server reader thread that listens on the port, accepts sockets and spawns readers
   * for the sockets.
   */
  private static class ServerThread implements Runnable
  {
    volatile boolean serverRunning = true;
    private ServerSocket server;
    private Map<String, String> requestResponse = null;
    private Map<SocketReader, Thread> socketThreads = new HashMap<SocketReader, Thread>();
    private Set<Socket> socketReaders = new HashSet<Socket>();

    ServerThread(ServerSocket server, Map<String, String> requestResponse)
    {
      this.server = server;
      this.requestResponse = requestResponse;
    }

    /**
     * Spawn a socket reader for incoming connections.
     */
    public void run()
    {
      try
      {
        while (serverRunning)
        {
          Socket socket = server.accept();

          SocketReader socketReader = new SocketReader(this, socket, requestResponse);
          Thread t = new Thread(socketReader);
          t.start();

          socketThreads.put(socketReader, t);
          socketReaders.add(socket);
        }
      }

      catch (Exception e)
      {
        System.err.println("Server failed: " + e.getMessage());
      }
    }

    /**
     * Try to do a clean shutdown on existing socket reader threads.
     *
     * @throws Exception  if things break
     */
    public void stop() throws Exception
    {
      serverRunning = false;

      for (SocketReader reader : socketThreads.keySet())
      {
        reader.stop();
      }

      Thread.sleep(250);

      for (SocketReader reader : socketThreads.keySet())
      {
        Thread t = socketThreads.get(reader);

        t.interrupt();
      }

      Thread.sleep(250);

      for (Socket s : socketReaders)
      {
        close(s);
      }
    }

    private void close(Socket s)
    {
      try
      {
        s.close();
      }

      catch (Exception e)
      {
        System.err.println("Can't close socket: " + e.getMessage());
      }
    }

    private void cleanUp(SocketReader reader, Socket s)
    {
      close(s);

      socketReaders.remove(s);

      socketThreads.remove(reader);
    }

  }


  /**
   * Socket reader and response thread.
   */
  private static class SocketReader implements Runnable
  {
    volatile boolean socketReading = true;
    private Socket socket = null;
    private ServerThread server = null;
    private Map<String, String> requestResponse = null;

    SocketReader(ServerThread server, Socket socket, Map<String, String> requestResponse)
    {
      this.socket = socket;
      this.server = server;
      this.requestResponse = requestResponse;
    }

    @Override public void run()
    {
      try
      {
        BufferedInputStream in = new BufferedInputStream(socket.getInputStream());

        while (socketReading)
        {
          BufferedReader bin = new BufferedReader(new InputStreamReader(in));
          String cmd = bin.readLine();

          if (cmd == null)
          {
            socketReading = false;
            break;
          }

          System.out.println("Telnet server received: '" + cmd + "'");

          respond(socket, cmd);
        }
      }

      catch (Exception e)
      {
        System.err.println("Socket reader failed: " + e.getMessage());
      }

      finally
      {
        server.cleanUp(this, socket);
      }
    }

    public void stop()
    {
      socketReading = false;
    }

    public void respond(Socket socket, String cmd) throws Exception
    {
      String response = requestResponse.get(cmd.trim());

      PrintStream print = new PrintStream(new BufferedOutputStream(socket.getOutputStream()));

      if (response != null)
      {
        print.write(response.getBytes());
      }

      else
      {
        System.err.println("\n\n*** Telnet server did not find a matching response to : '" + cmd + "'\n");
      }

      print.flush();

      System.out.println("Telnet server sent: '" + response + "'");
    }
  }




}
