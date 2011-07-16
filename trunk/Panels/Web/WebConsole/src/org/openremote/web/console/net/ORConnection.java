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
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
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
   private static final String HTTPS = "https";
   
   private HttpClient httpClient;
   private HttpRequestBase httpRequest;
   private HttpResponse httpResponse;
   private int statusCode;
   private InputStream responseData;
   
   /**
    * Instantiates a new OR connection without username and password.
    * 
    * @param url the url
    * @param httpMethod the http method
    */
   public ORConnection(String url, ORHttpMethod httpMethod) {
      this(url, httpMethod, null, null);
   }
   
   /**
    * Instantiates a new connection.
    * 
    * Please note the host name verification and SSL certificate trust are not
    * the same thing. The host name verification is an additional safeguard
    * one may want to execute in order to make sure the CN (common name) of
    * the certificate matches that of the target host. 
    * 
    * @param url the url
    * @param httpMethod the http method
    * @param username the username
    * @param password the password
    */
   public ORConnection(String url, ORHttpMethod httpMethod, String username, String password) {
   	this(url, httpMethod, username, password, false);
   }
   
	/**
	 * Instantiates a new OR connection while specifying JSON return type
	 * 
    * @param url the url
    * @param httpMethod the http method
    * @param username the username
    * @param password the password
    * @param jsonResponse JSON Response Required
	 */
   @SuppressWarnings("deprecation")
	public ORConnection(String url, ORHttpMethod httpMethod, String username, String password, boolean jsonResponse) {
      HttpParams params = new BasicHttpParams();
      HttpConnectionParams.setConnectionTimeout(params, 4 * 1000);
      HttpConnectionParams.setSoTimeout(params, 5 * 1000);
      
      httpClient = new DefaultHttpClient(params);
      try {
         URL uri = new URL(url);
         if (HTTPS.equals(uri.getProtocol())) {
            SSLSocketFactory socketFactory = new SSLSocketFactory(new TrustSelfSignedStrategy());
            socketFactory.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            Scheme sch = new Scheme(HTTPS, uri.getPort(), socketFactory);
            httpClient.getConnectionManager().getSchemeRegistry().register(sch);
         }
      } catch (MalformedURLException e) {
         log.error("MalformedURLException", e);
         throw new ORConnectionException("Request https cetificate error");
      } catch (KeyManagementException e) {
         log.error("KeyManagementException", e);
         throw new ORConnectionException("Request https cetificate error");
      } catch (UnrecoverableKeyException e) {
         log.error("UnrecoverableKeyException", e);
         throw new ORConnectionException("Request https cetificate error");
      } catch (NoSuchAlgorithmException e) {
         log.error("NoSuchAlgorithmException", e);
         throw new ORConnectionException("Request https cetificate error");
      } catch (KeyStoreException e) {
         log.error("KeyStoreException", e);
         throw new ORConnectionException("Request https cetificate error");
      }

      try {
         if (ORHttpMethod.POST.equals(httpMethod)) {
            httpRequest = new HttpPost(url);
         } else if (ORHttpMethod.GET.equals(httpMethod)) {
            httpRequest = new HttpGet(url);
         }
      } catch (IllegalArgumentException e) {
         log.error("Create HttpRequest fail" + url);
         throw new ORConnectionException("Create HttpRequest fail");
      }
      
      if (httpRequest == null) {
         log.error("Create HttpRequest fail" + url);
         return;
      }
      
      // If JSON Response required set Accept Header
      if (jsonResponse) {
      	httpRequest.addHeader("Accept", "application/json");
      }
      
      if (username != null && password != null) {
         BASE64Encoder base64Encoder = new BASE64Encoder();    
         String encodedUsernameAndPassword = base64Encoder.encode((username+":"+password).getBytes());
         httpRequest.addHeader("Authorization", "Basic " + encodedUsernameAndPassword);
      }
      
      try {
         httpResponse = httpClient.execute(httpRequest);
      } catch (ConnectException e) {
         log.error("ConnectException", e);
         throw new ORConnectionException(ControllerExceptionMessage.exceptionMessageOfCode(0));
      } catch (SocketTimeoutException e) {
         log.error("SocketTimeoutException", e);
         throw new ORConnectionException(ControllerExceptionMessage.exceptionMessageOfCode(0));
      } catch (ClientProtocolException e) {
         log.error("ClientProtocolException", e);
         throw new ORConnectionException(ControllerExceptionMessage.exceptionMessageOfCode(0));
      } catch (IOException e) {
         log.error("IOException", e);
         throw new ORConnectionException(ControllerExceptionMessage.exceptionMessageOfCode(0));
      }
      
      statusCode = httpResponse.getStatusLine().getStatusCode();
      if (statusCode == ControllerExceptionMessage.UNAUTHORIZED) {
         throw new NotAuthenticatedException(ControllerExceptionMessage.exceptionMessageOfCode(statusCode));
      } else if(statusCode == HTTP_SUCCESS) {
         try {
            responseData =  httpResponse.getEntity().getContent();
         } catch (IllegalStateException e) {
            log.error("Get the entity's content of httpresponse fail.", e);
            throw new ORConnectionException("Get the entity's content of httpresponse fail.");
         } catch (IOException e) {
            log.error("Get the entity's content of httpresponse fail.", e);
            throw new ORConnectionException("Get the entity's content of httpresponse fail.");
         }
      } else {
         throw new ORConnectionException(ControllerExceptionMessage.exceptionMessageOfCode(statusCode));
      }
   }

   
   /**
    * Gets the response data if request success.
    * 
    * @return the response data
    */
   public InputStream getResponseData() {
      return responseData;
   }
}
