package org.openremote.controller.protocol.lutron;

public class Keypad extends HomeWorksDevice {

	// Private Instance Fields ----------------------------------------------------------------------

	// TODO: implement LED feedback
	
	// Constructors ---------------------------------------------------------------------------------

	public Keypad(LutronHomeWorksGateway gateway, LutronHomeWorksAddress address) {
		super(gateway, address);
	}
	
	// Command methods ------------------------------------------------------------------------------

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
