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

package org.openremote.android.test.net;

import org.apache.http.HttpResponse;
import org.openremote.android.console.AppSettingsActivity;
import org.openremote.android.console.Constants;
import org.openremote.android.console.net.ORNetworkCheck;

import android.content.Context;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.MediumTest;
import android.util.Log;

/**
 * This is responsible for testing networkcheck.
 * 
 * @author handy 2010-04-28
 *
 */
public class ORNetworkCheckTest extends ActivityInstrumentationTestCase2<AppSettingsActivity> {

	public ORNetworkCheckTest() {
		super("org.openremote.android.console", AppSettingsActivity.class);
	}
	
	@MediumTest
	public void testReachability() {
		Context context = getActivity();
		HttpResponse response = ORNetworkCheck.checkAllWithControllerServerURL(context, "http://192.168.100.108:8080/controller");
		boolean isSuccessful = (response !=null && response.getStatusLine().getStatusCode() == Constants.HTTP_SUCCESS);
		Log.i("INFO", "Network check was " +  (isSuccessful ? "successful" : "failed"));
	}

}
