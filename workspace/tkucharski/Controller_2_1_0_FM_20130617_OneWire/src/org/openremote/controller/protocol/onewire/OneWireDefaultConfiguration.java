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

	public static final String ONEWIRE_TEMPERATURE_SCALE = "onewire.temperature.scale";

	public static final String ONEWIRE_ALARMING_INITIAL_DELAY = "onewire.alarming.initial.delay";

	private static Logger log = OneWireLogger.getLogger();

	/**
	 * owserver port number. Can be override in command configuration
	 * Value stored in field as it saves a lot of time to read controller.xml every command is created via command builder
	 */
	private String host;

	/**
	 * owserver port number. Can be override in command configuration
	 * Value stored in field as it saves a lot of time to read controller.xml every command is created via command builder
	 */
	private String port;

	/**
	 * Scale of temperature returned by owserver. Can be CELSIUS, FAHRENHEIT, KELVIN or RANKINE
	 * Value stored in field as it saves a lot of time to read controller.xml every command is created via command builder
	 */
	private Enums.OwTemperatureScale temperatureScale;

	/**
	 *
	 * Used only for alarming devices. Defines delay (in miliseconds) between registering first OpenRemote alarming command and start of jowfsclient
	 * active process looking for devices in alarming state. As a result first sensor update connected to alarming command will be updated at least
	 * after passing period defined in this variable.
	 * If this value is too small jowfsclient alarming server can start its job before installing all alarming devices.
	 * Although it is completely safe to add new alarming devices, great decrease in speed of adding new alarming command can be observed as alarming
	 * server works in highly concurrent environment.
	 * Value stored in field as it saves a lot of time to read controller.xml every command is created via command builder
	 */
	private Integer alarmingInitialDelay;

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

	public Integer getAlarmingInitialDelay() {
		if (alarmingInitialDelay == null) {
			String value = preferAttrCustomValue(ONEWIRE_ALARMING_INITIAL_DELAY, StringUtils.EMPTY);
			try {
				alarmingInitialDelay = Integer.parseInt(value);
			} catch (Exception e) {
				log.info("Missing alarmingInitialDelay value in '" + ONEWIRE_ALARMING_INITIAL_DELAY + "' property. Default value will be used");
				return null;
			}
		}
		return alarmingInitialDelay;
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
