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

package org.openremote.android.test.net;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.test.AndroidTestCase;
import org.apache.http.HttpResponse;
import org.openremote.android.console.Constants;
import org.openremote.android.console.net.ORNetworkCheck;

/**
 * Tests for {@link org.openremote.android.console.net.ORNetworkCheck} class.
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 * @author handy 2010-04-28
 *
 */
public class ORNetworkCheckTest extends AndroidTestCase
{

  public void testReachability()
  {
    Context context = getContext();

    WifiManager wifi = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);

    if (!wifi.isWifiEnabled())
    {
      fail(wifiRequired());
    }


    HttpResponse response = ORNetworkCheck.checkAllWithControllerServerURL(
        context, "http://controller.openremote.org/test/controller"
    );

    assertNotNull("Got null HTTP response.", response);

    int status = response.getStatusLine().getStatusCode();

    assertTrue("Was expecting HTTP_SUCCESS, got " + status,
               status == Constants.HTTP_SUCCESS);
  }


  // Test helper methods --------------------------------------------------------------------------


  private String wifiRequired()
  {
    return
        "\n\n******************************\n\n" +
        " This test assumes availability of WiFi network.\n" +
        " If you're running in the emulator, WiFi may not be available.\n" +
        " Run the full test suite on an Android device to include this test.\n\n" +
        "******************************\n";
  }
}
