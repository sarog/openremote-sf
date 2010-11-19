package org.openremote.controller.protocol.lutron;

public class Keypad {

	/**
	 * Gateway we're associated with. This is the gateway we'll use to send the commands.
	 */
	private LutronHomeWorksGateway gateway;
	
	/**
	 * Address of this Keypad module in the Lutron system.
	 */
	private LutronHomeWorksAddress address;
	
	public Keypad(LutronHomeWorksGateway gateway, LutronHomeWorksAddress address) {
		this.gateway = gateway;
		this.address = address;
	}
	
	public void press(Integer key) {
		this.gateway.sendCommand("KBP, " + address + ", " + key); 
	}

	public void release(Integer key) {
		this.gateway.sendCommand("KBR, " + address + ", " + key); 
	}

	public void hold(Integer key) {
		this.gateway.sendCommand("KBH, " + address + ", " + key); 
	}

	public void doubleTap(Integer key) {
		this.gateway.sendCommand("KBDT, " + address + ", " + key); 
	}
}
