package org.openremote.modeler.client.rpc;

import java.util.List;

import org.openremote.modeler.domain.Switch;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface SwitchRPCServiceAsync {

   void loadAll(AsyncCallback<List<Switch>> callback);

   void delete(long id, AsyncCallback<Void> callback);

   void save(Switch switchToggle, AsyncCallback<Switch> callback);

   void update(Switch switchToggle, AsyncCallback<Switch> callback);
   
}
