package org.openremote.web.console.service;

import org.openremote.web.console.service.MyFactory;
import com.google.gwt.core.client.GWT;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;
import com.google.web.bindery.autobean.shared.Splittable;

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
	
	public <T> String toJsonString(Class<T> clazz, AutoBean<T> obj) {
		return AutoBeanCodex.encode(obj).getPayload();
	}
	
	public <T> String toJsonString(Class<T> clazz, T obj) {
		AutoBean<T> bean = null;
		bean = AutoBeanUtils.getAutoBean(obj);
		if (bean == null) {
			bean = factory.create(clazz, obj);
		}	    
	   return AutoBeanCodex.encode(bean).getPayload();
	}

	public <T> AutoBean<T> fromJsonString(Class<T> clazz, String json) {
	   AutoBean<T> bean = AutoBeanCodex.decode(factory, clazz, json);     
	   return bean;
	}
	
	public <T> AutoBean<T> fromJsonString(Class<T> clazz, Splittable data) {
	   AutoBean<T> bean = AutoBeanCodex.decode(factory, clazz, data); 
	   return bean;
	}
	
	public MyFactory getFactory() {
		return this.factory;
	}
}
