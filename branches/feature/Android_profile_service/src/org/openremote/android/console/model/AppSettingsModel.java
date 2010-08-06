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

import org.openremote.android.console.Constants;

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
   private static final String USE_SSL = "useSSL";
   private static final String SSL_PORT = "sslPort";
   public static final int DEFAULT_SSL_PORT = 8443;
   
   private AppSettingsModel() {
   }

   /**
    * Gets the current server from appSettings.xml.
    * 
    * @param context the context
    * 
    * @return the current server
    */
   public static String getCurrentServer(Context context) {
      return context.getSharedPreferences(APP_SETTINGS, 0).getString(CURRENT_SERVER, "");
   }
   
   /**
    * Gets the secured server.
    * return the current server if not set ssl, otherwise convert the current server to secured server and return. 
    * 
    * @param context the context
    * 
    * @return the server
    */
   public static String getSecuredServer(Context context) {
      String currentServer = AppSettingsModel.getCurrentServer(context);
      if (isUseSSL(context)) {
         if (currentServer.indexOf("http:") != -1) {
            currentServer = currentServer.replaceFirst("http:", "https:");
         }
         if (currentServer.indexOf(":") != -1) {
            currentServer = currentServer.replaceFirst("\\:\\d+", ":" + getSSLPort(context));
         }
         Log.i("SECURE", currentServer);
      }
      return currentServer;
   }
   
   /**
    * Sets the current server into appSettings.xml.
    * 
    * @param context the context
    * @param currentServer the current server
    */
   public static void setCurrentServer(Context context, String currentServer) {
      SharedPreferences.Editor editor = context.getSharedPreferences(APP_SETTINGS, 0).edit();
      editor.putString(CURRENT_SERVER, currentServer);
      editor.commit();
   }
   
   /**
    * Sets the auto discovery mode.
    * 
    * @param context the context
    * @param isAuto the is auto
    */
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
   
   /**
    * Sets the current panel identity.
    * 
    * @param context the context
    * @param currentPanelIdentity the current panel identity
    */
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
   
   public static boolean isUseSSL(Context context) {
      return context.getSharedPreferences(APP_SETTINGS, 0).getBoolean(USE_SSL, false);
   }
   
   /**
    * Sets use security or not.
    * 
    * @param context the context
    * @param isUseSSL the is use ssl
    */
   public static void setUseSSL(Context context, boolean isUseSSL) {
      SharedPreferences.Editor editor = context.getSharedPreferences(APP_SETTINGS, 0).edit();
      editor.putBoolean(USE_SSL, isUseSSL);
      editor.commit();
   }
   
   public static int getSSLPort(Context context) {
      return context.getSharedPreferences(APP_SETTINGS, 0).getInt(SSL_PORT, DEFAULT_SSL_PORT);
   }
   
   /**
    * Sets the ssl port.
    * 
    * @param context the context
    * @param sslPort the ssl port
    */
   public static void setSSLPort(Context context, int sslPort) {
      SharedPreferences.Editor editor = context.getSharedPreferences(APP_SETTINGS, 0).edit();
      editor.putInt(SSL_PORT, sslPort);
      editor.commit();
   }
   
}
