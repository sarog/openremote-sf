package org.openremote.controller.daemon;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedExceptionAction;
import java.security.PrivilegedActionException;

import org.jboss.beans.metadata.api.annotations.Start;
import org.jboss.logging.Logger;
import org.openremote.controller.core.Bootstrap;
import static org.openremote.controller.daemon.IOModule.PingProtocol.PING_MESSAGE;
import static org.openremote.controller.daemon.IOModule.PingProtocol.PING_RESPONSE;
import static org.openremote.controller.daemon.IOProxy.OperatingSystem.LINUX;
import static org.openremote.controller.daemon.IOProxy.OperatingSystem.MAC_OSX;
import static org.openremote.controller.daemon.IOProxy.OperatingSystem.WINDOWS_VISTA;
import static org.openremote.controller.daemon.IOProxy.OperatingSystem.WINDOWS_XP;

/**
 * This is the Java side of the native I/O daemon that implements I/O operations.
 *
 * TODO: autostart
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
   *
   * Value: {@value}
   */
  public final static int DEFAULT_NATIVE_IODAEMON_PORT = 9999;

  /**
   * Default autostart mode determines whether this I/O proxy should attempt to start its
   * native counterpart automatically if the initial attempt to connect fails.
   *
   * Value: {@value}
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
   * Indicates whether this I/O proxy should attempt to spawn the native I/O daemon process
   * itself should connecting to it fail (possibly because it was not started).
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
   * @return  port number between [0..65535]
   */
  public int getPort()
  {
    return nativeIODaemonPort;
  }

  /**
   * Sets the port number this component will use to attempt to connect to the native operating
   * system level I/O daemon.  <p>
   *
   * This value can be injected by the microcontainer if configured in the service's XML descriptor.
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
   * Indicates whether this I/O proxy has autostart mode enabled. Autostart means that the
   * proxy may attempt to spawn the corresponding native I/O daemon automatically should
   * connecting to it fail.
   *
   * @return true if autostart enabled; false otherwise
   */
  public boolean isAutoStart()
  {
    return autoStartNativeDaemon;
  }

  /**
   * Sets the autostart mode for this I/O proxy instance. Autostart means that the
   * proxy may attempt to spawn the corresponding native I/O daemon automatically should
   * connecting to it fail.    <p>
   *
   * This value can be injected by the microcontainer if configured in the service's XML descriptor.
   *
   * @param enabled   true to enable, false to disable
   */
  public void setAutoStart(boolean enabled)
  {
    this.autoStartNativeDaemon = enabled;
  }


  // Public Instance Methods ----------------------------------------------------------------------

  /**
   * TODO
   *
   * @param ioModule
   * @param bytes
   */
  public void sendBytes(IOModule ioModule, byte[] bytes)
  {

    // TODO : this should be debug (or even trace)
    log.info("Sending Bytes to I/O Daemon...");

    try
    {
      getConnection();

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
    log.debug("Using classloader: " + IOProxy.class.getClassLoader());

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
   * @param filename
   * @param processOut
   * @param processErr
   */
  private void attachProcessOutputs(String filename, InputStream processOut, InputStream processErr)
  {
    final BufferedReader in = new BufferedReader(new InputStreamReader(processOut));
    final BufferedReader err = new BufferedReader(new InputStreamReader(processErr));

    Runnable daemonOutputReader = new Runnable()
    {
      public void run()
      {
        log.debug("Started native I/O daemon standard output reader.");

        String output;

        try
        {
          while ((output = in.readLine()) != null)
          {
            log.info("[OUT] " + output);
          }
        }
        catch (IOException e)
        {
          log.error("Failed to read the output stream of external daemon process: " + e, e);
        }
        finally
        {
          log.debug("Finished native I/O daemon standard output reader.");
        }
      }
    };

    Runnable daemonErrorReader = new Runnable()
    {
      public void run()
      {
        log.debug("Started native I/O daemon standard output reader.");

        String output;

        try
        {
          while ((output = err.readLine()) != null)
          {
            log.info("[ERR] " + output);
          }
        }
        catch (IOException e)
        {
          log.error("Failed to read the error stream of external daemon process: " + e, e);
        }
        finally
        {
          log.debug("Finished native I/O daemon standard error reader.");
        }
      }
    };

    Thread outputReader = new Thread(daemonOutputReader);
    outputReader.setDaemon(true);
    outputReader.setName("Input reader for external process " + filename);
    outputReader.start();

    Thread errorReader = new Thread(daemonErrorReader);
    errorReader.setDaemon(true);
    errorReader.setName("Error reader for external process " + filename);
    errorReader.start();
  }

  /**
   * TODO
   *
   * @param daemon
   */
  private void addProcessShutdownHook(final Process daemon)
  {
    Runnable daemonKillThread = new Runnable()
    {
      public void run()
      {
        // TODO : send kill first, wait for timeout, then forcibly kill if necessary

        daemon.destroy();
      }
    };

    Thread thread = new Thread(daemonKillThread, "Shutdwon hook for I/O daemon process");

    try
    {
      Runtime.getRuntime().addShutdownHook(thread);
    }
    catch (Throwable t)
    {
      log.warn(
          "Adding external I/O daemon shutdown hook failed. The external process may " +
          "need to be killed manually. (" + t + ")", t
      );
    }
  }

  /**
   * TODO
   *
   * @throws IOException
   */
  private void startNativeDaemon() throws IOException
  {
    log.info("Attempting to start native I/O daemon...");

    try
    {
      // can throw an error if O/S is not supported...

      OperatingSystem OS = getOperatingSystem();

      log.debug("Operating system: " + OS);

      // TODO: returned string could be null
      String absoluteCommandPath = getFileResourceFromJBossMCLoader(OS.getNativeProcessPath());

      // Start the native daemon -- configure the port to whatever was configured for this
      // service...
      
      ProcessBuilder builder = new ProcessBuilder(
          absoluteCommandPath,
          "--port",
          String.valueOf(getPort())
      );

      log.debug("Native daemon: " + absoluteCommandPath);

      try
      {
        // Starting a process may cause a security exception if security manager is installed
        // and execution rights are denied. Not executing this in a privileged block as it seems
        // a fundamentally bad idea (the started process would not be within Java security
        // sandbox anyway).
        //
        // This means any use of security manager requires that process execution rights are
        // granted explicitly.

        Process daemon = builder.start();

        addProcessShutdownHook(daemon);

        attachProcessOutputs(
            new File(absoluteCommandPath).getName(),
            daemon.getInputStream(),
            daemon.getErrorStream()
        );
      }
      catch (SecurityException securityexception)
      {
        // rethrow as I/O Exception...

        throw new IOException(
            "Security manager has denied external process execution. " +
            "Permission should be set as 'FilePermission(" + absoluteCommandPath +
            ", \"execute\")' or 'autoStart' property should be set to false. " +
            "(" + securityexception + ")", securityexception
        );
      }
    }
    catch (Error e)
    {
      // rethrow as I/O Exception...

      throw new IOException(e.getMessage(), e);
    }

    // TODO: STOP method to kill the external process, if any
  }


  /**
   * TODO
   *
   *
   * @throws IOException
   */
  private void getConnection() throws IOException
  {
    if (connection == null)
    {
      // if don't have a connection yet, try to create one... should that fail try to
      // autostart native daemon (if feature enabled) and then create the connection...

      try
      {
        // may throw IOException if native daemon has not been started, or error if something
        // more serious... nevertheless, keep trying.

        if (createConnection())
          return;
      }
      catch (Throwable t)
      {
        // autostart not enabled and connection failed, rethrow...

        if (!autoStartNativeDaemon)
        {
          throw new IOException(
              "Could not connect to native I/O daemon. Autostart is not enabled, therefore " +
              "the native I/O daemon must be started manually, or restarted if it has stopped " +
              "responding. (" + t + ")", t);
        }

        // otherwise, keep trying...
        
        log.info("Initial attempt at connecting to native I/O daemon failed: " + t);
      }

      if (autoStartNativeDaemon)
      {
        startNativeDaemon();

        if (!createConnection())
        {
          throw new IOException("Creating");  // TODO
        }
      }

      else
      {

      }

    }

    // TODO : test return value
    ping();

  }


  /**
   * TODO
   *
   * @throws IOException TODO
   *
   * @throws Error  if the loopback interface cannot be resolved or the security manager
   *                prevents connecting to the local I/O daemon
   *
   * @return true if socket connection was succesfully created; false otherwise
   */
  private boolean createConnection() throws IOException
  {
    try
    {
      return AccessController.doPrivileged(new PrivilegedExceptionAction<Boolean>()
      {
        public Boolean run() throws IOException
        {

          String loopbackInterface = null;

          try
          {
            loopbackInterface = InetAddress.getByName(null).getHostName();

            connection = new Socket(loopbackInterface, nativeIODaemonPort);

            // TODO : set socket properties

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
    catch (PrivilegedActionException e)
    {
      Exception shouldBeIOException = e.getException();

      if (shouldBeIOException instanceof IOException)
      {
        throw (IOException)shouldBeIOException;
      }

      else
      {
        throw new Error(e);   // don't know what this is
      }
    }
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
