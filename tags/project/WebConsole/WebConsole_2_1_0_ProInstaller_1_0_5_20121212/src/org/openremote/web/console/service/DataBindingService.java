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

import java.util.List;

import org.openremote.web.console.client.WebConsole;
import org.openremote.web.console.controller.ControllerCredentials;
import org.openremote.web.console.controller.ControllerCredentialsList;
import org.openremote.web.console.panel.PanelIdentity;
import org.openremote.web.console.panel.PanelIdentityList;
import org.openremote.web.console.panel.entity.DataValuePair;
import org.openremote.web.console.panel.entity.DataValuePairContainer;

import com.google.web.bindery.autobean.shared.AutoBean;
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
