package org.openremote.android.console.model;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


public class PollingHelper {

   private String pollingStatusIds;
   private boolean isPolling;
   private boolean isError;
   private HttpClient client;
   public PollingHelper(HashSet<Integer> ids) {
      Iterator<Integer> id = ids.iterator();
      if (id.hasNext()) {
         pollingStatusIds = id.next().toString();
      }
      while(id.hasNext()) {
         pollingStatusIds = pollingStatusIds + "," + id.next();
      }
      HttpParams params = new BasicHttpParams();
      HttpConnectionParams.setConnectionTimeout(params, 50 * 1000);
      HttpConnectionParams.setSoTimeout(params, 50 * 1000);
      client = new DefaultHttpClient(params);
   }
   
   public void requestCurrentStatusAndStartPolling(String serverUrl) {
      HttpPost post = new HttpPost(serverUrl + "/rest/status/" + pollingStatusIds);
      try {
         HttpResponse response = client.execute(post);
         if (response.getStatusLine().getStatusCode() == 200) {
            PollingStatusParser.parse(response.getEntity().getContent());
         }
      } catch (ClientProtocolException e) {
         e.printStackTrace();
      } catch (IOException e) {
         e.printStackTrace();
      }
   }
   
   public void doPolling() {
      
   }
   
   public void cancelPolling() {
      
   }
}
