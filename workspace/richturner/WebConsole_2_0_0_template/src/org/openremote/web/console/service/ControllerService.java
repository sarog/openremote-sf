package org.openremote.web.console.service;

import org.openremote.web.console.controller.ControllerCredentials;
import org.openremote.web.console.controller.ControllerGetSensorValueResponse;
import org.openremote.web.console.controller.ControllerResponse;
import org.openremote.web.console.controller.ControllerSendCommandResponse;
import org.openremote.web.console.event.value.ControllerValueChangeHandler;
import org.openremote.web.console.event.value.UiValueChangeHandler;
import org.openremote.web.console.panel.PanelIdentity;
import org.openremote.web.console.panel.entity.Panel;

public interface ControllerService extends ControllerValueChangeHandler, UiValueChangeHandler {
	public PanelIdentity[] getPanelNames();
	
	public ControllerResponse authenticate(ControllerCredentials credentials);
	
	public Panel getPanelLayout(String panelName);
	
	public void sendCommand(int controlId, String commandParameter, AsyncControllerCallback<ControllerSendCommandResponse> callback);
	
	public void getSensorValue(int sensorId, AsyncControllerCallback<ControllerGetSensorValueResponse> callback);
	
	public void monitorSensorValues(int[] sensorIds, AsyncControllerCallback<ControllerGetSensorValueResponse> callback);
}
