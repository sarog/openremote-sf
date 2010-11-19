package org.openremote.controller.protocol.lutron;

import org.apache.log4j.Logger;
import org.openremote.controller.command.ExecutableCommand;
import org.openremote.controller.exception.NoSuchCommandException;

public class GrafikEyeCommand extends LutronHomeWorksCommand implements ExecutableCommand {

	// Class Members
	// --------------------------------------------------------------------------------

	/**
	 * Lutron logger. Uses a common category for all Lutron related logging.
	 */
	private final static Logger log = Logger.getLogger(LutronHomeWorksCommandBuilder.LUTRON_LOG_CATEGORY);

	public static LutronHomeWorksCommand createCommand(String name, LutronHomeWorksGateway gateway, LutronHomeWorksAddress address, Integer scene, Integer key) {
		
		log.info("createCommand ("+ name + "," + gateway + "," + address + "," + scene + "," +key + ")");

		// Check for mandatory attributes
		if (address == null) {
		    throw new NoSuchCommandException("Address is required for any GrafikEye command");
		}
		
		if (scene == null) {
		    throw new NoSuchCommandException("Scene is required for any GrafikEye command");
		}
		
		return new GrafikEyeCommand(name, gateway, address, scene);
	}

	// Private Instance Fields
	// ----------------------------------------------------------------------

	/**
	 * Destination address for this command.
	 */
	private LutronHomeWorksAddress address;
	
	/**
	 * Number of the scene this command must select.
	 */
	private Integer scene;

	// Constructors
	// ---------------------------------------------------------------------------------

	public GrafikEyeCommand(String name, LutronHomeWorksGateway gateway, LutronHomeWorksAddress address, Integer scene) {
		super(name, gateway);
		this.address = address;
		this.scene = scene;
	}

	  // Implements ExecutableCommand -----------------------------------------------------------------

	  /**
	   * {@inheritDoc}
	   */
	  public void send()
	  {
		  GrafikEye grafikEye = (GrafikEye) gateway.getHomeWorksDevice(address, GrafikEye.class);
		  grafikEye.selectScene(scene);
	  }
}
