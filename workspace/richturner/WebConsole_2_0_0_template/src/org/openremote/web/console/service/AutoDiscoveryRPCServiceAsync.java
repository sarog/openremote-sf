package org.openremote.web.console.service;

import java.util.List;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface AutoDiscoveryRPCServiceAsync {
   void getAutoDiscoveryServers(AsyncCallback<List<String>> callback);
}
