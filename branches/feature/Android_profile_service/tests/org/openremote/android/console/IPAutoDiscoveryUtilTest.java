package org.openremote.android.console;

import java.util.List;

import org.openremote.android.console.util.IPAutoDiscoveryUtil;

public class IPAutoDiscoveryUtilTest extends TestBase {

   public void testGetAutoDiscoveryIPs() {
      List<String> servers = IPAutoDiscoveryUtil.getAutoDiscoveryIPs();
      for (String string : servers) {
         System.out.println("avilable server:"+string);
      }
   }
}
