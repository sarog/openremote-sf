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

import java.net.URL;
import java.net.HttpURLConnection;
import java.util.Map;
import java.util.HashMap;

import org.junit.Test;
import org.junit.Assert;
import org.openremote.controller.suite.RESTXMLTests;
import org.openremote.controller.Constants;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;

/**
 * TODO
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class ListPanelIDsTest
{

  // Constants ------------------------------------------------------------------------------------

  /**
   * Controller 2.0 REST/XML URI for listing all available panel ids in the controller
   */
  public final static String RESTAPI_PANELS_URI = "/rest/panels";


  // Test Setup -----------------------------------------------------------------------------------







  
  // Tests ----------------------------------------------------------------------------------------

  @Test public void testListPanelIds() throws Exception
  {
    URL panelList = new URL(RESTXMLTests.containerURL + RESTAPI_PANELS_URI);

    HttpURLConnection connection = (HttpURLConnection)panelList.openConnection();

    RESTXMLTests.assertHttpResponse(
        connection, HttpURLConnection.HTTP_OK, RESTXMLTests.ASSERT_BODY_CONTENT,
        Constants.MIME_APPLICATION_XML, Constants.CHARACTER_ENCODING_UTF8
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

}

