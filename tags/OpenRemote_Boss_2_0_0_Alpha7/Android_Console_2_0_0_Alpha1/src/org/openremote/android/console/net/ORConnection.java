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
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.scheme.Scheme;
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
   protected ORConnectionDelegate delegate;
   private Context context;
   protected Handler handler;
   
   public static final int SUCCESS = 1;
   public static final int ERROR = 0;
   
   /** 
    * Establish the HttpBasicAuthentication httpconnection depend on param <b>isNeedHttpBasicAuth</b> and param <b>isUseSSLfor</b> with url caller,<br />
    * and then the caller can deal with the httprequest result within ORConnectionDelegate instance.
    */
   public ORConnection (final Context context, ORHttpMethod httpMethod, boolean isNeedHttpBasicAuth, String url, ORConnectionDelegate delegateParam) {
      initHandler(context);
      delegate = delegateParam;
      this.context = context;
      HttpParams params = new BasicHttpParams();
      HttpConnectionParams.setConnectionTimeout(params, 4 * 1000);
      HttpConnectionParams.setSoTimeout(params, 5 * 1000);
      httpClient = new DefaultHttpClient(params);
      if (ORHttpMethod.POST.equals(httpMethod)) {
         httpRequest = new HttpPost(url);
      } else if (ORHttpMethod.GET.equals(httpMethod)) {
         httpRequest = new HttpGet(url);
      }
      
      try {
         URL uri = new URL(url);
         if ("https".equals(uri.getProtocol())) {
            Scheme sch = new Scheme(uri.getProtocol(), new SelfCertificateSSLSocketFactory(), uri.getPort());
            httpClient.getConnectionManager().getSchemeRegistry().register(sch);
         }
      } catch (MalformedURLException e) {
         Log.e("ORConnection", "Create URL fail:" + url);
      }
      if (httpRequest == null) {
         Log.e("ORConnection", "Create HttpRequest fail:" + url);
         return;
      }
      
      if (isNeedHttpBasicAuth) {
         SecurityUtil.addCredentialToHttpRequest(context, httpRequest);
      }
      execute();
   }

   protected void initHandler(final Context context) {
      handler = new Handler() {
         @Override
         public void handleMessage(Message msg) {
            int message = msg.what;
            if (message == ERROR) {
               connectionDidFailWithException(context, new ORConnectionException("Httpclient execute httprequest fail."));
            } else {
               dealWithResponse();
            }
         }
     };
   }

   /** Execute the http request.*/
   public void execute() {
      new Thread(new Runnable() {
         public void run() {
            try {
               httpResponse = httpClient.execute(httpRequest);
               handler.sendEmptyMessage(SUCCESS);
            } catch (SocketTimeoutException e) {
               handler.sendEmptyMessage(ERROR);
            } catch (ClientProtocolException e) {
               handler.sendEmptyMessage(ERROR);
            } catch (IOException e) {
               handler.sendEmptyMessage(ERROR);
            }
         }
      }).start(); 
   }
   
   /** Deal with the response while httpconnection of android console to controller success. */
   protected void dealWithResponse() {
      connectionDidReceiveResponse();
      connectionDidReceiveData();
   }
   
   /**  delegate methods of self */
   
   /** 
    * This method is invoked by self while the connection of android console to controller was failed,
    * and sends a notification to delegate with <b>urlConnectionDidFailWithException</b> method calling.
    */
   protected void connectionDidFailWithException(Context context, ORConnectionException e) {
      delegate.urlConnectionDidFailWithException(e);
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
            Log.e("ORConnection", "Get the entity's content of httpresponse fail."); 
         }
      } catch (IllegalStateException e) {
         Log.e("ORConnection", "Get the entity's content of httpresponse fail.", e);
      } catch (IOException e) {
         Log.e("ORConnection", "Get the entity's content of httpresponse fail.", e);
      }
   }
   
   /** 
    * Establish the httpconnection with url for caller<br />
    * and then the caller can deal with the httprequest result within ORConnectionDelegate instance.
    * if check failed, return null.
    */
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
         Log.i("ORConnection", "checking URL creation failed:" + url);
         return null;
      }
      if (isNeedBasicAuth) {
         SecurityUtil.addCredentialToHttpRequest(context, request);
      }
      
      try {
         URL uri = new URL(url);
         if ("https".equals(uri.getProtocol())) {
            Scheme sch = new Scheme(uri.getProtocol(), new SelfCertificateSSLSocketFactory(), uri.getPort());
            client.getConnectionManager().getSchemeRegistry().register(sch);
         }
         response = client.execute(request);
      } catch (MalformedURLException e) {
         Log.e("ORConnection", "Create URL fail:" + url);
      } catch (ClientProtocolException e) {
         Log.i("ORConnection", "checking URL failed:" + url + ", " + e.getMessage());
      } catch (SocketTimeoutException e) {
         Log.i("ORConnection", "checking URL failed:" + url + ", " + e.getMessage());
      } catch (IOException e) {
         Log.i("ORConnection", "checking URL failed:" + url + ", " + e.getMessage());
      } catch (OutOfMemoryError e) {
         Log.i("ORConnection", "checking URL failed:" + url + ", " + e.getMessage());
      }
      return response;
   }
   
}