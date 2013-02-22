package org.openremote.beehive.rest;

import java.net.URISyntaxException;

import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.mock.MockHttpResponse;
import org.openremote.beehive.TestBase;

/**
 * User: allenwei
 * Date: 2009-2-9
 * Time: 16:47:46
 */
public class ModelRESTServiceTest extends TestBase {


    public void testGetModelsXml() throws URISyntaxException {
        Dispatcher dispatcher = RESTTestUtils.createDispatcher(ModelRESTService.class);
        MockHttpRequest mockHttpRequest = MockHttpRequest.get("/lirc/3m/");
        mockHttpRequest.accept("application/xml");

        MockHttpResponse mockHttpResponse = new MockHttpResponse();
        dispatcher.invoke(mockHttpRequest, mockHttpResponse);

        System.out.println(mockHttpResponse.getContentAsString());

    }

    public void testGetModelsJson() throws URISyntaxException {
        Dispatcher dispatcher = RESTTestUtils.createDispatcher(ModelRESTService.class);
        MockHttpRequest mockHttpRequest = MockHttpRequest.get("/lirc/3m");
        mockHttpRequest.accept("application/json");

        MockHttpResponse mockHttpResponse = new MockHttpResponse();
        dispatcher.invoke(mockHttpRequest, mockHttpResponse);

        System.out.println(mockHttpResponse.getContentAsString());

    }
}