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
import junit.framework.TestCase;

import org.apache.http.client.ClientProtocolException;
import org.openremote.android.console.HTTPUtil;

/**
 * it's responsible for testing whether the HTTPUtil works after it's refactored with ORConnection.
 * 
 * @author handy 2010-04-27
 *
 */
public class HTTPUtilTest extends TestCase {
	
	private static final int HTTP_REQUEST_SUCCESS = 200;
	private static final String CONTROLLER_SERVER_ROOT_URL = "http://localhost:8080/controller";
	
	public void testSendButton() {
		try {
			int httpResponseStatusCode = HTTPUtil.sendButton(CONTROLLER_SERVER_ROOT_URL, String.valueOf(81), "off");
			Assert.assertEquals(HTTP_REQUEST_SUCCESS, httpResponseStatusCode);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void testSendCommand() {
		try {
			int httpResponseStatusCode = HTTPUtil.sendCommand(CONTROLLER_SERVER_ROOT_URL, 81, "off");
			Assert.assertEquals(HTTP_REQUEST_SUCCESS, httpResponseStatusCode);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void testGetPanels() {
		List<String> panelsname = HTTPUtil.getPanels(CONTROLLER_SERVER_ROOT_URL);
		Assert.assertEquals(true, panelsname.size() > 0);
	}
}
