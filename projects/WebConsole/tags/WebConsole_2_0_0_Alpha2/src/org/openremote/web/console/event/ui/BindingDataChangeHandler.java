package org.openremote.web.console.event.ui;

import com.google.gwt.event.shared.EventHandler;

public interface BindingDataChangeHandler extends EventHandler {
	void onBindingDataChange(BindingDataChangeEvent event);
}
