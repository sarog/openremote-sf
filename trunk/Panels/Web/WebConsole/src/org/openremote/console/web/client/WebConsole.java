package org.openremote.console.web.client;

import org.openremote.console.web.client.binding.Activity;
import org.openremote.console.web.client.binding.UiDef;
import org.openremote.console.web.client.binding.UiXmlParser;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
// TODO: create unit test
public class WebConsole implements EntryPoint {

	private FlexTable activitiesTable;

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

	private RequestCallback newGetUiXmlCallback() {
		RequestCallback callback = new RequestCallback() {
			@Override
			public void onResponseReceived(Request request, Response response) {
				if (response.getStatusCode() == 200) {
					UiDef uiDef = parseUiDef(response);
					initUi(uiDef);
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

	private void initUi(UiDef uiDef) {

		this.activitiesTable = new FlexTable();
		activitiesTable.setText(0, 0, "Activites");
		int rowNum = 1;
		for (Activity activity : uiDef.getActivities()) {
			Hyperlink link = new Hyperlink(activity.getName(), "activities");
			activitiesTable.setWidget(rowNum, 0, link);
			rowNum++;
		}
		RootPanel.get("console").add(activitiesTable);

	}

	private UiDef parseUiDef(Response response) {
		UiXmlParser parser = new UiXmlParser(response.getText());
		UiDef uiDef = parser.parse();
		return uiDef;
	}

}
