package org.openremote.web.console.service;

import java.util.List;

import org.openremote.web.console.client.WebConsole;
import org.openremote.web.console.controller.ControllerCredentials;
import org.openremote.web.console.controller.ControllerCredentialsList;
import org.openremote.web.console.panel.entity.DataValuePair;

import com.google.web.bindery.autobean.shared.AutoBean;

public class DataBindingService {
	private static DataBindingService instance = null;
	
	private enum BindingMap {
		DEFAULT_CONTROLLER(ControllerCredentials.class, "defaultControllerCredentials"),
		CONTROLLER_LIST(ControllerCredentialsList.class, "controllerCredentialsList"),
		CONTROLLER_BY_URL(ControllerCredentials.class, "controllerCredentialsByUrl");
		
		private Class clazz;
		private String dataSource;
		
		private BindingMap(Class<?> clazz, String dataSource) {
			this.clazz = clazz;
			this.dataSource = dataSource;
		}
		
		public String getDataSource() {
			return dataSource;
		}
		
		public Class getClazz() {
			return clazz;
		}
		
		public static BindingMap getBindingMap(String dataSource) {
			BindingMap result = null;
			for (BindingMap map : BindingMap.values()) {
				if (map.getDataSource().equals(dataSource)) {
					result = map;
					break;
				}
			}
			return result;
		}
	}
	
	private DataBindingService() {
	}
	
	public static synchronized DataBindingService getInstance() {
		if (instance == null) {
			instance = new DataBindingService();
		}
		return instance;
	}
	
	public Class getClass(String dataSource) {
		Class clazz = null;
		BindingMap map = BindingMap.getBindingMap(dataSource);
		if (map != null) {
			clazz = map.getClazz();
		}
		return clazz;
	}
	
	public AutoBean<?> getData(String dataSource) {
		return getData(dataSource, null);
	}
	
	@SuppressWarnings("unchecked")
	public AutoBean<?> getData(String dataSource, List<DataValuePair> data) {
		BindingMap map = BindingMap.getBindingMap(dataSource);
		AutoBean<?> bean = null;
		
		if (map != null) {
			switch(map) {
			case DEFAULT_CONTROLLER:
				ControllerCredentials credentials = WebConsole.getConsoleUnit().getLocalDataService().getDefaultControllerCredentials();
				if (credentials != null) {
					bean = AutoBeanService.getInstance().getFactory().create(map.getClazz(), credentials);
				} else {
					bean = AutoBeanService.getInstance().getFactory().create(map.getClazz());
				}
				break;
			case CONTROLLER_LIST:
				ControllerCredentialsList credentialsList = WebConsole.getConsoleUnit().getLocalDataService().getControllerCredentialsList();
				if (credentialsList != null) {
					bean = AutoBeanService.getInstance().getFactory().create(map.getClazz(), credentialsList);
				} else {
					bean = AutoBeanService.getInstance().getFactory().create(map.getClazz());
				}
				break;
			case CONTROLLER_BY_URL:
				ControllerCredentialsList credentialsList2 = WebConsole.getConsoleUnit().getLocalDataService().getControllerCredentialsList();
				String url = null;
				if (data != null) {
					for (DataValuePair dvp : data) {
						if (dvp.getName().equalsIgnoreCase("url")) {
							url = dvp.getValue();
						}
					}
				}
				if (url != null) {
					for (ControllerCredentials creds : credentialsList2.getControllerCredentials()) {
						if (creds.getUrl().equalsIgnoreCase(url)) {
							bean = AutoBeanService.getInstance().getFactory().create(map.getClazz(), creds);
							break;
						}
					}
				} else {
					bean = AutoBeanService.getInstance().getFactory().create(map.getClazz());
				}
				break;
			}
		}
		return bean;
	}	
	
	public void setData(String dataSource, AutoBean<?> bean) {
		BindingMap map = BindingMap.getBindingMap(dataSource);
		if (map != null) {
			try {
				switch(map) {
				case DEFAULT_CONTROLLER:
					WebConsole.getConsoleUnit().getLocalDataService().setDefaultControllerCredentials(((AutoBean<ControllerCredentials>)bean).as());
					break;
				case CONTROLLER_LIST:
					WebConsole.getConsoleUnit().getLocalDataService().setControllerCredentialsList(((AutoBean<ControllerCredentialsList>)bean).as());
					break;
				}
			} catch (Exception e) {}
		}
	}	
}
