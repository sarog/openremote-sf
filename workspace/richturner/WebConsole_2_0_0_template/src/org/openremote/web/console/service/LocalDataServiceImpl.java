package org.openremote.web.console.service;

import org.openremote.web.console.panel.PanelCredentials;
import org.openremote.web.console.panel.PanelCredentialsImpl;
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
	
	@Override
	public PanelCredentials getLastPanelCredentials() {
		PanelCredentials panelCredentials = null;
		String panelString = null;
		
		if (dataStore != null) {
			panelString = dataStore.getItem("LocalDataService.LastPanelCredentials");
		} else {
			panelString = Cookies.getCookie("LocalDataService.LastPanelCredentials");
		}
		if (panelString != null && !panelString.equals("null")) {
			panelCredentials = PanelCredentialsImpl.fromJson(panelString);
		}
		return panelCredentials;
	}

	@Override
	public void setLastPanelCredentials(PanelCredentials panelCredentials) {
		String data = PanelCredentialsImpl.toJson(panelCredentials);
		if (dataStore != null) {
			dataStore.setItem("LocalDataService.LastPanelCredentials", data);
		} else {
			Cookies.setCookie("LocalDataService.LastPanelCredentials", data);
		}
	}
}
