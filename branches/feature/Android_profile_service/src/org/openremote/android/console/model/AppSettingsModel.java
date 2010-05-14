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

import java.io.Serializable;
import java.util.ArrayList;

import org.openremote.android.console.Constants;
import org.openremote.android.console.net.IPAutoDiscoveryClient;
import org.openremote.android.console.net.IPAutoDiscoveryServer;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Provides accessing to Application Settings.
 * 
 * @author Tomsky Wang
 * @author Dan Cong
 *
 */
public class AppSettingsModel implements Serializable {

   private static final long serialVersionUID = Constants.MODEL_VERSION;
   private static final String APP_SETTINGS = "appSettings";
   private static final String CUSTOM_SERVERS = "customServers";
   private static final String CURRENT_SERVER = "currentServer";
   private static final String AUTO_MODE = "autoMode";
   private static final String CURRENT_PANEL_IDENTITY = "currentPanelIdentity";
   
   private AppSettingsModel() {
   }

   public static String getCurrentServer(Context context) {
      return context.getSharedPreferences(APP_SETTINGS, 0).getString(CURRENT_SERVER, "");
   }
   
   public static String getCurrentSecuredServer(Context context) {
      String server = getCurrentServer(context);
      if (server.indexOf("http") != -1) {
         server = server.replaceFirst("http", "https");
      }
      if (server.indexOf(":") != -1) {
         server = server.replaceFirst("\\:\\d+", ":" + Constants.SECURED_HTTP_PORT);
      }
      Log.i("SECURE", server);
      return getCurrentServer(context);
   }
   
   public static void setCurrentServer(Context context, String currentServer) {
      SharedPreferences.Editor editor = context.getSharedPreferences(APP_SETTINGS, 0).edit();
      editor.putString(CURRENT_SERVER, currentServer);
      editor.commit();
   }
   
   public static void setAutoMode(Context context, boolean isAuto) {
      SharedPreferences.Editor editor = context.getSharedPreferences(APP_SETTINGS, 0).edit();
      editor.putBoolean(AUTO_MODE, isAuto);
      editor.commit();
   }
   
   public static boolean isAutoMode(Context context) {
      return context.getSharedPreferences(APP_SETTINGS, 0).getBoolean(AUTO_MODE, true);
   }
   
   public static String getCurrentPanelIdentity(Context context) {
      return context.getSharedPreferences(APP_SETTINGS, 0).getString(CURRENT_PANEL_IDENTITY, "");
   }
   
   public static void setCurrentPanelIdentity(Context context, String currentPanelIdentity) {
      SharedPreferences.Editor editor = context.getSharedPreferences(APP_SETTINGS, 0).edit();
      editor.putString(CURRENT_PANEL_IDENTITY, currentPanelIdentity);
      editor.commit();
   }
   
   public static void setCustomServers(Context context, String customServers) {
      SharedPreferences.Editor editor = context.getSharedPreferences(CUSTOM_SERVERS, 0).edit();
      editor.putString(CUSTOM_SERVERS, customServers);
      editor.commit();
   }
   
   public static String getCustomServers(Context context) {
      return context.getSharedPreferences(CUSTOM_SERVERS, 0).getString(CUSTOM_SERVERS, "");
   }
   
   public static ArrayList<String> getAutoServers() {
      new Thread(new IPAutoDiscoveryServer()).start();
      new Thread(new IPAutoDiscoveryClient()).start();
      try {
         Thread.sleep(200);
      } catch (InterruptedException e) {
         Log.e("AppSettingsModel", "can not auto get servers.", e);
      }
      return IPAutoDiscoveryServer.autoServers;
   }
}
