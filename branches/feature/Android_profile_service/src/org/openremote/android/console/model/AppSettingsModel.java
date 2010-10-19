/*
 * OpenRemote, the Home of the Digital Home.
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
 * Utility methods to access application's settings.
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 * @author Tomsky Wang
 * @author Dan Cong
 *
 */
public class AppSettingsModel implements Serializable
{

  // Serialization --------------------------------------------------------------------------------

  private static final long serialVersionUID = Constants.MODEL_VERSION;


  // Constants ------------------------------------------------------------------------------------

  public static final int DEFAULT_SSL_PORT = 8443;


  private static final String APP_SETTINGS = "appSettings";
  private static final String CUSTOM_SERVERS = "customServers";
  private static final String CURRENT_SERVER = "currentServer";
  private static final String AUTO_MODE = "autoMode";
  private static final String CURRENT_PANEL_IDENTITY = "currentPanelIdentity";
  private static final String USE_SSL = "useSSL";
  private static final String SSL_PORT = "sslPort";


  // Class Methods --------------------------------------------------------------------------------

  /**
   * Returns controller URL from appSettings.xml. Note that this method always returns the
   * configured URL regardless of whether SSL has been configured or not.
   *
   * @param context  global Android application context
   *
   * @return TODO
   */
  public static String getCurrentServer(Context context)
  {
    return context.getSharedPreferences(APP_SETTINGS, Context.MODE_PRIVATE)
                  .getString(CURRENT_SERVER, "");
  }


  /**
   * Returns controller URL. <p>
   *
   * This method will return the configured URL if SSL has not been configured, otherwise the
   * configured controller URL is modified to use HTTPS instead.
   *
   * @param context  global Android application context
   *
   * @return TODO
   */
  public static String getSecuredServer(Context context)
  {
    String currentServer = getCurrentServer(context);

    if (isUseSSL(context))
    {
       if (currentServer.indexOf("http:") != -1)
       {
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
   * Saves controller URL into appSettings.xml.
   *
   * @param context        global Android application context
   * @param controllerURL  controller URL to save in the application settings
   */
  public static void setCurrentServer(Context context, String controllerURL)
  {
    // TODO : use URL instead of String in the API

    SharedPreferences.Editor editor = context.getSharedPreferences(
        APP_SETTINGS,
        Context.MODE_PRIVATE
    ).edit();

    editor.putString(CURRENT_SERVER, controllerURL);
    editor.commit();
  }



   /**
    * Sets the auto discovery mode.
    * 
    * @param isAuto the auto mode
    */
   public static void setAutoMode(Context context, boolean isAuto) {
      SharedPreferences.Editor editor = context.getSharedPreferences(APP_SETTINGS, 0).edit();
      editor.putBoolean(AUTO_MODE, isAuto);
      editor.commit();
   }
   
   public static boolean isAutoMode(Context context) {
      return context.getSharedPreferences(APP_SETTINGS, 0).getBoolean(AUTO_MODE, true);
   }


  /**
   * Returns the current, in-use panel identity from application's settings.
   *
   * @param context   global Android application context
   *
   * @return  panel identity (name)
   */
  public static String getCurrentPanelIdentity(Context context)
  {
     return context.getSharedPreferences(APP_SETTINGS, Context.MODE_PRIVATE)
            .getString(CURRENT_PANEL_IDENTITY, "");
  }
   
  /**
   * Saves a current, in-use panel identity to application's settings.
   *
   * @param context               global Android application context
   * @param currentPanelIdentity  new panel identity (name)
   */
  public static void setCurrentPanelIdentity(Context context, String currentPanelIdentity)
  {
    SharedPreferences.Editor editor = context.getSharedPreferences(
       APP_SETTINGS,
       Context.MODE_PRIVATE
    ).edit();

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
    * @param context  TODO
    * @param isUseSSL TODO
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
    * @param sslPort the ssl port
    */
   public static void setSSLPort(Context context, int sslPort) {
      SharedPreferences.Editor editor = context.getSharedPreferences(APP_SETTINGS, 0).edit();
      editor.putInt(SSL_PORT, sslPort);
      editor.commit();
   }


  // Constructors ---------------------------------------------------------------------------------


  private AppSettingsModel() {}

}
