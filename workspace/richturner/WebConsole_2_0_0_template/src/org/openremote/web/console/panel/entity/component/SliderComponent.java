package org.openremote.web.console.panel.entity.component;

import org.openremote.web.console.panel.entity.Link;

public interface SliderComponent {
	int getId();
	String getThumbImage();
	boolean getVertical();
	boolean getPassive();
	Link getLink();
	SliderMinMax getMin();
	SliderMinMax getMax();
	
	void setId(int id);
	void setThumbImage(String thumb);
	void setVertical(boolean vertical);
	void setPassive(boolean passive);
	void setLink(Link link);
	void setMin(SliderMinMax min);
	void setMax(SliderMinMax max);
}
