package org.openremote.web.console.panel.entity.component;

import org.openremote.web.console.panel.entity.Link;

public interface SliderComponent {
	Integer getId();
	String getThumbImage();
	Boolean getVertical();
	Boolean getPassive();
	Link getLink();
	SliderMinMax getMin();
	SliderMinMax getMax();
	
	void setId(Integer id);
	void setThumbImage(String thumb);
	void setVertical(Boolean vertical);
	void setPassive(Boolean passive);
	void setLink(Link link);
	void setMin(SliderMinMax min);
	void setMax(SliderMinMax max);
}
