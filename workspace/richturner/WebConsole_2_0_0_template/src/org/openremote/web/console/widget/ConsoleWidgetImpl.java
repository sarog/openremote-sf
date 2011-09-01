package org.openremote.web.console.widget;

public abstract class ConsoleWidgetImpl extends ConsoleComponent implements ConsoleWidget {
	protected boolean isInitialised = false;
	
	public void initialise() {
		//this.addHandler(eventManager.getPressMoveReleaseHandler(), PressStartEvent.getType());
		//this.addHandler(eventManager.getPressMoveReleaseHandler(), PressEndEvent.getType());
		configure();
		setVisible(true);
		isInitialised = true;
	}
	
	public void setVisible(boolean visible) {
		this.getWidget().setVisible(visible);
	}
}
