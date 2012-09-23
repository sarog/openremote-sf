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

import org.openremote.web.console.controller.ControllerCredentials;
import org.openremote.web.console.controller.ControllerCredentialsList;
import org.openremote.web.console.panel.Panel;
import org.openremote.web.console.panel.PanelIdentity;
import org.openremote.web.console.panel.PanelIdentityList;
import org.openremote.web.console.panel.entity.AbsoluteLayout;
import org.openremote.web.console.panel.entity.AbsolutePosition;
import org.openremote.web.console.panel.entity.Background;
import org.openremote.web.console.panel.entity.DataValuePair;
import org.openremote.web.console.panel.entity.DataValuePairContainer;
import org.openremote.web.console.panel.entity.Gesture;
import org.openremote.web.console.panel.entity.Group;
import org.openremote.web.console.panel.entity.GroupList;
import org.openremote.web.console.panel.entity.Link;
import org.openremote.web.console.panel.entity.ListItemLayout;
import org.openremote.web.console.panel.entity.Navigate;
import org.openremote.web.console.panel.entity.PanelSizeInfo;
import org.openremote.web.console.panel.entity.Screen;
import org.openremote.web.console.panel.entity.ScreenList;
import org.openremote.web.console.panel.entity.ScreenRef;
import org.openremote.web.console.panel.entity.Status;
import org.openremote.web.console.panel.entity.StatusList;
import org.openremote.web.console.panel.entity.TabBar;
import org.openremote.web.console.panel.entity.TabBarItem;
import org.openremote.web.console.panel.entity.WelcomeFlag;
import org.openremote.web.console.panel.entity.component.ImageComponent;
import org.openremote.web.console.panel.entity.component.LabelComponent;

import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;
/**
 * 
 *  
 * @author <a href="mailto:richard@openremote.org">Richard Turner</a>
 */
public interface MyFactory extends AutoBeanFactory {
	AutoBean<ControllerCredentials> controllerCredentials();
	AutoBean<ControllerCredentials> controllerCredentials(ControllerCredentials toWrap);
	AutoBean<ControllerCredentialsList> controllerCredentialsList();
	AutoBean<ControllerCredentialsList> controllerCredentials(ControllerCredentialsList toWrap);
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
	AutoBean<DataValuePairContainer> dvpContainer();
	AutoBean<DataValuePair> dvp();	
	AutoBean<Link> link();
	AutoBean<LabelComponent> label();
	AutoBean<ScreenRef> screenRef();
	AutoBean<Gesture> gesture();
	AutoBean<Background> background();
	AutoBean<AbsoluteLayout> absolute();
	AutoBean<ImageComponent> image();
	AutoBean<StatusList> statuses();
	AutoBean<Status> status();
	AutoBean<ListItemLayout> listItem();
	AutoBean<WelcomeFlag> welcomeFlag();
	AutoBean<AbsolutePosition> absolutePosition();
	AutoBean<PanelSizeInfo> panelSizeInfo();
}