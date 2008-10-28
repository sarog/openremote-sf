package org.openremote.controller.daemon;

import org.jboss.beans.metadata.api.annotations.Start;
import org.jboss.beans.metadata.api.annotations.Inject;
import org.jboss.beans.metadata.api.annotations.FromContext;
import org.jboss.logging.Logger;
import org.jboss.kernel.spi.dependency.KernelControllerContext;
import org.openremote.controller.core.Bootstrap;
import static org.openremote.controller.daemon.IOModule.PingProtocol.PING_MESSAGE;
import static org.openremote.controller.daemon.IOModule.PingProtocol.PING_RESPONSE;
import static org.openremote.controller.daemon.IOProxy.OperatingSystem.WINDOWS_XP;
import static org.openremote.controller.daemon.IOProxy.OperatingSystem.MAC_OSX;
import static org.openremote.controller.daemon.IOProxy.OperatingSystem.LINUX;
import org.openremote.controller.daemon.IOModule;

import java.net.*;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.io.IOException;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;

/**
 * TODO
 *
 * @author <a href = "mailto:juha@juhalindfors.com">Juha Lindfors</a>
 */
public class IOProxy
{
  // Constants ------------------------------------------------------------------------------------

  /**
   * Log category name for this component. The actual full log category can be constructed by
   * appending {@link org.openremote.controller.core.Bootstrap#ROOT_LOG_CATEGORY} and this
   * string using a dot '.' separator (ROOT_LOG_CATEGORY.LOG_CATEGORY).
   *
   * Value: {@value}
   */
  public final static String LOG_CATEGORY = "IOPROXY";

  /**
   * This is the default port of the operating system level native daemon that low level I/O
   * operations are delegated to.
   */
  public final static int DEFAULT_NATIVE_IODAEMON_PORT = 9999;

  /**
   * TODO
   */
  public final static boolean DEFAULT_AUTOSTART_MODE = true;



  // Class Members --------------------------------------------------------------------------------

  /**
   * Logger API for this component. Currently uses the JBoss logging API.
   */
  private final static Logger log = Logger.getLogger(Bootstrap.ROOT_LOG_CATEGORY + "." + LOG_CATEGORY);



  // Instance Fields ------------------------------------------------------------------------------

  /**
   * TODO
   */
  private Socket connection = null;

  /**
   * The port number we use to connect to native I/O daemon.
   */
  private int nativeIODaemonPort = DEFAULT_NATIVE_IODAEMON_PORT;

  /**
   * TODO
   */
  private boolean autoStartNativeDaemon = DEFAULT_AUTOSTART_MODE;



  // MC Component Methods -------------------------------------------------------------------------


  /**
   * Injects the microcontainer context (service context) at component deployment time (prior
   * to component start).
   *
   * @param ctx   a service context which allows access to other deployed services and their
   *              configuration and metadata via the microcontainer deployment framework (kernel)
   */
  @Inject(fromContext = FromContext.CONTEXT)
  public void setServiceContext(KernelControllerContext ctx)
  {
    //this.serviceContext = ctx;    TODO

    log.info("FROM CTX: " + ctx.getBeanMetaData().getClassLoader());
  }
  
  /**
   * This method is invoked by the microcontainer before component deployment is complete and after
   * all configuration properties have been injected and/or set.  We can initialize the component
   * here and make it 'ready'.  <p>
   *
   * Currently for this component we do nothing except announce it's ready to go.
   */
  @Start public void setup()
  {
    log.fatal("CTX AT START: " + Thread.currentThread().getContextClassLoader());
    log.fatal("AT START: " + IOProxy.class.getClassLoader());
    
    log.info("I/O Proxy started.");
  }


  // JavaBean Methdods ----------------------------------------------------------------------------

  /**
   * Returns the port number this component uses to connect to the native operating system level
   * I/O daemon.
   *
   * @return  port number between [0...65535]
   */
  public int getPort()
  {
    return nativeIODaemonPort;
  }


  /**
   * Sets the port number this component will use to attempt to connect to the native operating
   * system level I/O daemon
   *
   * @param port    port number between [0...65535]
   */
  public void setPort(int port)
  {
    if (port < 0 || port > 65535)
    {
      log.error(
          "Invalid port value: " + port + ". Port value must be in range [0..65535]. " +
          "Falling back to default port number " + DEFAULT_NATIVE_IODAEMON_PORT + "...");

      port = DEFAULT_NATIVE_IODAEMON_PORT;
    }

    nativeIODaemonPort = port;
  }


  /**
   * TODO
   *
   * @return
   */
  public boolean isAutoStart()
  {
    return autoStartNativeDaemon;
  }

  /**
   * TODO
   *
   * @param enabled
   */
  public void setAutoStart(boolean enabled)
  {
    this.autoStartNativeDaemon = enabled;
  }


  // Public Instance Methods ----------------------------------------------------------------------

  public void sendBytes(IOModule ioModule, byte[] bytes)
  {

    // TODO : this should be trace
    log.info("Sending Bytes to I/O Daemon...");

    try
    {
      connection = getConnection();

      log.info("Connected to : " + connection.getInetAddress() + ":" + connection.getPort());
      log.info("(" + connection.getLocalAddress() + ":" + connection.getLocalPort() + ")");

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


  private boolean startNativeDaemon()
  {
    try
    {
      // can throw an error if O/S is not supported...

      OperatingSystem OS = getOperatingSystem();

      log.info("OS: " + OS);
      
//      URL url = Thread.currentThread().getContextClassLoader().getResource(OS.getNativeProcessPath());

      log.info("PARENT LOADER: " + IOProxy.class.getClassLoader().getParent());
      log.info("CLASS LOADER: " + IOProxy.class.getClassLoader());
      log.info("CTX LOADER: " + Thread.currentThread().getContextClassLoader());
      
//      URL url = Thread.currentThread().getContextClassLoader().getResource("iodaemon-1.0.0.exe");
      URL url = Thread.currentThread().getContextClassLoader().getResource("native/cygwin/iodaemon-1.0.0.exe");

      if (url == null)
      {
//        log.error("Cannot find native executable: " + OS.getNativeProcessPath());
        log.error("Cannot find native executable: ");

        return false;
      }
      
      log.info("Resource: " + url);

      try
      {
        URI fileURI = new URL("file", url.getHost(), url.getFile()).toURI();

        log.info("RESULTING URI: " + fileURI);
      }
      catch (URISyntaxException e)
      {
        log.fatal(e);
      }
      catch (MalformedURLException e)
      {
        log.fatal(e);
      }

      ProcessBuilder builder = new ProcessBuilder();  
    }
    catch (Error e)
    {
      log.error(e.getMessage(), e);

      return false;
    }

    return false;   // TODO REMOVE THIS
  }


  /**
   * TODO
   *
   * @return
   *
   * @throws IOException
   */
  private Socket getConnection() throws IOException
  {
    if (connection == null)
    {
      if (!createConnection() && autoStartNativeDaemon)
      {
        startNativeDaemon();

        if (!createConnection())
        {
          throw new IOException();  // TODO
        }
      }
    }
/*
    else
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
*/
    throw new Error("HOW DID I GET HERE?");
  }


  /**
   * TODO
   *
   * @throws Error  if the loopback interface cannot be resolved or the security manager
   *                prevents connecting to the local I/O daemon
   *
   * @return true if socket connection was succesfully created; false otherwise
   */
  private boolean createConnection()
  {
    return AccessController.doPrivileged(new PrivilegedAction<Boolean>()
    {
      public Boolean run()
      {

        String loopbackInterface = null;

        try
        {
          loopbackInterface = InetAddress.getByName(null).getHostName();

          connection = new Socket(loopbackInterface, nativeIODaemonPort);

          return connection.isConnected();
        }
        catch (UnknownHostException exception)
        {
          // We connect to loopback interface (localhost) so this shouldn't happen....

          throw new Error(
              "Unable to connect to loopback interface (" +
              loopbackInterface + ":" + nativeIODaemonPort + "): " + exception.toString(),
              exception
          );
        }
        catch (IOException e)
        {
          return false;
        }
        catch (SecurityException securityexception)
        {
          throw new Error(
              "Caller's security domain prevents socket connection to '"
              + loopbackInterface + ":" + nativeIODaemonPort + "': " + securityexception.toString(),
              securityexception
          );
        }
      }
    });
  }


  private boolean ping() throws IOException
  {
    log.info("Pinging I/O Daemon....");
    
    BufferedInputStream in = new BufferedInputStream(connection.getInputStream());
    BufferedOutputStream out = new BufferedOutputStream(connection.getOutputStream());

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


  /**
   * Returns the host operating system name identifier.
   *
   * @throws Error    if security manager prevents access to the 'os.name' property or the
   *                  operating system is not supported
   *
   * @return  operating system identifier
   */
  private OperatingSystem getOperatingSystem()
  {
    String osIdentifier = AccessController.doPrivileged(

        new PrivilegedAction<String>()
        {
          public String run()
          {
            try
            {
              return System.getProperty("os.name");
            }
            catch (SecurityException exception)
            {
              throw new Error(
                  "Caller's security domain prevented access to operating system name " +
                  "('os.name') property: " + exception.toString(), exception
              );
            }
          }
        }
    );

    if (osIdentifier.startsWith(WINDOWS_XP.getOSIdentifier()))
      return WINDOWS_XP;

    else if (osIdentifier.startsWith(MAC_OSX.getOSIdentifier()))
      return MAC_OSX;

    else if (osIdentifier.startsWith(LINUX.getOSIdentifier()))
      return LINUX;

    else
    {
      StringBuilder builder = new StringBuilder(256);

      builder.append("Your operating system identifier '").append(osIdentifier)
          .append("' is currently not supported. Supported OS's are: ");

      for (OperatingSystem os : OperatingSystem.values())
        builder.append("'").append(os).append("' ");

      throw new Error(builder.toString());
    }
  }


  protected enum OperatingSystem
  {
    WINDOWS_XP("Windows XP", "iodaemon-1.0.0.exe"),

    MAC_OSX("Mac OS X", ""),  // TODO

    LINUX("Linux", "");       // TODO


    // Instance Fields ----------------------------------------------------------------------------

    private String identifierString;

    private String processPath;


    // Constructors -------------------------------------------------------------------------------

    private OperatingSystem(String identifierString, String nativeProcessPath)
    {
      this.identifierString = identifierString;
      this.processPath = nativeProcessPath;
    }


    // Instance Methods ---------------------------------------------------------------------------

    private String getOSIdentifier()
    {
      return identifierString;
    }

    private String getNativeProcessPath()
    {
      return processPath;
    }
  }
}
