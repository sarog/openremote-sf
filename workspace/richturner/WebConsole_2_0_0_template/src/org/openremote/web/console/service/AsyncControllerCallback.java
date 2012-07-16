package org.openremote.web.console.service;

import org.openremote.web.console.client.WebConsole;
import org.openremote.web.console.controller.EnumControllerResponseCode;
import org.openremote.web.console.panel.PanelIdentityList;

import com.google.gwt.jsonp.client.TimeoutException;
import com.google.gwt.user.client.rpc.AsyncCallback;

public abstract class AsyncControllerCallback<T> implements AsyncCallback<T> {
	@Override
	public void onFailure(Throwable exception) {
		if (exception instanceof TimeoutException) {
			onFailure(EnumControllerResponseCode.NO_RESPONSE);
		} else {
			onFailure(EnumControllerResponseCode.UNKNOWN_ERROR);
		}
	}
	
	public void onFailure(EnumControllerResponseCode response) {
		WebConsole.getConsoleUnit().onError(response);
	}
}
