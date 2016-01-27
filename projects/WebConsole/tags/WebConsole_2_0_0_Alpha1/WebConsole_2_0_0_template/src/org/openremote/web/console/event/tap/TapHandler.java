package org.openremote.web.console.event.tap;

import com.google.gwt.event.shared.EventHandler;

public interface TapHandler extends EventHandler {
	void onTap(TapEvent event);
}
