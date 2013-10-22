package org.openremote.controller.protocol.onewire;

import org.apache.commons.lang.StringUtils;
import org.openremote.controller.Configuration;
import org.openremote.controller.ControllerConfiguration;
import org.openremote.controller.utils.Logger;
import org.owfs.jowfsclient.Enums;

/**
 * OneWire protocol configuration service that defines defaults for every 1-wire command
 * @author Tom Kucharski <kucharski.tom@gmail.com>
 */
public class OneWireDefaultConfiguration extends Configuration {

	private static Logger log = OneWireLoggerFactory.getLogger();

	public static final String ONEWIRE_HOST = "onewire.host";

	public static final String ONEWIRE_PORT = "onewire.port";

	public static final String ONEWIRE_TEMPERATURE_SCALE = "onewire.temperaturescale";

	public OneWireDefaultConfiguration() {
		ControllerConfiguration.updateWithControllerXMLConfiguration(this);
		log.info("Default host: '"+getHost()+"'");
		log.info("Default port: '"+getPort()+"'");
	}

	public String getHost() {
		return preferAttrCustomValue(ONEWIRE_HOST,StringUtils.EMPTY);
	}

	public String getPort() {
		return preferAttrCustomValue(ONEWIRE_PORT,StringUtils.EMPTY);
	}

	public Enums.OwTemperatureScale getTemperatureScale() {
		String temperatureScale = preferAttrCustomValue(ONEWIRE_TEMPERATURE_SCALE, StringUtils.EMPTY);
		try {
			return Enums.OwTemperatureScale.valueOf(temperatureScale);
		} catch (Exception e) {
			log.warn("Missing temperature scale value in '"+ONEWIRE_TEMPERATURE_SCALE+"' property. "+ Enums.OwTemperatureScale.CELSIUS+" will be return");
			return Enums.OwTemperatureScale.CELSIUS;
		}
	}

}
