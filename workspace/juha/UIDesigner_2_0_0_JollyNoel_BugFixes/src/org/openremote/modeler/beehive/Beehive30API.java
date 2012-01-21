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
package org.openremote.modeler.beehive;

import java.io.File;
import java.io.IOException;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.HttpURLConnection;
import java.util.UUID;
import java.util.Collection;
import java.text.DecimalFormat;
import java.text.MessageFormat;

import org.apache.log4j.Logger;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.commons.codec.binary.Base64;
import org.openremote.modeler.client.Configuration;
import org.openremote.modeler.client.Constants;
import org.openremote.modeler.domain.User;
import org.openremote.modeler.domain.Panel;
import org.openremote.modeler.domain.Account;
import org.openremote.modeler.exception.ConfigurationException;
import org.openremote.modeler.exception.NetworkException;

/**
 * Implements {@link BeehiveService} for Beehive 3.0 REST API. <p>
 *
 * For storing resource archives, this implementation uses a local file system.
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class Beehive30API implements BeehiveService <File, Void>
{


  // Class Members --------------------------------------------------------------------------------

  /**
   * Logger for storing network performance stats in a specific sub-category.
   */
  private final static Logger downloadPerfLog = Logger.getLogger(BEEHIVE_DOWNLOAD_PERF_LOG_CATEGORY);

  /**
   * Specialized logger for this service that supports multiple category-logging to MDC context
   * loggers.
   */
  private BeehiveServiceLog log = new BeehiveServiceLog();



  // Instance Fields ------------------------------------------------------------------------------

  /**
   * Designer configuration.
   */
  private Configuration config;



  // Constructors ---------------------------------------------------------------------------------

  /**
   * Initializes a Beehive client API.
   *
   * @param config    Designer configuration.
   */
  public Beehive30API(Configuration config)
  {
    this.config = config;
  }



  // Implements BeehiveService --------------------------------------------------------------------

  /**
   *
   * @param currentUser
   * @param target
   *
   * @throws ConfigurationException
   *              If designer configuration error prevents the service from executing
   *              normally. Often a fatal error type that should be logged/notified to
   *              admins, configuration corrected and application re-deployed.
   *
   * @throws NetworkException
   *              If (possibly recoverable) network errors occured during the service
   *              operation. Network errors may be recoverable in which case this operation
   *              could be re-attempted. See {@link NetworkException.Severity} for an
   *              indication of the network error type.
   *
   * @throws ServerException
   *            TODO :
   *              what looks like a Beehive implementation or deployment error -- could
   *              be recoverable because is fundamentally a network error but may well not be
   *
   * @throws ArchiveNotFoundException
   *
   * @throws ArchiveStoreException
   *            TODO : file operations error on local filesystem when storing archive
   */
  @Override public Void downloadArchive(User currentUser, File target) throws
      ConfigurationException, NetworkException, ServerException, ArchiveNotFoundException,
      ArchiveStoreException
  {

    // TODO :
    //    - Must use HTTPS


    // Construct the request...

    HttpClient httpClient = new DefaultHttpClient();

    URI beehiveArchiveURI = null;

    try
    {
      beehiveArchiveURI = new URI(
          config.getBeehiveRESTRootUrl() + "user/" +
          currentUser.getUsername() + "/openremote.zip"
      );
    }

    catch (URISyntaxException e)
    {
      throw new ConfigurationException(
          "Incorrect Beehive REST URL defined in config.properties : {0}", e, e.getMessage()
      );
    }

    HttpGet httpGet = new HttpGet(beehiveArchiveURI);


    // Authenticate...

    addHTTPAuthenticationHeader(httpGet, currentUser.getUsername(), currentUser.getPassword());


    // Collect some network statistics...

    long starttime = System.currentTimeMillis();


    // HTTP GET to Beehive...

    HttpResponse response;

    try
    {
      response = httpClient.execute(httpGet);
    }

    catch (IOException e)
    {
      throw new NetworkException(
          "Network error while downloading account (OID = {0}) archive from Beehive " +
          "(URL : {1}) : {2}", e,

          currentUser.getAccount().getOid(), beehiveArchiveURI, e.getMessage()
      );
    }


    // Make sure we got a response and a proper HTTP return code...

    if (response == null)
    {
      throw new ServerException(
          "Beehive did not respond to HTTP GET request, URL : {0} {1}",
          beehiveArchiveURI, printUser(currentUser)
      );
    }

    StatusLine statusLine = response.getStatusLine();

    if (statusLine == null)
    {
      throw new ServerException(
          "There was no status from Beehive to HTTP GET request, URL : {0} {1}",
          beehiveArchiveURI, printUser(currentUser)
      );
    }

    int httpResponseCode = statusLine.getStatusCode();


    // Deal with the HTTP OK (200) case.
    //
    // Download the archive to a temp file first:
    //
    //  - This way we can keep writing the incoming bytes directly to file
    //    system which helps with memory management (don't need to keep the
    //    full archive in memory)
    //  - But in case the download won't be complete, we don't want to make
    //    the archive 'final' until we know all bytes have arrived and the
    //    archive has been validated.

    if (httpResponseCode == HttpURLConnection.HTTP_OK)
    {
      HttpEntity httpEntity = response.getEntity();

      if (httpEntity == null)
      {
        throw new ServerException(
            "No content received from Beehive to HTTP GET request, URL : {0} {1}",
            beehiveArchiveURI, printUser(currentUser)
        );
      }


      // Download to temp...

      BufferedOutputStream tempTargetOutputStream = null;

      File tempDownloadFile = new File(
          target.getAbsolutePath() + "." + UUID.randomUUID().toString() + ".download"
      );

      log.debug("Downloading to ''{0}''", tempDownloadFile.getAbsolutePath());

      try
      {
        tempTargetOutputStream = new BufferedOutputStream(new FileOutputStream(tempDownloadFile));

        httpEntity.writeTo(tempTargetOutputStream);


        // Record network performance stats...

        long endtime = System.currentTimeMillis();

        float kbytes  = ((float)tempDownloadFile.length()) / 1000;
        float seconds = ((float)(endtime - starttime)) / 1000;
        float kbpersec = kbytes / seconds;

        String kilobytes  = new DecimalFormat("###########0.00").format(kbytes);
        String nettime    = new DecimalFormat("##########0.000").format(seconds);
        String persectime = new DecimalFormat("##########0.000").format(kbpersec);

        downloadPerfLog.info(
            "Downloaded " + kilobytes + " kilobytes in " + nettime + " seconds (" +
            persectime + "kb/s)"
        );
      }

      catch (FileNotFoundException e)
      {
        // If file cannot be created

        throw new ArchiveStoreException(
            "Cannot create file ''{0}'' : {1}",
            e, tempDownloadFile.getAbsolutePath(), e.getMessage()
        );
      }

      catch (SecurityException e)
      {
        throw new ConfigurationException(
            "Security Manager has denied r/w access to ''{0}'' ({1}).",
            e, tempDownloadFile.getAbsolutePath(), e.getMessage()
        );
      }

      catch (IOException e)
      {
        throw new ArchiveStoreException(
            "Failed to write downloaded Beehive archive to temporary file ''{0}'' : {1}",
            e, tempDownloadFile.getAbsolutePath(), e.getMessage()
        );
      }


      // Validate the download -- can check that we have space on the filesystem to
      // extract the archive, that archive is not corrupt, and specific files are
      // included in the archive.

      validateArchive(tempDownloadFile);


      // Got complete download, archive has been validated. Now make it 'final'.
      // Move is often much faster than copy.

      try
      {
        boolean success = tempDownloadFile.renameTo(target);

        if (!success)
        {
          throw new ArchiveStoreException(
              "Failed to replace existing Beehive archive ''{0}'' with ''{1}''",
              target.getAbsolutePath(), tempDownloadFile.getAbsolutePath()
          );
        }

        log.info(
            "Moved ''{0}'' to ''{1}''", tempDownloadFile.getAbsolutePath(), target.getAbsolutePath()
        );
      }

      catch (SecurityException e)
      {
        throw new ConfigurationException(
            "Security manager has denied write access to ''{0}'' : {1}",
            e, target.getAbsolutePath(), e.getMessage()
        );
      }
    }


    // Special case for HTTP response 404...

    else if (httpResponseCode == HttpURLConnection.HTTP_NOT_FOUND)
    {
      // TODO
      //
      //  - The semantics here are unclear: does 404 indicate a real error (user data has
      //    disappeared?) or does it merely indicate a new user account (no data, return 404)?
      //    New user account (no data) should not be responded with an error (404).
      //
      //    This should be verified/fixed in Beehive REST API.

      throw new ArchiveNotFoundException(
          "Received HTTP NOT FOUND (404) from Beehive {0}", printUser(currentUser)
      );
    }


    // Any other HTTP response code is an error...

    else
    {
      // TODO :
      //
      //   Currently assumes any other HTTP return code is a standard network error.
      //   This could be improved by handling more specific error codes (some are
      //   fatal, some are recoverable) such as 500 Internal Error (permanent) or
      //   307 Temporary Redirect

      throw new NetworkException(
          "Failed to download Beehive archive from URL ''{0}'' {1}, " +
          "HTTP Response code: {2}",

          beehiveArchiveURI, printUser(currentUser), httpResponseCode
      );
    }

    return null;    // stands for void
  }






  private void validateArchive(File tempArchive)
  {
    // TODO
  }


  /**
   * Adds a HTTP 1.1 Authentication header to the given HTTP request. The header
   * value (username and password) are base64 encoded as required by the HTTP
   * specification.
   *
   * @param request   the HTTP request to add the header to
   * @param username  username string
   * @param password  password string
   */
  private void addHTTPAuthenticationHeader(HttpRequest request, String username, String password)
  {
    request.setHeader(
        Constants.HTTP_BASIC_AUTH_HEADER_NAME,
        Constants.HTTP_BASIC_AUTH_HEADER_VALUE_PREFIX +
        base64EncodeAuthHeaderValue(username + ":" + password)
    );
  }


  /**
   * Base64 encode the value string for a HTTP authentication header, as
   * required by the HTTP specification. The name, password string must follow
   * the required formatting name:password.
   *
   * @param namePassword  name:password string per the HTTP spec
   *
   * @return  base64 encoded authentication header value
   */
  private static String base64EncodeAuthHeaderValue(String namePassword)
  {
    if (namePassword == null)
    {
      return null;
    }

    return new String(Base64.encodeBase64(namePassword.getBytes()));
  }


  /**
   * Utility to print some user account information for logging.
   *
   * @param user    current user
   *
   * @return    (user name - email, account ID)
   */
  private String printUser(User user)
  {
    return "(User: " + user.getUsername() + " - " + user.getEmail() +
           ", Account OID: " + user.getAccount().getOid() + ")";
  }


  // Nested Classes -------------------------------------------------------------------------------


  private static class BeehiveServiceLog
  {

    /**
     * Generic logger for this service implementation.
     */
    private final static Logger beehiveLog = Logger.getLogger(BEEHIVE_SERVICE_LOG_CATEGORY);
      
    private BeehiveServiceLog()
    {

    }

    private void info(String msg, Object... params)
    {
      beehiveLog.info(format(msg, params));
    }

    private void debug(String msg, Object... params)
    {
      beehiveLog.debug(format(msg, params));
    }

    private String format(String msg, Object... params)
    {
      try
      {
        return MessageFormat.format(msg, params);
      }

      catch (Throwable t)
      {
        return msg + "  [EXCEPTION MESSAGE FORMATTING ERROR: " + t.getMessage().toUpperCase() + "]";
      }
    }
  }


  /**
   * Service-specific exception type to indicate issues with accessing local file system to
   * cache Beehive Archives.
   */
  public static class ArchiveStoreException extends BeehiveServiceException
  {

    /**
     * Constructs a new exception with a given message
     *
     * @param msg   exception message
     */
    ArchiveStoreException(String msg)
    {
      super(msg);
    }

    /**
     * Constructs a new exception with a parameterized message
     *
     * @param msg     exception message
     * @param params  message parameters
     */
    ArchiveStoreException(String msg, Object... params)
    {
      super(msg, params);
    }

    /**
     * Constructs a new exception with a given message and root cause.
     *
     * @param msg       exception message
     * @param cause     root cause exception
     */
    ArchiveStoreException(String msg, Throwable cause)
    {
      super(msg, cause);
    }

    /**
     * Constructs a new exception with a parameterized message and root cause.
     *
     * @param msg       exception message
     * @param cause     root cause exception
     * @param params    message parameters
     */
    ArchiveStoreException(String msg, Throwable cause, Object... params)
    {
      super(msg, cause, params);
    }
  }


  /**
   * TODO :
   *
   *   Temporary exception type to deal with 404 errors on accessing
   *   Beehive archives on the server. See the notes on Beehive30API
   *   implementation for details. Should be revised once the Beehive
   *   REST API semantics have been clarified.
   */
  public static class ArchiveNotFoundException extends BeehiveServiceException
  {
    ArchiveNotFoundException(String msg)
    {
      super(msg);
    }

    ArchiveNotFoundException(String msg, Object... params)
    {
      super(msg, params);
    }
  }


}

