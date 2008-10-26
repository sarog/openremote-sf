package org.openremote.controller.output;

import org.jboss.beans.metadata.api.annotations.Start;
import org.jboss.logging.Logger;
import org.openremote.controller.core.Bootstrap;
import static org.openremote.controller.output.IOModule.PingProtocol.PING_MESSAGE;
import static org.openremote.controller.output.IOModule.PingProtocol.PING_RESPONSE;

import java.net.Socket;
import java.net.UnknownHostException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedExceptionAction;
import java.security.PrivilegedActionException;
import java.io.IOException;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;

/**
 * TODO
 *
 * @author <a href = "mailto:juha@juhalindfors.com">Juha Lindfors</a>
 */
public class IOProxy
{
  // Constants ------------------------------------------------------------------------------------

  public final static String LOG_CATEGORY = "IOPROXY";

  public final static int DEFAULT_NATIVE_IODAEMON_PORT = 9999;


  // Class Members --------------------------------------------------------------------------------

  private final static Logger log = Logger.getLogger(Bootstrap.ROOT_LOG_CATEGORY + "." + LOG_CATEGORY);


  // Instance Fields ------------------------------------------------------------------------------

  private Socket socket = null;

  private int nativeIODaemonPort = DEFAULT_NATIVE_IODAEMON_PORT;



  // MC Component Methods -------------------------------------------------------------------------

  @Start public void setup()
  {

    log.info("I/O Proxy started.");
  }


  // JavaBean Methdods ----------------------------------------------------------------------------

  public int getPort()
  {
    return nativeIODaemonPort;
  }

  public void setPort(int port)
  {
    nativeIODaemonPort = port;
  }

  
  // Public Instance Methods ----------------------------------------------------------------------

  public void sendBytes(IOModule ioModule, byte[] bytes)
  {

    log.info("Sending Bytes to I/O Daemon...");

    try
    {
      socket = getConnection();

      log.info("Connected to : " + socket.getInetAddress() + ":" + socket.getPort());
      log.info("(" + socket.getLocalAddress() + ":" + socket.getLocalPort() + ")");

      try
      {
        ping();
      }
      catch (IOException e)
      {
        log.error("PING FAILED: " + e);
      }
    }
    catch (IOException e)
    {
      log.error("Unable to create connection to I/O daemon, has it been started?");
    }

    // TODO : boolean return on success
  }


  // Private Instance Methods ---------------------------------------------------------------------

  private Socket getConnection() throws IOException
  {
    if (socket != null)
    {
      try
      {
        ping();

        return socket;
      }
      catch (IOException e)
      {
        log.info("No existing or broken connection to IODaemon, creating a connection...");
      }
    }

    try
    {
      return AccessController.doPrivileged(new PrivilegedExceptionAction<Socket>()
      {
        public Socket run() throws IOException
        {
          try
          {
            return new Socket((String)null /* loopback interface */, nativeIODaemonPort);
          }
          catch (UnknownHostException e)
          {
            throw new Error(); // TODO
          }
          catch (SecurityException e)
          {
            throw new Error();  // TODO
          }
        }
      });
    }
    catch (PrivilegedActionException e)
    {
      Exception ioe = e.getException();

      if (ioe instanceof IOException)
      {
        throw (IOException)ioe;
      }

      else
        throw new Error(ioe);   // TODO
    }
  }

  private boolean ping() throws IOException
  {
    log.info("Pinging I/O Daemon....");
    
    BufferedInputStream in = new BufferedInputStream(socket.getInputStream());
    BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream());

    out.write(PING_MESSAGE.getBytes());
    out.flush();

    log.info("Wrote : " + PING_MESSAGE.getMessage());

    byte[] buffer = new byte[PING_RESPONSE.getLength()];

    int len = in.read(buffer, 0, buffer.length);

    if (len == PING_RESPONSE.getLength() && new String(buffer).equals(PING_RESPONSE.getMessage()))
    {
      log.info("IT'S ALIVE!!");
    }
    
    return true;
  }
}
