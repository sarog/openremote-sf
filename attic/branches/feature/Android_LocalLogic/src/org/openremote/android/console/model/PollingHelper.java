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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.openremote.android.console.Constants;
import org.openremote.android.console.Main;
import org.openremote.android.console.bindings.LocalSensor;
import org.openremote.android.console.bindings.LocalTask;
import org.openremote.android.console.net.IPAutoDiscoveryClient;
import org.openremote.android.console.net.ORControllerServerSwitcher;
import org.openremote.android.console.net.SelfCertificateSSLSocketFactory;
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
 * @author Eric Bariaux (eric@openremote.org)
 */
public class PollingHelper {

   /** The polling status ids is split by ",". */
   private String pollingStatusIds;
   private boolean isPolling;
   private HttpClient client;
   private HttpGet httpGet;
   private String serverUrl;
   private Context context;
   private static String deviceId = null;
   private Handler handler;
   private static final int NETWORK_ERROR = 0;
   
   private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
   /** We keep track of the scheduled local sensor poll, so we need to stop them */
   private final Map<Integer, ScheduledFuture<?>> scheduledLocalSensors = new HashMap<Integer, ScheduledFuture<?>>();


   /** We keep track of the scheduled tasks, so we need to stop them */
   private final Map<Integer, ScheduledFuture<?>> scheduledTasks = new HashMap<Integer, ScheduledFuture<?>>();

   /**
    * Instantiates a new polling helper.
    * 
    * @param ids the ids
    * @param context the context
    */
   public PollingHelper(HashSet<Integer> ids, final Context context) {
      this.context = context;
      this.serverUrl = AppSettingsModel.getSecuredServer(context);
      readDeviceId(context);
      
      Log.i("POLLING", "PollingHelper being constructed with ids: " + ids);
      // Iterate over the ids and only keep the ones that are remote for building the string will use to talk to ORB      
      Iterator<Integer> id = ids.iterator();
      HashSet<Integer> remoteIds = new HashSet<Integer>();
      while (id.hasNext()) {
    	  Integer anId = id.next();
    	  if (XMLEntityDataBase.getLocalSensor(anId) == null) {
    		  Log.i("POLLING", "Id " + anId + " is remote");
    		  remoteIds.add(anId);
    	  }
      }
      
      Iterator<Integer> remoteId = remoteIds.iterator();
      if (remoteId.hasNext()) {
         pollingStatusIds = remoteId.next().toString();
      }
      while (remoteId.hasNext()) {
         pollingStatusIds = pollingStatusIds + "," + remoteId.next();
      }
      
      handler = new Handler() {
         @Override
         public void handleMessage(Message msg) {
            isPolling = false;
            Log.i("POLLING", "polling failed and canceled." + msg.what);
            // only if the network is error, server error and request error, 
            // switch controller server, or endless loop would happen to switch server.
            int statusCode = msg.what;
            if (statusCode == NETWORK_ERROR || statusCode == ControllerException.SERVER_ERROR
                  || statusCode == ControllerException.REQUEST_ERROR) {
               ORControllerServerSwitcher.doSwitch(context);
            } else {
               ViewHelper.showAlertViewWithTitle(context, "Polling Error", ControllerException
                     .exceptionMessageOfCode(statusCode));
            }
         }
      };
   }

   /**
    * Request current status and start polling.
    */
   public void requestCurrentStatusAndStartPolling() {
	   // Only go to ORB if we have remote sensors
	   if (pollingStatusIds != null) {
	      HttpParams params = new BasicHttpParams();
	      HttpConnectionParams.setConnectionTimeout(params, 50 * 1000);
	      
	      //make polling socket timout bigger than Controller (50s)
	      HttpConnectionParams.setSoTimeout(params, 55 * 1000);
	      
	      client = new DefaultHttpClient(params);
	      if (isPolling) {
	         return;
	      }
	      
	      try {
	         URL uri = new URL(serverUrl);
	         if ("https".equals(uri.getProtocol())) {
	            Scheme sch = new Scheme(uri.getProtocol(), new SelfCertificateSSLSocketFactory(), uri.getPort());
	            client.getConnectionManager().getSchemeRegistry().register(sch);
	         }
	      } catch (MalformedURLException e) {
	         Log.e("POLLING", "Create URL fail:" + serverUrl);
	      }
	      isPolling = true;
	      handleRequest(serverUrl + "/rest/status/" + pollingStatusIds);
	   }
	   
	   // For local sensors, schedule the call of their static method for a new value
	   if (XMLEntityDataBase.localLogic != null) {
		   for (final LocalSensor sensor : XMLEntityDataBase.localLogic.getLocalSensors()) {
			   scheduledLocalSensors.put(sensor.getId(), scheduler.scheduleAtFixedRate(new Runnable() {
	               public void run() {
	            	   handleLocalSensor(sensor);
	               }
			   }, 0, sensor.getRefreshRate(), TimeUnit.MILLISECONDS));
		   }
	   }
      
	   // Handle the tasks here too, not entirely related but it'll do for now
	   for (final LocalTask task : XMLEntityDataBase.localLogic.getLocalTasks()) {
		   if (task.getFrequency() == 0) {
			   // 0 frequency = execute only once at start
			   // TODO: ! this is executed at each poll start, not once for the whole application lifecycle
			   handleLocalTask(task);
		   } else {
			   scheduledTasks.put(task.getId(), scheduler.scheduleAtFixedRate(new Runnable() {
	               public void run() {
	            	   handleLocalTask(task);
	               }
			   }, 0, task.getFrequency(), TimeUnit.MILLISECONDS));
		   }
	   }

      while (isPolling) {
         doPolling();
      }
   }

   private void doPolling() {
	   // Only go to ORB if we have remote sensors
	   if (pollingStatusIds != null) {
	      if (httpGet != null) {
	         httpGet.abort();
	         httpGet = null;
	      }
	      handleRequest(serverUrl + "/rest/polling/" + deviceId + "/" + pollingStatusIds);
	   }
   }

   /**
    * Execute request and handle the result.
    * 
    * @param requestUrl the request url
    */
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
               response.getEntity().getContent().close();
               handleServerErrorWithStatusCode(statusCode);
            }
            return;
         } catch (SocketTimeoutException e) {
            Log.i("POLLING", "polling [" + pollingStatusIds +"] socket timeout.");
         } catch (ClientProtocolException e) {
            isPolling = false;
            Log.e("POLLING", "polling [" + pollingStatusIds +"] failed.", e);
            handler.sendEmptyMessage(NETWORK_ERROR);
         } catch (SocketException e) {
            isPolling = false;
            Log.e("POLLING", "polling [" + pollingStatusIds +"] failed.", e);
            handler.sendEmptyMessage(NETWORK_ERROR);
         } catch (IllegalArgumentException e) {
            isPolling = false;
            Log.e("POLLING", "polling [" + pollingStatusIds +"] failed", e);
            handler.sendEmptyMessage(NETWORK_ERROR);
         } catch (OutOfMemoryError e) {
            isPolling = false;
            Log.e("POLLING", "OutOfMemoryError");
         } catch (InterruptedIOException e) {
            isPolling = false;
            Log.i("POLLING", "last polling [" + pollingStatusIds +"] has been shut down");
         } catch (IOException e) {
            isPolling = false;
            Log.i("POLLING", "last polling [" + pollingStatusIds +"] already aborted");
         }
         
      }
   }

   /**
    * Execute request for a local sensor, calling the appropriate method and handling the returned value.
    * 
    * @param sensor the local sensor to poll
    */
	private void handleLocalSensor(LocalSensor sensor) {
		try {
			Class<?> clazz = Class.forName(sensor.getClassName());
			Method m = clazz.getMethod(sensor.getMethodName(), (Class<?>[])null);
			String result = (String) m.invoke(null, (Object[])null);
			PollingStatusParser.handleLocalResult(sensor.getId(), result);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	   /**
	    * Execute a local task, calling the appropriate method.
	    * 
	    * @param task the local task to execute
	    */
		private void handleLocalTask(LocalTask task) {
			try {
				Class<?> clazz = Class.forName(task.getClassName());
				Method m = clazz.getMethod(task.getMethodName(), new Class<?>[]{Context.class});
				m.invoke(null, new Object[]{context});
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
   /**
    * Cancel the polling, abort http request.
    */
   public void cancelPolling() {
      Log.i("POLLING", "polling [" + pollingStatusIds +"] canceled");
      isPolling = false;
      if (httpGet != null) {
         httpGet.abort();
         httpGet = null;
      }
      
      // Stop the scheduled local polls and forget about them
      for (ScheduledFuture<?> scheduledPoll : scheduledLocalSensors.values()) {
    	  scheduledPoll.cancel(true);
      }
      scheduledLocalSensors.clear();

      // Stop the scheduled local tasks and forget about them
      for (ScheduledFuture<?> scheduledPoll : scheduledTasks.values()) {
    	  scheduledPoll.cancel(true);
      }
      scheduledTasks.clear();
}

   /**
    * Handle server error with status code.
    * If request timeout, return and start a new request.
    * 
    * @param statusCode the status code
    */
   private void handleServerErrorWithStatusCode(int statusCode) {
      if (statusCode != Constants.HTTP_SUCCESS) {
         httpGet = null;
         if (statusCode == ControllerException.GATEWAY_TIMEOUT) { // polling timeout, need to refresh
            return;
         } if (statusCode == ControllerException.REFRESH_CONTROLLER) {
            Main.prepareToastForRefreshingController();
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

   /**
    * Read the device id for send it in polling request url.
    * 
    * @param context the context
    */
   private static void readDeviceId(Context context) {
      if (deviceId == null) {
         TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
         if (IPAutoDiscoveryClient.isNetworkTypeWIFI) {
            deviceId = tm.getDeviceId();
         } else {
            deviceId = UUID.randomUUID().toString();
         }
      }
   }

   
}
