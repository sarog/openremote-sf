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

package org.openremote.android.test.console.util;

import java.io.IOException;
import java.util.List;

import junit.framework.Assert;

import org.apache.http.client.ClientProtocolException;
import org.openremote.android.console.AppSettingsActivity;
import org.openremote.android.console.Constants;
import org.openremote.android.console.HTTPUtil;
import org.openremote.android.console.model.UserCache;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.MediumTest;
import android.util.Log;

/**
 * it's responsible for testing whether the HTTPUtil works after it's refactored with ORConnection.
 * 
 * @author handy 2010-04-27
 *
 */
public class HTTPUtilTest extends ActivityInstrumentationTestCase2<AppSettingsActivity> {
	
	public HTTPUtilTest() {
		super("org.openremote.android.console", AppSettingsActivity.class);
		
	}
	
	private static final String CONTROLLER_SERVER_ROOT_URL = "http://192.168.100.108:8080/controller";
	
	@MediumTest
	public void testSendButton() {
		try {
			UserCache.saveUser(getActivity(), "handy", "handy");
			int httpResponseStatusCode = HTTPUtil.sendButton(getActivity(), CONTROLLER_SERVER_ROOT_URL, String.valueOf(81), "off");
			Log.i("INFO", httpResponseStatusCode + "");
			System.out.println("StatusCode : " + httpResponseStatusCode);
			Assert.assertEquals(Constants.HTTP_SUCCESS, httpResponseStatusCode);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@MediumTest
	public void testSendCommand() {
		try {
			UserCache.saveUser(getActivity(), "handy", "handy");
			int httpResponseStatusCode = HTTPUtil.sendCommand(getActivity(), CONTROLLER_SERVER_ROOT_URL, 81, "off");
			Assert.assertEquals(Constants.HTTP_SUCCESS, httpResponseStatusCode);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@MediumTest
	public void testGetPanels() {
		UserCache.saveUser(getActivity(), "handy", "handy");
		List<String> panelsname = HTTPUtil.getPanels(getActivity(), CONTROLLER_SERVER_ROOT_URL);
		Assert.assertEquals(true, panelsname.size() > 0);
	}
}
