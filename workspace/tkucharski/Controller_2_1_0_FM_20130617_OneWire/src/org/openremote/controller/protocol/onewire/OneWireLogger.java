/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2012, OpenRemote Inc.
 *
 * See the contributors.txt file in the distribution for a
 * full listing of individual contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.openremote.controller.protocol.onewire;

import org.openremote.controller.Constants;
import org.openremote.controller.utils.Logger;

/**
 * Logger factory used in all classes in onewire package
 *
 * @author Tom Kucharski <kucharski.tom@gmail.com>
 */
public class OneWireLogger {

	private final static String ONEWIRE_PROTOCOL_LOG_CATEGORY = Constants.CONTROLLER_PROTOCOL_LOG_CATEGORY + "onewire";

	private final static Logger logger = Logger.getLogger(ONEWIRE_PROTOCOL_LOG_CATEGORY);

	public static Logger getLogger() {
		return logger;
	}

	public static void info(String message) {
		logger.info(message);
	}

	public static void warn(String message) {
		logger.warn(message);
	}

	public static void error(String message) {
		logger.error(message);
	}

	public static void error(String message, Throwable e) {
		logger.error(message, e);
	}
}
