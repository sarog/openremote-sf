package org.openremote.web.console.service;

import java.io.InputStream;
import java.util.List;

public interface PanelIdentityService {

   List<String> getPanels(String url, String username, String password);
   
   List<String> parsePanelsFromInputStream(InputStream inputStream);
}
