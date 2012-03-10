package org.openremote.web.console.view;

import java.util.List;
import java.util.Set;
import org.openremote.web.console.panel.entity.DataValuePairContainer;
import org.openremote.web.console.widget.panel.PanelComponent;

public interface ScreenView {
	public Set<PanelComponent> getPanelComponents();
	
	public Set<Integer> getSensorIds();
	
	public void setIsLandscape(boolean isLandscape);
	
	public boolean isLandscape();
	
	public void onAdd(int width, int height, List<DataValuePairContainer> data);
}
