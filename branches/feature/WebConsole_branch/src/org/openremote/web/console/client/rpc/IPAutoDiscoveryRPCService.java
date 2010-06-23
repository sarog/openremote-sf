package org.openremote.web.console.client.rpc;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("autodiscovery.smvc")
public interface IPAutoDiscoveryRPCService extends RemoteService {
   List<String> getAutoDiscoveryServers();
}
