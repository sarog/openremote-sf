package org.openremote.web.console.service;

import org.openremote.web.console.controller.ControllerCredentials;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.storage.client.StorageMap;
import com.google.gwt.user.client.Cookies;

public class LocalDataServiceImpl implements LocalDataService {
	private Storage dataStore = null;
	private StorageMap dataStoreMap = null;
	
	public LocalDataServiceImpl() {
		dataStore = Storage.getLocalStorageIfSupported();
		if (dataStore != null) {
			dataStoreMap = new StorageMap(dataStore);
		}
	}
	
	private void setData(String dataName, String data) {
		if (dataStore != null) {
			dataStore.setItem(dataName, data);
		} else {
			Cookies.setCookie(dataName, data);
		}
	}
	
	private String getData(String dataName) {
		String data = "";
		
		if (dataStore != null) {
			data = dataStore.getItem(dataName);
		} else {
			data = Cookies.getCookie(dataName);
		}
		return data;
	}
	
	@Override
	public ControllerCredentials getLastControllerCredentials() {
		ControllerCredentials credentials = null;
		String jsonString = getData("LocalDataService.LastControllerCredentials");

		if (jsonString != null && !jsonString.equals("null") && !jsonString.equals("")) {
			credentials = AutoBeanService.getInstance().fromJsonString(ControllerCredentials.class, jsonString);
		}
		return credentials;
	}

	@Override
	public void setLastControllerCredentials(ControllerCredentials credentials) {
		String data = AutoBeanService.getInstance().toJsonString(ControllerCredentials.class, credentials);
		setData("LocalDataService.LastControllerCredentials", data);
	}

	@Override
	public String getLastPanelName() {
		// TODO Auto-generated method stub
		return getData("LocalDataService.LastPanelName");
	}

	@Override
	public void setLastPanelName(String panelName) {
		setData("LocalDataService.LastPanelName", panelName);
	}
	
	@Override
	public ControllerCredentials getDefaultControllerCredentials() {
		ControllerCredentials credentials = null;
		String jsonString = getData("LocalDataService.DefaultControllerCredentials");

		if (jsonString != null && !jsonString.equals("null") && !jsonString.equals("")) {
			credentials = AutoBeanService.getInstance().fromJsonString(ControllerCredentials.class, jsonString);
		}
		return credentials;
	}

	@Override
	public void setDefaultControllerCredentials(ControllerCredentials credentials) {
		String data = AutoBeanService.getInstance().toJsonString(ControllerCredentials.class, credentials);
		setData("LocalDataService.DefaultControllerCredentials", data);
	}
}
