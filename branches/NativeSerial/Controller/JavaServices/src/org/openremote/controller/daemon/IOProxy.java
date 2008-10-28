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
import static org.openremote.controller.daemon.IOProxy.OperatingSystem.WINDOWS_VISTA;
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

  // TODO : service status


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
   * This method is invoked by the microcontainer before component deployment is complete and after
   * all configuration properties have been injected and/or set.  We can initialize the component
   * here and make it 'ready'.  <p>
   *
   * Currently for this component we do nothing except announce it's ready to go.
   */
  @Start public void setup()
  {
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


  /**
   * TODO
   *
   * @param resourcePath
   * @return
   */
  private String getFileResourceFromJBossMCLoader(String resourcePath)
  {
    log.info(IOProxy.class.getClassLoader());

    // NOTE ON THE CHOICE OF CLASSLOADER:
    //
    // This is very specific to the middleware platform being used (and without a doubt what
    // particular version of the said middleware is being used). The current description applies
    // to the JBoss MC version the OpenRemote Controller has been tested against.
    //
    // The context classloader in the current MC version is the one of the invoking service.
    // This means it's no good to us since it has zero visibility to the resources in our package.
    //
    // The classloader that loaded this class will have visibility to the top-level deployment
    // and down, including all the resource files that are included within. This works as long
    // as the top level directory (since we are using exploded deployments) has a '.' notation
    // somewhere in the name which identifies it as a deployment package rather than just a
    // regular directory used for grouping other deployment packages (in the latter case we'd
    // get a classloader for each 'deployable' inside the directory which doesn't help us finding
    // the additional resources that are not recognized as anything deployable).
    //
    //
    // NOTE ON THE RETURNED URL
    //
    // If the resource is found the returned URL is actually a JBoss MC specific URI with
    // virtual file system (VFS) schema. We'll just grab the host and file part of that URL
    // (it's a local file anyway since we deploy all files in regular directory) and reconstruct
    // them what should be a valid absolute file path to the resource in question.

    URL url = IOProxy.class.getClassLoader().getResource(resourcePath);

    if (url == null)
    {
      log.error("Cannot find native executable: " + resourcePath);

      return null;    // TODO : maybe error instead
    }

    //return url.getHost() + File.separator + url.getFile();
    try
    {
      return new File(new URL("file", url.getHost(), url.getFile()).toURI()).getAbsolutePath();
    }
    catch (URISyntaxException e)
    {
      log.error(e);   // TODO

      return null;
    }
    catch (MalformedURLException e)
    {
      log.error(e);   // TODO

      return null;
    }
  }


  /**
   * TODO
   *
   * @return
   */
  private boolean startNativeDaemon()
  {
    log.debug("Attempting to start native I/O daemon...");

    try
    {
      // can throw an error if O/S is not supported...

      OperatingSystem OS = getOperatingSystem();

      log.debug("Operating system: " + OS);

      String absoluteCommandPath = getFileResourceFromJBossMCLoader(OS.getNativeProcessPath());

      ProcessBuilder builder = new ProcessBuilder(
          absoluteCommandPath,
          "--port",
          String.valueOf(getPort())
      );

      try
      {
        // Starting a process may cause a security exception if security manager is installed
        // and execution rights are denied. Not executing this in a privileged block as it seems
        // a fundamentally bad idea (the started process would not be within Java security
        // sandbox anyway.
        //
        // This means any use of security manager requires that process execution access is
        // granted explicitly.
        
        Process daemon = builder.start();

        log.info(absoluteCommandPath + " started...");
        
        final BufferedInputStream in = new BufferedInputStream(daemon.getInputStream());
        final BufferedInputStream err = new BufferedInputStream(daemon.getErrorStream());

        Runnable daemonOutputReader = new Runnable()
        {
          public void run()
          {
            int len = 0;
            byte[] buffer = new byte[256];

            while (len != -1)
            {
              try
              {
                len = in.read(buffer, 0, buffer.length);

                log.info("[OUT] " + new String(buffer));
              }
              catch (IOException e)
              {
                log.error("Failed to read the input stream of external daemon process: " + e, e);

                len = -1;
              }
            }
          }
        };

        Runnable daemonErrorReader = new Runnable()
        {
          public void run()
          {
            int len = 0;
            byte[] buffer = new byte[256];

            while (len != -1)
            {
              try
              {
                len = err.read(buffer, 0, buffer.length);

                log.info("[ERR] " + new String(buffer));
              }
              catch (IOException e)
              {
                log.error("Failed to read the input stream of external daemon process: " + e, e);

                len = -1;
              }
            }
          }
        };

        Thread outputReader = new Thread(daemonOutputReader);
        outputReader.setDaemon(true);
        outputReader.setName("Input reader for external process " + absoluteCommandPath);
        outputReader.start();

        Thread errorReader = new Thread(daemonErrorReader);
        errorReader.setDaemon(true);
        errorReader.setName("Error reader for external process " + absoluteCommandPath);
        errorReader.start();

        return true;
      }
      catch (IOException e)
      {
        log.error("Starting the native daemon process failed: " + e, e);

        return false;
      }
      catch (SecurityException securityexception)
      {
        log.error(
            "Security manager has denied external process execution. " +
            "Permission should be set as 'FilePermission(" + absoluteCommandPath +
            ", \"execute\")' or 'autoStart' property should be set to false. " +
            "(" + securityexception + ")"
        );

        return false;
      }
    }
    catch (Error e)
    {
      log.error(e.getMessage(), e);

      return false;
    }

    // TODO: STOP method to kill the external process, if any
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

    ping();

    return connection;
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


  /**
   * TODO
   *
   * @return
   * @throws IOException
   */
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

    else if (osIdentifier.startsWith(WINDOWS_VISTA.getOSIdentifier()))
      return WINDOWS_VISTA;

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
    WINDOWS_VISTA ("Windows Vista", "native/cygwin/iodaemon-1.0.0.exe"),
    WINDOWS_XP    ("Windows XP",    "native/cygwin/iodaemon-1.0.0.exe"),
    MAC_OSX       ("Mac OS X",      ""),  // TODO
    LINUX         ("Linux",         "");     // TODO


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
