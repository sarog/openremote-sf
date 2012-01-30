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
package org.openremote.modeler.cache;

import java.io.File;
import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;

import org.openremote.modeler.domain.Panel;
import org.openremote.modeler.domain.User;
import org.openremote.modeler.domain.Account;
import org.openremote.modeler.configuration.PathConfig;
import org.openremote.modeler.client.Configuration;
import org.openremote.modeler.logging.LogFacade;
import org.openremote.modeler.exception.ConfigurationException;

/**
 * Resource cache based on local file system access. This class provides an API for handling
 * and caching account's file resources.
 *
 * @see ResourceCache
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class LocalFileCache implements ResourceCache
{

  // Class Members --------------------------------------------------------------------------------

  /**
   * Log category for this cache implementation.
   */
  private final static LogFacade cacheLog = LogFacade.getInstance(LogFacade.Category.CACHE);



  // Instance Fields ------------------------------------------------------------------------------

  /**
   *  Designer path configuration.
   */
  private PathConfig pathConfig;

  /**
   * The account with which this cache is associated with.
   */
  private Account account;




  // Constructors ---------------------------------------------------------------------------------


  /**
   * Constructs a new instance to manage operations on the given user account's local file cache.
   *
   * @param config    Designer configuration
   * @param user      The current user whose associated account and it's cache in local file
   *                  system will be manipulated.
   */
  public LocalFileCache(Configuration config, User user)
  {
    this.pathConfig = PathConfig.getInstance(config);

    this.account = user.getAccount();
  }



  // Implements ResourceCache ---------------------------------------------------------------------


  /**
   * Creates an export zip on the local file system (in this account's cache directory)
   * and returns a readable input stream from it. The zip archive contents is created based
   * on the current (un-versioned) in-memory object model of the designer.
   *
   * @param   panels    this account's current, in-memory object model of the designer UI,
   *                    configuration and associated artifacts
   *
   * @return  an input stream from a zip archive in the local filesystem cache containing
   *          all account artifacts
   *
   * @throws CacheOperationException
   *            if any of the local file system operations fail
   *
   * @throws ConfigurationException
   *            if there are security restrictions on any of the file access
   */
  @Override public InputStream getExportArchiveInputStream(Set<Panel> panels)
      throws CacheOperationException, ConfigurationException
  {
    File exportArchiveFile = createExportArchive(panels);

    try
    {
      return new BufferedInputStream(new FileInputStream(exportArchiveFile));
    }

    catch (Throwable t)
    {
      throw new CacheOperationException(
          "Failed to create input stream to export archive ''{0}'' : {1}",
          t, exportArchiveFile, t.getMessage()
      );
    }
  }



  // Public Instance Methods ----------------------------------------------------------------------



  /**
   * Creates an exportable zip file archive on the local file system in the configured
   * account's cache directory. <p>
   *
   * This archive is used to send user design and configuration changes, added resource
   * files, etc. as a single HTTP POST payload to Beehive server. <p>
   *
   * This implementation is based on the current object model used in Designer which is
   * not (yet) versioned. The list of artifacts included in the export archive therefore
   * include : <p>
   *
   * <ul>
   *   <li>panel.xml</li>
   *   <li>controller.xml</li>
   *   <li>panels.obj</li>
   *   <li>lircd.conf</li>
   *   <li>image resources</li>
   * </ul>
   *
   *
   * @param panels    the current, temporary and in-memory, object model state of the designer
   *
   * @return  reference to the export archive file in the account's cache directory
   *
   * @throws  CacheOperationException
   *              if any of the file operations fail
   *
   * @throws  ConfigurationException
   *              if there are any security restrictions on file access
   *
   */
  public File createExportArchive(Set<Panel> panels) throws CacheOperationException,
                                                            ConfigurationException
  {
    // TODO :
    //   - Note that this still has a functional dependency to the existing legacy
    //     resource service implementation that exports the appropriate XML files
    //     into the correct file cache directory first (initResource() call).
    

    // Collect all image file names (without path references to this account's
    // local cache) included in panel definitions and components in panels...

    Set<String> imageNames = new HashSet<String>();

    if (panels == null)
    {
      cacheLog.warn(
          "getAllImageNames(panels) was called with null argument (Account : {0})", account
      );
    }

    else
    {
      for (Panel panel : panels)
      {
        imageNames.addAll(Panel.getAllImageNames(panel));
      }
    }


    // Resolve the image names from Panel object model to actual paths to files
    // stored in this account's local cache...

    Set<File> imageFiles;

    try
    {
      imageFiles = resolveImagePaths(imageNames);
    }

    catch (FileNotFoundException e)
    {
      // TODO
      //   - Preserving existing semantics of throwing an exception in case any of the image
      //     files cannot be resolved in local cache, thus aborting the export operation.
      //     Unclear if this is a too strict behavior. If this issue starts appearing (thus
      //     this exception is thrown) may need to revisit the logic.

      throw new CacheOperationException(
          "Could not resolve all image paths in local file cache for account ID = {0} : {1}",
          e, account, e.getMessage()
      );
    }

    // File paths to add to export/upload archive...
    //   - panel.xml
    //   - controller.xml
    //   - panels.obj
    //   - lircd.conf
    //   - image resources

    File panelXMLFile = new File(pathConfig.panelXmlFilePath(account));
    File controllerXMLFile = new File(pathConfig.controllerXmlFilePath(account));
    File panelsObjFile = new File(pathConfig.getSerializedPanelsFile(account));
    File lircdFile = new File(pathConfig.lircFilePath(account));


    // Collect all the files going into the archive...

    Set<File> exportFiles = new HashSet<File>();
    exportFiles.addAll(imageFiles);
    exportFiles.add(panelXMLFile);
    exportFiles.add(controllerXMLFile);
    exportFiles.add(panelsObjFile);

    try
    {
      if (lircdFile.exists())
      {
        exportFiles.add(lircdFile);
      }
    }

    catch (SecurityException e)
    {
      throw new ConfigurationException(
          "Security manager denied read access to file ''{0}'' (Account : {1}) : {2}",
          e, lircdFile, account, e.getMessage()
      );
    }


    // Create export archive file (do not overwrite the existing beehive archive)...

    File targetFile = new File(pathConfig.openremoteZipFilePath(account) + ".export");

    try
    {
      if (targetFile.exists())
      {
        boolean success = targetFile.delete();

        if (!success)
        {
          throw new CacheOperationException(
              "Cannot complete export archive operation. Unable to delete pre-existing " +
              "file ''{0}'' (Account ID = {1})", targetFile.getAbsolutePath(), account
          );
        }
      }
    }

    catch (SecurityException e)
    {
      throw new ConfigurationException(
          "Security manager denied access to temporary export archive file ''{0}'' for " +
          "account ID = {1} : {2}", e, targetFile.getAbsolutePath(), account, e.getMessage()
      );
    }


    // Zip it up...

    compress(targetFile, exportFiles);


    // Done.

    return targetFile;
  }


  /**
   * Resolves a set of image file names against the local file cache of the account associated
   * with this cache instance.
   *
   * @param imageNames    set of image file names (without file paths)
   *
   * @return  set of file references to the local file cache of the account associated with
   *          this cache instance
   *
   * @throws FileNotFoundException
   *              If any one of the file names in the set cannot be resolved to a file
   *              path in account's cache directory. The exception message will contain
   *              the name of the image that failed a resolution to a local cache directory.
   *
   * @throws ConfigurationException
   *              If there are any security restrictions in accessing the file paths
   */
  public Set<File> resolveImagePaths(Set<String> imageNames) throws FileNotFoundException,
                                                                    ConfigurationException
  {
    File userFolder = new File(pathConfig.userFolder(account));

    Set<File> fileNames = new HashSet<File>();

    for (String fileName : imageNames)
    {
      //String fileName = image.getImageFileName();

      File imageFile = new File(userFolder, fileName);

      try
      {
        if (imageFile.exists())
        {
          fileNames.add(imageFile);
        }

        else
        {
          cacheLog.error(
              "Image file ''{0}'' for account ID = {1} was not found in local file resource cache.",
              imageFile.getName(), account.getOid()
          );

          throw new FileNotFoundException(fileName); 
        }
      }

      catch (SecurityException e)
      {
        throw new ConfigurationException(
            "Security manager has denied read access to file ''{0}'' (Account : {1}) : {2}",
            e, imageFile.getAbsolutePath(), account.getOid(), e.getMessage()
        );
      }
    }

    return fileNames;
  }






  // Private Instance Methods ---------------------------------------------------------------------

  /**
   * Compresses a set of files into a target zip archive.
   *
   * @param target    target file path where the zip archive will be stored
   * @param files     set of file paths to include in the zip archive
   *
   * @throws CacheOperationException
   *            if any of the zip file operations fail
   *
   * @throws ConfigurationException
   *            if there are any security restrictions about reading the set of included files
   *            or writing the target zip archive file
   */
  private void compress(File target, Set<File> files) throws CacheOperationException,
                                                             ConfigurationException
  {
    ZipOutputStream zipOutput = null;

    try
    {
      zipOutput = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(target)));

      for (File file : files)
      {
        BufferedInputStream fileInput = null;

        try
        {
          if (!file.exists())
          {
            throw new CacheOperationException(
                "Expected to add file ''{0}'' to export archive ''{1}'' (Account : {2}) but it " +
                "has gone missing (cause unknown). This can indicate implementation or deployment " +
                "error. Aborting export operation as a safety precaution.",
                file.getAbsolutePath(), target.getAbsolutePath(), account.getOid()
            );
          }

          fileInput = new BufferedInputStream(new FileInputStream(file));

          ZipEntry entry = new ZipEntry(file.getName());
          entry.setSize(file.length());
          entry.setTime(file.lastModified());

          zipOutput.putNextEntry(entry);

          cacheLog.debug("Added new export zip entry ''{0}''.", file.getName());

          int count, total = 0;
          int buffer = 2048;
          byte[] data = new byte[buffer];

          while ((count = fileInput.read(data, 0, buffer)) != -1)
          {
            zipOutput.write(data, 0, count);

            total += count;
          }

          zipOutput.flush();

          if (total != file.length())
          {
            throw new CacheOperationException(
                "Only wrote {0} out of {1} bytes when archiving file ''{2}'' (Account : {3}). " +
                "This could have occured either due implementation error or file I/O error. " +
                "Aborting archive operation to prevent a potentially corrupt export archive to " +
                "be created.", total, file.length(), file.getAbsolutePath(), account.getOid()
            );
          }

          else
          {
            cacheLog.debug(
                "Wrote {0} out of {1} bytes to zip entry ''{2}''",
                total, file.length(), file.getName()
            );
          }
        }

        catch (SecurityException e)
        {
          // we've messed up deployment... quite likely unrecoverable...

          throw new ConfigurationException(
              "Security manager has denied r/w access when attempting to read file ''{0}'' and " +
              "write it to archive ''{1}'' (Account : {2}) : {3}",
              e, file.getAbsolutePath(), target, account.getOid(), e.getMessage()
          );
        }

        catch (IllegalArgumentException e)
        {
          // This may occur if we overrun some fixed size limits in ZIP format...

          throw new CacheOperationException(
              "Error creating ZIP archive for account ID = {0} : {1}",
              e, account.getOid(), e.getMessage()
          );
        }

        catch (FileNotFoundException e)
        {
          throw new CacheOperationException(
              "Attempted to include file ''{0}'' in export archive but it has gone missing " +
              "(Account : {1}). Possible implementation error in local file cache. Aborting  " +
              "export operation as a precaution ({2})",
              e, file.getAbsolutePath(), account.getOid(), e.getMessage()
          );
        }

        catch (ZipException e)
        {
          throw new CacheOperationException(
              "Error writing export archive for account ID = {0} : {1}",
              e, account.getOid(), e.getMessage()
          );
        }

        catch (IOException e)
        {
          throw new CacheOperationException(
              "I/O error while creating export archive for account ID = {0}. " +
              "Operation aborted ({1})", e, account.getOid(), e.getMessage()
          );
        }

        finally
        {
          if (zipOutput != null)
          {
            try
            {
              zipOutput.closeEntry();
            }

            catch (Throwable t)
            {
              cacheLog.warn(
                  "Unable to close zip entry for file ''{0}'' in export archive ''{1}'' " +
                  "(Account : {2}) : {3}.",
                  t, file.getAbsolutePath(), target.getAbsolutePath(), account.getOid(), t.getMessage()
              );
            }
          }

          if (fileInput != null)
          {
            try
            {
              fileInput.close();
            }

            catch (Throwable t)
            {
              cacheLog.warn(
                  "Failed to close input stream from file ''{0}'' being added " +
                  "to export archive (Account : {1}) : {2}", t, file, account.getOid(), t.getMessage()
              );
            }
          }
        }
      }
    }

    catch (FileNotFoundException e)
    {
      throw new CacheOperationException(
          "Unable to create target export archive ''{0}'' for account {1) : {2}",
          e, target, account.getOid(), e.getMessage()
      );
    }

    finally
    {
      try
      {
        if (zipOutput != null)
        {
          zipOutput.close();
        }
      }

      catch (IOException e)
      {
        cacheLog.warn(
            "Failed to close the stream to export archive ''{0}'' : {1}.", 
            e, target, e.getMessage()
        );
      }
    }
  }


}

