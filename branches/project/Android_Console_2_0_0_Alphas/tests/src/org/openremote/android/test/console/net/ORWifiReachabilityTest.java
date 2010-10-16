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
package org.openremote.android.test.console.net;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.test.AndroidTestCase;
import org.openremote.android.console.net.ORWifiReachability;

/**
 * Unit tests for {@link org.openremote.android.console.net.ORWifiReachability} class.
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class ORWifiReachabilityTest extends AndroidTestCase
{


  /**
   * Basic test to ensure the singleton is created correctly.
   */
  public void testSingletonCreation()
  {
    ORWifiReachability reachability1 = ORWifiReachability.getInstance(getContext());
    ORWifiReachability reachability2 = ORWifiReachability.getInstance(getContext());

    assertNotNull(reachability1);
    assertNotNull(reachability2);

    assertEquals(reachability1, reachability2);
    assertEquals(reachability2, reachability1);
  }

  /**
   * Basic test of {@link org.openremote.android.console.net.ORWifiReachability#canReachWifiNetwork()}
   *
   * NOTE: assumes WiFi is available, generally needs to be run on device, emulator tends not
   *       to have access to localhost Wifi
   */
  public void testCanReachWifiNetwork()
  {
    Context ctx = getContext();
    WifiManager wifi = (WifiManager)ctx.getSystemService(Context.WIFI_SERVICE);

    if(!wifi.isWifiEnabled())
      fail(wifiRequired());
    
    ORWifiReachability reachability = ORWifiReachability.getInstance(ctx);

    assertTrue(reachability.canReachWifiNetwork());
  }


//    Currently commenting out as I can't get past :
//
//    java.lang.SecurityException:
//    WifiService: Neither user 10035 nor current process has android.permission.CHANGE_WIFI_STATE.
//
//    Despite the <uses-permission> set in the test applications AndroidManifest.xml
//
//                                                                                      [JPL]
//  
//  public void testCanReachWifiNetworkNotAvailable()
//  {
//    Context ctx = getContext();
//
//    WifiManager wifi = (WifiManager)ctx.getSystemService(Context.WIFI_SERVICE);
//
//    try
//    {
//      wifi.setWifiEnabled(false);
//
//      ORWifiReachability reachability = ORWifiReachability.getInstance(ctx);
//
//      assertFalse(reachability.canReachWifiNetwork());
//    }
//    finally
//    {
//      wifi.setWifiEnabled(true);
//    }
//  }

  // somewhat poor substitute for the above (but always works on emulator) will do for now
  public void testCanReachWifiNetworkNotAvailable()
  {
    Context ctx = getContext();
    WifiManager wifi = (WifiManager)ctx.getSystemService(Context.WIFI_SERVICE);

    if (!wifi.isWifiEnabled())
    {
      ORWifiReachability reachability = ORWifiReachability.getInstance(ctx);

      assertTrue(!reachability.canReachWifiNetwork());
    }
  }




  // Helper methods for tests ---------------------------------------------------------------------

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


