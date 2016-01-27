package org.openremote.web.console.view;

import org.openremote.web.console.panel.entity.ListItemLayout;
import org.openremote.web.console.service.AutoBeanService;
import org.openremote.web.console.widget.panel.list.ListItem;
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
