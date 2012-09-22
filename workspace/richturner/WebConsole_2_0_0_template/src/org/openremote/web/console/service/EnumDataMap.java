/* OpenRemote, the Home of the Digital Home.
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
package org.openremote.web.console.service;

import org.openremote.web.console.controller.ControllerCredentials;
import org.openremote.web.console.controller.ControllerCredentialsList;
import org.openremote.web.console.panel.Panel;
import org.openremote.web.console.panel.entity.WelcomeFlag;

public enum EnumDataMap {
	CONTROLLER_CREDENTIALS_LIST("controllerCredentialsList", ControllerCredentialsList.class, "{\"controllerCredentials\":[]}"),
	CONTROLLER_CREDENTIALS("controllerCredentials", ControllerCredentials.class),
	WELCOME_FLAG("welcomeFlag", WelcomeFlag.class, "{\"welcomeDone\":false}"),
	LAST_CONTROLLER_CREDENTIALS("lastControllerCredentials", ControllerCredentials.class),
	SYSTEM_PANEL("SystemPanel", Panel.class);
	
	private Class<?> clazz;
	private String dataName;
	private String initValue;
	
	private EnumDataMap(String dataName, Class<?> clazz) {
		this(dataName, clazz, "{}");
	}

	private EnumDataMap(String dataName, Class<?> clazz, String initValue) {
		this.clazz = clazz;
		this.dataName = dataName;
		this.initValue = initValue;
	}
	
	public String getDataName() {
		return dataName;
	}
	
	public Class<?> getClazz() {
		return clazz;
	}
	
	public String getInitValue() {
		return initValue;
	}
	
	public static EnumDataMap getDataMap(String dataName) {
		EnumDataMap result = null;
		for (EnumDataMap map : EnumDataMap.values()) {
			if (map.getDataName().equals(dataName)) {
				result = map;
				break;
			}
		}
		return result;
	}
}