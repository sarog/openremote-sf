package org.openremote.web.console.service;

import java.util.List;

import org.openremote.web.console.client.WebConsole;
import org.openremote.web.console.controller.ControllerCredentials;
import org.openremote.web.console.controller.ControllerCredentialsList;
import org.openremote.web.console.event.ConsoleUnitEventManager;
import org.openremote.web.console.event.press.PressStartEvent;
import org.openremote.web.console.event.ui.BindingDataChangeEvent;
import org.openremote.web.console.panel.PanelIdentity;
import org.openremote.web.console.panel.PanelIdentityList;
import org.openremote.web.console.panel.entity.DataValuePair;
import org.openremote.web.console.panel.entity.DataValuePairContainer;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Window;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.Splittable;

/**
 * Used as a crude way of providing data binding, bindable objects must
 * be defined in the binding map, all bindable objects must exist in local
 * data store and are referenced in the binding map and local data store
 * using the same string literal 
 *
 * @author <a href="mailto:richard@openremote.org">Rich Turner</a>
 */
public class DataBindingService {
	private static DataBindingService instance = null;
	
	private enum BindingMap {
		CONTROLLER_CREDS_LIST(ControllerCredentialsList.class, "controllerCredentialsList"),
		CONTROLLER_CREDS(ControllerCredentials.class, "controllerCredentials"),
		PANEL_IDENTITY_LIST(PanelIdentityList.class, "panelIdentityList"),
		PANEL_IDENTITIES(PanelIdentity.class, "panelIdentities");
		
		private Class<?> clazz;
		private String dataSource;
		
		private BindingMap(Class<?> clazz, String dataSource) {
			this.clazz = clazz;
			this.dataSource = dataSource;
		}
		
		public String getDataSource() {
			return dataSource;
		}
		
		public Class<?> getClazz() {
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
	
	public Class<?> getClass(String dataSource) {
		Class<?> clazz = null;
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
	public AutoBean<?> getData(String dataSource, List<DataValuePairContainer> data) {
		BindingMap map = BindingMap.getBindingMap(dataSource);
		AutoBean<?> bean = null;
		if (map != null) {
			String obj = WebConsole.getConsoleUnit().getLocalDataService().getObjectString(map.getDataSource());
			
			if (obj == null || obj.equals("")) {
				// Look in supplied data
				String dataString = null;
				if (data != null) {
					for (DataValuePairContainer dvpC : data) {
						if (dvpC != null) {
							DataValuePair dvp = dvpC.getDataValuePair();
							if (dvp.getName().equalsIgnoreCase(dataSource)) {
								obj = dvp.getValue();
								break;
							}
						}
					}
				}
			}
			
			if (obj != null && !obj.equals("")) {
				bean = AutoBeanService.getInstance().fromJsonString(map.getClazz(), obj);
			} else {
				bean = AutoBeanService.getInstance().getFactory().create(map.getClazz());
			}
		}
		return bean;
	}	
	
	public void setData(String dataSource, AutoBean<?> bean) {
		BindingMap map = BindingMap.getBindingMap(dataSource);
		String dataStr = AutoBeanService.getInstance().toJsonString(bean);
		WebConsole.getConsoleUnit().getLocalDataService().setObject(map.getDataSource(), dataStr);
	}
}
