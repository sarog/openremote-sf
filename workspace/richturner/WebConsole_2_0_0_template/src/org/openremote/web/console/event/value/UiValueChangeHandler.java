package org.openremote.web.console.event.value;

import com.google.gwt.event.shared.EventHandler;

public interface UiValueChangeHandler extends EventHandler {
	void onUiValueChange(UiValueChangeEvent event);
}
