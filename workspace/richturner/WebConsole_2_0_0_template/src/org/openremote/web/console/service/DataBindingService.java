package org.openremote.web.console.service;

import org.openremote.web.console.client.WebConsole;
import org.openremote.web.console.controller.ControllerCredentials;
import com.google.web.bindery.autobean.shared.AutoBean;

public class DataBindingService {
	private static DataBindingService instance = null;
	
	private enum BindingMap {
		DEFAULT_CONTROLLER(ControllerCredentials.class, "defaultControllerCredentials");
		
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
	
	public AutoBean<?> getData(String dataSource) {
		BindingMap map = BindingMap.getBindingMap(dataSource);
		AutoBean<?> bean = null;
		if (map != null) {
			switch(map) {
			case DEFAULT_CONTROLLER:
				ControllerCredentials credentials = WebConsole.getConsoleUnit().getLocalDataService().getDefaultControllerCredentials();
				if (credentials != null) {
					bean = AutoBeanService.getInstance().getFactory().create(map.getClazz(), credentials);
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
				}
			} catch (Exception e) {}
		}
	}	
}
