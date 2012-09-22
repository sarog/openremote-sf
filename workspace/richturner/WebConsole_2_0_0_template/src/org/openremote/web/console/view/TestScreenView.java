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
package org.openremote.web.console.view;

import org.openremote.web.console.panel.entity.ListItemLayout;
import org.openremote.web.console.service.AutoBeanService;
import org.openremote.web.console.widget.panel.list.ListPanelComponent;

public class TestScreenView extends ScreenViewImpl {
	public TestScreenView() {
		// Create a list panel
		try {
			ListPanelComponent listPanel = ListPanelComponent.build(null);
			listPanel.setWidth("100%");
			listPanel.setHeight("100%");
			listPanel.setDataSource("controllerCredentialsList");
			listPanel.setItemBindingObject("controllerCredentials");
			ListItemLayout itemLayout = AutoBeanService.getInstance().fromJsonString(ListItemLayout.class, "{\"absolute\":[{\"height\":\"50\",\"width\":\"280\",\"label\":{\"id\":97,\"text\":\"${url}\",\"color\":\"#FFFFFF\",\"fontSize\":14},\"left\":\"0\",\"top\":\"0\"}]}").as();
			listPanel.setItemTemplate(itemLayout);
			
			// Add to screen view
			super.addPanelComponent(listPanel);
		} catch (Exception e) {}
	}
}
