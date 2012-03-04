package org.openremote.web.console.service;

import org.openremote.web.console.controller.ControllerCredentials;
import org.openremote.web.console.controller.ControllerCredentialsList;

public enum EnumDataMap {
	CONTROLLER_CREDENTIALS_LIST("controllerCredentialsList", ControllerCredentialsList.class, "{\"controllerCredentials\":[]}"),
	CONTROLLER_CREDENTIALS("controllerCredentials", ControllerCredentials.class),
	LAST_CONTROLLER_CREDENTIALS("lastControllerCredentials", ControllerCredentials.class);
	
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