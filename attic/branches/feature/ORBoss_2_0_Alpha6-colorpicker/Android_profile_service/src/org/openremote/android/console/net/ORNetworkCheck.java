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

import org.apache.http.HttpResponse;
import org.openremote.android.console.Constants;
import org.openremote.android.console.model.AppSettingsModel;
import org.openremote.android.console.util.HTTPUtil;
import org.openremote.android.console.util.IpUitl;

import android.content.Context;

/**
 * This is responsible for network check.
 * 
 * @author handy 2010-04-28
 *
 */
public class ORNetworkCheck {
	
	/**
	 * Check all related to the specified controller server url.
	 */
	public static HttpResponse checkAllWithControllerServerURL(Context context, String controllerServerURL) {
		AppSettingsModel.setCurrentServer(context, controllerServerURL);
		return checkPanelXMlOfCurrentPanelIdentity(context);
	}
	
	/**
	 * Check if the RESTful url {controllerServerURL}/rest/panel/{panel identity} is available.
	 */
	private static HttpResponse checkPanelXMlOfCurrentPanelIdentity(Context context) {
	   HttpResponse response = checkControllerAvailable(context);
		if(response !=null && response.getStatusLine().getStatusCode() == Constants.HTTP_SUCCESS) {
			String currentControllerServerURL = AppSettingsModel.getSecuredServer(context);
			if (currentControllerServerURL == null || "".equals(currentControllerServerURL)) {
				return null;
			}
			String currentPanelIdentity = AppSettingsModel.getCurrentPanelIdentity(context);
			if (currentPanelIdentity == null || "".equals(currentPanelIdentity)) {
				return null;
			}
			String restfulPanelURL = currentControllerServerURL + "/rest/panel/" + HTTPUtil.encodePercentUri(currentPanelIdentity);
			return ORConnection.checkURLWithHTTPProtocol(context, ORHttpMethod.GET, restfulPanelURL, true);
		}
		return response;
	}

	/**
	 * Check if the ControllerServerURL is available.
	 */
	private static HttpResponse checkControllerAvailable(Context context) {
		if (checkControllerIPAddress(context)) {
			String currentControllerServerURL = AppSettingsModel.getSecuredServer(context);
			if (currentControllerServerURL == null || "".equals(currentControllerServerURL)) {
				return null;
			}
			return ORConnection.checkURLWithHTTPProtocol(context, ORHttpMethod.GET, currentControllerServerURL, false);
		}
		return null;
	}

	/**
	 * Check if the IP of controller is reachable.
	 */
	private static boolean checkControllerIPAddress(Context context) {
	   if (!IPAutoDiscoveryClient.isNetworkTypeWIFI) {
	      return true;
	   }
		if (ORWifiReachability.getInstance(context).canReachWifiNetwork()) {
			String currentControllerServerURL = AppSettingsModel.getCurrentServer(context);
			if (currentControllerServerURL == null || "".equals(currentControllerServerURL)) {
				return false;
			}
			String currentControllerServerIp = IpUitl.splitIpFromURL(currentControllerServerURL);
			return ORWifiReachability.getInstance(context).checkIpString(currentControllerServerIp);
		}
		return false;
	}

}
