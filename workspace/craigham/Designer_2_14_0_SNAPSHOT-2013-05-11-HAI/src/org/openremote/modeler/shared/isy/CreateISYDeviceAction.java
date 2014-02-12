package org.openremote.modeler.shared.isy;

import net.customware.gwt.dispatch.shared.Action;

public class CreateISYDeviceAction implements Action<CreateISYDeviceResult> {

	private String mUrl;
	private String mUserName;
	private String mPassward;

	public CreateISYDeviceAction() {
		super();
	}

	public CreateISYDeviceAction(String url, String userName, String password) {
		super();
		mUrl = url;
		mUserName = userName;
		mPassward = password;
	}

	public String getUrl() {
		return mUrl;
	}

	public String getUserName() {
		return mUserName;
	}

	public String getPassward() {
		return mPassward;
	}
}
