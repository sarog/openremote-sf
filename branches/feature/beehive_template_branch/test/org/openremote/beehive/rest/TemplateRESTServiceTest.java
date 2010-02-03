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
package org.openremote.beehive.rest;

import java.net.URISyntaxException;

import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.mock.MockHttpResponse;
import org.openremote.beehive.TemplateTestBase;
import org.openremote.beehive.rest.service.TemplateRESTTestService;


public class TemplateRESTServiceTest  extends TemplateTestBase {
   
   
   public void testGetTemplatesByAccountInXML() throws URISyntaxException {
      Dispatcher dispatcher = RESTTestUtils.createDispatcher(TemplateRESTTestService.class);
      MockHttpRequest mockHttpRequest = MockHttpRequest.get("/account/1/templates");
      mockHttpRequest.accept("application/xml");

      MockHttpResponse mockHttpResponse = new MockHttpResponse();
      dispatcher.invoke(mockHttpRequest, mockHttpResponse);

      System.out.println(mockHttpResponse.getContentAsString());
   }
   
   public void testGetTemplatesByAccountInJSON() throws URISyntaxException {
      Dispatcher dispatcher = RESTTestUtils.createDispatcher(TemplateRESTTestService.class);
      MockHttpRequest mockHttpRequest = MockHttpRequest.get("/account/1/templates");
      mockHttpRequest.accept("application/json");

      MockHttpResponse mockHttpResponse = new MockHttpResponse();
      dispatcher.invoke(mockHttpRequest, mockHttpResponse);

      System.out.println(mockHttpResponse.getContentAsString());
   }

}
