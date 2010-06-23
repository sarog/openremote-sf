package org.openremote.web.console.client.rpc;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface IPAutoDiscoveryRPCServiceAsync {

   void getAutoDiscoveryServers(AsyncCallback<List<String>> callback);

}
