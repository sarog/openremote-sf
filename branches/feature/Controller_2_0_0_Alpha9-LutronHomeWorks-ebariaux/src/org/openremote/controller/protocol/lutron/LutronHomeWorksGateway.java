package org.openremote.controller.protocol.lutron;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import org.apache.log4j.Logger;

public class LutronHomeWorksGateway {

	  // Class Members --------------------------------------------------------------------------------

	  /**
	   * Lutron HomeWorks logger. Uses a common category for all Lutron related logging.
	   */
	  private final static Logger log = Logger.getLogger(LutronHomeWorksCommandBuilder.LUTRON_LOG_CATEGORY);

	  private static HashMap<LutronHomeWorksAddress, HomeWorksDevice> deviceCache = new HashMap<LutronHomeWorksAddress, HomeWorksDevice>(); 
	  
	  public void sendCommand(String command) {
		  System.out.println("Asked to send command " + command);
	  }
	
	  /**
	   * Gets the HomeWorks device from the cache, creating it if not already present.
	   * 
	   * @param address
	 * @return 
	   * @return
	   */
	  public HomeWorksDevice getHomeWorksDevice(LutronHomeWorksAddress address, Class<? extends HomeWorksDevice> deviceClass) {
		  HomeWorksDevice device = deviceCache.get(address);
		  if (device == null) {
			  // No device yet in the cache, try to create one
			  try {
				Constructor<? extends HomeWorksDevice> constructor = deviceClass.getConstructor(LutronHomeWorksGateway.class, LutronHomeWorksAddress.class);
				device = constructor.newInstance(this, address);
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		  }
		  if (!(deviceClass.isInstance(device))) {
			  throw new RuntimeException("Invalid device type found at given address"); // TODO, have a typed exception
		  }
		  return device;
	  }	  
}
