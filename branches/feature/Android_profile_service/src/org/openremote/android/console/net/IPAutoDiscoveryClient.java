package org.openremote.android.console.net;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import org.openremote.android.console.Constants;

public class IPAutoDiscoveryClient implements Runnable {

   public void run() {
      try {
         DatagramSocket socket = new DatagramSocket();
         byte[] b = new byte[512];
         DatagramPacket dgram;
         // temp use "10.0.2.2" in emulator to discovery localhost controller.
         // use "Constants.MULTICAST_ADDRESS" in real device to discovery the controller.
         dgram = new DatagramPacket(b, b.length, InetAddress.getByName("10.0.2.2"), Constants.MULTICAST_PORT);
         socket.send(dgram);
      } catch (Exception e) {
         e.printStackTrace();
      }

   }

}
