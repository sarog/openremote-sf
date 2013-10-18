package org.openremote.controller.protocol.onewire;

import org.openremote.controller.Constants;
import org.openremote.controller.utils.Logger;

/**
 * Logger factory used in all classes in onewire package
 * @author Tom Kucharski <kucharski.tom@gmail.com>
 */
public class OneWireLoggerFactory {

	public final static String ONEWIRE_PROTOCOL_LOG_CATEGORY = Constants.CONTROLLER_PROTOCOL_LOG_CATEGORY + "onewire";
	private final static Logger logger = Logger.getLogger(ONEWIRE_PROTOCOL_LOG_CATEGORY);

	public static Logger getLogger() {
		return logger;
	}
}
