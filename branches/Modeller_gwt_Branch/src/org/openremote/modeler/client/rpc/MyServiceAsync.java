package org.openremote.modeler.client.rpc;

import java.util.List;

import org.openremote.modeler.domain.Activity;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface MyServiceAsync {
   public void getString(AsyncCallback<List<Activity>> callback);
   
   public void addScreen(AsyncCallback<Void> callback);

}
