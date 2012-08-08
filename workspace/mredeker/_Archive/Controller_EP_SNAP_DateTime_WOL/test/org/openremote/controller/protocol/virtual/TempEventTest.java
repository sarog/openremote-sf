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
package org.openremote.controller.protocol.virtual;

import java.net.URL;
import java.net.HttpURLConnection;

import org.junit.Test;
import org.junit.Assert;
import org.openremote.controller.suite.RESTTests;
import org.openremote.controller.Constants;
import org.openremote.controller.service.ServiceContext;
import org.openremote.controller.service.ControllerXMLChangeService;
import org.openremote.controller.rest.FindPanelByIDTest;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/**
 * TODO
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class TempEventTest
{


  @Test public void testTemperatureEvents() throws Exception
  {
   // RESTTests.replaceControllerPanelXML("VirtualTempEventListenerAndRangeSensor-controller.xml");

    URL statusRequest = new URL(RESTTests.containerURL + FindPanelByIDTest.RESTAPI_STATUS_URI + "1002");

    ControllerXMLChangeService deployer = ServiceContext.getDeployer();
    deployer.refreshController();
    
    HttpURLConnection connection = (HttpURLConnection)statusRequest.openConnection();

//    RESTTests.assertHttpResponse(
//        connection, Constants.HTTP_RESPONSE_INVALID_PANEL_XML,
//        RESTTests.ASSERT_BODY_CONTENT, Constants.MIME_APPLICATION_XML,
//        Constants.CHARACTER_ENCODING_UTF8
//    );

    Document doc = RESTTests.getDOMDocument(connection.getInputStream());

//    RESTTests.assertOpenRemoteRootElement(doc);

    NodeList list = doc.getElementsByTagName("status");

    Assert.assertTrue(list.getLength() == 1);

  }
}

