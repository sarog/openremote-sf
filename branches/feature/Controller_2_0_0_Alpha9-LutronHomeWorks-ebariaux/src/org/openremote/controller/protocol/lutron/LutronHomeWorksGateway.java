package org.openremote.controller.protocol.lutron;

import org.apache.log4j.Logger;

public class LutronHomeWorksGateway {

	  // Class Members --------------------------------------------------------------------------------

	  /**
	   * Lutron HomeWorks logger. Uses a common category for all Lutron related logging.
	   */
	  private final static Logger log = Logger.getLogger(LutronHomeWorksCommandBuilder.LUTRON_LOG_CATEGORY);

	  
	  public void sendCommand(String command) {
		  System.out.println("Asked to send command " + command);
	  }
	  
	  public GrafikEye getGrafikEye(LutronHomeWorksAddress address) {
		  // TODO: have a cache ...
		  return new GrafikEye(this, address);
	  }
	  
	  public Keypad getKeypad(LutronHomeWorksAddress address) {
		  // TODO: have a cache ...
		  return new Keypad(this, address);
	  }

	  public Dimmer getDimmer(LutronHomeWorksAddress address) {
		  // TODO: have a cache ...
		  return new Dimmer(this, address);
	  }
}
