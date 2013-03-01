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

package org.openremote.android.console.net;

import org.openremote.android.console.util.IpUitl;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

/**
 * It's responsible for checking the wifi reachability.
 * 
 * @author handy 2010-04-28
 *
 */
public class ORWifiReachability {

	private static ORWifiReachability reachability;
	private Context androidAppContext;
	
	private WifiManager wifiManager;
	private ConnectivityManager connectivityManager;
	
	private ORWifiConnectionStatus wifiConnectionStatus;
	
	private ORWifiReachability(Context androidAppContextParam) {
		super();
		wifiConnectionStatus = ORWifiConnectionStatus.UNREACHABLE;
		androidAppContext = androidAppContextParam;
		wifiManager = (WifiManager) androidAppContext.getSystemService(Context.WIFI_SERVICE);
		connectivityManager = (ConnectivityManager) androidAppContext.getSystemService(Context.CONNECTIVITY_SERVICE); 
	}
	
	public static ORWifiReachability getInstance(Context androidAppContext) {
		if (reachability == null) {
			reachability = new ORWifiReachability(androidAppContext);
		}
		return reachability;
	}
	
	/** Check whether network connectivity is possible. */
	public boolean canReachWifiNetwork() {
		localWifiConnectionStatus();
		return !ORWifiConnectionStatus.UNREACHABLE.equals(this.wifiConnectionStatus);
	}
	
	/** 
	 * Ensure that a network route exists to deliver traffic to the specified host via the specified network interface.
	 * 
	 * @param ip The format must follow "xxx.xxx.xxx.xxx", eg: 192.168.1.11  
	 */
	public boolean checkIpString(String ip) {
	   if (ip != null) {
	      return connectivityManager.requestRouteToHost(ConnectivityManager.TYPE_WIFI, (int) IpUitl.ipStringToLong(ip));
	   }
	   return false;
	}
	
	private void localWifiConnectionStatus() {
		if (!wifiManager.isWifiEnabled() || wifiManager.getWifiState() != WifiManager.WIFI_STATE_ENABLED) {
			this.wifiConnectionStatus = ORWifiConnectionStatus.UNREACHABLE;
			Log.i("INFO", "Wifi in handset wasn't enabled or wifi network didn't detect.");
			return;
		}
		
		NetworkInfo wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (!wifiNetworkInfo.isAvailable()) {// Indicates whether network connectivity is possible.
			Log.i("INFO", "Wifi network detected but wasn't available.");
			this.wifiConnectionStatus = ORWifiConnectionStatus.UNREACHABLE;
		} else {
			this.wifiConnectionStatus = ORWifiConnectionStatus.REACHABLE_VIA_WIFINETWORK;
		}
	}	
	
}
