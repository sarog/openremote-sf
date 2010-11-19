package org.openremote.controller.protocol.lutron;

/**
 * Represents a GrafikEye component on the Lutron bus.
 * This class sends command to the HomeWorks processor for action.
 * It also listen for feedback from the processor and keeps its state up to date.
 * 
 * @author ebr
 *
 */
public class GrafikEye {

	/**
	 * Currently selected scene, as reported by the system. Null if we don"t have this info.
	 */
	private Integer selectedScene;
	
	/**
	 * Gateway we're associated with. This is the gateway we'll use to send the commands.
	 */
	private LutronHomeWorksGateway gateway;
	
	/**
	 * Address of this GrafikEye module in the Lutron system.
	 */
	private LutronHomeWorksAddress address;
	
	public GrafikEye(LutronHomeWorksGateway gateway, LutronHomeWorksAddress address) {
		this.gateway = gateway;
		this.address = address;
	}
	
	public void selectScene(Integer scene) {
		this.gateway.sendCommand("GSS, " + address + ", " + scene); 
	}
}
