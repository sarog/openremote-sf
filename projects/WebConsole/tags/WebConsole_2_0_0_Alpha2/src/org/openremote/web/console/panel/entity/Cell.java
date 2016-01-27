package org.openremote.web.console.panel.entity;

import org.openremote.web.console.panel.entity.component.ButtonComponent;
import org.openremote.web.console.panel.entity.component.ImageComponent;
import org.openremote.web.console.panel.entity.component.LabelComponent;
import org.openremote.web.console.panel.entity.component.SliderComponent;
import org.openremote.web.console.panel.entity.component.SwitchComponent;

public interface Cell {
	Integer getX();
	Integer getY();
	Integer getRowspan();
	Integer getColspan();
	ButtonComponent getButton();
	LabelComponent getLabel();
	SliderComponent getSlider();
	ImageComponent getImage();
	SwitchComponent getSwitch();
	
	void setX(Integer x);
	void setY(Integer y);
	void setRowspan(Integer rowspan);
	void setColspan(Integer colspan);
	void setButton(ButtonComponent component);
	void setLabel(LabelComponent component);
	void setSlider(SliderComponent component);
	void setImage(ImageComponent component);
	void setSwitch(SwitchComponent component);
}
