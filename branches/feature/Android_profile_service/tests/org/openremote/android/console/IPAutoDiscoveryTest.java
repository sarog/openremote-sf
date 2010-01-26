package org.openremote.android.console;

import java.util.List;

import org.openremote.android.console.model.AppSettingsModel;

public class IPAutoDiscoveryTest extends TestBase {

   public void testGetAutoServers() {
      List<String> servers = AppSettingsModel.getAutoServers();
      for (String string : servers) {
         System.out.println("avilable server:"+string);
      }
   }
}
