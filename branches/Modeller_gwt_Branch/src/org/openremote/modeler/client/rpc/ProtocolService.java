package org.openremote.modeler.client.rpc;

import java.util.Map;

import org.openremote.modeler.protocol.ProtocolDefinition;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("protocol.smvc")
public interface ProtocolService extends RemoteService{

   public Map<String,ProtocolDefinition> getProtocolContainer();
}
