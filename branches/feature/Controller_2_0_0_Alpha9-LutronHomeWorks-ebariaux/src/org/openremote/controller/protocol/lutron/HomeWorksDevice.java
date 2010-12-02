package org.openremote.controller.protocol.lutron;

public abstract class HomeWorksDevice {

	/**
	 * Gateway we're associated with. This is the gateway we'll use to send the commands.
	 */
	protected LutronHomeWorksGateway gateway;
	
	/**
	 * Address of this device in the Lutron system.
	 */
	protected LutronHomeWorksAddress address;
	
	public void processUpdate(String info) {
		// Do nothing here, implemented by subclasses that require it
	}
	
	public HomeWorksDevice(LutronHomeWorksGateway gateway, LutronHomeWorksAddress address) {
		this.gateway = gateway;
		this.address = address;
	}
}
