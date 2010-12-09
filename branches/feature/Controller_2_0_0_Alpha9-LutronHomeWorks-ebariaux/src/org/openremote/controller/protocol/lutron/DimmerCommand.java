package org.openremote.controller.protocol.lutron;

import java.util.Map;

import org.openremote.controller.command.ExecutableCommand;
import org.openremote.controller.command.StatusCommand;
import org.openremote.controller.component.EnumSensorType;
import org.openremote.controller.exception.NoSuchCommandException;


public class DimmerCommand extends LutronHomeWorksCommand implements ExecutableCommand, StatusCommand {
	

	  // Class Members --------------------------------------------------------------------------------

	public static LutronHomeWorksCommand createCommand(String name, LutronHomeWorksGateway gateway, LutronHomeWorksAddress address, Integer scene, Integer key, Integer level) {
		// Check for mandatory attributes
		if (address == null) {
		    throw new NoSuchCommandException("Address is required for any dimmer command");
		}
		
		return new DimmerCommand(name, gateway, address, level);
	}

	// Private Instance Fields ----------------------------------------------------------------------

	/**
	 * Destination address for this command.
	 */
	private LutronHomeWorksAddress address;

	/**
	 * Level to set when sending the command. Also used to compare with reported level for Switch type sensors.
	 */
	private Integer level;
	
	// Constructors ---------------------------------------------------------------------------------

	public DimmerCommand(String name, LutronHomeWorksGateway gateway, LutronHomeWorksAddress address, Integer level) {
		super(name, gateway);
		this.address = address;
		this.level = level;
	}

	  // Implements ExecutableCommand -----------------------------------------------------------------

	  /**
	   * {@inheritDoc}
	   */
	  public void send()
	  {
		  Dimmer dimmer = (Dimmer) gateway.getHomeWorksDevice(address, Dimmer.class);
		  if ("RAISE".equals(name)) {
			  dimmer.raise();
		  } else if ("LOWER".equals(name)) {
			  dimmer.lower();
		  } else if ("STOP".equals(name)) {
			  dimmer.stop();
		  } else if ("FADE".equals(name)) {
			  dimmer.fade(level);
		  }
	  }

	  // Implements StatusCommand -------------------------------------------------------------------

	@Override
	public String read(EnumSensorType sensorType, Map<String, String> stateMap) {
		Dimmer dimmer = (Dimmer) gateway.getHomeWorksDevice(address, Dimmer.class);
		if (dimmer == null) {
			// TODO anything better to do ? send query to Lutron ?
			return "";
		}
		if (dimmer.getLevel() == null) {
			return "";
		}
		if (sensorType == EnumSensorType.SWITCH) {
			return (dimmer.getLevel().intValue() != 0)?"on":"off";
		} else if (sensorType == EnumSensorType.RANGE) {
			return Integer.toString(dimmer.getLevel());
		} else {
			// TODO log
			return "";
		}
	}

}
