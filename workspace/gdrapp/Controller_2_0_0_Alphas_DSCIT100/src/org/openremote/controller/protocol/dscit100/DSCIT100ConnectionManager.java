/**
 * 
 */
package org.openremote.controller.protocol.dscit100;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.openremote.controller.protocol.dscit100.Packet.PacketCallback;
import org.openremote.controller.protocol.dscit100.PanelState.State;

/**
 * @author Greg Rapp
 * 
 */
public class DSCIT100ConnectionManager
{

  // Class Members
  // --------------------------------------------------------------------------------

  /**
   * DSCIT100 logger. Uses a common category for all DSCIT100 related logging.
   */
  private final Logger log = Logger
      .getLogger(DSCIT100CommandBuilder.DSCIT100_LOG_CATEGORY);

  /**
   * Connection timeout for DSCIT100 IP socket
   */
  private final static int IP_CONNECT_TIMEOUT = 5000;

  /**
   * Default TCP port if none is provided in command definition
   */
  private final static int DEFAULT_TCP_PORT = 5000;

  // Instance Fields
  // ------------------------------------------------------------------------------

  public Map<String, DSCIT100Connection> connections;

  // Constructors
  // ---------------------------------------------------------------------------------

  public DSCIT100ConnectionManager()
  {
    connections = new ConcurrentHashMap<String, DSCIT100Connection>();

    // add shutdown hook in an attempt to avoid leaving open connections behind
    log.debug("Adding shutdown hook to manage unclosed DSC IT100 connections in case of controller exit.");
    addShutdownHook();
  }

  // Protected Instance Methods
  // -------------------------------------------------------------------

  protected synchronized DSCIT100Connection getConnection(String address)
      throws Exception
  {
    // If connection exists, return it
    if (connections.containsKey(address) && connections.get(address) != null
        && connections.get(address).isConnected())
      return connections.get(address);

    // Couldn't find an exiting connection, so try to build one
    buildIPConnection(address);

    // If connection exists, return it, else return null
    if (connections.containsKey(address) && connections.get(address) != null
        && connections.get(address).isConnected())
      return connections.get(address);
    else
      return null;
  }

  // Private Instance Methods
  // ---------------------------------------------------------------------

  private synchronized void buildIPConnection(String address) throws Exception
  {
    String[] arrAddress = address.split(":", 2);
    String host = arrAddress[0];

    int port = DEFAULT_TCP_PORT;
    if (arrAddress.length == 2)
    {
      try
      {
        port = Integer.parseInt(arrAddress[1]);
      }
      catch (NumberFormatException e)
      {
        log.error("Invalid TCP port specified in command definition : "
            + arrAddress);
        throw e;
      }
    }

    Socket socket = null;
    try
    {

      log.debug("Creating new socket for host : " + address);
      socket = new Socket();
      SocketAddress socketAddress = new InetSocketAddress(host, port);

      socket.connect(socketAddress, IP_CONNECT_TIMEOUT);

      this.connections.put(address, new IpConnection(socket));
    }
    catch (UnknownHostException e)
    {
      log.error("Unknown host : " + address, e);
    }
    catch (SocketTimeoutException e)
    {
      log.warn("Timeout connecting to host : " + address, e);
    }
    catch (IOException e)
    {
      log.error("Couldn't get I/O for the connection", e);
    }
  }

  /**
   * Registers a shutdown hook in the JVM to attempt to close any open IT100
   * connections when JVM process is killed.
   * 
   * Adding shutdown hook in a privileged code block -- as long as calling code
   * has sufficient security permissions, we don't require additional
   * permissions for this operation.
   */
  private void addShutdownHook()
  {
    try
    {
      final Thread shutdown = new Thread(new Shutdown());

      // BEGIN PRIVILEGED CODE BLOCK
      // --------------------------------------------------------------
      AccessController.doPrivileged(new PrivilegedAction<Void>()
      {
        public Void run()
        {
          Runtime.getRuntime().addShutdownHook(shutdown);

          return null;
        }
      });
      // END PRIVILEGED CODE BLOCK
      // ----------------------------------------------------------------

    }
    catch (SecurityException exception)
    {
      log.warn(
          "Cannot register shutdown hook. Most likely due to lack of security permissions "
              + "in the JVM security manager. DSC IT100 connection manager service will operate normally "
              + "but may be unable to clean up all the connection resources in case of an unexpected "
              + "shutdown (security exception: " + exception.getMessage() + ")",
          exception);
    }
    catch (IllegalStateException exception)
    {
      log.error(
          "Unable to register shutdown hook due to illegal state exception ("
              + exception.getMessage()
              + "). This may be due to the JVM already starting the "
              + "shutdown process.", exception);
    }
  }

  // Inner Classes
  // --------------------------------------------------------------------------------

  private class IpConnection implements DSCIT100Connection
  {

    private Socket socket = null;
    private PrintWriter out = null;
    private IpListener listener;
    protected PacketCallback packetCallback;

    /**
     * @param socket
     */
    public IpConnection(Socket socket)
    {
      this.socket = socket;

      listener = new IpListener(socket);

      try
      {
        this.out = new PrintWriter(socket.getOutputStream(), true);
      }
      catch (IOException e)
      {
        log.error("Error creating socket output stream for "
            + socket.getInetAddress().getHostAddress(), e);
      }
    }

    @Override
    public void send(ExecuteCommand command)
    {
      sendInternal(command.getPacket());
    }

    @Override
    public void send(Packet packet)
    {
      sendInternal(packet);
    }

    private void sendInternal(Packet packet)
    {
      if (isConnected())
      {
        log.debug("Sending data to address "
            + socket.getInetAddress().getHostAddress() + " : "
            + packet.toPacket());
        if (packet.getCallback() != null)
          this.packetCallback = packet.getCallback();
        out.println(packet.toPacket());
      }
      else
      {
        log.warn("Could not send data to address "
            + socket.getInetAddress().getHostAddress() + " : "
            + packet.toPacket());
      }
    }

    @Override
    public boolean isConnected()
    {
      return socket.isConnected();
    }

    @Override
    public void close()
    {
      log.debug("Closing connection to "
          + socket.getInetAddress().getHostAddress());
      try
      {
        socket.close();
      }
      catch (IOException e)
      {
        log.warn("Error closing connection to "
            + socket.getInetAddress().getHostAddress(), e);
      }
    }

    @Override
    public String getAddress()
    {
      StringBuffer sb = new StringBuffer();
      sb.append(socket.getInetAddress().getHostAddress());
      sb.append(":");
      sb.append(socket.getPort());
      return sb.toString();
    }

    @Override
    public State getState(StateDefinition stateDefinition)
    {
      if (listener != null && listener.state != null)
        return listener.state.getState(stateDefinition);
      else
      {
        log.warn("Unable to get connection listener or listener state database is unavailable for connection to "
            + socket.getInetAddress().getHostAddress());
        return null;
      }
    }

    private class IpListener implements Runnable
    {
      private Thread thread;
      private Socket socket;

      public PanelState state;

      public IpListener(Socket socket)
      {
        this.socket = socket;
        this.state = new PanelState();

        thread = new Thread(this);
        thread.start();
      }

      @Override
      public void run()
      {
        log.info("Starting connection listener thread for "
            + socket.getInetAddress().getHostAddress());
        BufferedReader in = null;
        try
        {
          in = new BufferedReader(
              new InputStreamReader(socket.getInputStream()));
        }
        catch (IOException e)
        {
          log.error("I/O error creating reader socket for "
              + socket.getInetAddress().getHostAddress(), e);
        }

        log.info("Starting read loop for "
            + socket.getInetAddress().getHostAddress());

        // Send IT100 state discovery packet to get current system state
        sendInternal(new Packet("001", ""));
        // Send IT100 labels request packet to get system labels
        sendInternal(new Packet("002", ""));

        boolean isConnected = true;

        while (isConnected)
        {
          Packet packet = null;
          try
          {
            String rawData = in.readLine();
            log.debug("Received data from "
                + socket.getInetAddress().getHostAddress() + " : " + rawData);
            packet = new Packet(rawData);
          }
          catch (IOException e)
          {
            log.warn("Error parsing packet", e);
            isConnected = false;
            // Connection has failed, close the socket so it can be recreated
            // later
            IpConnection.this.close();
          }

          if (packet != null)
          {
            state.processPacket(packet);

            if (packetCallback != null)
            {
              log.debug("Executing callback method for packet " + packet);
              packetCallback.receive(IpConnection.this, packet);
            }
          }

        }
      }
    }
  }

  /**
   * Implements shutdown hook for the DSC IT100 connection manager.
   */
  private class Shutdown implements Runnable
  {
    public void run()
    {
      for (String key : connections.keySet())
      {
        try
        {
          log.debug("Executing JVM shutdown hook to close DSC IT100 connections...");

          DSCIT100Connection connection = connections.get(key);
          if (connection != null)
          {
            String connectionName = connection.getAddress();

            // format the message...

            StringBuilder builder = new StringBuilder(256)
                .append("Closing connection (name = ").append(connectionName)
                .append(")");

            log.info(builder.toString());

            connection.close();

            log.info("Connection closed.");
          }
        }
        catch (Throwable t)
        {
          log.error("Closing connection failed: " + t.getMessage(), t);
        }
      }
    }
  }

}
