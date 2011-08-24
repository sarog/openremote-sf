package org.openremote.web.console.widget;

import org.openremote.web.console.event.ConsoleUnitEventManager;
import org.openremote.web.console.event.press.PressEndEvent;
import org.openremote.web.console.event.press.PressStartEvent;

public abstract class ConsoleWidgetImpl extends ConsoleComponent implements ConsoleWidget {
	protected boolean isInitialised = false;
	
	public void initialise(ConsoleUnitEventManager eventManager) {
		this.addHandler(eventManager.getPressMoveReleaseHandler(), PressStartEvent.getType());
		this.addHandler(eventManager.getPressMoveReleaseHandler(), PressEndEvent.getType());
		configure();
		setVisible(true);
		isInitialised = true;
	}
	
	public void setVisible(boolean visible) {
		this.getWidget().setVisible(visible);
	}
}
