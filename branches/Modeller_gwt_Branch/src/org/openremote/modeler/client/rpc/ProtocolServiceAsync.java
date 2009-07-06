package org.openremote.modeler.client.rpc;

import java.util.Map;

import org.openremote.modeler.protocol.ProtocolDefinition;

import com.google.gwt.user.client.rpc.AsyncCallback;


public interface ProtocolServiceAsync {
   public void getProtocolContainer(AsyncCallback<Map<String,ProtocolDefinition>> callback);
}
