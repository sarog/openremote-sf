package org.openremote.modeler.client.rpc;


import org.openremote.modeler.domain.Authority;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface AuthorityServiceAsync {
   public void getAuthoritication(AsyncCallback<Authority> callback);
}
