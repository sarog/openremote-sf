package org.openremote.android.console.util;

import java.util.ArrayList;

import org.openremote.android.console.net.IPAutoDiscoveryClient;
import org.openremote.android.console.net.IPAutoDiscoveryServer;

public class IPAutoDiscoveryUtil {

   public static ArrayList<String> getAutoDiscoveryIPs() {
      new Thread(new IPAutoDiscoveryServer()).start();
      new Thread(new IPAutoDiscoveryClient()).start();
      try {
         Thread.sleep(1000);
      } catch (InterruptedException e) {
         e.printStackTrace();
      }
      return IPAutoDiscoveryServer.autoServers;
   }
}

