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

package org.openremote.android.console.net;

import java.io.IOException;
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
import org.openremote.android.console.Constants;
import org.openremote.android.console.exceptions.ORConnectionException;
import org.openremote.android.console.util.SecurityUtil;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * This is responsible for manage the connection of android console to controller
 * 
 * @author handy 2010-04-27
 *
 */

public class ORConnection {   
   private HttpClient httpClient;
   private HttpRequestBase httpRequest;
   private HttpResponse httpResponse;
   private ORConnectionDelegate delegate;
   private Context context;
   /** 
    * Establish the HttpBasicAuthentication httpconnection depend on param <b>isNeedHttpBasicAuth</b> with url for caller,<br />
    * and then the caller can deal with the httprequest result within ORConnectionDelegate instance.
    */
   public ORConnection (final Context context, ORHttpMethod httpMethod, boolean isNeedHttpBasicAuth, String url, ORConnectionDelegate delegateParam) {
      handler = new Handler() {
         @Override
         public void handleMessage(Message msg) {
            int statusCode = msg.what;
            if (statusCode == 0) {
               connectionDidFailWithException(context, new ORConnectionException("Httpclient execute httprequest fail."));
            } else {
               dealWithResponse();
            }
         }
     };
      
      delegate = delegateParam;
      this.context = context;
      HttpParams params = new BasicHttpParams();
      HttpConnectionParams.setConnectionTimeout(params, 50 * 1000);
      HttpConnectionParams.setSoTimeout(params, 50 * 1000);
      httpClient = new DefaultHttpClient(params);
      if (ORHttpMethod.POST.equals(httpMethod)) {
         httpRequest = new HttpPost(url);
      } else if (ORHttpMethod.GET.equals(httpMethod)) {
         httpRequest = new HttpGet(url);
      }
      
      if (httpRequest == null) {
         throw new ORConnectionException("Create HttpRequest fail.");
      }
      
      if (isNeedHttpBasicAuth) {
         SecurityUtil.addCredentialToHttpRequest(context, httpRequest);
      }
      execute();
   }

   /** Execute the http request.*/
   public void execute() {
      new Thread(new Runnable() {
         public void run() {
            try {
               httpResponse = httpClient.execute(httpRequest);
            } catch (SocketTimeoutException e) {
               handler.sendEmptyMessage(0);
            } catch (ClientProtocolException e) {
               handler.sendEmptyMessage(0);
            } catch (IOException e) {
               handler.sendEmptyMessage(0);
            }
            handler.sendEmptyMessage(200);
         }
      }).start(); 
   }
   
   /** Deal with the response while httpconnection of android console to controller success. */
   private void dealWithResponse() {
      connectionDidReceiveResponse();
      connectionDidReceiveData();
   }
   
   /**  delegate methods of self */
   
   /** 
    * This method is invoked by self while the connection of android console to controller was failed,
    * and sends a notification to delegate with <b>urlConnectionDidFailWithException</b> method calling and
    * switching the connection of android console to a available controller server in groupmembers of self.
    */
   private void connectionDidFailWithException(Context context, ORConnectionException e) {
      delegate.urlConnectionDidFailWithException(e);
      ORControllerServerSwitcher.doSwitch(context);
   }
   
   /** 
    * This method is invoked by self while the connection of android console to controller received response,
    * and sends a notification to delegate with <b>urlConnectionDidReceiveResponse</b> method calling.
    */
   private void connectionDidReceiveResponse() {
      delegate.urlConnectionDidReceiveResponse(httpResponse);
   }
   
   /**
    * This method is invoked by self while the connection of android console to controller received data,
    * and sends a notification to delegate with <b>urlConnectionDidReceiveData</b> method calling.
    */
   private void connectionDidReceiveData() {
      try {
         if (httpResponse.getStatusLine().getStatusCode() == Constants.HTTP_SUCCESS) {
            delegate.urlConnectionDidReceiveData(httpResponse.getEntity().getContent());
         } else {
            new ORConnectionException("Get the entity's content of httpresponse fail."); 
         }
      } catch (IllegalStateException e) {
         throw new ORConnectionException("Get the entity's content of httpresponse fail.", e);
      } catch (IOException e) {
         throw new ORConnectionException("Get the entity's content of httpresponse fail.", e);
      }
   }
   
   /** 
    * Establish the httpconnection with url for caller<br />
    * and then the caller can deal with the httprequest result within ORConnectionDelegate instance.
    */
   @SuppressWarnings("finally")
   public static HttpResponse checkURLWithHTTPProtocol (Context context, ORHttpMethod httpMethod, String url, boolean isNeedBasicAuth) {
      HttpRequestBase request = null;
      HttpResponse response = null;
      
      HttpParams params = new BasicHttpParams();
      HttpConnectionParams.setConnectionTimeout(params, 5 * 1000);
      HttpConnectionParams.setSoTimeout(params, 5 * 1000);
      HttpClient client = new DefaultHttpClient(params);
      if (ORHttpMethod.POST.equals(httpMethod)) {
         request = new HttpPost(url);
      } else if (ORHttpMethod.GET.equals(httpMethod)) {
         request = new HttpGet(url);
      }
      
      if (request == null) {
         throw new ORConnectionException("Create HttpRequest fail.");
      }
      if (isNeedBasicAuth) {
         SecurityUtil.addCredentialToHttpRequest(context, request);
      }
      
        try {
         response = client.execute(request);
         return response;
      } catch (ClientProtocolException e) {
         Log.e("ERROR", "ClientProtocolException while checking URLWithHTTPProtocol.");
      } catch (SocketTimeoutException e) {
         Log.e("ERROR", "SocketTimeoutException while checking URLWithHTTPProtocol.");
      } catch (IOException e) {
         Log.e("ERROR", "IOException while checking URLWithHTTPProtocol.");
      } finally {
         return response;
      }
   }
   
   private Handler handler;
}