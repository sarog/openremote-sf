package org.openremote.modeler.client.rpc;


import org.openremote.modeler.auth.Authority;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("auth.smvc")
public interface AuthorityService extends RemoteService {
   public Authority getAuthoritication();
}
