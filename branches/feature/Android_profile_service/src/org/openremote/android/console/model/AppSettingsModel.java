package org.openremote.android.console.model;

import java.io.Serializable;
import java.util.ArrayList;

import org.openremote.android.console.Constants;
import org.openremote.android.console.net.IPAutoDiscoveryClient;
import org.openremote.android.console.net.IPAutoDiscoveryServer;

import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.util.Log;

public class AppSettingsModel implements Serializable {

   private static final long serialVersionUID = Constants.MODEL_VERSION;
   private static final String CURRENT_SERVER = "currentServer";
   private static final String AUTO_MODE = "autoMode";
   private static final String CURRENT_PANEL_IDENTITY = "currentPanelIdentity";
   
   public static String getCurrentServer(ContextWrapper context) {
      return context.getSharedPreferences(CURRENT_SERVER, 0).getString(CURRENT_SERVER, "");
   }
   
   public static void setCurrentServer(ContextWrapper context, String currentServer) {
      SharedPreferences.Editor editor = context.getSharedPreferences(CURRENT_SERVER, 0).edit();
      editor.putString(CURRENT_SERVER, currentServer);
      editor.commit();
   }
   
   public static void setAutoMode(ContextWrapper context, boolean isAuto) {
      SharedPreferences settings = context.getSharedPreferences(AUTO_MODE, 0);
      SharedPreferences.Editor editor = settings.edit();
      editor.putBoolean(AUTO_MODE, isAuto);
      editor.commit();
   }
   
   public static boolean isAutoMode(ContextWrapper context) {
      return context.getSharedPreferences(AUTO_MODE, 0).getBoolean(AUTO_MODE, true);
   }
   
   public static String getCurrentPanelIdentity(ContextWrapper context) {
      return context.getSharedPreferences(CURRENT_PANEL_IDENTITY, 0).getString(CURRENT_PANEL_IDENTITY, "");
   }
   
   public static void setCurrentPanelIdentity(ContextWrapper context, String currentPanelIdentity) {
      SharedPreferences.Editor editor = context.getSharedPreferences(CURRENT_PANEL_IDENTITY, 0).edit();
      editor.putString(CURRENT_PANEL_IDENTITY, currentPanelIdentity);
      editor.commit();
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
