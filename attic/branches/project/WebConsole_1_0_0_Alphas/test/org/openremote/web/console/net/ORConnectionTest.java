package org.openremote.web.console.net;

import java.io.InputStream;
import java.util.List;

import org.junit.Test;
import org.openremote.web.console.utils.XmlParserUtil;

public class ORConnectionTest {

   @Test
   public void testORConnection() {
      List<String> panels = null;
      String url = "http://192.168.100.113:8080/beehive/rest/user/wang/rest/panels";
      ORConnection orConnection = new ORConnection(url, ORHttpMethod.GET, "wang", "123456");
      InputStream data = orConnection.getResponseData();
      if (data != null) {
         panels = XmlParserUtil.parsePanelNamesFromInputStream(orConnection.getResponseData());
      }
      System.out.println(panels.size());
   }
}
