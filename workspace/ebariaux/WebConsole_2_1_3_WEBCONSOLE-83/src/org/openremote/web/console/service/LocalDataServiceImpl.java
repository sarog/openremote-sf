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

import java.util.Date;

import org.openremote.web.console.controller.ControllerCredentials;
import org.openremote.web.console.controller.ControllerCredentialsList;
import org.openremote.web.console.event.ConsoleUnitEventManager;
import org.openremote.web.console.event.ui.BindingDataChangeEvent;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.user.client.Cookies;
/**
 * 
 *  
 * @author <a href="mailto:richard@openremote.org">Richard Turner</a>
 */
public class LocalDataServiceImpl implements LocalDataService {
	private static LocalDataServiceImpl instance;
	private static final String PREFIX = "ConsoleUnit";
	private Storage dataStore = null;
//	private StorageMap dataStoreMap = null;
	
	private LocalDataServiceImpl() {
		try {
			// Chrome throws exception here when cookies disabled even though it shouldn't
			dataStore = Storage.getLocalStorageIfSupported();
		} catch (Exception e) {}
	}
	
	public static synchronized LocalDataServiceImpl getInstance() {
		if (instance == null) {
			instance = new LocalDataServiceImpl();
			instance.initData();
		}
		return instance;
	}
	
	public boolean isAvailable() {
		return dataStore != null;
	}
	
	/*
	 * Initialise each data item in the enumDataMap so the
	 * AutoBean is correctly generated when requested
	 */
	private void initData() {
		for (EnumDataMap map : EnumDataMap.values()) {
			String data = getData(map.getDataName());
			if (map.getInitValue() != null && (data == null || data.equals(""))) {
				setData(map.getDataName(),map.getInitValue());
			}
		}
	}
	

	
	private static String buildPathString(String object) {
		return PREFIX + "." + object;
	}
	
	private void setData(String dataName, String data) {
		String oldData = getObjectString(dataName);
		if (oldData == null || !data.equals(oldData)) {
			String dataNamePath = buildPathString(dataName);
			try {
				if (dataStore != null) {
					dataStore.removeItem(dataNamePath);
					dataStore.setItem(dataNamePath, data);
				} else {
					Cookies.setCookie(dataNamePath, data, new Date(new Date().getTime() + (1000 * 60 * 60 * 24 * 365 * 100)));
				}
			} catch (Exception e) {}
			HandlerManager eventBus = ConsoleUnitEventManager.getInstance().getEventBus();
			BindingDataChangeEvent event = new BindingDataChangeEvent(dataName);
			eventBus.fireEvent(event);
		}
	}
	
	private String getData(String dataName) {
		if (dataName == null || dataName.equals("")) return "";
		
		dataName = buildPathString(dataName);
		String data = null;
		try {
			if (dataStore != null) {
				data = dataStore.getItem(dataName);
			} else {
				data = Cookies.getCookie(dataName);
			}
		} catch (Exception e) {}
	
		if (data == null || data.equals("null")) {
			data = "";
		}
		return data;
	}

	@Override
	public void clearData(String dataName) {
		if (dataName == null || dataName.equals("")) return;
		
		dataName = buildPathString(dataName);
		try {
			if (dataStore != null) {
				dataStore.removeItem(dataName);
			} else {
				Cookies.removeCookie(dataName);
			}
		} catch (Exception e) {}
		initData();
	}
	
	@Override
	public void clearAllData() {
		try {
			if (dataStore != null) {
				dataStore.clear();
			} else {
				// TODO: Clear out cookies
			}
		} catch (Exception e) {}
		initData();
	}
	
	@Override
	public ControllerCredentials getLastControllerCredentials() {
		ControllerCredentials credentials = null;
		EnumDataMap map = EnumDataMap.LAST_CONTROLLER_CREDENTIALS;
		String dataStr = getData(map.getDataName());
		if (dataStr != null && !dataStr.isEmpty())
		{
			credentials = (ControllerCredentials) AutoBeanService.getInstance().fromJsonString(map.getClazz(), dataStr).as();
		}
		return credentials;
	}

	@Override
	public void setLastControllerCredentials(ControllerCredentials credentials) {
		EnumDataMap map = EnumDataMap.LAST_CONTROLLER_CREDENTIALS;
		if (credentials == null) {
			clearData(map.getDataName());
		} else {
			String data = AutoBeanService.getInstance().toJsonString(credentials);
			setData(map.getDataName(), data);
		}
	}

	@Override
	public ControllerCredentialsList getControllerCredentialsList() {
		ControllerCredentialsList credentialsList = null;
		EnumDataMap map = EnumDataMap.CONTROLLER_CREDENTIALS_LIST;
		String dataStr = getData(map.getDataName());
		if (dataStr != null && !dataStr.isEmpty())
		{
			credentialsList = (ControllerCredentialsList) AutoBeanService.getInstance().fromJsonString(map.getClazz(), dataStr).as();
		}
		return credentialsList;
	}

	@Override
	public void setControllerCredentialsList(ControllerCredentialsList credentialsList) {
		EnumDataMap map = EnumDataMap.CONTROLLER_CREDENTIALS_LIST;
		if (credentialsList == null) {
			clearData(map.getDataName());
		} else {
			String data = AutoBeanService.getInstance().toJsonString(credentialsList);
			setData(map.getDataName(), data);
		}
	}

	@Override
	public String getObjectString(String objName) {
		String obj = null;
		obj = getData(objName);
		if (obj != null && (obj.equals("") || obj.equals("{}"))) obj = null;
		return obj;
	}

	@Override
	public void setObject(String objName, String obj) {
		setData(objName, obj);
	}
}
