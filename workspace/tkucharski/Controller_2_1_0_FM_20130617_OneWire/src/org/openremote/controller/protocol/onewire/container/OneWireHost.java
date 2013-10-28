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

import org.owfs.jowfsclient.Enums;

/**
 * Simple composite to store owserver host and port number within one object. Used in {@org.openremote.controller.protocol.onewire.OneWireHostFactory}
 *
 * @author Tom Kucharski <kucharski.tom@gmail.com>
 */
public class OneWireHost {

	private String hostname;

	private int port;

	private Enums.OwTemperatureScale temperatureScale;

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public Enums.OwTemperatureScale getTemperatureScale() {
		return temperatureScale;
	}

	public void setTemperatureScale(Enums.OwTemperatureScale temperatureScale) {
		this.temperatureScale = temperatureScale;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		OneWireHost that = (OneWireHost) o;

		if (port != that.port) return false;
		if (hostname != null ? !hostname.equals(that.hostname) : that.hostname != null) return false;
		if (temperatureScale != that.temperatureScale) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = hostname != null ? hostname.hashCode() : 0;
		result = 31 * result + port;
		result = 31 * result + (temperatureScale != null ? temperatureScale.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("OneWireHost{");
		sb.append("hostname='").append(hostname).append('\'');
		sb.append(", port=").append(port);
		sb.append(", temperatureScale=").append(temperatureScale);
		sb.append('}');
		return sb.toString();
	}
}
