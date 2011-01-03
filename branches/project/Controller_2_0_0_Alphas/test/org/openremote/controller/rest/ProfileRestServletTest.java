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
import java.util.Map;
import java.util.HashMap;

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
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;

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

  // Constants ------------------------------------------------------------------------------------

  /**
   * Controller 2.0 REST/XML URI for listing all available panel ids in the controller
   */
  public final static String RESTAPI_PANELS_URI = "/rest/panels";

  /**
   * Controller 2.0 REST/XML URI for retrieving panel by id from the controller.
   */
  public final static String RESTAPI_PANEL_DEFINITION_URI = "/rest/panel/";

  /**
   * Controller 2.0 REST/XML URI for write commands to the controller.
   */
  public final static String RESTAPI_CONTROL_URI = "/rest/control/";

  /**
   * Controller 2.0 REST/XML URI for read commands.
   */
  public final static String RESTAPI_STATUS_URI = "/rest/status/";



  // Class Members --------------------------------------------------------------------------------


  public static URL containerURL = null;

  private final static Logger logger = Logger.getLogger("REST/XML API Tests");

  
  static
  {
    try
    {
      containerURL = new URL(
          "http://" + TestConstraint.WEBAPP_IP + ":" +
          TestConstraint.WEBAPP_PORT + "/controller"
      );
    }
    catch (Throwable t)
    {
      Assert.fail("Can't initialize tests: " + t.getMessage());
    }
  }


  // Instance Fields ------------------------------------------------------------------------------


  private String panelXmlPath;




  // Test Lifecycle -------------------------------------------------------------------------------

  /**
   * backup xml files.
   */
  @Before public void setup()
  {
    String panelXmlFixturePath = this.getClass().getClassLoader()
        .getResource(TestConstraint.FIXTURE_DIR + Constants.PANEL_XML).getFile();

    panelXmlPath = PathUtil.addSlashSuffix(ConfigFactory.getCustomBasicConfigFromDefaultControllerXML()
        .getResourcePath()) + Constants.PANEL_XML;

    if (new File(panelXmlPath).exists())
    {
       new File(panelXmlPath).renameTo(new File(panelXmlPath + ".bak"));
    }

    copyFile(panelXmlFixturePath, panelXmlPath);
  }

  /**
   * restore xml files.
   */
  @After public void tearDown()
  {
    if (new File(panelXmlPath + ".bak").exists())
    {
       new File(panelXmlPath + ".bak").renameTo(new File(panelXmlPath));
    }

    else
    {
       deleteFile(panelXmlPath);
    }
  }


  // Tests ----------------------------------------------------------------------------------------

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
   

  @Test public void requestAllPanels() throws Exception
  {
    URL panelList = new URL(containerURL + RESTAPI_PANELS_URI);

    HttpURLConnection connection = (HttpURLConnection)panelList.openConnection();

    RESTXMLTests.assertHttpResponse(
        connection, HttpURLConnection.HTTP_OK, RESTXMLTests.ASSERT_BODY_CONTENT,
        RESTXMLTests.APPLICATIONXML_MIMETYPE, Constants.CHARACTER_ENCODING_UTF8
    );

    Document doc = RESTXMLTests.getDOMDocument(connection.getInputStream());

    RESTXMLTests.assertOpenRemoteRootElement(doc);


    NodeList list = doc.getElementsByTagName("panel");

    Assert.assertTrue(list.getLength() >= 3);

    Map<String, String> panels = new HashMap<String, String>(3);

    for (int panelIndex = 0; panelIndex < list.getLength(); ++panelIndex)
    {
      Node panel = list.item(panelIndex);
      NamedNodeMap attrs = panel.getAttributes();

      Node id = attrs.getNamedItem("id");
      String idVal = id.getNodeValue();

      Node name = attrs.getNamedItem("name");
      String nameVal = name.getNodeValue();

      panels.put(idVal, nameVal);
    }

    Assert.assertNotNull(
        "Expected panel id 'MyIphone' but that was not found.",
        panels.get("MyIphone")
    );

    Assert.assertNotNull(
        "Expected panel id 'MyAndroid' but that was not found.",
        panels.get("MyAndroid")
    );

    Assert.assertNotNull(
        "Expected panel id '2fd894042c668b90aadf0698d353e579' but that was not found.",
        panels.get("2fd894042c668b90aadf0698d353e579")
    );


    Assert.assertTrue(
        "Expected panel id=MyIphone with name 'father', got " + panels.get("MyIphone"),
        panels.get("MyIphone").equalsIgnoreCase("father")
    );

    Assert.assertTrue(
        "Expected panel id=MyAndroid with name 'mother', got " + panels.get("MyAndroid"),
        panels.get("MyAndroid").equalsIgnoreCase("mother")
    );

    Assert.assertTrue(
        "Expected panel id=2fd894042c668b90aadf0698d353e579 with name 'me', got " + panels.get("2fd894042c668b90aadf0698d353e579"),
        panels.get("2fd894042c668b90aadf0698d353e579").equalsIgnoreCase("me")
    );

  }


  @Test public void testGetNonExistentPanelProfile() throws Exception
  {
    URL doesNotExist = new URL(containerURL + RESTAPI_PANEL_DEFINITION_URI + "doesNotExist");

    HttpURLConnection connection = (HttpURLConnection)doesNotExist.openConnection();

    RESTXMLTests.assertHttpResponse(
        connection, 428, RESTXMLTests.ASSERT_BODY_CONTENT,
        RESTXMLTests.APPLICATIONXML_MIMETYPE, Constants.CHARACTER_ENCODING_UTF8
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
