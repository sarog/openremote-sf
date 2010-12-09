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
		this.gateway.sendCommand("RAISEDIM, " + address); 
	}
	
	public void lower() {
		this.gateway.sendCommand("LOWERDIM, " + address); 
	}

	public void stop() {
		this.gateway.sendCommand("STOPDIM, " + address); 
	}
	
	public void fade(Integer level) {
		this.gateway.sendCommand("FADE, " + level + ", 1, 0, " + address);
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
