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

import java.util.List;

import junit.framework.Assert;

import org.openremote.android.console.AppSettingsActivity;
import org.openremote.android.console.model.UserCache;
import org.openremote.android.console.util.HTTPUtil;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.MediumTest;

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
	public void testGetPanels() {
		UserCache.saveUser(getActivity(), "handy", "handy");
		List<String> panelsname = HTTPUtil.getPanels(getActivity(), CONTROLLER_SERVER_ROOT_URL);
		Assert.assertEquals(true, panelsname.size() > 0);
	}
}
