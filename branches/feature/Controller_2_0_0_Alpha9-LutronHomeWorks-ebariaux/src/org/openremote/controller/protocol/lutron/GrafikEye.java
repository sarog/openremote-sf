package org.openremote.controller.protocol.lutron;

/**
 * Represents a GrafikEye component on the Lutron bus.
 * This class sends command to the HomeWorks processor for action.
 * It also listen for feedback from the processor and keeps its state up to date.
 * 
 * @author ebr
 *
 */
public class GrafikEye extends HomeWorksDevice {

	/**
	 * Currently selected scene, as reported by the system. Null if we don"t have this info.
	 */
	private Integer selectedScene;

	public GrafikEye(LutronHomeWorksGateway gateway, LutronHomeWorksAddress address) {
		super(gateway, address);
	}

	public void selectScene(Integer scene) {
		this.gateway.sendCommand("GSS, " + address + ", " + scene); 
	}
}
