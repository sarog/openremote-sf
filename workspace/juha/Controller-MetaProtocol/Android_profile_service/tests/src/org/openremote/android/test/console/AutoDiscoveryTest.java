package org.openremote.android.test.console;

import java.util.List;

import junit.framework.TestCase;

import org.openremote.android.console.model.AppSettingsModel;

public class AutoDiscoveryTest extends TestCase {
// temp use junit test.
   public void testAutoDiscovery() {
      List<String> servers = AppSettingsModel.getAutoServers();
      for (String server : servers) {
//         System.out.println(server);
      }
   }
}
