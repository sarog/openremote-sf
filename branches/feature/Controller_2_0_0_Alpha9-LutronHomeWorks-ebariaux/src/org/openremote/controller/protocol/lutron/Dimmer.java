package org.openremote.controller.protocol.lutron;

public class Dimmer extends HomeWorksDevice {

	public Dimmer(LutronHomeWorksGateway gateway, LutronHomeWorksAddress address) {
		super(gateway, address);
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
