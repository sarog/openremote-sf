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
package org.openremote.controller.protocol.onewire.container;

import java.util.HashMap;
import java.util.Map;
import org.owfs.jowfsclient.OwfsConnectionFactory;

/**
 * Storage for 1-wire servers, Factory Method that returns single OwfsConnectionFactory per server configuration.
 *
 * @author Tom Kucharski <kucharski.tom@gmail.com>
 */
public class OneWireHostRepository {

	private Map<OneWireHost, OwfsConnectionFactory> factories = new HashMap<OneWireHost, OwfsConnectionFactory>();

	public OwfsConnectionFactory loadOrCreate(OneWireHost key) {
		OwfsConnectionFactory factory = factories.get(key);
		if (factory == null) {
			factory = new OwfsConnectionFactory(key.getHostname(), key.getPort());
			factory.getConnectionConfig().setTemperatureScale(key.getTemperatureScale());
			if (key.getAlarmingInitialDelay() != null) {
				factory.getConnectionConfig().setAlarmingInitialDelay(key.getAlarmingInitialDelay());
			}
			factories.put(key, factory);
		}
		return factory;
	}
}
