package org.openremote.web.console.panel;

import org.openremote.web.console.service.AutoBeanService;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;

public class SystemPanel {
	private static boolean isRunning;
	private static Panel instance = null;
	private SystemPanel() {}
	
	public static Panel get() {
		if (instance == null && !isRunning) {
		try {
				new RequestBuilder(RequestBuilder.GET, "resources/systempanel.def").sendRequest("", new RequestCallback() {
					  @Override
					  public void onResponseReceived(Request req, Response resp) {
						  instance = AutoBeanService.getInstance().fromJsonString(Panel.class, resp.getText()).as();
						  isRunning = false;
					  }
		
					  @Override
					  public void onError(Request res, Throwable throwable) {
					    // handle errors
					  }
					});
			} catch (Exception e) {}
		}
		return instance;
	}
}
