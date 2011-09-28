package org.openremote.web.console.panel.entity.component;

public interface SliderMinMax {
	int getValue();
	String getImage();
	String getTrackImage();
	
	void setValue(int value);
	void setImage(String image);
	void setTrackImage(String trackImage);
}
