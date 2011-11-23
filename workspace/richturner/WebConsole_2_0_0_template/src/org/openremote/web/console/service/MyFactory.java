package org.openremote.web.console.service;

import org.openremote.web.console.controller.ControllerCredentials;
import org.openremote.web.console.panel.Panel;
import org.openremote.web.console.panel.PanelIdentity;
import org.openremote.web.console.panel.PanelIdentityList;
import org.openremote.web.console.panel.entity.*;
import org.openremote.web.console.panel.entity.component.*;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;

public interface MyFactory extends AutoBeanFactory {
	AutoBean<ControllerCredentials> controllerCredentials();
	AutoBean<ControllerCredentials> controllerCredentials(ControllerCredentials toWrap);
	AutoBean<PanelIdentityList> panelIdentityList();
	AutoBean<PanelIdentity> panelIdentity();
	AutoBean<Panel> panel();
	AutoBean<GroupList> groups();
	AutoBean<ScreenList> screens();
	AutoBean<TabBar> tabBar();
	AutoBean<TabBarItem> tabBarItem();
	AutoBean<Group> group();
	AutoBean<Screen> screen();
	AutoBean<Navigate> navigate();
	AutoBean<Link> link();
	AutoBean<LabelComponent> label();
	AutoBean<ScreenRef> screenRef();
	AutoBean<Gesture> gesture();
	AutoBean<Background> background();
	AutoBean<AbsoluteLayout> absolute();
	AutoBean<ImageComponent> image();
	AutoBean<StatusList> statuses();
	AutoBean<Status> status();
}