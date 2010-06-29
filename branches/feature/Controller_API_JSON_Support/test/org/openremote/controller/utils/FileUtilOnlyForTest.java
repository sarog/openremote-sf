/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2010, OpenRemote Inc.
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
package org.openremote.controller.utils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * 
 * @author handy.wang 2010-06-29
 *
 */
public class FileUtilOnlyForTest {
   
   public static void copyFile(String src, String dest) {
      File inputFile = new File(src);
      File outputFile = new File(dest);

      FileReader in;
      try {
         in = new FileReader(inputFile);
         if (!outputFile.getParentFile().exists()) {
            outputFile.getParentFile().mkdirs();
         }
         if (!outputFile.exists()) {
            outputFile.createNewFile();
         }
         FileWriter out = new FileWriter(outputFile);
         int c;

         while ((c = in.read()) != -1) {
            out.write(c);
         }

         in.close();
         out.close();
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   public static void deleteFile(String fileName) {

      // A File object to represent the filename
      File f = new File(fileName);

      // Make sure the file or directory exists and isn't write protected
      if (!f.exists()) {
         throw new IllegalArgumentException("Delete: no such file or directory: " + fileName);
      }

      if (!f.canWrite()) {
         throw new IllegalArgumentException("Delete: write protected: " + fileName);
      }

      // If it is a directory, make sure it is empty
      if (f.isDirectory()) {
         String[] files = f.list();
         if (files.length > 0) throw new IllegalArgumentException("Delete: directory not empty: " + fileName);
      }

      // Attempt to delete it
      boolean success = f.delete();

      if (!success) {
         throw new IllegalArgumentException("Delete: deletion failed");
      }
   }

}
