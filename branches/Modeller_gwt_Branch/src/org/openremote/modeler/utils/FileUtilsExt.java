/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2009, OpenRemote Inc.
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
package org.openremote.modeler.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;


/**
 * The Class FileUtils.
 * @author handy.wang
 */
public class FileUtilsExt {
   
   public static boolean deleteQuietly(File file) {
      if (file == null || !file.exists()) {
          return false;
      }
      try {
          if (file.isDirectory()) {
              cleanDirectory(file);
          }
      } catch (Exception e) {
      }

      try {
          return file.delete();
      } catch (Exception e) {
          return false;
      }
  }
   
   /**
    * Writes a String to a file creating the file if it does not exist using the default encoding for the VM.
    * 
    * @param file  the file to write
    * @param data  the content to write to the file
    * @throws IOException in case of an I/O error
    */
   public static void writeStringToFile(File file, String data) throws IOException {
       writeStringToFile(file, data, null);
   }
   
   /**
    * Writes a String to a file creating the file if it does not exist.
    *
    * NOTE: As from v1.3, the parent directories of the file will be created
    * if they do not exist.
    *
    * @param file  the file to write
    * @param data  the content to write to the file
    * @param encoding  the encoding to use, <code>null</code> means platform default
    * @throws IOException in case of an I/O error
    * @throws java.io.UnsupportedEncodingException if the encoding is not supported by the VM
    */
   public static void writeStringToFile(File file, String data, String encoding) throws IOException {
       OutputStream out = null;
       try {
           out = openOutputStream(file);
           IOUtils.write(data, out, encoding);
       } finally {
           IOUtils.closeQuietly(out);
       }
   }
   
   /**
    * Opens a {@link FileOutputStream} for the specified file, checking and
    * creating the parent directory if it does not exist.
    * <p>
    * At the end of the method either the stream will be successfully opened,
    * or an exception will have been thrown.
    * <p>
    * The parent directory will be created if it does not exist.
    * The file will be created if it does not exist.
    * An exception is thrown if the file object exists but is a directory.
    * An exception is thrown if the file exists but cannot be written to.
    * An exception is thrown if the parent directory cannot be created.
    * 
    * @param file  the file to open for output, must not be <code>null</code>
    * @return a new {@link FileOutputStream} for the specified file
    * @throws IOException if the file object is a directory
    * @throws IOException if the file cannot be written to
    * @throws IOException if a parent directory needs creating but that fails
    * @since Commons IO 1.3
    */
   public static FileOutputStream openOutputStream(File file) throws IOException {
       if (file.exists()) {
           if (file.isDirectory()) {
               throw new IOException("File '" + file + "' exists but is a directory");
           }
           if (file.canWrite() == false) {
               throw new IOException("File '" + file + "' cannot be written to");
           }
       } else {
           File parent = file.getParentFile();
           if (parent != null && parent.exists() == false) {
               if (parent.mkdirs() == false) {
                   throw new IOException("File '" + file + "' could not be created");
               }
           }
       }
       return new FileOutputStream(file);
   }   
   
   /**
    * Cleans a directory without deleting it.
    *
    * @param directory directory to clean
    * @throws IOException in case cleaning is unsuccessful
    */
   public static void cleanDirectory(File directory) throws IOException {
       if (!directory.exists()) {
           String message = directory + " does not exist";
           throw new IllegalArgumentException(message);
       }

       if (!directory.isDirectory()) {
           String message = directory + " is not a directory";
           throw new IllegalArgumentException(message);
       }

       File[] files = directory.listFiles();
       if (files == null) {  // null if security restricted
           throw new IOException("Failed to list contents of " + directory);
       }

       IOException exception = null;
       for (int i = 0; i < files.length; i++) {
           File file = files[i];
           try {
               forceDelete(file);
           } catch (IOException ioe) {
               exception = ioe;
           }
       }

       if (null != exception) {
           throw exception;
       }
   }
   
   /**
    * Deletes a file. If file is a directory, delete it and all sub-directories.
    * <p>
    * The difference between File.delete() and this method are:
    * <ul>
    * <li>A directory to be deleted does not have to be empty.</li>
    * <li>You get exceptions when a file or directory cannot be deleted.
    *      (java.io.File methods returns a boolean)</li>
    * </ul>
    *
    * @param file  file or directory to delete, must not be <code>null</code>
    * @throws NullPointerException if the directory is <code>null</code>
    * @throws FileNotFoundException if the file was not found
    * @throws IOException in case deletion is unsuccessful
    */
   public static void forceDelete(File file) throws IOException {
       if (file.isDirectory()) {
           deleteDirectory(file);
       } else {
           boolean filePresent = file.exists();
           if (!file.delete()) {
               if (!filePresent){
                   throw new FileNotFoundException("File does not exist: " + file);
               }
               String message =
                   "Unable to delete file: " + file;
               throw new IOException(message);
           }
       }
   }
   
   /**
    * Deletes a directory recursively. 
    *
    * @param directory  directory to delete
    * @throws IOException in case deletion is unsuccessful
    */
   public static void deleteDirectory(File directory) throws IOException {
       if (!directory.exists()) {
           return;
       }

       cleanDirectory(directory);
       if (!directory.delete()) {
           String message =
               "Unable to delete directory " + directory + ".";
           throw new IOException(message);
       }
   }
}
