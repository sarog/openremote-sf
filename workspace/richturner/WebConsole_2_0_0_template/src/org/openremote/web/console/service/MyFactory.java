package org.openremote.web.console.service;

import org.openremote.web.console.panel.Panel;
import org.openremote.web.console.panel.PanelCredentials;
import org.openremote.web.console.panel.entity.Absolute;
import org.openremote.web.console.panel.entity.Background;
import org.openremote.web.console.panel.entity.Gesture;
import org.openremote.web.console.panel.entity.Group;
import org.openremote.web.console.panel.entity.Groups;
import org.openremote.web.console.panel.entity.Label;
import org.openremote.web.console.panel.entity.Link;
import org.openremote.web.console.panel.entity.Navigate;
import org.openremote.web.console.panel.entity.Screen;
import org.openremote.web.console.panel.entity.Screens;
import org.openremote.web.console.panel.entity.TabBar;
import org.openremote.web.console.panel.entity.TabBarItem;
import org.openremote.web.console.panel.entity.component.Image;

import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;

interface MyFactory extends AutoBeanFactory {
	AutoBean<PanelCredentials> panelCredentials();
	AutoBean<PanelCredentials> panelCredentials(PanelCredentials toWrap);
	
	AutoBean<Panel> panel();
	AutoBean<Groups> groups();
	AutoBean<Group> group();
	AutoBean<Screens> screens();	
	AutoBean<Screen> screen();
	AutoBean<TabBar> tabBar();
	AutoBean<TabBarItem> tabBarItem();
	AutoBean<Navigate> navigate();
	AutoBean<Link> link();
	AutoBean<Label> label();
	AutoBean<Gesture> gesture();
	AutoBean<Background> background();
	AutoBean<Absolute> absolute();
	AutoBean<Image> image();
}