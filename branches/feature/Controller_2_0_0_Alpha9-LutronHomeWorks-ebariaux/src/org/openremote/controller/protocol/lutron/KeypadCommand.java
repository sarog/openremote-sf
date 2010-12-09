package org.openremote.controller.protocol.lutron;

import org.apache.log4j.Logger;
import org.openremote.controller.command.ExecutableCommand;
import org.openremote.controller.exception.NoSuchCommandException;

public class KeypadCommand extends LutronHomeWorksCommand implements ExecutableCommand {

	// Class Members --------------------------------------------------------------------------------

	/**
	 * Lutron logger. Uses a common category for all Lutron related logging.
	 */
	private final static Logger log = Logger.getLogger(LutronHomeWorksCommandBuilder.LUTRON_LOG_CATEGORY);
	
	public static LutronHomeWorksCommand createCommand(String name, LutronHomeWorksGateway gateway, LutronHomeWorksAddress address, Integer scene, Integer key, Integer level) {
		// Check for mandatory attributes
		if (address == null) {
		    throw new NoSuchCommandException("Address is required for any Keypad command");
		}
		
		if (key == null) {
		    throw new NoSuchCommandException("Key is required for any Keypad command");
		}
		
		return new KeypadCommand(name, gateway, address, key);
	}

	// Private Instance Fields ----------------------------------------------------------------------

	/**
	 * Destination address for this command.
	 */
	private LutronHomeWorksAddress address;

	/**
	 * Number of key on the keypad this command must act upon.
	 */
	private Integer key;
	
	// Constructors ---------------------------------------------------------------------------------

	public KeypadCommand(String name, LutronHomeWorksGateway gateway, LutronHomeWorksAddress address, Integer key) {
		super(name, gateway);
		this.address = address;
		this.key = key;
	}

	  // Implements ExecutableCommand -----------------------------------------------------------------

	  /**
	   * {@inheritDoc}
	   */
	  public void send()
	  {
		  Keypad keypad = (Keypad) gateway.getHomeWorksDevice(address, Keypad.class);
		  if ("PRESS".equals(name)) {
			  keypad.press(key);
		  } else if ("RELEASE".equals(name)) {
			  keypad.release(key);
		  } else if ("HOLD".equals(name)) {
			  keypad.hold(key);
		  } else if ("DOUBLE_TAP".equals(name)) {
			  keypad.doubleTap(key);
		  }
	  }

}
