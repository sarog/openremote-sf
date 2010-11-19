package org.openremote.controller.protocol.lutron;

public class Dimmer {

	/**
	 * Gateway we're associated with. This is the gateway we'll use to send the commands.
	 */
	private LutronHomeWorksGateway gateway;
	
	/**
	 * Address of this Dimmer module in the Lutron system.
	 */
	private LutronHomeWorksAddress address;
	
	public Dimmer(LutronHomeWorksGateway gateway, LutronHomeWorksAddress address) {
		this.gateway = gateway;
		this.address = address;
	}
	
	public void raise() {
		this.gateway.sendCommand("RAISEDIM, " + address); 
	}
	
	public void lower() {
		this.gateway.sendCommand("LOWERDIM, " + address); 
	}

	public void stop() {
		this.gateway.sendCommand("STOPDIM, " + address); 
	}
	
}
