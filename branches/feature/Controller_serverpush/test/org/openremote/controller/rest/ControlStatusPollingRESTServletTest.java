package org.openremote.controller.rest;

import junit.framework.TestCase;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;

/**
 * ControlStatusPollingRESTServlet TestCase with JUnit and HttpUnit
 * 
 * It mainly test if the StatusPolling RESTful Service works well in the several situation.
 * 
 * @author Handy.Wang 2009-10-20
 */
public class ControlStatusPollingRESTServletTest extends TestCase {

   /**
    * Situation 1
    * 
    * Test StatusPolling RESTful Service when the App server didn't startup.
    * So plean run this method in the situation of app server wasn't running.
    * 
    * @throws Exception
    */
   public void testDoPostWithAppServerNotStartup() throws Exception {
      WebConversation wc = new WebConversation();
      WebResponse wr = wc.getResponse("http://localhost:8080/controller/rest/polling/1,2,3");
      System.out.println(wr.getText());
   }
   
   /**
    * Situation2
    * 
    * Test StatusPolling RESTful Service when app server was running but the response will be time out.
    * 
    * If you want 
    * 
    * @throws Exception
    */
   public void testDoPostWithTimeOut() throws Exception {
      WebConversation wc = new WebConversation();
      WebRequest pollingGetMethodRequest = new GetMethodWebRequest("http://localhost:8080/controller/rest/polling/1,2,3,4");
      WebResponse pollingResponse = wc.getResponse(pollingGetMethodRequest);      
      System.out.println(pollingResponse.getText());
   }
   
   /**
    * Situation3
    * 
    * Test StatusPolling RESTful Service when app server was running and the response will be getted by client.
    * 
    * @throws Exception
    */
   public void testDoPostWithoutTimeOut() throws Exception {
      WebConversation wc = new WebConversation();
      WebResponse wr = wc.getResponse("http://localhost:8080/controller/rest/polling/1,2,3");
      System.out.println(wr.getText());
   }

}
