package org.openremote.console.web.client;

import org.openremote.console.web.client.def.UiDef;
import org.openremote.console.web.client.def.UiXmlParser;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
// TODO: create unit test
public class WebConsole implements EntryPoint {

	private UiBuilder uiBuilder = new UiBuilder();

	/**
	 * This is the entry point method. It retrieves the iphone.xml from the
	 * controller and builds the web console UI based on the file definition.
	 */
	public void onModuleLoad() {

		RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET,
				"/webconsole/ControllerProxy");
		RequestCallback getUiXmlCallback = newGetUiXmlCallback();
		try {
			requestBuilder.sendRequest(null, getUiXmlCallback);
		} catch (RequestException e) {
			// TODO: handle exception
		}

	}

	private void initUi(Response getUiXmlresponse) {
		UiXmlParser parser = new UiXmlParser(getUiXmlresponse.getText());
		UiDef uiDef = parser.parse();
		uiBuilder.buildUi(uiDef);
	}

	private RequestCallback newGetUiXmlCallback() {
		RequestCallback callback = new RequestCallback() {
			@Override
			public void onResponseReceived(Request request, Response response) {
				if (response.getStatusCode() == 200) {
					initUi(response);
				} else {
					// TODO: display error message
				}
			}

			@Override
			public void onError(Request request, Throwable exception) {
				GWT.log("onError", exception);
				// TODO: display error message
			}
		};
		return callback;
	}

}
