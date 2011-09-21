package org.openremote.web.console.panel;

import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;

interface PanelCredentialsFactory extends AutoBeanFactory {
	AutoBean<PanelCredentials> panelCredentials();
}