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
		dataStore = Storage.getLocalStorageIfSupported();
//		if (dataStore != null) {
//			dataStoreMap = new StorageMap(dataStore);
//		}
	}
	
	public static synchronized LocalDataServiceImpl getInstance() {
		if (instance == null) {
			instance = new LocalDataServiceImpl();
			instance.initData();
		}
		return instance;
	}
	
	/*
	 * Initialise each data item in the enumDataMap so the
	 * AutoBean is correctly generated when requested
	 */
	private void initData() {
		for (EnumDataMap map : EnumDataMap.values()) { 
			if (map.getInitValue() != null && getData(map.getDataName()).equals("")) {
				setData(map.getDataName(),map.getInitValue());
			}
		}
	}
	
	private static String buildPathString(String object) {
		return PREFIX + "." + object;
	}
	
	private void setData(String dataName, String data) {
		String oldData = getObjectString(dataName);
		if (!data.equals(oldData)) {
			String dataNamePath = buildPathString(dataName);
			if (dataStore != null) {
				dataStore.removeItem(dataNamePath);
				dataStore.setItem(dataNamePath, data);
			} else {
				Cookies.setCookie(dataNamePath, data, new Date(new Date().getTime() + (1000 * 60 * 60 * 24 * 365 * 100)));
			}
			
			HandlerManager eventBus = ConsoleUnitEventManager.getInstance().getEventBus();
			BindingDataChangeEvent event = new BindingDataChangeEvent(dataName);
			eventBus.fireEvent(event);
		}
	}
	
	private String getData(String dataName) {
		if (dataName == null || dataName.equals("")) return "";
		
		dataName = buildPathString(dataName);
		String data;
		if (dataStore != null) {
			data = dataStore.getItem(dataName);
		} else {
			data = Cookies.getCookie(dataName);
		}
		if (data == null || data.equals("null")) {
			data = "";
		}
		return data;
	}

	@Override
	public void clearData(String dataName) {
		if (dataName == null || dataName.equals("")) return;
		
		dataName = buildPathString(dataName);
		if (dataStore != null) {
			dataStore.removeItem(dataName);
		} else {
			Cookies.removeCookie(dataName);
		}
		initData();
	}
	
	@Override
	public void clearAllData() {
		if (dataStore != null) {
			dataStore.clear();
		} else {
			// TODO: Clear out cookies
		}
		initData();
	}
	
	@Override
	public ControllerCredentials getLastControllerCredentials() {
		ControllerCredentials credentials = null;
		EnumDataMap map = EnumDataMap.LAST_CONTROLLER_CREDENTIALS;
		credentials = (ControllerCredentials) AutoBeanService.getInstance().fromJsonString(map.getClazz(), getData(map.getDataName())).as();
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
		credentialsList = (ControllerCredentialsList) AutoBeanService.getInstance().fromJsonString(map.getClazz(), getData(map.getDataName())).as();
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
