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
import org.openremote.beehive.Constant;
import org.openremote.beehive.SpringTestContext;
import org.openremote.beehive.TemplateTestBase;
import org.openremote.beehive.api.service.impl.GenericDAO;
import org.openremote.beehive.domain.User;
import org.openremote.beehive.rest.service.TemplateRESTTestService;
import org.openremote.beehive.utils.FixtureUtil;

import com.sun.syndication.io.impl.Base64;


public class TemplateRESTServiceTest  extends TemplateTestBase {
   
   private GenericDAO genericDAO = (GenericDAO) SpringTestContext.getInstance().getBean("genericDAO");
   
   @Override
   protected void setUp() throws Exception {
      super.setUp();
      User user = new User();
      user.setUsername("dan");
      user.setPassword("cong");
      genericDAO.save(user);
   }

   @Override
   protected void tearDown() throws Exception {
      super.tearDown();
      User u = genericDAO.getByNonIdField(User.class, "username", "dan");
      genericDAO.delete(u);
   }

   public void testGetTemplatesByAccountInXML() throws URISyntaxException {
      Dispatcher dispatcher = RESTTestUtils.createDispatcher(TemplateRESTTestService.class);
      MockHttpRequest mockHttpRequest = MockHttpRequest.get("/account/1/templates");
      mockHttpRequest.accept("application/xml");

      MockHttpResponse mockHttpResponse = new MockHttpResponse();
      dispatcher.invoke(mockHttpRequest, mockHttpResponse);

      System.out.println(mockHttpResponse.getContentAsString());
   }
   public void testGetAllPublicTemplateInXML() throws URISyntaxException {
      Dispatcher dispatcher = RESTTestUtils.createDispatcher(TemplateRESTTestService.class);
      MockHttpRequest mockHttpRequest = MockHttpRequest.get("/account/0/templates");
      mockHttpRequest.accept("application/xml");

      MockHttpResponse mockHttpResponse = new MockHttpResponse();
      dispatcher.invoke(mockHttpRequest, mockHttpResponse);

      System.out.println(mockHttpResponse.getContentAsString());
   }
   public void testGetTemplatesByAccountInXMLWithInvalidAccountId() throws URISyntaxException {
      Dispatcher dispatcher = RESTTestUtils.createDispatcher(TemplateRESTTestService.class);
      MockHttpRequest mockHttpRequest = MockHttpRequest.get("/account/1a/templates");
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
   public void testGetTemplatesByAccountInJSONWithInvalidAccountId() throws URISyntaxException {
      Dispatcher dispatcher = RESTTestUtils.createDispatcher(TemplateRESTTestService.class);
      MockHttpRequest mockHttpRequest = MockHttpRequest.get("/account/1a/templates");
      mockHttpRequest.accept("application/json");
      
      MockHttpResponse mockHttpResponse = new MockHttpResponse();
      dispatcher.invoke(mockHttpRequest, mockHttpResponse);
      
      System.out.println(mockHttpResponse.getContentAsString());
   }

   public void testGetTemplateByIdInXML() throws URISyntaxException {
      Dispatcher dispatcher = RESTTestUtils.createDispatcher(TemplateRESTTestService.class);
      MockHttpRequest mockHttpRequest = MockHttpRequest.get("/account/1/template/2");
      mockHttpRequest.accept("application/xml");

      MockHttpResponse mockHttpResponse = new MockHttpResponse();
      dispatcher.invoke(mockHttpRequest, mockHttpResponse);

      System.out.println(mockHttpResponse.getContentAsString());
   }

   public void testGetTemplateByIdInJSON() throws URISyntaxException {
      Dispatcher dispatcher = RESTTestUtils.createDispatcher(TemplateRESTTestService.class);
      MockHttpRequest mockHttpRequest = MockHttpRequest.get("/account/1/template/2");
      mockHttpRequest.accept("application/json");

      MockHttpResponse mockHttpResponse = new MockHttpResponse();
      dispatcher.invoke(mockHttpRequest, mockHttpResponse);

      System.out.println(mockHttpResponse.getContentAsString());
   }

   public void testSaveTemplateIntoAccountInXMLWithoutAuth() throws URISyntaxException {
      Dispatcher dispatcher = RESTTestUtils.createDispatcher(TemplateRESTTestService.class);
      MockHttpRequest mockHttpRequest = MockHttpRequest.post("/account/1/template");
      MockHttpResponse mockHttpResponse = new MockHttpResponse();
      dispatcher.invoke(mockHttpRequest, mockHttpResponse);
   }
   
   public void testSaveTemplateIntoAccountInXML() throws URISyntaxException {
      Dispatcher dispatcher = RESTTestUtils.createDispatcher(TemplateRESTTestService.class);
      MockHttpRequest mockHttpRequest = MockHttpRequest.post("/account/1/template");
      mockHttpRequest.accept("application/xml");
      mockHttpRequest.contentType("application/x-www-form-urlencoded");
      mockHttpRequest.header(Constant.HTTP_BASIC_AUTH_HEADER_NAME, Constant.HTTP_BASIC_AUTH_HEADER_VALUE_PREFIX
            + Base64.encode("dan:cong"));
      String postData = "name=dan&content=" + FixtureUtil.getFileContent("template.json");
      mockHttpRequest.content(postData.getBytes());
      MockHttpResponse mockHttpResponse = new MockHttpResponse();
      dispatcher.invoke(mockHttpRequest, mockHttpResponse);

      System.out.println(mockHttpResponse.getContentAsString());
   }

   public void testSaveTemplateIntoAccountInJSON() throws URISyntaxException {
      Dispatcher dispatcher = RESTTestUtils.createDispatcher(TemplateRESTTestService.class);
      MockHttpRequest mockHttpRequest = MockHttpRequest.post("/account/1/template");
      mockHttpRequest.accept("application/json");
      mockHttpRequest.contentType("application/x-www-form-urlencoded");
      mockHttpRequest.header(Constant.HTTP_BASIC_AUTH_HEADER_NAME, Constant.HTTP_BASIC_AUTH_HEADER_VALUE_PREFIX
            + Base64.encode("dan:cong"));
      String postData = "name=dan&content=" + FixtureUtil.getFileContent("template.json");
      mockHttpRequest.content(postData.getBytes());
      MockHttpResponse mockHttpResponse = new MockHttpResponse();
      dispatcher.invoke(mockHttpRequest, mockHttpResponse);

      System.out.println(mockHttpResponse.getContentAsString());
   }
   
   public void testDeleteTemplateInAccount() throws URISyntaxException {
      Dispatcher dispatcher = RESTTestUtils.createDispatcher(TemplateRESTTestService.class);
      MockHttpRequest mockHttpRequest = MockHttpRequest.delete("/account/1/template/1");
      mockHttpRequest.header(Constant.HTTP_BASIC_AUTH_HEADER_NAME, Constant.HTTP_BASIC_AUTH_HEADER_VALUE_PREFIX
            + Base64.encode("dan:cong"));
      MockHttpResponse mockHttpResponse = new MockHttpResponse();
      dispatcher.invoke(mockHttpRequest, mockHttpResponse);

      System.out.println(mockHttpResponse.getContentAsString());
   }
   
   public void testDeleteTemplateInAccountWithoutAuth() throws URISyntaxException {
      Dispatcher dispatcher = RESTTestUtils.createDispatcher(TemplateRESTTestService.class);
      MockHttpRequest mockHttpRequest = MockHttpRequest.delete("/account/1/template/1");
      MockHttpResponse mockHttpResponse = new MockHttpResponse();
      dispatcher.invoke(mockHttpRequest, mockHttpResponse);
   }


}
