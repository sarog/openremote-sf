package org.openremote.web.console.view;

import java.util.Set;
import org.openremote.web.console.widget.PanelComponent;

public interface ScreenView {
	public Set<PanelComponent> getPanelComponents();
	
	public Set<Integer> getSensorIds();
}
