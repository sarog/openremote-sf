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
    * @throws Exception the exception
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
    * If you want simulate several panels making polling request, you can run this method more times.
    * 
    * And also, if you want simulate: 
    *     some polling requests will time out, some won't.
    * You can run this method at the same time running the next test method(testDoPostWithoutTimeOut).
    * 
    * @throws Exception the exception
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
    * If you want simulate several panels making polling request, you can run this method more times.
    * And also, if you want simulate: 
    *     some polling requests will time out, some won't.
    * You can run this method at the same time running the previous test method(testDoPostWithoutTimeOut).
    * 
    * @throws Exception the exception
    */
   public void testDoPostWithoutTimeOut() throws Exception {
      WebConversation wc = new WebConversation();
      WebResponse wr = wc.getResponse("http://localhost:8080/controller/rest/polling/1,2,3");
      System.out.println(wr.getText());
   }

}
