package org.openremote.controller.protocol.lutron;

import org.openremote.controller.command.ExecutableCommand;
import org.openremote.controller.exception.NoSuchCommandException;


public class DimmerCommand extends LutronHomeWorksCommand implements ExecutableCommand {

	public static LutronHomeWorksCommand createCommand(String name, LutronHomeWorksGateway gateway, LutronHomeWorksAddress address, Integer scene, Integer key) {
		// Check for mandatory attributes
		if (address == null) {
		    throw new NoSuchCommandException("Address is required for any dimmer command");
		}
		
		return new DimmerCommand(name, gateway, address);
	}

	// Private Instance Fields
	// ----------------------------------------------------------------------

	/**
	 * Destination address for this command.
	 */
	private LutronHomeWorksAddress address;

	// Constructors
	// ---------------------------------------------------------------------------------

	public DimmerCommand(String name, LutronHomeWorksGateway gateway, LutronHomeWorksAddress address) {
		super(name, gateway);
		this.address = address;
	}

	  // Implements ExecutableCommand -----------------------------------------------------------------

	  /**
	   * {@inheritDoc}
	   */
	  public void send()
	  {
		  Dimmer dimmer = gateway.getDimmer(address);
		  if ("RAISE".equals(name)) {
			  dimmer.raise();
		  } else if ("LOWER".equals(name)) {
			  dimmer.lower();
		  } else if ("STOP".equals(name)) {
			  dimmer.stop();
		  }
	  }

}
