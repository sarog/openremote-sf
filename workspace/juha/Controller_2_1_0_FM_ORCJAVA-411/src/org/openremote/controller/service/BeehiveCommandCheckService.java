/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2014, OpenRemote Inc.
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
package org.openremote.controller.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.InterfaceAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.ProtocolException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.openremote.controller.Constants;
import org.openremote.controller.ControllerConfiguration;
import org.openremote.controller.exception.ConfigurationException;
import org.openremote.controller.exception.ConnectionException;
import org.openremote.controller.exception.OpenRemoteException;
import org.openremote.controller.proxy.ControllerProxy;
import org.openremote.controller.utils.Logger;
import org.openremote.controllercommand.domain.ControllerCommandDTO;
import org.openremote.rest.GenericResourceResultWithErrorMessage;
import org.openremote.useraccount.domain.ControllerDTO;
import org.restlet.data.ChallengeScheme;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

import flexjson.JSONDeserializer;

import javax.net.ssl.HttpsURLConnection;

/**
 * TODO
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 * @author Marcus Redeker
 */
public class BeehiveCommandCheckService
{

  // TODO : Remove this enclosing class, it has no purpose, all the functionality is in the nested class


  // Constants ------------------------------------------------------------------------------------

  /**
   * Name for this thread: {@value}
   */
  public final static String REMOTE_COMMAND_THREAD_NAME = "Remote Command Thread";


  // Class Members --------------------------------------------------------------------------------

  /**
   * Log for this service.
   */
  private final static Logger log = Logger.getLogger(Constants.BEEHIVE_COMMAND_CHECKER_LOG_CATEGORY);


  public static String getMACAddresses() throws Exception {
     StringBuffer macs = new StringBuffer();
     Enumeration<NetworkInterface> enum1 = NetworkInterface.getNetworkInterfaces();
     while (enum1.hasMoreElements()) {
        NetworkInterface networkInterface = (NetworkInterface) enum1.nextElement();
        if (!networkInterface.isLoopback()) {
           boolean onlyLinkLocal = true;
           for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
              if (!interfaceAddress.getAddress().isLinkLocalAddress()) {
                 onlyLinkLocal = false;
              }
           }
           if (onlyLinkLocal) continue;
           byte[] mac = networkInterface.getHardwareAddress();
           if (mac != null) {
              macs.append(getMACString(networkInterface.getHardwareAddress()));
              macs.append(",");
           }
        }
     }
     if (macs.length()==0) {
        return "no-mac-address-found";
     }
     macs.deleteCharAt(macs.length()-1);
     return macs.toString();
  }

  private static String getMACString(byte[] mac) {
     StringBuilder sb = new StringBuilder();
     for (int i = 0; i < mac.length; i++) {
        sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
     }
     return sb.toString();
  }

  // Instance Fields ------------------------------------------------------------------------------

  private BeehiveCommandChecker commandChecker;
  private ControllerConfiguration config;


  // Constructors ---------------------------------------------------------------------------------

  public BeehiveCommandCheckService(ControllerConfiguration config)
  {
    this.config = config;
  }


  // Instance Methods -----------------------------------------------------------------------------

  public void start(Deployer deployer)
  {
    this.commandChecker = new BeehiveCommandChecker(deployer, config);
    this.commandChecker.start();
  }
   
  public void stop()
  {
    this.commandChecker.stop();
  }
   


  // Nested Classes -------------------------------------------------------------------------------

  private static class BeehiveCommandChecker implements Runnable
  {

    // Instance Variables -------------------------------------------------------------------------

    /**
     * Shared thread variable to indicate this thread's running status.
     */
    private volatile boolean running = true;

    /**
     * Shared thread field for the HTTPS connection.
     */
    private volatile HttpsURLConnection https = null;

    /**
     * Deployer service reference.
     */
    private Deployer deployer;

    /**
     * Controller's current configuration
     */
    private ControllerConfiguration config;

    /**
     * The host thread for this runnable implementation.
     */
    private Thread remoteCommandThread = null;


    // Constructors -------------------------------------------------------------------------------

    BeehiveCommandChecker(Deployer deployer, ControllerConfiguration config)
    {
      this.deployer = deployer;
      this.config = config;
    }


    // Implements Runnable ------------------------------------------------------------------------

    @Override public void run()
    {
      int sleepTime = config.getRemoteCommandRequestIntervalMillis();

      try
      {
        URL serviceURL = getRemoteCommandService(deployer.getUserName());

        log.info("Starting remote command service to {0}...", serviceURL);

        while (running)
        {
          try
          {
            String str = httpGet(serviceURL, deployer.getUserName() /* TODO : dont fetch for each conn. */);

            GenericResourceResultWithErrorMessage res =
                new JSONDeserializer<GenericResourceResultWithErrorMessage>()
                    .use(null, GenericResourceResultWithErrorMessage.class)
                    .use("result", ArrayList.class)
                    .use("result.values", ControllerCommandDTO.class).deserialize(str);

            if (res.getErrorMessage() != null)
            {
              log.warn("Remote command service returned an error : {0}", res.getErrorMessage());
            }

            else
            {
              List<ControllerCommandDTO> commands = resolveResult(res.getResult());

              if (!commands.isEmpty())
              {
                executeCommand(commands.get(0));
              }
            }
          }

          catch (ConnectionException e)
          {
            log.warn(
                "Unable to connect to remote command service, retrying in {0} milliseconds. " +
                "Connection error: {1}",
                e, sleepTime, e.getMessage()
            );
          }

          log.trace(
              "Remote command check waiting for next connection attempt in {0} ms...", sleepTime
          );

          try
          {
            Thread.sleep(sleepTime);
          }

          catch (InterruptedException e)
          {
            running = false;

            Thread.currentThread().interrupt();

            log.info("Remote command thread was interrupted, shutting down...");
          }
        }
      }

      catch (ConfigurationException e)
      {
        running = false;

        log.error(
            "Could not start remote access service due to configuration error: ",
            e, e.getMessage()
        );
      }

      catch (ServiceClosedException e)
      {
        log.info("Shutting down {0}: {1}", Thread.currentThread().getName(), e.getMessage());
      }

      catch (Throwable t)
      {
        log.error("{0} : {1}", t, Thread.currentThread().getName(), t.getMessage());
      }

      log.info("Thread ''{0}'' stopped.", Thread.currentThread().getName());
    }


    // Private Instance Methods -------------------------------------------------------------------


    private void start()
    {
      remoteCommandThread = new Thread(this);

      remoteCommandThread.setName(REMOTE_COMMAND_THREAD_NAME);
      remoteCommandThread.setDaemon(true);

      remoteCommandThread.start();
    }

    private void stop()
    {
      running = false;

      if (https != null)
      {
        https.disconnect();
      }

      remoteCommandThread.interrupt();
    }

    /**
     * Executes HTTP GET to given URL and reads the response document for the request.
     *
     * @param url         URL to connect to
     * @param username    username to authenticate with
     *
     * @return  HTTP response document
     *
     * @throws ConfigurationException
     *            If HTTP GET request could not be executed. Typically indicates an error
     *            that cannot be resolved without a re-start and re-configuration and
     *            attempting to retry the request usually doesn't help.
     *
     * @throws ConnectionException
     *            If the HTTP connection failed because of request time out, unresponsive
     *            or unavailable server, connection was closed by the other side, or other
     *            types of network errors. These types of errors can resolve themselves
     *            some times by waiting and then re-attempting the request.
     *
     * @throws ServiceClosedException
     *            If we were interrupted while the running flag were set to false. Should
     *            proceed with an orderly shut-down.
     */
    private String httpGet(URL url, String username) throws ConfigurationException,
                                                            ConnectionException,
                                                            ServiceClosedException
    {
      this.https = connect(url, username);

      int responseCode = getResponseCode();

      switch (responseCode)
      {
        case HttpURLConnection.HTTP_OK:

          return readResponse();


        case HttpURLConnection.HTTP_UNAUTHORIZED:

          throw new ConnectionException(
              "Unrecognized username ''{0}'' or incorrect password connecting to ''{1}''",
              username, url
          );

        case HttpURLConnection.HTTP_NOT_FOUND:

          throw new ConnectionException(
              "Remote command service at ''{0}'' was not available or not found.", url
          );

        default:

          try
          {
            throw new ConnectionException(
                "Connection to ''{0}'' failed, HTTP error code {1} - {2}",
                url, responseCode, https.getResponseMessage()
            );
          }

          catch (IOException e)
          {
            throw new ConnectionException(
                "Connection to ''{0}'' failed, HTTP error code {1}", url, responseCode
            );
          }
      }
    }

    /**
     * Reads an HTTPS response document from the connection input stream.
     *
     * @return  HTTP response document
     *
     * @throws ConnectionException
     *            If the HTTP connection failed because of request time out, unresponsive
     *            or unavailable server, connection was closed by the other side, or other
     *            types of network errors. These types of errors can resolve themselves
     *            some times by waiting and then re-attempting the request.
     *
     * @throws ServiceClosedException
     *            If we were interrupted while the running flag were set to false. Should
     *            proceed with an orderly shut-down.
     */
    private String readResponse() throws ConnectionException, ServiceClosedException
    {
      try
      {
        BufferedReader reader = new BufferedReader(new InputStreamReader(https.getInputStream()));
        StringBuilder builder = new StringBuilder(1024);

        String line = "";

        while (line != null)
        {
          line = reader.readLine();

          builder.append(line);
        }

        return builder.toString();
      }

      catch (SocketTimeoutException e)
      {
        // in case of time-out, translate to connection exception that allows connection
        // retries...

        throw new ConnectionException(
            "Connection read timeout while reading response document : {0}",
            e, e.getMessage()
        );
      }

      catch (InterruptedIOException e)
      {
        // We were interrupted -- trying to resolve by what reason. If running flag has
        // been set to false, it's an interrupt for an orderly shutdown, so convert
        // to specific close service exception. Otherwise treat it as a regular I/O
        // exception and allow retries via connection exception type...

        if (!running)
        {
          throw new ServiceClosedException(
            "Remote command service was interrupted while reading a response document : {0}",
            e, e.getMessage()
          );
        }

        else
        {
          throw new ConnectionException(
              "Reading response document was interrupted: {0}", e, e.getMessage()
          );
        }
      }

      catch (IOException e)
      {
        // Generic I/O exception. Allow retries by re-throwing it as a connection exception...

        throw new ConnectionException(
            "I/O error while reading a response document : {0}",
            e, e.getMessage()
        );
      }
    }


    /**
     * Creates an HTTPS connection to a given URL with a HTTP Basic authentication.
     *
     * @param url         URL to connect to
     * @param username    username to authenticate with
     *
     * @return  HTTPS connection
     *
     * @throws ConfigurationException
     *            If HTTPS request could not be constructed. Typically indicates an error
     *            that cannot be resolved without a re-start and re-configuration and
     *            attempting to retry the request usually doesn't help.
     *
     * @throws ConnectionException
     *            If the HTTP connection failed because of request time out, unresponsive
     *            or unavailable server, connection was closed by the other side, or other
     *            types of network errors. These types of errors can resolve themselves
     *            some times by waiting and then re-attempting the request.
     *
     * @throws ServiceClosedException
     *            If we were interrupted while the running flag were set to false. Should
     *            proceed with an orderly shut-down.
     */
    private HttpsURLConnection connect(URL url, String username) throws ConnectionException,
                                                                        ConfigurationException,
                                                                        ServiceClosedException
    {
      this.https = createConnection(url);

      try
      {
        https.setDoInput(true);
        https.setRequestMethod("GET");
        https.setReadTimeout(config.getRemoteCommandResponseTimeoutMillis());
        https.setConnectTimeout(config.getRemoteCommandConnectionTimeoutMillis());
        https.setRequestProperty("User-Agent", "OpenRemote Controller");  // TODO : add version
        https.setRequestProperty("Accept", "application/json");

        String encodedPwd = deployer.getPassword(username);

        String base64Pwd = new String(Base64.encodeBase64((username + ":" + encodedPwd).getBytes()));

        https.addRequestProperty(
            Constants.HTTP_AUTHORIZATION_HEADER,
            Constants.HTTP_BASIC_AUTHORIZATION + base64Pwd
        );

        log.debug(
            "Connecting to ''{0}'' \n  Connection Timeout : {1} \n  Response Timeout : {2}",
            url, config.getRemoteCommandConnectionTimeoutMillis(),
            config.getRemoteCommandResponseTimeoutMillis()
        );

        https.connect();

        return https;
      }

      catch (SocketTimeoutException e)
      {
        // Socket timeout, convert to connection exception to allow retries...

        throw new ConnectionException(
            "Remote access proxy service at ''{0}'' did not respond within {1} milliseconds.",
            url, config.getRemoteCommandConnectionTimeoutMillis()
        );
      }

      catch (InterruptedIOException e)
      {
        // Other generic I/O interrupts. If running flag has been set to false, most likely
        // asked to perform an orderly shutdown. Otherwise convert to a connection exception
        // type that will allow retries...

        if (!running)
        {
          throw new ServiceClosedException("Interrupted while connecting to ''{0}''", url);
        }

        else
        {
          throw new ConnectionException("Interrupted while connecting to ''{0}''", url);
        }
      }

      catch (ConnectException e)
      {
        // Regular connection exception will return to the connection loop and retry later...

        throw new ConnectionException("Could not connect to ''{0}'' : {1}", e, url, e.getMessage());
      }

      catch (Deployer.PasswordException e)
      {
        // If we can't find the credentials, keep re-trying... the user might not yet have
        // entered his credentials.

        throw new ConnectionException(
            "The required password for user ''{0}'' was not found. Password manager error : {1}",
            e, username, e.getMessage()
        );
      }

      catch (ProtocolException e)
      {
        // Things are pretty bad if we can't even construct a HTTP GET request. Shut down the
        // service with a configuration exception...

        throw new ConfigurationException(
            "Could not construct a HTTP GET request : {0}", e, e.getMessage()
        );
      }

      catch (IOException e)
      {
        // I/O error during the connect. Connection exception will allow us to retry...

        throw new ConnectionException("I/O error while connecting to ''{0}'' : {1}");
      }
    }


    /**
     * Reads an HTTPS response code from the connection input stream.
     *
     * @return  HTTP response code
     *
     * @throws ConnectionException
     *            If the HTTP connection failed because of request time out, unresponsive
     *            or unavailable server, connection was closed by the other side, or other
     *            types of network errors. These types of errors can resolve themselves
     *            some times by waiting and then re-attempting the request.
     *
     * @throws ServiceClosedException
     *            If we were interrupted while the running flag were set to false. Should
     *            proceed with an orderly shut-down.
     */
    private int getResponseCode() throws ConnectionException, ServiceClosedException
    {
      try
      {
        return https.getResponseCode();
      }

      catch (SocketTimeoutException e)
      {
        // Socket time out, allow retry by translating to connection exception...

        throw new ConnectionException(
            "Read timeout while waiting for HTTP response message : {0}", e, e.getMessage()
        );
      }

      catch (InterruptedIOException e)
      {
        // Other generic I/O interruptions. Attempt to resolve the reason. If running
        // flag has been set to false, we've been interrupted to perform an orderly shutdown.
        // Otherwise allow retries via connection exception type...

        if (!running)
        {
          throw new ServiceClosedException("Service interrupted and requested to close...");
        }

        else
        {
          throw new ConnectionException(
              "Interrupted while reading HTTPS response code : {0}", e, e.getMessage()
          );
        }
      }

      catch (IOException e)
      {
        // Generic I/O errors. Allow retries via connection exception type...

        throw new ConnectionException(
            "I/O error while reading HTTPS response code : {0}", e, e.getMessage()
        );
      }
    }


    private HttpsURLConnection createConnection(URL url) throws ConnectionException
    {
      try
      {
        URLConnection conn = url.openConnection();

        return (HttpsURLConnection)conn;
      }

      catch (ClassCastException e)
      {
        throw new ConnectionException(
            "Configuration error: the ''{0}'' property ''{1}'' must be a URI with https:// schema.",
            ControllerConfiguration.REMOTE_COMMAND_SERVICE_URI, url
        );
      }

      catch (IOException e)
      {
        throw new ConnectionException(
            "Failed to create a connection to ''{0}'' : {1}", e, url, e.getMessage()
        );
      }
    }


    //
    // TODO
    //
    private void executeCommand(ControllerCommandDTO controllerCommand)
    {
      switch (controllerCommand.getCommandTypeEnum())
      {
        case INITIATE_PROXY:
            initiateProxy(controllerCommand);
            break;

        case UNLINK_CONTROLLER:

            stop();
            deployer.unlinkController();

            break;

        default:
            log.error("ControllerCommand not implemented yet: " + controllerCommand.getCommandType());
      }
    }


    //
    // TODO
    //
    private void initiateProxy(ControllerCommandDTO command)
    {
      Long id = command.getOid();
      String url = command.getCommandParameter().get("url");
      String token = command.getCommandParameter().get("token");

      Socket beehiveSocket = null;

      boolean needsAck = true;

      try
      {
        log.info("Connecting to beehive at "+url+" for proxy");
        beehiveSocket = ControllerProxy.makeClientSocket(url, token, config.getProxyTimeout());

        // at this point the command should already have been marked as ack by the listening end at beehive

        log.info("Connected to beehive");
        needsAck = false;

        // try to connect to it, see if it's still valid

        String ip = config.getWebappIp();
        int port = config.getWebappPort();

        if (ip == null || ip.trim().length() == 0)
        {
          ip = "localhost";
        }

        if (port == 0)
        {
          port = 8080;
        }

        ControllerProxy proxy = new ControllerProxy(beehiveSocket, ip, port, config.getProxyTimeout());
        log.info("Starting proxy");
        proxy.start();
      }

      catch (IOException e)
      {
        log.info("Got exception while connecting to beehive", e);

        if(beehiveSocket != null)
        {
          try
          {
            beehiveSocket.close();
          }

          catch (IOException e1)
          {
            // ignore
          }
        }

        // the server should have closed it, but let's help him to make sure

        if(needsAck)
        {
          ackCommand(id);
        }
      }
    }


    //
    // TODO
    //
    private void ackCommand(Long id)
    {
      log.debug("Acking command "+id) ;
      ClientResource cr = null;

      try
      {
        // TODO : multiple URIs possible... need to ack to same server

        URI base = config.getRemoteCommandURIs() [0];

        URI uri = base.resolve(base.getPath() + "/command/" + id);

        cr = new ClientResource(uri);

        String username = deployer.getUserName();

        cr.setChallengeResponse(ChallengeScheme.HTTP_BASIC, username, deployer.getPassword(username));

        Representation r = cr.delete();
        String str;
        str = r.getText();

        GenericResourceResultWithErrorMessage res =
            new JSONDeserializer<GenericResourceResultWithErrorMessage>()
                .use(null, GenericResourceResultWithErrorMessage.class)
                .use("result", String.class).deserialize(str);

        if (res.getErrorMessage() != null)
        {
              throw new RuntimeException(res.getErrorMessage());
        }
      }

      catch (Exception e)
      {
        log.error("!!! Unable to ACK controller command with id: " + id, e);
      }

      finally
      {
        if (cr != null)
        {
          cr.release();
        }
      }
    }



    /**
     * TODO
     *
     * Resolves the URL to request remote commands based on controller's configuration
     * and controller's identity.
     *
     * @param username    currently required because controller's identity is stored remotely
     */
    private URL getRemoteCommandService(String username) throws ConfigurationException
    {
      String uriPath = "";
      URI base = null;
      URI uri = null;

      try
      {
        // TODO : implement handling for multiple URIs for client side fail-over...

        base = config.getRemoteCommandURIs() [0];

        if (base == null)
        {
          throw new ConfigurationException(
              "Remote command service property ''{0}'' has not been set.",
              ControllerConfiguration.REMOTE_COMMAND_SERVICE_URI
          );
        }

        uriPath = base.getPath() + "/commands/" + getControllerIdentity(username);

        uri = base.resolve(uriPath);

        URL url = uri.toURL();

        if (!url.getProtocol().equalsIgnoreCase("https"))
        {
          throw new ConfigurationException(
              "The ''{0}'' property ''{1}'' must be a URI with https:// schema.",
              ControllerConfiguration.REMOTE_COMMAND_SERVICE_URI, url
          );
        }

        return url;
      }

      catch (IllegalArgumentException e)
      {
        throw new ConfigurationException(
            "Cannot resolve remote command service URI ''{0}'' : {1}",
            e, base.toString() + "/" + uriPath, e.getMessage()
        );
      }

      catch (MalformedURLException e)
      {
        throw new ConfigurationException(
            "Unable to convert URI ''{0}'' to URL : {1}",
            e, uri, e.getMessage()
        );
      }
    }


    @SuppressWarnings("unchecked")
    private List<ControllerCommandDTO> resolveResult(Object result) throws ConnectionException
    {
      // TODO : GenericResource... class needs a rethink to avoid requiring suppress warning annotations

      try
      {
        return (ArrayList<ControllerCommandDTO>)result;
      }

      catch (ClassCastException e)
      {
        throw new ConnectionException(e.toString());
      }
    }









    // ----- 8< -----------------------------------------------------------------------------------


    // TODO :
    //        This is a temporary implementation to abstract away the controller
    //        identity construction -- currently done as a remote call to the
    //        Beehive back-end but should be ideally an identifier the controller
    //        can construct locally.
    private Long getControllerIdentity(String username) throws ConfigurationException
    {
      String url = config.getBeehiveAccountServiceRESTRootUrl();

      // If acct management hasn't been configured or it has been configured with a local
      // loopback controller identity value...

      if (url == null || (url.startsWith("::loopback") && url.endsWith("::")))
      {
        return localControllerID(url);
      }

      else
      {
        try
        {
          url = config.getBeehiveAccountServiceRESTRootUrl() + "controller/announce/" + getMACAddresses();
        }

        catch (Exception e)
        {
          throw new ConfigurationException(e.toString());
        }
      }

      final int DELAY = 1000;

      // Because controller id is remote, we need to wait for an actual response before
      // we can continue...

      try
      {
        while (running)
        {
          try
          {
            String str = httpGet(new URL(url), username);

            GenericResourceResultWithErrorMessage res =
                new JSONDeserializer<GenericResourceResultWithErrorMessage>()
                    .use(null, GenericResourceResultWithErrorMessage.class)
                    .use("result", ControllerDTO.class)
                    .deserialize(str);

            if (res.getErrorMessage() != null)
            {
              throw new ConfigurationException("Controller identity error : {0}", res.getErrorMessage());
            }

            else
            {
              return ((ControllerDTO)res.getResult()).getOid();
            }
          }

          catch (ConnectionException e)
          {
            log.info("Unable to retrieve controller identity, retrying in {0} milliseconds...", DELAY);
          }

          catch (ServiceClosedException e)
          {
            throw new ConfigurationException("Service closed.", e);
          }

          try
          {
            Thread.sleep(DELAY);
          }

          catch (InterruptedException e)
          {
            Thread.currentThread().interrupt();

            throw new ConfigurationException("Controller identity process interrupted.");
          }
        }
      }

      catch (MalformedURLException e)
      {
        throw new ConfigurationException(
            "Configuration property ''{0}'' value ''{1}'' is not a valid URL: {2}",
            ControllerConfiguration.BEEHIVE_ACCOUNT_SERVICE_REST_ROOT_URL, url, e.getMessage()
        );
      }

      throw new ConfigurationException("Controller identity: SNAFU");
    }

    private Long localControllerID(String url) throws ConfigurationException
    {
      // No remote acct mgmt, just use a fixed value for controller's identity..

      if (url == null)
      {
        return 1L;
      }

      url = url.trim();
      url = url.substring(2, url.length() - 2);
      String[] elements = url.split(",");

      if (elements.length == 1)
      {
        return 1L;
      }

      else if (elements.length >= 2)
      {
        try
        {
          return Long.parseLong(elements[1]);
        }

        catch (NumberFormatException e)
        {
          throw new ConfigurationException(
              "Unable to parse loopback value from ''{0}'' : {1}", e, url, e.getMessage()
          );
        }
      }

      throw new ConfigurationException("LocalControllerID SNAFU");
    }
  }

  public static class ServiceClosedException extends OpenRemoteException
  {
    public ServiceClosedException(String msg, Object... params)
    {
      super(msg, params);
    }
  }

}

