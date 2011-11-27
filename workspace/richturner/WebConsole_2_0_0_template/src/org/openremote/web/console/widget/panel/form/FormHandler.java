package org.openremote.web.console.widget.panel.form;

import com.google.gwt.core.client.JavaScriptObject;

public interface FormHandler {
	public void onValidationSuccess(JavaScriptObject obj);
	
	public void onValidationFailure(Throwable exception);
}
