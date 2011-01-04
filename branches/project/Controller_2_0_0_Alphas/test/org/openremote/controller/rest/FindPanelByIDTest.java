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
import java.util.Set;
import java.util.HashSet;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openremote.controller.Constants;
import org.openremote.controller.suite.RESTXMLTests;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;


/**
 * TODO
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 *
 */
public class FindPanelByIDTest
{

  // Constants ------------------------------------------------------------------------------------

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




  // Test Lifecycle -------------------------------------------------------------------------------

  /**
   * backup xml files.
   */
  @Before public void setup()
  {
    String panelXmlFixture = RESTXMLTests.getFixtureFile(Constants.PANEL_XML);

    RESTXMLTests.replaceControllerPanelXML(panelXmlFixture);
  }


  /**
   * restore xml files.
   */
  @After public void tearDown()
  {
    RESTXMLTests.restoreControllerPanelXML();
  }


  // Tests ----------------------------------------------------------------------------------------

  @Test public void requestFatherPanelProfile() throws Exception
  {

    URL panelList = new URL(RESTXMLTests.containerURL + RESTAPI_PANEL_DEFINITION_URI + "father");

    HttpURLConnection connection = (HttpURLConnection)panelList.openConnection();

    RESTXMLTests.assertHttpResponse(
        connection, HttpURLConnection.HTTP_OK, RESTXMLTests.ASSERT_BODY_CONTENT,
        Constants.MIME_APPLICATION_XML, Constants.CHARACTER_ENCODING_UTF8
    );

    Document doc = RESTXMLTests.getDOMDocument(connection.getInputStream());

    RESTXMLTests.assertOpenRemoteRootElement(doc);

    NodeList list = doc.getElementsByTagName("screens");

    Assert.assertTrue("Expected one <screens> element in panel definition.", list.getLength() == 1);

    list = doc.getElementsByTagName("groups");

    Assert.assertTrue("Expected one <groups> element in panel definition.", list.getLength() == 1);

    list = doc.getElementsByTagName("group");

    Assert.assertTrue("Expected at least two groups in the panel definition.", list.getLength() >= 2);

    Set<String> screenReferences = new HashSet<String>(10);

    for (int groupIndex = 0; groupIndex < list.getLength(); ++groupIndex)
    {
      Node group = list.item(groupIndex);

      if (group.getNodeType() != Node.ELEMENT_NODE)
        continue;

      NodeList includes = group.getChildNodes();

      for (int includeIndex = 0; includeIndex < includes.getLength(); ++includeIndex)
      {
        Node include = includes.item(includeIndex);

        if (include.getNodeType() != Node.ELEMENT_NODE)
          continue;

        if (!include.getNodeName().equalsIgnoreCase("include"))
          continue;
        
        NamedNodeMap attrs = include.getAttributes();

        Assert.assertNotNull("Expected to find attributes in include", attrs);
        Assert.assertNotNull("Expected to find 'ref' attribute in <include>, got null", attrs.getNamedItem("ref"));

        Node ref = attrs.getNamedItem("ref");

        String screenRef = ref.getNodeValue();

        screenReferences.add(screenRef);
      }
    }

    list = doc.getElementsByTagName("screen");

    Set<String> screenIDs = new HashSet<String>(10);

    Assert.assertTrue("Expected four <screen> elements in panel definition.", list.getLength() >= 4);

    for (int screenIndex = 0; screenIndex < list.getLength(); ++screenIndex)
    {
      Node screen = list.item(screenIndex);

      NamedNodeMap attrs = screen.getAttributes();

      Assert.assertNotNull("Expected to find 'id' attribute in screen, not null.", attrs.getNamedItem("id"));

      Node id = attrs.getNamedItem("id");

      screenIDs.add(id.getNodeValue());
    }

    Assert.assertTrue(screenIDs.contains("5"));
    Assert.assertTrue(screenIDs.contains("6"));
    Assert.assertTrue(screenIDs.contains("7"));
    Assert.assertTrue(screenIDs.contains("8"));
    


    for (String ref : screenReferences)
    {
      Assert.assertTrue("Expected to find screen with id: " + ref, screenIDs.contains(ref));
    }

  }


  @Test public void testGetNonExistentPanelProfile() throws Exception
  {
    URL doesNotExist = new URL(RESTXMLTests.containerURL + RESTAPI_PANEL_DEFINITION_URI + "doesNotExist");

    HttpURLConnection connection = (HttpURLConnection)doesNotExist.openConnection();

    RESTXMLTests.assertHttpResponse(
        connection, 428, RESTXMLTests.ASSERT_BODY_CONTENT,
        Constants.MIME_APPLICATION_XML, Constants.CHARACTER_ENCODING_UTF8
    );

    Document doc = RESTXMLTests.getDOMDocument(connection.getErrorStream());

    RESTXMLTests.assertErrorDocument(doc, 428);
  }



}
