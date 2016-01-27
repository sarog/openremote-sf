package org.openremote.web.console.event.sensor;

import com.google.gwt.event.shared.EventHandler;

public interface SensorChangeHandler extends EventHandler {
	void onSensorChange(SensorChangeEvent event);
	
	void onSensorAdd();
	
	void sensorChanged(String value);
}
