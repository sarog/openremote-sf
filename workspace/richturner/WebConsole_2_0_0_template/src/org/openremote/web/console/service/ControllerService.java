package org.openremote.web.console.service;

import org.openremote.web.console.controller.Controller;
import org.openremote.web.console.controller.ControllerCallBackHandler;
import org.openremote.web.console.controller.ControllerConnector;
import org.openremote.web.console.controller.message.ControllerRequestMessage;
import org.openremote.web.console.controller.message.ControllerResponseMessage;
import org.openremote.web.console.event.ConsoleUnitEventManager;
import org.openremote.web.console.event.controller.ControllerMessageEvent;
import org.openremote.web.console.event.value.UiValueChangeEvent;
import org.openremote.web.console.event.value.UiValueChangeHandler;
import org.openremote.web.console.rpc.json.ControllerJsonConnector;

import com.google.gwt.event.shared.HandlerManager;

public class ControllerService implements UiValueChangeHandler, ControllerCallBackHandler {
	private Controller controller;
	private static ControllerConnector controllerConnector = new ControllerJsonConnector();
	private static HandlerManager eventBus = ConsoleUnitEventManager.getInstance().getEventBus();
	
	public ControllerService(Controller controller) {
		this.controller = controller;
	}

	@Override
	public void onUiValueChange(UiValueChangeEvent event) {
		// TODO Auto-generated method stub
		
	}

	public void authenticate() {
		// TODO Auto-generated method stub
		
	}
	
	public void sendCommand(int requestId, ControllerRequestMessage message) {
		if (controller != null) {
			controllerConnector.getData(requestId, controller.getUrl(), message, this);
		}
	}
	
	@Override
	public void processControllerResponse(ControllerResponseMessage message) {
		eventBus.fireEvent(new ControllerMessageEvent(message));
	}
	
	public static boolean isControllerAlive(int requestId, String controllerUrl) {
		return controllerConnector.isAlive(requestId, controllerUrl);
	}
	
	public static boolean isControllerSecure(int requestId, String controllerUrl) {
		return controllerConnector.isSecure(requestId, controllerUrl);
	}
	
//	public void getSensorValue(int sensorId, AsyncControllerCallback<ControllerMessage> callback);
//	
//	public void monitorSensorValues(int[] sensorIds, AsyncControllerCallback<ControllerMessage> callback);
}