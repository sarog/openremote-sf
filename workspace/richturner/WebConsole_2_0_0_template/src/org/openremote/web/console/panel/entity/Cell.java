package org.openremote.web.console.panel.entity;

import org.openremote.web.console.panel.entity.component.ButtonComponent;
import org.openremote.web.console.panel.entity.component.ImageComponent;
import org.openremote.web.console.panel.entity.component.LabelComponent;
import org.openremote.web.console.panel.entity.component.SliderComponent;
import org.openremote.web.console.panel.entity.component.SwitchComponent;

public interface Cell extends ComponentContainer {
	Integer getX();
	Integer getY();
	Integer getRowspan();
	Integer getColspan();

	void setX(Integer x);
	void setY(Integer y);
	void setRowspan(Integer rowspan);
	void setColspan(Integer colspan);
}
