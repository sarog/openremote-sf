/*
 * OpenRemote, the Internet-enabled Home.
 *
 * This work is licensed under Creative Commons Attribution-Noncommercial-Share Alike 3.0
 * United States, http://creativecommons.org/licenses/by-nc-sa/3.0/us/
 */
package org.openremote.controller.client;

import java.io.IOException;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.net.URL;
import java.net.MalformedURLException;
import java.net.URLConnection;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.kernel.Kernel;


/**
 * A client REST interface for receiving commands from the remote control using HTTP protocol.
 *
 * @author <a href="mailto:juha@juhalindfors.com">Juha Lindfors</a>
 */
public class ClientCommand extends HttpServlet
{

  // TODO : logging


  // Constants ------------------------------------------------------------------------------------

  /**
   * HTTP request parameter specifying a device that a command (included in the same request)
   * should be forwarded to.
   */
  public final static String HTTP_PARAM_DEVICE = "device";

  /**
   * HTTP request parameter specifying a command to be sent to a device.
   */
  public final static String HTTP_PARAM_COMMAND = "command";

  /**
   * HTTP request parameter specifying a device package update. The value of the parameter is the
   * package name.
   */
  public final static String HTTP_PARAM_UPDATE = "update";

  /**
   * Bean name of main deployer (the JMX version) on the kernel.
   */
  private final static String MAIN_DEPLOYER_NAME = "jboss.system:service=MainDeployer";


  // Servlet Overrides ----------------------------------------------------------------------------

  /**
   * Handles incoming REST requests.
   *
   * TODO : document REST API
   *
   * @param request       see superclass
   * @param response      see superclass
   *
   * @throws java.io.IOException  see superclass
   */
  @Override public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException
  {
    String deviceParam = request.getParameter(HTTP_PARAM_DEVICE);
    String commandParam = request.getParameter(HTTP_PARAM_COMMAND);
    String updateParam = request.getParameter(HTTP_PARAM_UPDATE);


    // Handle the device commands...

    if (deviceParam != null)
    {
      int returnCode = handleDeviceCommand(deviceParam, commandParam);

      // We will get SC_OK for successful invocation, SC_BAD_REQUEST for any error...

      if (returnCode == HttpServletResponse.SC_OK)
        response.setStatus(returnCode);
      else
        response.sendError(returnCode);
    }


    // Handle sync requests...

    else if (updateParam != null)
    {
      int returnCode = doSync(updateParam);

      // We will get SC_OK for successful sync, SC_CONFLICT on any deployment error...

      if (returnCode == HttpServletResponse.SC_OK)
        response.setStatus(returnCode);
      else
        response.sendError(returnCode);
    }

    else
    {
      System.out.println("Unknown command. Request skipped.");

      response.sendError(HttpServletResponse.SC_BAD_REQUEST);
    }
  }

  /**
   * Redirects to {@link #doGet}.
   *
   * @param request       see {@link #doGet}
   * @param response      see {@link #doGet}
   * @throws IOException  see {@link #doGet}
   */
  @Override public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException
  {
    doGet(request, response);
  }


  // Private Instance Methods ---------------------------------------------------------------------

  /**
   * Handles the incoming device command.
   *
   * TODO : expected HTTP request format
   *
   * @param device
   * @param command
   *
   * @return  HTTP response code. This will return {@link HttpServletResponse#SC_OK SC_OK (200)}
   *          if the command invocation is succesfully received and handled by the kernel.
   *          {@link HttpServletResponse#SC_BAD_REQUEST SC_BAD_REQUEST (400)} will be returned
   *          in case of missing HTTP request parameters, incorrect device name, unknown command
   *          or any other error invoking the kernel occurs.
   */
  private int handleDeviceCommand(String device, String command)
  {
    // If the command is missing, it's a bad request...

    if (command == null)
    {
      System.out.println("Device request is missing a command. Request skipped.");

      return HttpServletResponse.SC_BAD_REQUEST;
    }

    // Kernel will be found from application context...

    Kernel kernel = (Kernel)getServletContext().getAttribute(ClientStartup.KERNEL_ATTRIBUTE);

    // And invoke assuming device name is a bean name in
    // the kernel, and command is a no-arg method in the bean...

    try
    {
      kernel.getBus().invoke(device, command, new Object[] {}, new String[] {});

      return HttpServletResponse.SC_OK;
    }
    catch (Throwable t)
    {
      System.out.println("Error invoking device. Request not handled. (" + t.toString() + ")");

      return HttpServletResponse.SC_BAD_REQUEST;
    }
  }

  /**
   * Handles incoming sync command.    <p>
   *
   * This will download a new package to controller's download directory. Download
   * will first be saved to a temporary file to ensure it is received completely.
   * Once download is complete, the original file will be replaced by the new package.
   *
   * TODO : expected HTTP request command
   *
   * @param urlString
   *
   * @return  HTTP response code. This will return {@link HttpServletResponse#SC_OK SC_OK (200)}
   *          if download and deployment of new package is successful. If there is any problem
   *          with either downloading or deploying an updated package,
   *          {@link HttpServletResponse#SC_CONFLICT SC_CONFLICT (409)} will be returned.
   */
  private int doSync(String urlString)
  {
    try
    {
      // Get the download stream. Invalid url, I/O errors or security checks will cause a
      // runtime error to be thrown.

      BufferedInputStream in = getDownloadStream(urlString);

      // Will download to temp file first. If creating temp file fails, an error is thrown.

      File tmpDownload = createTempFile();
      BufferedOutputStream out = getFileOutputStream(tmpDownload);

      // Read in socket, write out to temp file...

      byte[] buffer = new byte[8092];
      int len;

      try
      {
        while ((len = in.read(buffer)) != -1)
        {
          out.write(buffer, 0, len);
        }
      }
      finally
      {
        in.close();
        out.flush();
        out.close();
      }

      // We've got the complete download.
      // Time to do the switch from tmp to real.

      File downloads = (File)getServletContext().getAttribute(ClientStartup.SERVER_DOWNLOADS_ATTRIBUTE);

      // Split the original URL and use the last
      // name (assuming valid filename) as a package
      // file name...

      String[] paths = urlString.split("/");
      String filename = paths[paths.length - 1];
      File download = new File(downloads, filename);

      // Check if the file exists in the download directory.
      // If yes, we try to undeploy.

      undeploy(download);

      // Still keeping the original pkg safe, renaming it as
      // tmp to make space for the newly downloaded temporary
      // file to be renamed and moved into its place.

      File originalAsTemp = renameAsTemp(download);
      boolean success = rename(/*from*/ tmpDownload, /*to*/ download);

      if (!success)
      {
        // Our rename from tmp download file to original has failed...
        // Try to restore the original from tmp and bail out.

        if (originalAsTemp != null)
        {
          System.out.println("Downloading has failed. Restoring the original package.");

          // Get rid of the download we did..

          delete(tmpDownload);

          // And restore the original..

          success = rename(/*from*/ originalAsTemp, /*to*/ download);

          if (!success)
          {
            // this would be bad... we still have the original but can't rename it back
            // to become a deployable.

            throw new Error("Cannot restore original package from file '" + originalAsTemp + "'.");
          }
        }

        System.out.println("Could not remove original package: " + download);

        return HttpServletResponse.SC_CONFLICT;
      }

      // deploy the new download...

      deploy(download);

      // Finally delete the original

      if (originalAsTemp != null)
      {
        success = delete(originalAsTemp);

        if (!success)
          System.out.println("Cannot delete old package '" + originalAsTemp + "'.");
      }

      System.out.println("Downloaded and deployed " + download + ".");

      return HttpServletResponse.SC_OK;
    }
    catch (IOException e)
    {
      System.out.println("Unable to synchronize from URL '" + urlString + "': " + e.toString());

      return HttpServletResponse.SC_CONFLICT;
    }
    catch (SecurityException e)
    {
      System.out.println(
          "Insufficient security permission to complete synchronization: " + e.toString()
      );

      return HttpServletResponse.SC_CONFLICT;
    }
    catch (Error e)
    {
      System.out.println(
          "An error occured during the synchronization: " + e.toString()
      );

      return HttpServletResponse.SC_CONFLICT;
    }
  }


  /**
   * Deploys a given file to the kernel.
   *
   * @param file  file to deploy
   */
  private void deploy(File file)
  {
    // TODO : Remove once JBAS-4310 is fixed
    file = workaround_JBAS_4310_for_MSWindows(file, true /*deploy*/);

    Kernel kernel = (Kernel)getServletContext().getAttribute(ClientStartup.KERNEL_ATTRIBUTE);

    try
    {
      URL url = file.toURI().toURL();

      // Note that this is still the legacy JMX based API -- it's just much more convenient
      // to use with the simple deploy(URL) call compared to the half a dozen API calls of
      // the new VDF to accomplish the same simple task. And since we still need to drag the
      // legacy JMX kernel around (AS5 Beta 4 is not fully MC-fied), might as well make use
      // of it...

      kernel.getBus().invoke(
          MAIN_DEPLOYER_NAME,
          "deploy",
          new Object[] { url },
          new String[] { URL.class.getName() }
      );
    }
    catch (MalformedURLException e)
    {
      // Shouldn't really get here since we are using a known existing file location and
      // just converting it to a URL... in any case, report it

      System.out.println(file + ": " + e.toString());
    }
    catch (Throwable t)
    {
      System.out.println("Error deploying file '" + file + "': " + t.toString());
    }
  }

  /**
   * Checks if the given file exists and if it does, attempts to undeploy it from the kernel.
   *
   * @param download  full path to file to undeploy
   */
  private void undeploy(final File download)
  {
    boolean exists = AccessController.doPrivileged(

      new PrivilegedAction<Boolean>()
      {
        public Boolean run()
        {
          try
          {
            return download.exists();
          }
          catch (SecurityException e)
          {
            System.out.println(
                "Security Manager has denied access to file '" + download + "': " + e.toString()
            );

            return false;
          }
        }
      }
    );

    if (exists)
    {
      // TODO : remove once JBAS-4310 is fixed
      File _download = workaround_JBAS_4310_for_MSWindows(download, false /* undeploy */);

      Kernel kernel = (Kernel)getServletContext().getAttribute(ClientStartup.KERNEL_ATTRIBUTE);

      try
      {
        kernel.getBus().invoke(
            MAIN_DEPLOYER_NAME,
            "undeploy",
            new Object[] { _download.toURI().toURL() },
            new String[] { URL.class.getName() }
        );
      }
      catch (MalformedURLException e)
      {
        System.out.println(
            "File '" + download + "' did not transform into a valid URL: " + e.toString()
        );
      }
      catch (Throwable t)
      {
        // This is an error from the kernel, either the bus is faulty or the operation we
        // invoked failed. We'll report it but continue hoping (fruitlessly?) that the
        // deploy might still work...

        System.out.println(
            "Something went wrong with undeploying file '" + download + "': " + t.toString()
        );
      }
    }
  }

  /**
   * Attempts to create a system temporary file.    <p>
   *
   * The temp file will be scheduled for deletion on JVM exit. However, this cleanup only
   * occurs when the JVM exits normally. Therefore it's a good idea to try and delete
   * the temporary files explicitly once they are no longer needed, whenever possible.
   *
   * @return  file handle to the created temp file.
   *
   * @throws Error              if the temporary file could not be created for any reason -- this
   *                            could be due to problems with the native file system
   *                            (disk full, etc.) or due to insufficient security permissions
   *                            if security manager is installed
   */
  private File createTempFile()
  {
    return AccessController.doPrivileged(

      new PrivilegedAction<File>()
      {
        public File run()
        {
          try
          {
            // Try to create a temp file in default system tmp directory.
            // This may or may not be directed to JBoss's tmp dir...

            File tmpDownload = File.createTempFile(
                "ORC",  /* prefix */
                null,   /* default suffix */
                null    /* default system tmp dir */
            );

            // Schedule for deletion on JVM exit. Deletion may be denied by
            // security manager. No big deal, we may leave some clutter behind
            // but no reason to propagate this exception any further...

            try
            {
              tmpDownload.deleteOnExit();
            }
            catch (SecurityException e)
            {
              // Just report it...

              System.out.println(
                  "Cannot delete temporary file '" + tmpDownload + "'. You may " +
                  "need to remove these files manually (" + e.toString() + ")."
              );
            }

            return tmpDownload;
          }
          catch (IOException e)
          {
            throw new Error("Unable to create temporary file: " + e.toString(), e);
          }
          catch (SecurityException e)
          {
            throw new Error(
                "Cannot create a temporary file due to security restrictions: " + e.toString(), e
            );
          }
        }
      }
    );
  }

  /**
   * Attempts to open a buffered input stream to the given URL and download
   * a package specified in the URL string.   <p>
   *
   * Errors caused by I/O, invalid URL, or security checks will be thrown as runtime
   * error instances from this method. The calling code should handle them and
   * return an appropriate HTTP error code in response to the original HTTP request.   <p>
   *
   * If security manager is installed, all URLs used for downloading must be explicitly
   * granted connection permits.
   *
   * TODO : Currently only direct URL connections are made. Proxy configuration API is still missing.
   *
   * @param   urlString   package download URL as a string
   *
   * @return  buffered    input stream to the package specified in the URL
   *
   * @throws  Error       if the URL is not valid, there's a problem connecting to the given URL,
   *                      or security manager is installed and has denied connection to given URL.
   */
  private BufferedInputStream getDownloadStream(String urlString)
  {
    try
    {
      URL url = new URL(urlString);                       // May throw a malformed URL exception.

      // Not executing openConnection() in privileged block since
      // the URL is parameterized and could in theory be used to
      // bypass some security restrictions.

      URLConnection connection = url.openConnection(/* currently always direct connection */);

      InputStream stream = connection.getInputStream();   // May throw I/O exception

      return new BufferedInputStream(stream);
    }
    catch (MalformedURLException e)
    {
      throw new Error("URL '" + urlString + "' is not valid: " + e.toString(), e);
    }
    catch (IOException e)
    {
      throw new Error("Error connecting to '" + urlString + "': " + e.toString(), e);
    }
    catch (SecurityException e)
    {
      throw new Error(
          "Security Manager has denied connection to '" + urlString + "': " + e.toString(), e
      );
    }
  }

  /**
   * Opens a buffered output stream to a file.
   *
   * @param file file to open
   *
   * @return  buffered output stream to a file
   *
   * @throws Error              if the file cannot be created, opened, is a directory,
   *                            security manager denies write access or any other error occurs
   */
  private BufferedOutputStream getFileOutputStream(final File file)
  {
    return AccessController.doPrivileged(

        new PrivilegedAction<BufferedOutputStream>()
        {
          public BufferedOutputStream run()
          {
            try
            {
              return new BufferedOutputStream(new FileOutputStream(file));
            }
            catch (FileNotFoundException e)
            {
              throw new Error("File was not found: " + e.toString());
            }
            catch (SecurityException e)
            {
              throw new Error(
                  "Caller's security manager has denied write access to file '" + file + "'.");
            }
          }
        }
    );
  }


  /**
   * Renames a file as a temporary file using a well-known, specific algorithm. The file will
   * <b>not</b> be scheduled for deletion at JVM exit.  <p>
   *
   * This method can fail in getting a valid temporary file name, or the rename can fail
   * for other reasons (such as security restrictions, limitations or errors on the native
   * file system). In such a case the method returns a null.
   *
   * @param file    file to rename
   *
   * @return  file reference to the renamed temporary file, or null if there was a failure
   */
  private File renameAsTemp(final File file)
  {
    File tempFile = getTempFileName(file);

    // we can fail to get a temp file name...

    if (tempFile == null)
      return null;

    try
    {
      boolean success = file.renameTo(tempFile);

      if (success)
        return tempFile;
      else
        return null;
    }
    catch (SecurityException e)
    {
      System.out.println(e.toString());

      return null;
    }
  }

  /**
   * Creates a temporary file name using a specific, predictable algorithm. Note that this is
   * not guaranteed to succeed.   <p>
   *
   * The first attempt is to rename the file as [filename].tmp. Should that fail, a suffix
   * is added to the .tmp extension in the range of 1 to 100, so [filename].tmp1 to
   * [filename].tmp100 is attempted. Should that still fail, a null is returned.  <p>
   *
   * This method will also <b>delete</b> left-over temp files if they happen to match the
   * attempted temporary file name pattern.
   *
   * @param file    file name to use to construct the temporary file name
   *
   * @return  temporary file name or null
   */
  private File getTempFileName(final File file)
  {
    return AccessController.doPrivileged(

      new PrivilegedAction<File>()
      {
        public File run()
        {
          // Try to find a temp file name in the current directory
          // that isn't in use yet (ideally should be the first one
          // but you never know in what ways things might have failed)...

          File tmpFilename = new File(file.getPath() + ".tmp");

          boolean exist = false;

          try
          {
            exist = tmpFilename.exists();
          }
          catch (SecurityException ignored) {}

          if (!exist)
            return tmpFilename;

          else
          {
            // So, we have left over clutter, try to delete it...

            boolean success = false;

            try
            {
              success = tmpFilename.delete();
            }
            catch (SecurityException e)
            {
              System.out.println("Cannot delete '" + tmpFilename + "': " + e.toString());
            }

            // successfully deleted, reuse the tmp filename...

            if (success)
              return tmpFilename;

            else
            {
              // now in the 'this-will-never-happen' category -- can't
              // seem to delete temp files, so keep adding index # at the end..

              try
              {
                for (int i = 1; i < 100; ++i)
                {
                  tmpFilename = new File(tmpFilename.getPath() + ".tmp" + i);

                  if (!tmpFilename.exists())
                    return tmpFilename;

                  else
                  {
                    try
                    {
                      success = tmpFilename.delete();
                    }
                    catch (SecurityException e)
                    {
                      System.out.println("Cannot delete '" + tmpFilename + "': " + e.toString());
                    }

                    if (success)
                      return tmpFilename;
                  }
                }
              }
              catch (SecurityException ignored) {}

              // ok give up..

              return null;
            }
          }
        }
      }
    );
  }

  /**
   * Rename executed in a privileged block. The first argument should always be a temporary
   * file name created within this class (not parameterized through public API) for security
   * considerations.
   *
   * @param fromTempFile    the name of the temporary file to rename from
   * @param toFile          the name of file to rename to
   *
   * @return true if rename is successful, false if any error occured
   */
  private boolean rename(final File fromTempFile, final File toFile)
  {
    return AccessController.doPrivileged(
      new PrivilegedAction<Boolean>()
      {
        public Boolean run()
        {
          try
          {
            return fromTempFile.renameTo(toFile);
          }
          catch (SecurityException e)
          {
            System.out.println("Rename failed due to security reasons: " + e.toString());

            return false;
          }
        }
      }
    );
  }

  /**
   * Executes delete in a privileged block. This method should be called only from this class
   * on file names (mostly temporary files) created within the current request processing for
   * security reasons. Any invocation that enables external API to parameterize the file name
   * should be avoided.
   *
   * @param file    file to delete
   *
   * @return true if the file was deleted; false if the file did not exist or delete was not
   *         successful for any other reason (i.e. lack of security permissions to delete file)
   */
  private boolean delete(final File file)
  {
    return AccessController.doPrivileged(

      new PrivilegedAction<Boolean>()
      {
        public Boolean run()
        {
          try
          {
            // Returns true if downloadable file does not exist in the first place or
            // was successfully deleted, false otherwise...

            return !file.exists() || file.delete();
          }
          catch (SecurityException e)
          {
            System.out.println("Delete failed due to security reasons: " + e.toString());

            return false;
          }
        }
      }
    );
  }


  /////////////////////////////////////////////////////////////////////////////////////////////////
  //
  //    THROW-AWAY CODE
  //
  /////////////////////////////////////////////////////////////////////////////////////////////////

  /*
   *  Works around file locking issues on Microsoft Windows. This is only needed for testing
   *  the home server code on Windows platform, and only until issue JBAS-4310 in AS 5 branch
   *  is fixed (prevents redeployment due to file lock).
   */

  protected static void workaround_JBAS_4310_for_MSWindows(Kernel kernel) throws Throwable
  {
    File f = new File(System.getProperty("jboss.server.home.dir") + File.separator + "downloads");

    File[] files = f.listFiles();

    for (File file : files)
    {
      file = workaround_JBAS_4310_for_MSWindows(file, true /* deploy */);

      URL url = file.toURI().toURL();

      kernel.getBus().invoke(MAIN_DEPLOYER_NAME, "deploy", new Object[] { url }, new String[] { URL.class.getName() });
    }
  }

  private static File workaround_JBAS_4310_for_MSWindows(File deployment, boolean deploy)
  {
    if (deploy)
    {
      try
      {
        File tmpDir = new File(System.getProperty("jboss.server.temp.dir"));
        File tempFile = new File(tmpDir, deployment.getName());
        tempFile.deleteOnExit();

        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(tempFile));
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(deployment));

        byte[] buffer = new byte[8092];
        int len;

        try
        {
          while ((len = in.read(buffer)) != -1)
          {
            out.write(buffer, 0, len);
          }
        }
        finally
        {
          in.close();
          out.flush();
          out.close();
        }

        deployments.put(deployment, tempFile);

        return tempFile;
      }
      catch (Throwable t)
      {
        throw new Error("Windows Hack for JBAS-4310 failed: " + t.toString());
      }
    }

    else
    {
      return deployments.remove(deployment);
    }
  }

  private static Map<File, File> deployments = new ConcurrentHashMap<File, File>();
}
