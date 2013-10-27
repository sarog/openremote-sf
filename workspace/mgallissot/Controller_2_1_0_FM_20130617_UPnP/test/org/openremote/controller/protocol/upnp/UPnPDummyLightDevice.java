/******************************************************************
*
*	CyberUPnP for Java
*
*	Copyright (C) Satoshi Konno 2002
*
*	File : ClockDevice.java
*
******************************************************************/
package org.openremote.controller.protocol.upnp;
import java.io.File;

import org.cybergarage.net.HostInterface;
import org.cybergarage.upnp.Action;
import org.cybergarage.upnp.AllowedValueList;
import org.cybergarage.upnp.AllowedValueRange;
import org.cybergarage.upnp.Argument;
import org.cybergarage.upnp.Device;
import org.cybergarage.upnp.Service;
import org.cybergarage.upnp.ServiceList;
import org.cybergarage.upnp.StateVariable;
import org.cybergarage.upnp.control.ActionListener;
import org.cybergarage.upnp.control.QueryListener;
import org.cybergarage.upnp.device.InvalidDescriptionException;

public class UPnPDummyLightDevice extends Device implements ActionListener, QueryListener
{
	private final static String DESCRIPTION_FILE_NAME = "test/org/openremote/controller/protocol/upnp/description/description.xml";

	private StateVariable powerVar;
	
	public UPnPDummyLightDevice() throws InvalidDescriptionException
	{
		super(new File(DESCRIPTION_FILE_NAME));
		
		setHTTPPort(60001);
		setSSDPBindAddress(
				HostInterface.getInetAddress(HostInterface.IPV4_BITMASK, null)
		);
		setHTTPBindAddress(
				HostInterface.getInetAddress(HostInterface.IPV4_BITMASK, null)
		);

		Action getPowerAction = getAction("GetPower");
		getPowerAction.setActionListener(this);
		
		Action setPowerAction = getAction("SetPower");
		setPowerAction.setActionListener(this);
		
		ServiceList serviceList = getServiceList();
		Service service = serviceList.getService(0);
		service.setQueryListener(this);

		powerVar = getStateVariable("Power");

		Argument powerArg = getPowerAction.getArgument("Power");
		StateVariable powerState = powerArg.getRelatedStateVariable();
		AllowedValueList allowList = powerState.getAllowedValueList();
		for (int n=0; n<allowList.size(); n++) 
			System.out.println("[" + n + "] = " + allowList.getAllowedValue(n));
			
		AllowedValueRange allowRange = powerState.getAllowedValueRange();
		System.out.println("maximum = " + allowRange.getMaximum());
		System.out.println("minimum = " + allowRange.getMinimum());
		System.out.println("step = " + allowRange.getStep());
	}
	
	////////////////////////////////////////////////
	//	on/off
	////////////////////////////////////////////////

	private boolean onFlag = false;
	
	public void on()
	{
		onFlag = true;
		powerVar.setValue("on");
	}

	public void off()
	{
		onFlag = false;
		powerVar.setValue("off");
	}

	public boolean isOn()
	{
		return onFlag;
	}
	
	public void setPowerState(String state)
	{
		if (state == null) {
			off();
			return;
		}
		if (state.compareTo("1") == 0) {
			on();
			return;
		}
		if (state.compareTo("0") == 0) {
			off();
			return;
		}
	}
	
	public String getPowerState()
	{
		if (onFlag == true)
			return "1";
		return "0";
	}

	////////////////////////////////////////////////
	// ActionListener
	////////////////////////////////////////////////

	public boolean actionControlReceived(Action action)
	{
		String actionName = action.getName();

		boolean ret = false;
		
		if (actionName.equals("GetPower") == true) {
			String state = getPowerState();
			Argument powerArg = action.getArgument("Power");
			powerArg.setValue(state);
			ret = true;
		}
		if (actionName.equals("SetPower") == true) {
			Argument powerArg = action.getArgument("Power");
			String state = powerArg.getValue();
			setPowerState(state);
			state = getPowerState();
			Argument resultArg = action.getArgument("Result");
			resultArg.setValue(state);
			ret = true;
		}

		return ret;
	}

	////////////////////////////////////////////////
	// QueryListener
	////////////////////////////////////////////////

	public boolean queryControlReceived(StateVariable stateVar)
	{
		stateVar.setValue(getPowerState());
		return true;
	}

}

