package org.openremote.modeler.client.rpc;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("config.smvc")
public interface ConfigurationService extends RemoteService {
   
  public String beehiveRESTUrl();

}
