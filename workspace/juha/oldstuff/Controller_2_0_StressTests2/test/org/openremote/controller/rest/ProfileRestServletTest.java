/*
 * OpenRemote, the Home of the Digital Home. Copyright 2008-2010, OpenRemote Inc.
 * 
 * See the contributors.txt file in the distribution for a full listing of individual contributors.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package org.openremote.controller.rest;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openremote.controller.Constants;
import org.openremote.controller.TestConstraint;
import org.openremote.controller.utils.ConfigFactory;
import org.openremote.controller.utils.PathUtil;
import org.openremote.controller.utils.SecurityUtil;

import com.meterware.httpunit.HttpException;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;

public class ProfileRestServletTest {

   private String panelXmlPath;

   private Logger logger = Logger.getLogger(this.getClass().getName());

   @Before
   public void setup() {
      String panelXmlFixturePath = this.getClass().getClassLoader().getResource(
            TestConstraint.FIXTURE_DIR + Constants.PANEL_XML).getFile();
      panelXmlPath = PathUtil.addSlashSuffix(ConfigFactory.getCustomBasicConfigFromDefaultControllerXML().getResourcePath()) + Constants.PANEL_XML;
      if (new File(panelXmlPath).exists()) {
         new File(panelXmlPath).renameTo(new File(panelXmlPath + ".bak"));
      }
      copyFile(panelXmlFixturePath, panelXmlPath);

   }

   @After
   public void tearDown() {
      if (new File(panelXmlPath + ".bak").exists()) {
         new File(panelXmlPath + ".bak").renameTo(new File(panelXmlPath));
      } else {
         deleteFile(panelXmlPath);
      }
   }

   @Test
   public void requestAllPanels() throws Exception {

      WebConversation wc = new WebConversation();
      WebRequest request = SecurityUtil.getSecuredRequest(wc, "http://127.0.0.1:" + TestConstraint.WEBAPP_PORT
            + "/controller/rest/panels");
      try {
         WebResponse wr = wc.getResponse(request);
         System.out.println(wr.getText());
      } catch (HttpException e) {
         if (e.getResponseCode() == 504) {
            logger.info("Polling request was  timeout.");
         }
      }

   }

   private void copyFile(String src, String dest) {
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

   private void deleteFile(String fileName) {

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
