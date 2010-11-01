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

import java.net.HttpURLConnection;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.SystemClock;
import android.test.ActivityInstrumentationTestCase2;
import org.apache.http.HttpResponse;
import org.openremote.android.console.AppSettingsActivity;
import org.openremote.android.console.net.ORNetworkCheck;

/**
 * Tests for {@link org.openremote.android.console.net.ORNetworkCheck} class.
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 * @author handy 2010-04-28
 *
 */
public class ORNetworkCheckTest extends ActivityInstrumentationTestCase2<AppSettingsActivity>
{

  public ORNetworkCheckTest()
  {
    super("org.openremote.android.console", AppSettingsActivity.class);
  }

  /**
   * TODO
   */
  public void testVerifyControllerURL()
  {
    WifiManager wifi = (WifiManager)getActivity().getSystemService(Context.WIFI_SERVICE);

    enableWifi();

    if (!wifi.isWifiEnabled())
      fail(wifiRequired());


    HttpResponse response = ORNetworkCheck.verifyControllerURL(
        getInstrumentation().getTargetContext(),
        "http://controller.openremote.org/test/controller"
    );

    assertNotNull("Got null HTTP response, was expecting: " + HttpURLConnection.HTTP_OK, response);

    int status = response.getStatusLine().getStatusCode();

    assertTrue("Was expecting HTTP_OK, got " + status, status == HttpURLConnection.HTTP_OK);
  }


  /**
   * TODO
   */
  public void testVerifyControllerWrongURL()
  {
    WifiManager wifi = (WifiManager)getActivity().getSystemService(Context.WIFI_SERVICE);

    enableWifi();

    if (!wifi.isWifiEnabled())
      fail(wifiRequired());

    HttpResponse response = ORNetworkCheck.verifyControllerURL(
        getInstrumentation().getTargetContext(), "http://controller.openremote.org/nothing/here"
    );

    assertNotNull("Got null HTTP response, was expecting: " + HttpURLConnection.HTTP_NOT_FOUND,
                  response);

    int status = response.getStatusLine().getStatusCode();

    assertTrue("Was expecting 404, got " + status,
               status == HttpURLConnection.HTTP_NOT_FOUND);
  }



  /**
   * TODO
   */
  public void testVerifyControllerEmptyURL()
  {
    WifiManager wifi = (WifiManager)getActivity().getSystemService(Context.WIFI_SERVICE);

    enableWifi();

    if (!wifi.isWifiEnabled())
      fail(wifiRequired());


    HttpResponse response = ORNetworkCheck.verifyControllerURL(
        getInstrumentation().getTargetContext(), ""
    );

    assertNull("Was expecting null, got " + response, response);
  }


//
// Commented the following test out due to despite turning off wifi, actual devices tend to
// automatically switch to a backup connection over 3G and still reach target (goal was to
// test behavior when URL cannot be reached). There was no obvious API in connectivity manager
// to switch off 3G connections -- it may exist elsewhere.
//                                                                                    [JPL]
//  /**
//   * TODO
//   */
//  public void testVerifyControllerNoWifi()
//  {
//
//    WifiManager wifi = (WifiManager)getActivity().getSystemService(Context.WIFI_SERVICE);
//
//    disableWifi();
//
//    if (wifi.isWifiEnabled())
//      fail(noWifiWanted());
//
//
//    HttpResponse response = ORNetworkCheck.verifyControllerURL(
//        getInstrumentation().getTargetContext(), "http://controller.openremote.org/test/controller"
//    );
//
//    int status = response.getStatusLine().getStatusCode();
//
//    assertNull("Was expecting null, got " + status, response);
//
//  }





  // Test helper methods --------------------------------------------------------------------------


  private void enableWifi() 
  {
    enableWifi(true);
  }

  private void disableWifi()
  {
    enableWifi(false);
  }

  private void enableWifi(boolean enable)
  {

    WifiManager wifi = (WifiManager)getActivity().getSystemService(Context.WIFI_SERVICE);

    if (enable && wifi.isWifiEnabled())
      return;

    if (!enable && !wifi.isWifiEnabled())
      return;

    if (enable && !wifi.isWifiEnabled())
    {
      if (!wifi.setWifiEnabled(true))
        fail("Cannot enable WiFi");
    }
    else
    {
      wifi.disconnect();
      wifi.setWifiEnabled(false);
    }

    // wait for it...

    for (int iterations = 0; iterations < 10; iterations++)
    {
      SystemClock.sleep(1000);

      if (enable && wifi.isWifiEnabled())
        break;
      if (!enable && !wifi.isWifiEnabled())
        break;
    }
  }


  private String wifiRequired()
  {
    return
        "\n\n******************************\n\n" +
        " This test assumes availability of WiFi network.\n" +
        " If you're running tests in the emulator, WiFi may not be available.\n" +
        " If you're running tests on a device, you may need to turn WiFi on.\n\n" +
        "******************************\n";
  }

  private String noWifiWanted()
  {
    return
        "\n\n******************************\n\n" +
        " Testing for behavior when WiFi is not enabled.\n" +
        " For some reason, could not disable the WiFi connection.\n\n" +
        "******************************\n";
  }


}
