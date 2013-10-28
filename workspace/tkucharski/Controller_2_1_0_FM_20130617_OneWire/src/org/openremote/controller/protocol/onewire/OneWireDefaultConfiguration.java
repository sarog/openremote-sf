package org.openremote.controller.protocol.onewire;

import org.apache.commons.lang.StringUtils;
import org.openremote.controller.Configuration;
import org.openremote.controller.ControllerConfiguration;
import org.openremote.controller.utils.Logger;
import org.owfs.jowfsclient.Enums;

/**
 * OneWire protocol configuration service that defines defaults for every 1-wire command
 *
 * @author Tom Kucharski <kucharski.tom@gmail.com>
 */
public class OneWireDefaultConfiguration extends Configuration {

	public static final String ONEWIRE_HOST = "onewire.host";

	public static final String ONEWIRE_PORT = "onewire.port";

	public static final String ONEWIRE_TEMPERATURE_SCALE = "onewire.temperaturescale";

	private static Logger log = OneWireLogger.getLogger();

	/**
	 * Cached host value. Saves a lot of time to read controller.xml every command is created via command builder
	 */
	private String host;

	/**
	 * Cached port value. Saves a lot of time to read controller.xml every command is created via command builder
	 */
	private String port;

	/**
	 * Cached temperatureScale value. Saves a lot of time to read controller.xml every command is created via command builder
	 */
	private Enums.OwTemperatureScale temperatureScale;

	public OneWireDefaultConfiguration() {
		ControllerConfiguration.updateWithControllerXMLConfiguration(this);
		log.info("Default host: '" + getHost() + "'");
		log.info("Default port: '" + getPort() + "'");
	}

	public String getHost() {
		if (host == null) {
			host = preferAttrCustomValue(ONEWIRE_HOST, StringUtils.EMPTY);
		}
		return host;
	}

	public String getPort() {
		if (port == null) {
			port = preferAttrCustomValue(ONEWIRE_PORT, StringUtils.EMPTY);
		}
		return port;
	}

	public Enums.OwTemperatureScale getTemperatureScale() {
		if (temperatureScale == null) {
			String temperatureScaleString = preferAttrCustomValue(ONEWIRE_TEMPERATURE_SCALE, StringUtils.EMPTY);
			try {
				temperatureScale = Enums.OwTemperatureScale.valueOf(temperatureScaleString);
			} catch (Exception e) {
				log.warn("Missing temperature scale value in '" + ONEWIRE_TEMPERATURE_SCALE + "' property. " + Enums.OwTemperatureScale.CELSIUS + " will be return");
				return Enums.OwTemperatureScale.CELSIUS;
			}
		}
		return temperatureScale;
	}

}
