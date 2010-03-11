package org.openremote.android.console.model;

import java.io.IOException;
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

import android.content.Context;

public class PollingHelper {

   private String pollingStatusIds;
   private boolean isPolling;
   private HttpClient client;
   private HttpGet httpGet;
   private String serverUrl;
   private Context context;
   public PollingHelper(HashSet<Integer> ids, Context context) {
      this.context = context;
      this.serverUrl = AppSettingsModel.getCurrentServer(context);
      Iterator<Integer> id = ids.iterator();
      if (id.hasNext()) {
         pollingStatusIds = id.next().toString();
      }
      while (id.hasNext()) {
         pollingStatusIds = pollingStatusIds + "," + id.next();
      }
      HttpParams params = new BasicHttpParams();
      HttpConnectionParams.setConnectionTimeout(params, 50 * 1000);
      HttpConnectionParams.setSoTimeout(params, 50 * 1000);
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
      String deviceId = "96e79218965eb72c92a549dd5a330316";
      handleRequest(serverUrl + "/rest/polling/" + deviceId + "/" + pollingStatusIds);
   }

   private void handleRequest(String requestUrl) {
      httpGet = new HttpGet(requestUrl);
      if (!httpGet.isAborted()) {
         try {
            HttpResponse response = client.execute(httpGet);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
               PollingStatusParser.parse(response.getEntity().getContent());
            }
            handleServerErrorWithStatusCode(statusCode);
         } catch (ClientProtocolException e) {
            if (!isPolling) {
               return;
            }
            e.printStackTrace();
         } catch (IOException e) {
            if (!isPolling) {
               return;
            }
            e.printStackTrace();
         }
      }
   }
   
   public void cancelPolling() {
      isPolling = false;
      if (httpGet != null) {
         httpGet.abort();
         httpGet = null;
      }
//      client.getConnectionManager().shutdown();
   }

   private void handleServerErrorWithStatusCode(int statusCode) {
      if (statusCode != 200) {
         String errorMessage = null;
         switch (statusCode) {
         case 404:
            errorMessage = "The command was sent to an invalid URL.";
            break;
         case 500:
            errorMessage = "Error in controller. Please check controller log.";
            break;
         case 503:
            errorMessage = "Controller is not currently available.";
            break;
         case 504:// polling timeout, need to refresh
//            if (isPolling) {
//               return;
//            }
            httpGet = null;
            return;
         }

         if (errorMessage == null) {
            errorMessage = "Unknown error occured , satus code is " + statusCode;
         }
         ViewHelper.showAlertViewWithTitle(context, "Send Request Error", errorMessage);
         isPolling = false;
      } else {
         httpGet = null;
//         if (isPolling) {
//            doPolling();
//         }
         return;
      }
   }

   

}
