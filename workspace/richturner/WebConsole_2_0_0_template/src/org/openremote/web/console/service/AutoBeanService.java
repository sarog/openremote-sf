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

import com.google.gwt.core.client.GWT;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;
import com.google.web.bindery.autobean.shared.Splittable;
/**
 * 
 *  
 * @author <a href="mailto:richard@openremote.org">Richard Turner</a>
 */
public class AutoBeanService {
	private static AutoBeanService instance = null;
	private MyFactory factory = GWT.create(MyFactory.class);
	
	private AutoBeanService() {
	}
	
	public static synchronized AutoBeanService getInstance() {
		if (instance == null) {
			instance = new AutoBeanService();
		}
		return instance;
	}
	
	public String toJsonString(AutoBean<?> obj) {
		String result = "";
		if (obj != null) {
			result = AutoBeanCodex.encode(obj).getPayload();
		}
		return result;
	}
	
	public <T, U extends T> String toJsonString(U obj) {
		String result = "";
		if (obj != null) {
			result = toJsonString((Class<T>)obj.getClass(), obj);
		}
		return result;
	}
	
	public <T, U extends T> String toJsonString(Class<T> clazz, U obj) {
		AutoBean<?> bean = null;
		bean = AutoBeanUtils.getAutoBean(obj);
		if (bean == null) {
			bean = factory.create(clazz, obj);
		}
	   return AutoBeanCodex.encode(bean).getPayload();
	}

	public <T> AutoBean<T> fromJsonString(Class<T> clazz, String json) {
		AutoBean<T> bean = null;
		if (json != null && !json.equals("")) {
	    bean = AutoBeanCodex.decode(factory, clazz, json);
		}
	  return bean;
	}
	
	public <T> AutoBean<T> fromJsonString(Class<T> clazz, Splittable data) {
		AutoBean<T> bean = null;
		if (data != null) {
	    bean = AutoBeanCodex.decode(factory, clazz, data);
		}
	  return bean;
	}
	
	public MyFactory getFactory() {
		return this.factory;
	}
}
