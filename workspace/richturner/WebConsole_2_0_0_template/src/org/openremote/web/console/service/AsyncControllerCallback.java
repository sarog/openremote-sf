package org.openremote.web.console.service;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public abstract class AsyncControllerCallback<T> implements AsyncCallback<T> {
	public void onFailure(Throwable exception) {
		//TODO: Implement Error handling for controller exceptions
		Window.alert(exception.getMessage());
	}
	
	public abstract void onSuccess(T result);
}
