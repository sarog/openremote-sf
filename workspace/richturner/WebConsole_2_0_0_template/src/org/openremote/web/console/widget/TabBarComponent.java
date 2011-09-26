package org.openremote.web.console.widget;

import java.util.ArrayList;
import java.util.List;

import org.openremote.web.console.client.WebConsole;
import org.openremote.web.console.event.tap.TapEvent;
import org.openremote.web.console.event.tap.TapHandler;
import org.openremote.web.console.unit.ConsoleDisplay;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class TabBarComponent extends InteractiveConsoleComponent {
	public static final String CLASS_NAME = "tabBarComponent";
	public static final int TABBAR_HEIGHT = 50;
	private static List<TabBarItemComponent> items = new ArrayList<TabBarItemComponent>();
	private HorizontalPanel container;
	private boolean hasOverflow = false;
	
	public class TabBarItemComponent extends VerticalPanel implements TapHandler {
		private static final int TAB_ITEM_MIN_WIDTH = 65;
		
		@Override
		public void onTap(TapEvent event) {
			// TODO Auto-generated method stub
			
		}
	}
	
	public TabBarComponent() {
		super(new HorizontalPanel());
		container = (HorizontalPanel)this.getWidget();
		container.setStylePrimaryName(CLASS_NAME);
		container.setHeight(TABBAR_HEIGHT + "px");
	}
	
	@Override
	public void onRender() {
		/*
		 * The position of this widget and the number of visible tab items
		 * is dependent on the display size, get that from console display
		 */
		ConsoleDisplay display = WebConsole.getConsoleUnit().getConsoleDisplay();
		
		int displayWidth = display.getWidth();
		int displayHeight = display.getHeight();
		int itemCount = items.size();
		if ((itemCount * TabBarItemComponent.TAB_ITEM_MIN_WIDTH) > displayWidth) {
			hasOverflow = true;
		}
		this.setWidth(displayWidth+"px");
	}
	
	public void addItem(TabBarItemComponent tabItem) {
		if (!items.contains(tabItem)) {
			items.add(tabItem);
		}
	}
	
	public void removeItem(TabBarItemComponent tabItem) {
		items.remove(tabItem);
	}
	
	public void removeItem(int index) {
		items.remove(index);
	}
}
