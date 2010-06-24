/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2010, OpenRemote Inc.
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
package org.openremote.web.console.net;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.log4j.Logger;
import org.openremote.web.console.exception.ControllerExceptionMessage;
import org.openremote.web.console.exception.NotAuthenticatedException;
import org.openremote.web.console.exception.ORConnectionException;

import sun.misc.BASE64Encoder;

/**
 * The Class ORConnection is for connection to controller and to accept response data.
 */
public class ORConnection {

   private static Logger log = Logger.getLogger(ORConnection.class);
   public static final int HTTP_SUCCESS = 200;
   
   private HttpClient httpClient;
   private HttpRequestBase httpRequest;
   private HttpResponse httpResponse;
   private int statusCode;
   private InputStream responseData;
   
   public ORConnection(String url, ORHttpMethod httpMethod) {
      this(url, httpMethod, null, null);
   }
   
   /**
    * Set up a connection, use http basic authorization.
    * 
    * @param url the url
    * @param httpMethod the http method
    * @param username the username
    * @param password the password
    */
   public ORConnection(String url, ORHttpMethod httpMethod, String username, String password) {
      HttpParams params = new BasicHttpParams();
      HttpConnectionParams.setConnectionTimeout(params, 4 * 1000);
      HttpConnectionParams.setSoTimeout(params, 5 * 1000);
      httpClient = new DefaultHttpClient(params);
      if (ORHttpMethod.POST.equals(httpMethod)) {
         httpRequest = new HttpPost(url);
      } else if (ORHttpMethod.GET.equals(httpMethod)) {
         httpRequest = new HttpGet(url);
      }
      
      if (httpRequest == null) {
         log.error("Create HttpRequest fail:" + url);
         return;
      }
      if (username != null && password != null) {
         BASE64Encoder base64Encoder = new BASE64Encoder();    
         String encodedUsernameAndPassword = base64Encoder.encode((username+":"+password).getBytes());
         httpRequest.addHeader("Authorization", "Basic " + encodedUsernameAndPassword);
      }
      
      try {
         httpResponse = httpClient.execute(httpRequest);
      } catch (SocketTimeoutException e) {
         log.error("SocketTimeoutException", e);
      } catch (ClientProtocolException e) {
         log.error("ClientProtocolException", e);
      } catch (IOException e) {
         log.error("IOException", e);
      }
      
      statusCode = httpResponse.getStatusLine().getStatusCode();
      if (statusCode == ControllerExceptionMessage.UNAUTHORIZED) {
         throw new NotAuthenticatedException(ControllerExceptionMessage.exceptionMessageOfCode(statusCode));
      } else if(statusCode == HTTP_SUCCESS) {
         try {
            responseData =  httpResponse.getEntity().getContent();
         } catch (IllegalStateException e) {
            log.error("Get the entity's content of httpresponse fail.", e);
         } catch (IOException e) {
            log.error("Get the entity's content of httpresponse fail.", e);
         }
      } else {
         throw new ORConnectionException(ControllerExceptionMessage.exceptionMessageOfCode(statusCode));
      }
   }
   
   public InputStream getResponseData() {
      return responseData;
   }
}
