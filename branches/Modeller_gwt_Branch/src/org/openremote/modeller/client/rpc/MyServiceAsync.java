package org.openremote.modeller.client.rpc;

import java.util.List;

import org.openremote.modeller.domain.Activity;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface MyServiceAsync {
   public void getString(AsyncCallback<List<Activity>> callback);
   
   public void addScreen(AsyncCallback<Void> callback);

}
