package org.openremote.web.console.service;

import org.openremote.web.console.panel.PanelCredentials;
import org.openremote.web.console.service.MyFactory;
import com.google.gwt.core.client.GWT;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;

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
	
	public <T> String toJsonString(Class<T> clazz, T obj) {
		AutoBean<T> bean = null;
		bean = AutoBeanUtils.getAutoBean(obj);
		if (bean == null) {
			bean = factory.create(clazz, obj);
		}	    
	   return AutoBeanCodex.encode(bean).getPayload();
	}

	public <T> T fromJsonString(Class<T> clazz, String json) {
	   AutoBean<T> bean = AutoBeanCodex.decode(factory, clazz, json);     
	   return bean.as();
	}
	
	public MyFactory getFactory() {
		return this.factory;
	}
}
