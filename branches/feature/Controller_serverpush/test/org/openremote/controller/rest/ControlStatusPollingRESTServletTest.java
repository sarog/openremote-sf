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
 * ControlStatusPollingRESTServlet TestCase with JUnit and HttpUnit.<br /><br />
 * 
 * It mainly test if the StatusPolling RESTful Service works well in the several situation.<br /><br />
 * 
 * <b>Test work flow with current Test class in IDE Eclipse3.4.0 .</b><br /><br />
 * <b>Step1:</b> Please deploy the controller application into tomcat in IDE Eclipse and then start tomcat application server in Eclipse.<br />
 * A listener named InitCachedStatusDBListener will be running after tomcat start up.<br />
 * This listener is responsible for initializing the cached DataBase which is used to cache statuses of devices.<br />
 * However, currently there is a thread(This thread is responsible for simulating the status change every 10seconds of devices and only change the status which of control id is "1") running 
 *   in the InitCachedStatusDBListener after InitCachedStatusDBListener was running.<br /><br />
 * 
 * <b>Step2:</b> There several situations exist: <b>Tomcat application server don't start up</b>, tomcat started up but <b>single request with time out</b>,<br />
 * <b>multi requests with time out</b>, <b>single request without time out</b> and <b>multi requests without time out</b>.<br />
 * So, you can test previous several situations with the following methods in this Test class.
 * 
 * @author Handy.Wang 2009-10-20
 */
public class ControlStatusPollingRESTServletTest extends TestCase {

   /**
    * <b>Situation 1</b><br />
    * 
    * Test StatusPolling RESTful Service when the App server didn't startup.<br />
    * So plean run this method in the situation of app server wasn't running.
    * 
    * @throws Exception the exception
    */
   public void testDoPostWithAppServerNotStartup() throws Exception {
      WebConversation wc = new WebConversation();
      WebResponse wr = wc.getResponse("http://localhost:8080/controller/rest/polling/96e79218965eb72c92a549dd5a330112/1,2");
      System.out.println(wr.getText());
   }
   
   /**
    * <b>Situation2</b><br />
    * 
    * Test StatusPolling RESTful Service when app server was running but the response will be time out.<br />
    * 
    * If you want simulate several panels making polling request, you can run this method more times.<br />
    * <b>And also</b>, if you want simulate: 
    *     some polling requests will time out, some won't, You can run this method at the same time running the method named testDoPostWithoutTimeOutSingleRequest.
    */
   public void testDoPostWithTimeOutSingleRequest() throws Exception {
      WebConversation wc = new WebConversation();
      WebRequest pollingGetMethodRequest = new GetMethodWebRequest("http://localhost:8080/controller/rest/polling/96e79218965eb72c92a549dd5a330112/3,4");
      WebResponse pollingResponse = wc.getResponse(pollingGetMethodRequest);      
      System.out.println(pollingResponse.getText());
   }
   
   /**
    * <b>Situation3</b><br />
    * 
    * This method simulate multi polling requests and the requests will time out.<br />
    */
   public void testDoPostWithTimeOutMultiRequests() throws Exception {
      for (int i = 1; i <=3; i++) {
         Thread t = new Thread() {
            @Override
            public void run() {
               try {
                  WebConversation wc = new WebConversation();
                  WebRequest pollingGetMethodRequest = new GetMethodWebRequest("http://localhost:8080/controller/rest/polling/96e79218965eb72c92a549dd5a330112/3,4");
                  WebResponse pollingResponse = wc.getResponse(pollingGetMethodRequest);
                  System.out.println(pollingResponse.getText());
               } catch (Exception e) {
                  e.printStackTrace();
               }
            }
         };
         t.start();
      }
      Thread.sleep(100000);
   }
   
   /**
    * <b>Situation4</b><br />
    * 
    * Test StatusPolling RESTful Service when app server was running and the response will be getted by client.<br />
    * 
    * If you want simulate several panels making polling request, you can run this method more times.<br />
    * <b>And also</b>, if you want simulate: 
    *     some polling requests will time out, some won't, You can run this method at the same time running the previous test method named testDoPostWithTimeOutSingleRequest.
    */
   public void testDoPostWithoutTimeOutSingleRequest() throws Exception {
      WebConversation wc = new WebConversation();
      WebResponse wr = wc.getResponse("http://localhost:8080/controller/rest/polling/96e79218965eb72c92a549dd5a330112/1,2");
      System.out.println(wr.getText());
   }
   
   /**
    * <b>Situation5</b><br />
    * 
    * This method simulate multi polling requests and response the corresponding result.<br />
    */
   public void testDoPostWithoutTimeOutMultiRequests() throws Exception {
      for (int i = 1; i <= 3; i++) {
         Thread t = new Thread() {
            @Override
            public void run() {
               try {
                  WebConversation wc = new WebConversation();
                  WebRequest pollingGetMethodRequest = new GetMethodWebRequest("http://localhost:8080/controller/rest/polling/96e79218965eb72c92a549dd5a330112/1,2");
                  WebResponse wr = wc.getResponse(pollingGetMethodRequest);
                  System.out.println(wr.getText());
               } catch (Exception e) {
                  e.printStackTrace();
               }
            }
         };
         t.start();
      }
      Thread.sleep(100000);
   }
}
