/**
 * 
 */
package org.openremote.beehive.rest;

import java.net.URISyntaxException;

import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.mock.MockHttpResponse;
import org.openremote.beehive.TestBase;

/**
 * @author Tomsky
 *
 */
public class IconRestServiceTest extends TestBase {
   
   public void _testGetIconsXml() throws URISyntaxException{
      Dispatcher dispatcher = RESTTestUtils.createDispatcher(IconRESTService.class);
      MockHttpRequest mockHttpRequest = MockHttpRequest.get("/icons");
      mockHttpRequest.accept("application/xml");
      
      MockHttpResponse mockHttpResponse = new MockHttpResponse();
      dispatcher.invoke(mockHttpRequest, mockHttpResponse);
      System.out.println(mockHttpResponse.getContentAsString());
   }
   
   public void testGetIconsJson() throws URISyntaxException{
      Dispatcher dispatcher = RESTTestUtils.createDispatcher(IconRESTService.class);
      MockHttpRequest mockHttpRequest = MockHttpRequest.get("/icons/Menu");
      mockHttpRequest.accept("application/json");
      
      MockHttpResponse mockHttpResponse = new MockHttpResponse();
      dispatcher.invoke(mockHttpRequest, mockHttpResponse);
      System.out.println(mockHttpResponse.getContentAsString());
   }
}
