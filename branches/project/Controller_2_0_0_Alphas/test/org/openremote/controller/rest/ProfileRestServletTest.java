/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2011, OpenRemote Inc.
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
package org.openremote.controller.rest;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openremote.controller.Constants;
import org.openremote.controller.TestConstraint;
import org.openremote.controller.suite.RESTXMLTests;
import org.openremote.controller.utils.ConfigFactory;
import org.openremote.controller.utils.PathUtil;
import org.openremote.controller.utils.SecurityUtil;
import org.w3c.dom.Document;

import com.meterware.httpunit.HttpException;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;

/**
 * TODO
 *
 */
public class ProfileRestServletTest
{

   private String panelXmlPath;

   private Logger logger = Logger.getLogger(this.getClass().getName());

   /**
    * backup xml files.
    */
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

   /**
    * restore xml files.
    */
   @After
   public void tearDown() {
      if (new File(panelXmlPath + ".bak").exists()) {
         new File(panelXmlPath + ".bak").renameTo(new File(panelXmlPath));
      } else {
         deleteFile(panelXmlPath);
      }
   }


   @Test
   public void requestFatherPanelProfile() throws Exception {

      WebConversation wc = new WebConversation();
      WebRequest request = SecurityUtil.getSecuredRequest(wc, "http://127.0.0.1:" + TestConstraint.WEBAPP_PORT
            + "/controller/rest/panel/father");
      try {
         WebResponse wr = wc.getResponse(request);
         String expectedXMLFilePath = this.getClass().getClassLoader().getResource(
               TestConstraint.FIXTURE_DIR + "fatherProfile.xml").getFile();
         File expectedXMLFile = new File(expectedXMLFilePath);
         String expectedXML = FileUtils.readFileToString(expectedXMLFile);
         String actualXML = wr.getText();
         Assert.assertEquals(expectedXML, actualXML);
      } catch (HttpException e) {
         if (e.getResponseCode() == 504) {
            logger.info("Polling request was  timeout.");
         }
      }

   }
   

//  @Test public void requestAllPanels() throws Exception {}


  @Test public void testGetNonExistentPanelProfile() throws Exception
  {
    URL doesNotExist = new URL("http://" + TestConstraint.WEBAPP_IP + ":" + TestConstraint.WEBAPP_PORT + "/controller/rest/panel/doesNotExist");

    HttpURLConnection connection = (HttpURLConnection)doesNotExist.openConnection();

    RESTXMLTests.assertHttpResponse(
        connection, 428, RESTXMLTests.ASSERT_BODY_CONTENT,
        RESTXMLTests.APPLICATIONXML_MIMETYPE, RESTXMLTests.CHARSET_UTF8
    );

    Document doc = RESTXMLTests.getDOMDocument(connection.getErrorStream());

    RESTXMLTests.assertErrorDocument(doc, 428);
  }











  // Helpers --------------------------------------------------------------------------------------

  private void copyFile(String src, String dest)
  {
    File inputFile = new File(src);
    File outputFile = new File(dest);

    FileReader in;

    try
    {
      in = new FileReader(inputFile);

      if (!outputFile.getParentFile().exists())
      {
        outputFile.getParentFile().mkdirs();
      }

      if (!outputFile.exists())
      {
        outputFile.createNewFile();
      }

      FileWriter out = new FileWriter(outputFile);

      int c;

      while ((c = in.read()) != -1)
      {
        out.write(c);
      }

      in.close();
      out.close();
    }

    catch (IOException e)
    {
      e.printStackTrace();
    }
  }


  private void deleteFile(String fileName)
  {
    // A File object to represent the filename...

    File f = new File(fileName);


    // Make sure the file or directory exists and isn't write protected...

    if (!f.exists())
    {
       throw new IllegalArgumentException("Delete: no such file or directory: " + fileName);
    }

    if (!f.canWrite())
    {
       throw new IllegalArgumentException("Delete: write protected: " + fileName);
    }

    // If it is a directory, make sure it is empty...

    if (f.isDirectory())
    {
       String[] files = f.list();

       if (files.length > 0)
         throw new IllegalArgumentException("Delete: directory not empty: " + fileName);
    }

    // Attempt to delete it...

    boolean success = f.delete();

    if (!success)
    {
       throw new IllegalArgumentException("Delete: deletion failed");
    }
  }

}
