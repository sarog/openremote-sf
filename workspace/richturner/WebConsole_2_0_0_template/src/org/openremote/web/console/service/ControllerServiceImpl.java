package org.openremote.web.console.service;

import org.openremote.web.console.controller.ControllerConnector;
import org.openremote.web.console.controller.ControllerCredentials;
import org.openremote.web.console.controller.EnumControllerCommand;
import org.openremote.web.console.controller.message.ControllerMessage;
import org.openremote.web.console.event.ConsoleUnitEventManager;
import org.openremote.web.console.event.controller.ControllerMessageEvent;
import org.openremote.web.console.event.value.UiValueChangeEvent;
import org.openremote.web.console.rpc.ControllerJsonConnector;

import com.google.gwt.event.shared.HandlerManager;

public class ControllerServiceImpl implements ControllerService {
	private ControllerCredentials credentials;
	private ControllerConnector controllerConnector = new ControllerJsonConnector();
	private HandlerManager eventBus = ConsoleUnitEventManager.getInstance().getEventBus();
	
	public ControllerServiceImpl(ControllerCredentials credentials) {
		this.credentials = credentials;
	}

	@Override
	public void onUiValueChange(UiValueChangeEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void getPanelNames() {
		// TODO Auto-generated method stub
		controllerConnector.getData(credentials.getUrl(), EnumControllerCommand.GET_PANEL_LIST, this);
	}

	@Override
	public void authenticate(ControllerCredentials credentials) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void getPanelLayout(String panelName) {
		// TODO Auto-generated method stub

	}

	@Override
	public void processControllerResponse(EnumControllerCommand command, Object obj) {
		// TODO Auto-generated method stub
		eventBus.fireEvent(ControllerMessageEvent.create(ControllerMessage.Type.COMMAND_RESPONSE, command, obj));
	}
}

