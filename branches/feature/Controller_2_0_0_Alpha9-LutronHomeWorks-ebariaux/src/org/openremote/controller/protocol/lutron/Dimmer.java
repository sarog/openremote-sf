package org.openremote.controller.protocol.lutron;

public class Dimmer extends HomeWorksDevice {

	// Private Instance Fields ----------------------------------------------------------------------

	/**
	 * Current level, as reported by the system. Null if we don't have this info.
	 */
	private Integer level;
	
	// Constructors ---------------------------------------------------------------------------------

	public Dimmer(LutronHomeWorksGateway gateway, LutronHomeWorksAddress address) {
		super(gateway, address);
	}
	
	// Command methods ------------------------------------------------------------------------------
	
	public void raise() {
		this.gateway.sendCommand("RAISEDIM", address, null); 
	}
	
	public void lower() {
		this.gateway.sendCommand("LOWERDIM", address, null); 
	}

	public void stop() {
		this.gateway.sendCommand("STOPDIM", address, null); 
	}
	
	public void fade(Integer level) {
		this.gateway.sendCommand("FADEDIM, " + level + ", 1, 0", address, null);
	}

	// Feedback method from HomeWorksDevice ---------------------------------------------------------

	@Override
	public void processUpdate(String info) {
		// Value reported as float, we're using integer precision
		level = (int)Float.parseFloat(info);
		// TODO: handle exception
	}

	// Getters/Setters ------------------------------------------------------------------------------
	
	public Integer getLevel() {
		return level;
	}

}
