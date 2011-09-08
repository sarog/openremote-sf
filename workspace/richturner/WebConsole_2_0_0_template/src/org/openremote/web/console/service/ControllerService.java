package org.openremote.web.console.service;

import org.openremote.web.console.controller.ControllerCredentials;
import org.openremote.web.console.controller.EnumControllerCommand;
import org.openremote.web.console.event.value.UiValueChangeHandler;

public interface ControllerService extends UiValueChangeHandler {
	public void getPanelNames();
	
	public void authenticate(ControllerCredentials credentials);
	
	public void getPanelLayout(String panelName);
	
//	public void sendCommand(int controlId, String commandParameter, AsyncControllerCallback<ControllerMessage> callback);
//	
//	public void getSensorValue(int sensorId, AsyncControllerCallback<ControllerMessage> callback);
//	
//	public void monitorSensorValues(int[] sensorIds, AsyncControllerCallback<ControllerMessage> callback);
	
	public void processControllerResponse(EnumControllerCommand command, Object obj);
}
