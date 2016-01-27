package org.openremote.web.console.panel.entity.component;

public interface SliderMinMax {
	Integer getValue();
	String getImage();
	String getTrackImage();
	
	void setValue(Integer value);
	void setImage(String image);
	void setTrackImage(String trackImage);
}
