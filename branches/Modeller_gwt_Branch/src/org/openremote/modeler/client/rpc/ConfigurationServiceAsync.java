package org.openremote.modeler.client.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ConfigurationServiceAsync {
   
   public void beehiveRESTUrl(AsyncCallback<String> callback);

}
