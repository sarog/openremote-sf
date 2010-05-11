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
package org.openremote.android.console.model;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.HashSet;
import java.util.Iterator;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.openremote.android.console.util.SecurityUtil;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * Polling Helper, this class will setup a polling thread to listen 
 * and notify screen component status changes.
 * 
 * @author Tomsky Wang, Dan Cong
 * 
 */
public class PollingHelper {

   private String pollingStatusIds;
   private boolean isPolling;
   private HttpClient client;
   private HttpGet httpGet;
   private String serverUrl;
   private Context context;
   private static String deviceId = null;
   
   public PollingHelper(HashSet<Integer> ids, Context context) {
      this.context = context;
      this.serverUrl = AppSettingsModel.getCurrentServer(context);
      readDeviceId(context);
      
      Iterator<Integer> id = ids.iterator();
      if (id.hasNext()) {
         pollingStatusIds = id.next().toString();
      }
      while (id.hasNext()) {
         pollingStatusIds = pollingStatusIds + "," + id.next();
      }
      HttpParams params = new BasicHttpParams();
      HttpConnectionParams.setConnectionTimeout(params, 50 * 1000);
      
      //make polling socket timout bigger than Controller (50s)
      HttpConnectionParams.setSoTimeout(params, 55 * 1000);
      
      client = new DefaultHttpClient(params);
   }

   public void requestCurrentStatusAndStartPolling() {
      if (isPolling) {
         return;
      }
      isPolling = true;
      handleRequest(serverUrl + "/rest/status/" + pollingStatusIds);
      while (isPolling) {
         doPolling();
      }
   }

   private void doPolling() {
      handleRequest(serverUrl + "/rest/polling/" + deviceId + "/" + pollingStatusIds);
   }

   private void handleRequest(String requestUrl) {
      httpGet = new HttpGet(requestUrl);
      if (!httpGet.isAborted()) {
         SecurityUtil.addCredentialToHttpRequest(context, httpGet);
         try {
            HttpResponse response = client.execute(httpGet);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
               PollingStatusParser.parse(response.getEntity().getContent());
            } else {
               handleServerErrorWithStatusCode(statusCode);
            }
         } catch (SocketTimeoutException e) {
            isPolling = false;
            Log.e("socket timeout", "polling socket timeout.");
            handler.sendEmptyMessage(0);
         } catch (ClientProtocolException e) {
            if (!isPolling) {
               return;
            }
            Log.e("ClientProtocolException", "polling failed.");
         } catch (IOException e) {
            if (!isPolling) {
               return;
            }
            Log.e("IOException", "polling failed.");
         }
      }
   }
   
   public void cancelPolling() {
      isPolling = false;
      if (httpGet != null) {
         httpGet.abort();
         httpGet = null;
      }
   }

   private void handleServerErrorWithStatusCode(int statusCode) {
      if (statusCode != 200) {
         httpGet = null;
         if (statusCode == 504) { // polling timeout, need to refresh
            return;
         } else {
            isPolling = false;
            handler.sendEmptyMessage(statusCode);
         }
      }
   }

   private static void readDeviceId(Context context) {
      if (deviceId == null) {
         TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
         deviceId = tm.getDeviceId();
      }
   }

   private Handler handler = new Handler() {
      @Override
      public void handleMessage(Message msg) {
         int statusCode = msg.what;
//         if (statusCode == 401) {
//            new LoginDialog(context);
//            // TODO: restore polling.
//         } else {
            ViewHelper.showAlertViewWithTitle(context, "Send Request Error", ControllerException.exceptionMessageOfCode(statusCode));
//         }
      }
  };
}
