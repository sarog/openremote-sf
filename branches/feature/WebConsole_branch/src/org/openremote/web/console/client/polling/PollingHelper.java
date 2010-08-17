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
package org.openremote.web.console.client.polling;

import java.util.HashSet;
import java.util.Iterator;

import org.openremote.web.console.client.Constants;
import org.openremote.web.console.client.utils.ClientDataBase;
import org.openremote.web.console.client.utils.ORListenerManager;
import org.openremote.web.console.client.utils.ORRoundRobin;
import org.openremote.web.console.client.window.LoginWindow;
import org.openremote.web.console.exception.ControllerExceptionMessage;

import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;

/**
 * Polling Helper, this class will setup a polling thread to listen 
 * and notify screen component status changes.
 */
public class PollingHelper {

   private String pollingStatusIds;
   private String serverUrl;
   private boolean isPolling;
   private Timer pollingTimer;
   private static String sessionId;
   
   public PollingHelper(HashSet<Integer> ids) {
      readSessionId();
      serverUrl = ClientDataBase.appSetting.getCurrentServer();
      
      Iterator<Integer> id = ids.iterator();
      if (id.hasNext()) {
         pollingStatusIds = id.next().toString();
      }
      while (id.hasNext()) {
         pollingStatusIds = pollingStatusIds + "," + id.next();
      }
   }
   
   /**
    * Request current screen components status and start polling.
    */
   public void requestCurrentStatusAndStartPolling() {
      if (isPolling) {
         return;
      }
      isPolling = true;
      
      // request current screen components status
      SimpleScriptTagProxy requestStatusProxy = new SimpleScriptTagProxy(serverUrl + "/rest/status/" + pollingStatusIds, new StatusResultReader());
      requestStatusProxy.load();
      
      // start polling.
      final SimpleScriptTagProxy pollingStatusProxy = new SimpleScriptTagProxy(serverUrl + "/rest/polling/" + sessionId
            + "/" + pollingStatusIds, new StatusResultReader());
      pollingTimer = new Timer() {
         @Override
         public void run() {
            isPolling = true;
            pollingStatusProxy.load();
         }

         @Override
         public void cancel() {
            super.cancel();
            pollingStatusProxy.cancel();
         }
         
      };
      pollingTimer.run();
   }
   
   /**
    * Cancel current screen's polling.
    */
   public void cancelPolling() {
      isPolling = false;
      if (pollingTimer != null) {
         pollingTimer.cancel();
      }
      pollingTimer = null;
   }
   
   /**
    * Read session id from cookies.
    */
   private void readSessionId() {
      if (sessionId == null) {
         sessionId = Cookies.getCookie("JSESSIONID");
      }
   }

   /**
    * Read sensor status.
    * 
    * @param jsonObj the json obj
    */
   private void readStatus(JSONObject jsonObj) {
      JSONArray statusArray = jsonObj.get("status").isArray();
      if (statusArray != null) {
         for (int i = 0; i < statusArray.size(); i++) {
            String id = statusArray.get(i).isObject().get("@id").isString().stringValue();
            String value = statusArray.get(i).isObject().get("#text").isString().stringValue();
            ClientDataBase.statusMap.put(id, value);
            ORListenerManager.getInstance().notifyOREventListener(Constants.ListenerPollingStatusIdFormat + id, null);
         }
      } else {
         JSONObject statusObj = jsonObj.get("status").isObject();
         String id = statusObj.get("@id").isString().stringValue();
         String value = statusObj.get("#text").isString().stringValue();
         ClientDataBase.statusMap.put(id, value);
         ORListenerManager.getInstance().notifyOREventListener(Constants.ListenerPollingStatusIdFormat + id, null);
      }
   }
   
   private class StatusResultReader implements JsonResultReader {
      public void read(JSONObject jsonObj) {
         if (jsonObj.containsKey("status")) {
            readStatus(jsonObj);
            if (pollingTimer != null) {
               pollingTimer.run();
            }
            return;
         } else if (jsonObj.containsKey("error")) {
            JSONObject errorObj = jsonObj.get("error").isObject();
            int errorCode = Integer.valueOf(errorObj.get("code").isString().stringValue());
            if (errorCode == ControllerExceptionMessage.GATEWAY_TIMEOUT) {
               if (pollingTimer != null) {
                  pollingTimer.run();
               }
               return;
            } else if (errorCode == ControllerExceptionMessage.UNAUTHORIZED) {
               cancelPolling();
               new LoginWindow();
            } else if (errorCode == ControllerExceptionMessage.REFRESH_CONTROLLER) {
               cancelPolling();
               MessageBox.alert("ERROR", errorObj.get("message").isString().stringValue(),
                     new Listener<MessageBoxEvent>() {
                        public void handleEvent(MessageBoxEvent be) {
                           Window.Location.reload();
                        }
                     });
            } else {
               cancelPolling();
               MessageBox.alert("ERROR", errorObj.get("message").isString().stringValue(),
                     new Listener<MessageBoxEvent>() {
                        public void handleEvent(MessageBoxEvent be) {
                           ORRoundRobin.doSwitch();
                        }
                     });
            }
         }
      }
   }
}
