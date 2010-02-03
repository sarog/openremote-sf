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
import org.openremote.beehive.SpringTestContext;
import org.openremote.beehive.TestBase;
import org.openremote.beehive.api.service.AccountService;
import org.openremote.beehive.domain.Account;
import org.openremote.beehive.domain.Template;
import org.openremote.beehive.rest.service.TemplateRESTTestService;


public class TemplateRESTServiceTest  extends TestBase {
   
   private AccountService accountService = (AccountService) SpringTestContext.getInstance().getBean("accountService");
   
   protected void setUp() throws Exception {
      super.setUp();
      Account a = new Account();
      Template t1 = new Template();
      t1.setAccount(a);
      t1.setName("t1");
      t1.setContent("content");
      a.addTemplate(t1);
      Template t2 = new Template();
      t2.setAccount(a);
      t2.setName("t2");
      t2.setContent("content");
      a.addTemplate(t2);
      accountService.save(a);
   }
   
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
