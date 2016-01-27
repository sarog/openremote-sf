package org.openremote.web.console.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.openremote.web.console.controller.ControllerCredentials;
import org.openremote.web.console.controller.ControllerCredentialsList;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.storage.client.StorageMap;
import com.google.gwt.user.client.Cookies;
import com.google.web.bindery.autobean.shared.AutoBean;

public class LocalDataServiceImpl implements LocalDataService {
	private static LocalDataServiceImpl instance;
	private Storage dataStore = null;
	private StorageMap dataStoreMap = null;
	
	private LocalDataServiceImpl() {
		dataStore = Storage.getLocalStorageIfSupported();
		if (dataStore != null) {
			dataStoreMap = new StorageMap(dataStore);
		}
	}
	
	public static synchronized LocalDataServiceImpl getInstance() {
		if (instance == null) {
			instance = new LocalDataServiceImpl();
		}
		return instance;
	}
	
	private void setData(String dataName, String data) {
		if (dataStore != null) {
			dataStore.setItem(dataName, data);
		} else {
			Cookies.setCookie(dataName, data, new Date(new Date().getTime() + (1000 * 60 * 60 * 24 * 365 * 100)));
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
			credentials = AutoBeanService.getInstance().fromJsonString(ControllerCredentials.class, jsonString).as();
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
			credentials = AutoBeanService.getInstance().fromJsonString(ControllerCredentials.class, jsonString).as();
		}
		return credentials;
	}

	@Override
	public void setDefaultControllerCredentials(ControllerCredentials credentials) {
		String data = AutoBeanService.getInstance().toJsonString(ControllerCredentials.class, credentials);
		setData("LocalDataService.DefaultControllerCredentials", data);
	}

	@Override
	public List<ControllerCredentials> getStoredControllerCredentials() {
		List<ControllerCredentials> credentials = new ArrayList<ControllerCredentials>();
		String jsonString = getData("LocalDataService.ControllerCredentialsList");

		if (jsonString != null && !jsonString.equals("null") && !jsonString.equals("")) {
			ControllerCredentialsList list = AutoBeanService.getInstance().fromJsonString(ControllerCredentialsList.class, jsonString).as();
			credentials = list.getControllerCredentials();
		}
		return credentials;
	}

	@Override
	public void setStoredControllerCredentials(List<ControllerCredentials> credentialsList) {
		AutoBean<ControllerCredentialsList> list = AutoBeanService.getInstance().getFactory().controllerCredentialsList();
		list.as().setControllerCredentials(credentialsList);
		String data = AutoBeanService.getInstance().toJsonString(ControllerCredentialsList.class, list);
		setData("LocalDataService.ControllerCredentialsList", data);
	}
}
