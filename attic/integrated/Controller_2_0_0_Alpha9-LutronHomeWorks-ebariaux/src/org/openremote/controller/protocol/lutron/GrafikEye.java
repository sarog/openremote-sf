package org.openremote.controller.protocol.lutron;

import org.apache.log4j.Logger;

/**
 * Represents a GRAFIK Eye component on the Lutron bus.
 * This class sends command to the HomeWorks processor for action.
 * It also listen for feedback from the processor and keeps its state up to date.
 * 
 * @author <a href="mailto:eric@openremote.org">Eric Bariaux</a>
 *
 */
public class GrafikEye extends HomeWorksDevice {

  /**
   * Lutron logger. Uses a common category for all Lutron related logging.
   */
  private final static Logger log = Logger.getLogger(LutronHomeWorksCommandBuilder.LUTRON_LOG_CATEGORY);

	// Private Instance Fields ----------------------------------------------------------------------

	/**
	 * Currently selected scene, as reported by the system. Null if we don't have this info.
	 */
	private Integer selectedScene;
	
	// Constructors ---------------------------------------------------------------------------------

	public GrafikEye(LutronHomeWorksGateway gateway, LutronHomeWorksAddress address) {
		super(gateway, address);
	}
	
	// Command methods ------------------------------------------------------------------------------

	/**
	 * Selects the requested local scene on the GRAFIK Eye.
	 * 
	 * @param scene scene to select
	 */
	public void selectScene(Integer scene) {
		this.gateway.sendCommand("GSS", address, Integer.toString(scene)); 
	}
	
	/**
	 * Requests currently selected local scene on the GRAFIK Eye
	 */
	public void queryScene() {
	  this.gateway.sendCommand("RGS", address, null);
	}
	
	// Feedback method from HomeWorksDevice ---------------------------------------------------------

	@Override
	public void processUpdate(String info) {
	  try {
	    selectedScene = Integer.parseInt(info);
	  } catch (NumberFormatException e) {
	    // Not understood as a scene, do not update ourself
	    log.warn("Invalid feedback received " + info, e);
	  }
	}

	// Getters/Setters ------------------------------------------------------------------------------

	public Integer getSelectedScene() {
		return selectedScene;
	}

}
