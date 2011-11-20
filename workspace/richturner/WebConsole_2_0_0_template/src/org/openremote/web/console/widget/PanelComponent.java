package org.openremote.web.console.widget;

import java.util.Set;

public interface PanelComponent {
	public Set<Sensor> getSensors();
	
	public Set<ConsoleComponent> getComponents();
}
