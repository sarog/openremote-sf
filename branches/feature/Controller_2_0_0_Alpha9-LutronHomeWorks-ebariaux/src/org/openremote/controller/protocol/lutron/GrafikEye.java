package org.openremote.controller.protocol.lutron;

/**
 * Represents a GrafikEye component on the Lutron bus.
 * This class sends command to the HomeWorks processor for action.
 * It also listen for feedback from the processor and keeps its state up to date.
 * 
 * @author Eric Bariaux
 *
 */
public class GrafikEye extends HomeWorksDevice {

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

	public void selectScene(Integer scene) {
		this.gateway.sendCommand("GSS, " + address + ", " + scene); 
	}
	
	// Feedback method from HomeWorksDevice ---------------------------------------------------------

	@Override
	public void processUpdate(String info) {
		selectedScene = Integer.parseInt(info);
		// TODO: handle exception
	}

	// Getters/Setters ------------------------------------------------------------------------------

	public Integer getSelectedScene() {
		return selectedScene;
	}

}
