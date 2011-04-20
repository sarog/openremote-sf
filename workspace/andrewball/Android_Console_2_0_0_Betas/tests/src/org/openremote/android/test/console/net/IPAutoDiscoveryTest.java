/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2011, OpenRemote Inc.
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

import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.openremote.android.console.AppSettingsActivity;
import org.openremote.android.console.Constants;
import org.openremote.android.console.net.IPAutoDiscoveryClient;
import org.openremote.android.console.net.IPAutoDiscoveryServer;

import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

/**
 * Test for auto discovering servers.
 *
 * @author Tomsky Wang
 */
public class IPAutoDiscoveryTest extends ActivityInstrumentationTestCase2<AppSettingsActivity> {
  public static final String TAG = Constants.LOG_CATEGORY + "IPAutoDiscoveryTest";

  public IPAutoDiscoveryTest() {
    super("org.openremote.android.console", AppSettingsActivity.class);
  }

  public void setUp() {
    IPAutoDiscoveryClient.isNetworkTypeWIFI = true;
  }

  public void tearDown() {
    IPAutoDiscoveryClient.isNetworkTypeWIFI = false;
  }

  public void testAutoDiscoveryServers() throws InterruptedException {
    getActivity().stopControllerAutoDiscovery();
    // wait for the auto-discovery process that was started when the activity
    // was created to be stopped so that we don't get a BindException for "address already in use"
    Thread.sleep(3000);

    // Initialize the IPAutoDiscoveryServer.
    final IPAutoDiscoveryServer autoDiscoveryServer = new IPAutoDiscoveryServer();

    // Start the mock controller.
    new Thread(new IPAutoDiscoveryControllerServer()).start();

    // Run the IPAutoDiscoveryServer in a UI thread.
    getActivity().runOnUiThread(new Runnable() {
      @Override
      public void run() {
        // Run the IPAutoDiscoveryServer.
        autoDiscoveryServer.execute((Void) null);
      }
    });

    try {
      // Get the auto discovered servers.
      List<String> servers = autoDiscoveryServer.get();

      assertTrue("we should at least have one response", servers.size() >= 1);
      boolean foundExpectedServer = false;
      for (String server : servers) {
        if (server.equals(IPResponseTCPClient.response)) {
          foundExpectedServer = true;
        }
      }
      assertTrue("we expected to see " + IPResponseTCPClient.response + " in the responses", foundExpectedServer);
    } catch (InterruptedException e) {
      fail("Can't auto discover servers -- InterruptedException!");
    } catch (ExecutionException e) {
      fail("Can't auto discover servers -- ExecutionException!");
    } finally {
      // Close the AppSettingsActivity.
      getActivity().finish();
    }
  }
}

/**
 * The class to mock the controller server.
 */
class IPAutoDiscoveryControllerServer implements Runnable {

  public void run() {
    final int MULTICAST_PORT = Constants.MULTICAST_PORT;
    final String MULTICAST_ADDRESS = Constants.MULTICAST_ADDRESS;
    String multicastLocation = MULTICAST_ADDRESS + ":" + MULTICAST_PORT;
    MulticastSocket socket = null;
    InetAddress address = null;
    try {
      socket = new MulticastSocket(MULTICAST_PORT);
      address = InetAddress.getByName(MULTICAST_ADDRESS);
      Log.i("OpenRemote/IPAutoDiscoveryControllerServer", "Created IP discover multicast server !");
    } catch (IOException e) {
      Log.e("OPENREMOTE/IPAutoDiscoveryControllerServer", "Can't create multicast socket on " + multicastLocation);
    }
    try {
      socket.joinGroup(address);
      Log.i("OpenRemote/IPAutoDiscoveryControllerServer", "Joined a group : "+multicastLocation);
    } catch (IOException e) {
      Log.e("OpenRemote/IPAutoDiscoveryControllerServer", "Can't join group of " + multicastLocation);
    }
    byte[] buf = new byte[512];
    DatagramPacket packet = new DatagramPacket(buf, buf.length);
    while (true) {
      try {
        Log.i("OpenRemote/IPAutoDiscoveryControllerServer", "Listening on  " + multicastLocation);
        socket.receive(packet);
        Log.i("OpenRemote/IPAutoDiscoveryControllerServer", "Received an IP auto-discovery request from " + packet.getAddress().getHostAddress());
      } catch (IOException e) {
        Log.e("OpenRemote/IPAutoDiscoveryControllerServer", "Can't receive packet on " + MULTICAST_ADDRESS + ":" + MULTICAST_PORT);
      }
      sendLocalIPBack(packet);
    }
  }

  /**
   * Starts a new thread that sends a response to the TCP server with the mock controller's URL.
   *
   * @param packet the packet received via multicast (used to determine the IP address of the TCP server)
   */
  private void sendLocalIPBack(DatagramPacket packet) {
    new Thread(new IPResponseTCPClient(packet.getAddress())).start();
  }
}

/**
 * Sends a mock controller URL to the TCP server listening after the auto discovery process has
 * been initiated.
 *
 * Please don't use a real controller with hostname "nobodywouldhavethishostnameright". 8-)
 */
class IPResponseTCPClient implements Runnable {
  /** the data to send to the TCP server with a fake server name */
  public static final String response = "http://nobodywouldhavethishostnameright:8080/controller";

  /** IP address of the TCP server that will receive our response */
  private InetAddress targetIP;

  /**
   * Instantiates a new TCP client.
   *
   * @param targetIP the target ip
   */
  public IPResponseTCPClient(InetAddress targetIP) {
    super();
    this.targetIP = targetIP;
  }

  @Override
  public void run() {
    sendTcp();
  }

  /**
   * Sends the mock controller's URL to the TCP server.
   */
  public void sendTcp() {
    String targetIPStr = targetIP.getHostAddress();
    Log.i("OpenRemote/IPResponseTCPClient", "Sending server IP '" + response + "' to " + targetIPStr);
    Socket skt = null;
    PrintWriter out = null;
    try {
      skt = new Socket(targetIP, Constants.LOCAL_SERVER_PORT);
      out = new PrintWriter(skt.getOutputStream(), true);
    } catch (IOException e) {
      Log.e("OpenRemote/IPResponseTCPClient", "Response failed! Can't create TCP socket on " + targetIPStr);
    } finally {
      out.print(response);
      out.close();
      try {
        skt.close();
      } catch (IOException e) {
        Log.e("OpenRemote/IPResponseTCPClient", "Can't close socket");
      }
    }

  }
}
