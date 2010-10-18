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
package org.openremote.android.console.net;

import org.apache.http.HttpResponse;
import org.openremote.android.console.Constants;
import org.openremote.android.console.model.AppSettingsModel;
import org.openremote.android.console.util.HTTPUtil;
import org.openremote.android.console.util.IpUitl;

import android.content.Context;
import android.util.Log;
import android.net.wifi.WifiManager;
import android.net.NetworkInfo;
import android.net.ConnectivityManager;


/**
 * TODO: Checks access to OpenRemote controller.
 * 
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 * @author handy 2010-04-28
 */
public class ORNetworkCheck
{

  // Constants ------------------------------------------------------------------------------------

  /**
   * Log category name used by this class.
   */
  private final static String LOG_CATEGORY = Constants.LOG_CATEGORY + "WiFi";



  // Class Members --------------------------------------------------------------------------------
  
  /**
   * Verifies the network access to the currently configured controller URL by checking if
   * REST API {controllerServerURL}/rest/panel/{panel identity} is available.
   *
   * @param context               global Android application context
   * @param url                   an URL to a controller instance
   *
   * @return TODO: returns null or HttpResponse
   */
  public static HttpResponse verifyControllerURL(Context context, String url)
  {
    // TODO : Use URL class instead of string in param


    // TODO : modifying the settings probably doesn't belong here, as it is an undocumented side-effect
    AppSettingsModel.setCurrentServer(context, url);


    HttpResponse response = checkControllerAvailable(context);

    Log.d(LOG_CATEGORY, "HTTP Response: " + response);

    if(response != null && response.getStatusLine().getStatusCode() == Constants.HTTP_SUCCESS)
    {
      String controllerURL = AppSettingsModel.getSecuredServer(context);

      if (controllerURL == null || "".equals(controllerURL))
      {
        return null;
			}

      String currentPanelIdentity = AppSettingsModel.getCurrentPanelIdentity(context);

      if (currentPanelIdentity == null || "".equals(currentPanelIdentity))
      {
        return null;
      }

      String restfulPanelURL = controllerURL + "/rest/panel/" + HTTPUtil.encodePercentUri(currentPanelIdentity);

      Log.i(LOG_CATEGORY, "Getting panel URL " + restfulPanelURL);

      return ORConnection.checkURLWithHTTPProtocol(context, ORHttpMethod.GET, restfulPanelURL, true);
    }

    return response;
  }

  /**
   * Check if the Controller URL is available.
   *
   * @param context   a global Android application context
   *
   * @return TODO
   */
  private static HttpResponse checkControllerAvailable(Context context)
  {
    if (checkControllerIPAddress(context))
    {
      String controllerURL = AppSettingsModel.getSecuredServer(context);

      Log.i(LOG_CATEGORY, "controllerURL: " + controllerURL);

      if (controllerURL == null || "".equals(controllerURL))
      {
        return null;
      }

      return ORConnection.checkURLWithHTTPProtocol(context, ORHttpMethod.GET, controllerURL, false);
    }

    return null;
  }

  /**
   * Check if the IP of controller is reachable.
   *
   * @param context   global Android application context
   *
   * @return TODO
   */
  private static boolean checkControllerIPAddress(Context context)
  {

    if (!IPAutoDiscoveryClient.isNetworkTypeWIFI)     // TODO : questionable use of global static field
    {
        return true;
    }

    if (!canReachWifiNetwork(context))
    {
      return false;
    }

    String controllerURL = AppSettingsModel.getCurrentServer(context);

    Log.d(LOG_CATEGORY, "controllerURL: " + controllerURL);

    if (controllerURL == null || "".equals(controllerURL))
    {
      return false;
    }

    String controllerIPAddress = IpUitl.splitIpFromURL(controllerURL);  // TODO : class name has a typo

    Log.d(LOG_CATEGORY, "ControllerIPAddress: " + controllerIPAddress);

    return true;
  }

  /**
   * Detects the current WiFi status.
   *
   * @param ctx     global Android application context
   * @return TODO
   */
  private static boolean canReachWifiNetwork(Context ctx)
  {

    WifiManager wifiManager = (WifiManager)ctx.getSystemService(Context.WIFI_SERVICE);
    ConnectivityManager connectivityManager = (ConnectivityManager)ctx.getSystemService(Context.CONNECTIVITY_SERVICE);

    if (!wifiManager.isWifiEnabled() || wifiManager.getWifiState() != WifiManager.WIFI_STATE_ENABLED)
    {
      Log.d(LOG_CATEGORY, "WiFi not enabled or WiFi network not detected.");

      return false;
    }

    NetworkInfo wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

    if (!wifiNetworkInfo.isAvailable())
    {
      Log.d(LOG_CATEGORY, "Wifi network detected but wasn't available.");

      return false;
    }

    else
    {
      return true;
    }



  }

}
