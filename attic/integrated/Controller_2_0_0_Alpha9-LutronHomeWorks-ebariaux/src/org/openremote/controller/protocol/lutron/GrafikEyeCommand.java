package org.openremote.controller.protocol.lutron;

import java.util.Map;

import org.apache.log4j.Logger;
import org.openremote.controller.command.ExecutableCommand;
import org.openremote.controller.command.StatusCommand;
import org.openremote.controller.component.EnumSensorType;
import org.openremote.controller.exception.NoSuchCommandException;

/**
 * 
 * @author <a href="mailto:eric@openremote.org">Eric Bariaux</a>
 *
 */
public class GrafikEyeCommand extends LutronHomeWorksCommand implements ExecutableCommand, StatusCommand {

	// Class Members --------------------------------------------------------------------------------

	/**
	 * Lutron logger. Uses a common category for all Lutron related logging.
	 */
	private final static Logger log = Logger.getLogger(LutronHomeWorksCommandBuilder.LUTRON_LOG_CATEGORY);

	public static LutronHomeWorksCommand createCommand(String name, LutronHomeWorksGateway gateway, LutronHomeWorksAddress address, Integer scene, Integer key, Integer level) {
		
		log.info("createCommand ("+ name + "," + gateway + "," + address + "," + scene + "," +key + "," + level + ")");

		// Check for mandatory attributes
		if (address == null) {
		    throw new NoSuchCommandException("Address is required for any GRAFIK Eye command");
		}
		
		if (!"STATUS_SCENE".equalsIgnoreCase(name) && scene == null) {
		    throw new NoSuchCommandException("Scene is required for 'write' GRAFIK Eye command");
		}
		
    if (!address.isValidGrafikEyeAddress()) {
      throw new NoSuchCommandException("Address must be one of a GRAFIK Eye");
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

	@Override
	public String read(EnumSensorType sensorType, Map<String, String> stateMap) {
		GrafikEye grafikEye = (GrafikEye) gateway.getHomeWorksDevice(address, GrafikEye.class);
		if (grafikEye == null) {
		  // This should never happen as above command is supposed to create device
		  log.warn("Gateway could not create a GRAFIK Eye we're receiving feedback for (" + address + ")");
			return "";
		}
		if (grafikEye.getSelectedScene() == null) {
		  // We don't have any information about the state yet, ask Lutron processor about it
		  grafikEye.queryScene();
			return "";
		}
		if (sensorType == EnumSensorType.SWITCH) {
			return (grafikEye.getSelectedScene() == scene)?"on":"off";
		} else if (sensorType == EnumSensorType.RANGE) {
			return Integer.toString(grafikEye.getSelectedScene());
		} else {
      log.warn("Query GRAFIK Eye status for incompatible sensor type " + sensorType);
			return "";
		}
	}
}
