package org.openremote.controller.protocol.lutron;

/**
 * Represents one of the Lutron "device" connected to the bus that can be used in OpenRemote.
 * Each device subclass implements methods used to "actuate" the device, specific to the functionality of the device.
 * Feedback received from the processor is passed to the device. It is up to the device to interpret the information in the appropriate way.
 * 
 * @author <a href="mailto:eric@openremote.org">Eric Bariaux</a>
 */
public abstract class HomeWorksDevice {

	// Instance Fields
	// ----------------------------------------------------------------------

	/**
	 * Gateway we're associated with. This is the gateway we'll use to send the commands.
	 */
	protected LutronHomeWorksGateway gateway;
	
	/**
	 * Address of this device in the Lutron system.
	 */
	protected LutronHomeWorksAddress address;

	// Constructors ---------------------------------------------------------------------------------

	public HomeWorksDevice(LutronHomeWorksGateway gateway, LutronHomeWorksAddress address) {
		this.gateway = gateway;
		this.address = address;
	}

	// Public methods -------------------------------------------------------------------------------
	
	/**
	 * Called when a feedback information is received from the Lutron HomeWorks in order for this device to update its status.
	 * This is implemented by each specific device to process the feedback received as appropriate for it.
	 * 
	 * @param info String as received from the Lutron after the device address
	 */
	public void processUpdate(String info) {
		// Do nothing here, implemented by subclasses that require it
	}
	
}
