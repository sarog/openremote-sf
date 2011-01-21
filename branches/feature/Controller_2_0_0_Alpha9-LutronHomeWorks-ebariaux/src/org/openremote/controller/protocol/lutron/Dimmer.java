package org.openremote.controller.protocol.lutron;

import org.apache.log4j.Logger;

/**
 * Represents a dimmer device on the Lutron bus.
 * 
 * @author <a href="mailto:eric@openremote.org">Eric Bariaux</a>
 */
public class Dimmer extends HomeWorksDevice {

  /**
   * Lutron logger. Uses a common category for all Lutron related logging.
   */
  private final static Logger log = Logger.getLogger(LutronHomeWorksCommandBuilder.LUTRON_LOG_CATEGORY);

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

	/**
	 * Starts raising the value of the dimmer
	 */
	public void raise() {
		this.gateway.sendCommand("RAISEDIM", address, null); 
	}
	
  /**
   * Starts lowering the value of the dimmer
   */
	public void lower() {
		this.gateway.sendCommand("LOWERDIM", address, null); 
	}

  /**
   * Stops raising or lowering the value of the dimmer
   */
	public void stop() {
		this.gateway.sendCommand("STOPDIM", address, null); 
	}
	
	/**
	 * Immediately sets the value of the dimmer to the given value
	 * 
	 * @param level Level to set the dimmer to, expressed in %
	 */
	public void fade(Integer level) {
		this.gateway.sendCommand("FADEDIM, " + level + ", 1, 0", address, null);
	}
	
	/**
	 * Requests level of dimmer from Lutron processor.
	 */
	public void queryLevel() {
	  this.gateway.sendCommand("RDL", address, null);
	}

	// Feedback method from HomeWorksDevice ---------------------------------------------------------

	@Override
	public void processUpdate(String info) {
		// Value reported as float, we're using integer precision
	  try {
	    level = (int)Float.parseFloat(info);
	  } catch (NumberFormatException e) {
	    // Not understood as a level, do not update ourself
	    log.warn("Invalid feedback received " + info, e);
	  }
	}

	// Getters/Setters ------------------------------------------------------------------------------
	
  /**
   * Returns the currently known level of the dimmer
   * @return current level
   */
	public Integer getLevel() {
		return level;
	}

}
