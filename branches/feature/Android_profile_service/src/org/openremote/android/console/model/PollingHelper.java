package org.openremote.android.console.model;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.content.Context;
import android.os.AsyncTask;

public class PollingHelper {

   private String pollingStatusIds;
   private boolean isPolling;
   private boolean isError;
   private HttpClient client;
   private HttpPost post;
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
   }

   private void doPolling() {
      String deviceId = "96e79218965eb72c92a549dd5a330316";
      handleRequest(serverUrl + "/rest/polling/" + deviceId + "/" + pollingStatusIds);
   }

   private void handleRequest(String requestUrl) {
      post = new HttpPost(requestUrl);
      try {
         HttpResponse response = client.execute(post);
         int statusCode = response.getStatusLine().getStatusCode();
         if (statusCode == 200) {
            PollingStatusParser.parse(response.getEntity().getContent());
         }
         handleServerErrorWithStatusCode(statusCode);
      } catch (ClientProtocolException e) {
         isError = true;
         e.printStackTrace();
      } catch (IOException e) {
         isError = true;
         e.printStackTrace();
      }
   }
   
   public void cancelPolling() {
      isPolling = false;
      if (post != null) {
         post.abort();
      }
      client.getConnectionManager().closeIdleConnections(0, TimeUnit.MICROSECONDS);
   }

   private void handleServerErrorWithStatusCode(int statusCode) {
      if (statusCode != 200) {
         isError = true;
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
            isError = false;
            if (isPolling) {
               doPolling();
            }
            return;
         }

         if (errorMessage == null) {
            errorMessage = "Unknown error occured , satus code is " + statusCode;
         }
         ViewHelper.showAlertViewWithTitle(context, "Send Request Error", errorMessage);
         isPolling = false;
      } else {
         isError = false;
         if (isPolling) {
            doPolling();
         }
      }
   }

   

}
