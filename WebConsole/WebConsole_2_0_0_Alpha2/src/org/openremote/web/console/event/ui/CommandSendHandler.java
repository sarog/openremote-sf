package org.openremote.web.console.event.ui;

import com.google.gwt.event.shared.EventHandler;

public interface CommandSendHandler extends EventHandler {
	void onCommandSend(CommandSendEvent event);
}
