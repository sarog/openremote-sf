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
import java.io.InterruptedIOException;
import java.net.SocketException;
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
import org.openremote.android.console.Constants;
import org.openremote.android.console.Main;
import org.openremote.android.console.util.SecurityUtil;

import android.content.Context;
import android.content.Intent;
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
   private Handler handler;
   
   public PollingHelper(HashSet<Integer> ids, final Context context) {
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

      
      handler = new Handler() {
         @Override
         public void handleMessage(Message msg) {
            int statusCode = msg.what;
            ViewHelper.showAlertViewWithTitle(context, "Send Request Error", ControllerException
                  .exceptionMessageOfCode(statusCode));
         }
      };
   }

   public void requestCurrentStatusAndStartPolling() {
      HttpParams params = new BasicHttpParams();
      HttpConnectionParams.setConnectionTimeout(params, 50 * 1000);
      
      //make polling socket timout bigger than Controller (50s)
      HttpConnectionParams.setSoTimeout(params, 55 * 1000);
      
      client = new DefaultHttpClient(params);
      
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
      Log.i("POLLING", "polling start");
      handleRequest(serverUrl + "/rest/polling/" + deviceId + "/" + pollingStatusIds);
   }

   private void handleRequest(String requestUrl) {
      Log.i("POLLING", requestUrl);
      httpGet = new HttpGet(requestUrl);
      if (!httpGet.isAborted()) {
         SecurityUtil.addCredentialToHttpRequest(context, httpGet);
         try {
            HttpResponse response = client.execute(httpGet);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == Constants.HTTP_SUCCESS) {
               PollingStatusParser.parse(response.getEntity().getContent());
            } else {
               handleServerErrorWithStatusCode(statusCode);
            }
         } catch (SocketTimeoutException e) {
            isPolling = false;
            Log.e("POLLING", "polling socket timeout.");
            handler.sendEmptyMessage(0);
         } catch (ClientProtocolException e) {
            isPolling = false;
            Log.e("POLLING", "polling failed.");
         } catch (SocketException e) {
            isPolling = false;
            Log.e("POLLING", "polling failed.", e);
         } catch (IllegalArgumentException e) {
            isPolling = false;
            Log.e("POLLING", "polling failed", e);
         } catch (InterruptedIOException e) {
            isPolling = false;
            Log.i("POLLING", "last polling [" + pollingStatusIds +"] has been shut down");
         } catch (IOException e) {
            isPolling = false;
            Log.e("POLLING", "polling failed.", e);
         }
      }
   }
   
   public void cancelPolling() {
      Log.i("POLLING", "polling [" + pollingStatusIds +"] canceled");
      isPolling = false;
      if (httpGet != null) {
         httpGet.abort();
         httpGet = null;
      }
   }

   private void handleServerErrorWithStatusCode(int statusCode) {
      if (statusCode != Constants.HTTP_SUCCESS) {
         httpGet = null;
         if (statusCode == ControllerException.GATEWAY_TIMEOUT) { // polling timeout, need to refresh
            return;
         } if (statusCode == ControllerException.REFRESH_CONTROLLER) {
            Intent refreshControllerIntent = new Intent();
            refreshControllerIntent.setClass(context, Main.class);
            context.startActivity(refreshControllerIntent);
            // Notify the groupactiviy to finish.
            ORListenerManager.getInstance().notifyOREventListener(ListenerConstant.FINISH_GROUP_ACTIVITY, null);
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

   
}
