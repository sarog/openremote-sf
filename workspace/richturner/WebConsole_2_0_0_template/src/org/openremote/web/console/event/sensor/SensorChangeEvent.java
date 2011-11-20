package org.openremote.web.console.event.sensor;

import com.google.gwt.event.shared.GwtEvent;

/**
 * This event provides a high level semantic event to indicate a quick press
 * and release event on a component with restricted X, Y movement during event
 * @author rich
 *
 */
public class SensorChangeEvent extends GwtEvent<SensorChangeHandler> {
	private static final Type<SensorChangeHandler> TYPE = new Type<SensorChangeHandler>();
	private int sensorId;
	private String sensorValue;
	
	public SensorChangeEvent(int sensorId, String sensorValue) {
		this.sensorId = sensorId;
		this.sensorValue = sensorValue;
	}
	
	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<SensorChangeHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(SensorChangeHandler handler) {
		handler.onSensorChange(this);
	}

	public static Type<SensorChangeHandler> getType() {
		return TYPE;
	}
	
	public int getSensorId() {
		return sensorId;
	}
	
	public String getNewValue() {
		return sensorValue;
	}
}
