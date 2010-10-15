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


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import org.openremote.android.console.Constants;

/**
 * Utilities for WiFi accessibility.
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 * @author handy 2010-04-28
 *
 */
public class ORWifiReachability
{

  // Constants ------------------------------------------------------------------------------------

  /**
   * Log category name used by this class.
   */
  private final static String LOG_CATEGORY = Constants.LOG_CATEGORY + "WiFi";


  // Class Members --------------------------------------------------------------------------------

  /**
   * Thread lock to guarantee only one singleton is ever created.
   */
  private final static Boolean LOCK = true;

  /**
   * Singleton instance of this class.
   */
  private static ORWifiReachability reachability;


  /**
   * Returns a singleton instance of this class.
   *
   * @param androidAppContext   a global android application context for accessing services
   *
   * @return a single, shared instance of this class
   */
  public static ORWifiReachability getInstance(Context androidAppContext)
  {
    synchronized (LOCK)
    {
      if (reachability == null)
      {
        reachability = new ORWifiReachability(androidAppContext);
      }
    }

    return reachability;
  }


  // Private Instance Fields ----------------------------------------------------------------------

  private WifiManager wifiManager;

  private ConnectivityManager connectivityManager;



  // Constructors ---------------------------------------------------------------------------------

  /**
   * Private singleton constructor.
   *
   * @param androidAppContext   a global android application context for accessing services
   */
  private ORWifiReachability(Context androidAppContext)
  {
    // TODO : this just initializes references to the two services -- the whole singleton construct isn't really needed [JPL]

    wifiManager = (WifiManager)androidAppContext.getSystemService(Context.WIFI_SERVICE);
    connectivityManager = (ConnectivityManager)androidAppContext.getSystemService(Context.CONNECTIVITY_SERVICE);
  }

  /**
   * Check whether network connectivity is possible.
   *
   * @return
   */
  public boolean canReachWifiNetwork()
  {
    ORWifiConnectionStatus status = localWifiConnectionStatus();

    return !ORWifiConnectionStatus.UNREACHABLE.equals(status);
  }


// Commented out the following checkIPString method -- while it worked fine on
// some devices (e.g. Google G1 with Android 1.5) it failed on others (e.g.
// Samsung Galaxy on Android 2.1-update1) even though the configured IP address
// can be routed (despite requestRouteToHost returning false). So relaxing the
// requirements and blindly trusting that we can route the packets.
//                                                                    - [JPL]
//
//
//  /**
//   * Ensure that a network route exists to deliver traffic to the specified host via the specified
//   * network interface.
//   *
//   * @param ip The format must follow "xxx.xxx.xxx.xxx", eg: 192.168.1.11
//   */
//  public boolean checkIpString(String ip)
//  {
//     if (ip != null)
//     {
//        return connectivityManager.requestRouteToHost(ConnectivityManager.TYPE_WIFI, (int) IpUitl.ipStringToLong(ip));
//     }
//     return false;
//    return true;
//  }

  /**
   * Detects the current WiFi status.
   *
   * @return
   */
  private ORWifiConnectionStatus localWifiConnectionStatus()
  {
    if (!wifiManager.isWifiEnabled() || wifiManager.getWifiState() != WifiManager.WIFI_STATE_ENABLED)
    {
      Log.d(LOG_CATEGORY, "WiFi not enabled or WiFi network not detected.");

      return ORWifiConnectionStatus.UNREACHABLE;
    }

    NetworkInfo wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

    if (!wifiNetworkInfo.isAvailable())
    {
      Log.d(LOG_CATEGORY, "Wifi network detected but wasn't available.");

      return ORWifiConnectionStatus.UNREACHABLE;
    }

    else
    {
      return ORWifiConnectionStatus.REACHABLE_VIA_WIFINETWORK;
    }
  }

}
