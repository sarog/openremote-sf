package org.openremote.web.console.widget;

import org.openremote.web.console.event.tap.TapEvent;
import org.openremote.web.console.event.tap.TapHandler;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class TabBarComponent extends InteractiveConsoleWidget {
	public static final int TABBAR_HEIGHT = 50;
	public static final String TABBAR_CLASSNAME = "tabBar";
	private static TabBarItem[] items;
	
	public class TabBarItem extends VerticalPanel implements TapHandler {

		@Override
		public void onTap(TapEvent event) {
			// TODO Auto-generated method stub
			
		}
	}
	
	public TabBarComponent() {
		HorizontalPanel container = new HorizontalPanel();
		container.setHeight(TABBAR_HEIGHT + "px");
		container.setStylePrimaryName(TABBAR_CLASSNAME);
		DOM.setStyleAttribute(container.getElement(),"position", "fixed");
		DOM.setStyleAttribute(container.getElement(),"bottom", "0");
		
		initWidget(container);
	}
	
	@Override
	public void onRender() {
		// TODO Auto-generated method stub
		
	}
	
	public void addItem(TabBarItem tabItem) {
		// TODO Auto-generated method stub
		
	}
	
	public void removeItem(TabBarItem tabItem) {
		// TODO Auto-generated method stub
		
	}
	
	public void removeItem(int index) {
		// TODO Auto-generated method stub
		
	}
}
