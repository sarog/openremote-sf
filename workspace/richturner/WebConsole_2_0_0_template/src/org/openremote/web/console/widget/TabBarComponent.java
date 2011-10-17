package org.openremote.web.console.widget;

import java.util.ArrayList;
import java.util.List;
import org.openremote.web.console.client.WebConsole;
import org.openremote.web.console.event.tap.TapEvent;
import org.openremote.web.console.event.tap.TapHandler;
import org.openremote.web.console.panel.entity.TabImage;
import org.openremote.web.console.panel.entity.TabBar;
import org.openremote.web.console.panel.entity.TabBarItem;
import org.openremote.web.console.service.AutoBeanService;
import org.openremote.web.console.service.MyFactory;
import org.openremote.web.console.unit.ConsoleDisplay;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.web.bindery.autobean.shared.AutoBean;

public class TabBarComponent extends InteractiveConsoleComponent {
	public static final String CLASS_NAME = "tabBarComponent";
	public static final int TABBAR_HEIGHT = 44;
	public static final int PADDING_TOP = 3;
	private static List<TabBarItemComponent> items = new ArrayList<TabBarItemComponent>();
	private HorizontalPanel container;
	private boolean hasOverflow = false;
	
	private static enum EnumSystemTabItems {
		PREVIOUS,
		NEXT;
	}
	
	public class TabBarItemComponent extends VerticalPanel implements TapHandler {
		private static final int TAB_ITEM_MIN_WIDTH = 65;
		
		public TabBarItemComponent(TabBarItem item) {
			TabImage tabImage = item.getImage();
			DOM.setStyleAttribute(this.getElement(), "overflow", "hidden");
			this.setHeight("100%");
			this.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
			this.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
			
			// Add Image
			if (tabImage != null) {
				Image imageComponent = new Image();
				String controllerUrl = WebConsole.getConsoleUnit().getControllerService().getController().getUrl();
				imageComponent.setUrl(controllerUrl + "/" + tabImage.getSrc());
				imageComponent.setSize(30 + "px", 30 + "px");
				imageComponent.setStylePrimaryName("tabBarItemImage");
				this.add(imageComponent);
			}
			
			// Add Text
			if (item.getName() != null && !item.getName().equals("")) {
				Label nameComponent = new Label();
				nameComponent.setText(item.getName());
				nameComponent.setSize("100%", "14px");
				nameComponent.setStylePrimaryName("tabBarItemName");
				this.add(nameComponent);
			}
		}

		@Override
		public void onTap(TapEvent event) {
			// TODO Auto-generated method stub
			
		}
	}
	
	public TabBarComponent(TabBar tabBar) {
		super(new HorizontalPanel());
		container = (HorizontalPanel)this.getWidget();
		container.setStylePrimaryName(CLASS_NAME);
		container.setHeight(TABBAR_HEIGHT + "px");
		DOM.setStyleAttribute(container.getElement(), "paddingTop", PADDING_TOP + "px");
		
		// Add Elements from tab bar entity
		for (TabBarItem item : tabBar.getItem()) {
			TabBarItemComponent tabBarItem = new TabBarItemComponent(item);
			this.addItem(tabBarItem);
		}
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
		int maxVisibleItemCount = (displayWidth / TabBarItemComponent.TAB_ITEM_MIN_WIDTH);
		this.setWidth(displayWidth+"px");

		// Insert Next and Prev Nav buttons if overflow
		if ((itemCount * TabBarItemComponent.TAB_ITEM_MIN_WIDTH) > displayWidth) {
			hasOverflow = true;
			int screenCount = (int)Math.round(((double)itemCount/maxVisibleItemCount));
			for (int i=1; i<=screenCount; i++) {
				int insertPos = (maxVisibleItemCount * i) - 1;
				// Insert next and previous if more screens
				if (i < screenCount) {
					TabBarItem nextItem = createSystemTabItem(EnumSystemTabItems.NEXT);
					TabBarItemComponent nextComponent = new TabBarItemComponent(nextItem);
					this.insertItem(insertPos, nextComponent);
					TabBarItem prevItem = createSystemTabItem(EnumSystemTabItems.PREVIOUS);
					TabBarItemComponent prevComponent = new TabBarItemComponent(prevItem);
					this.insertItem(insertPos+1, prevComponent);
				}
				itemCount = items.size();
				screenCount = (int)Math.round(((double)itemCount/maxVisibleItemCount));
			}
		}
		
		// Update item count
		itemCount = items.size();
		int itemsPerPage = itemCount >= maxVisibleItemCount ? itemCount : maxVisibleItemCount;
		
		// Draw the initial set of items		
		for (int i=0; i<maxVisibleItemCount; i++) {
			int widthPerItem = (int)Math.floor((double)displayWidth / itemsPerPage);
			TabBarItemComponent item = items.get(i);
			item.setWidth(widthPerItem + "px");
			container.add(items.get(i));
		}
	}
	
	public void addItem(TabBarItemComponent tabItem) {
		if (!items.contains(tabItem)) {
			items.add(tabItem);
		}
	}
	
	public void insertItem(int index, TabBarItemComponent tabItem) {
		items.add(index, tabItem);
	}
	
	public void removeItem(TabBarItemComponent tabItem) {
		items.remove(tabItem);
	}
	
	public void removeItem(int index) {
		items.remove(index);
	}
	
	private static TabBarItem createSystemTabItem(EnumSystemTabItems itemType) {
		TabBarItem tabBarItem = null;
		AutoBean<TabBarItem> TabBarItemBean = AutoBeanService.getInstance().getFactory().tabBarItem();
		tabBarItem = TabBarItemBean.as();
		switch (itemType) {
			case PREVIOUS:
				tabBarItem.setName("<");
				break;
			case NEXT:
				tabBarItem.setName(">");
				break;
		}
		return tabBarItem;
	}
	
	public int getHeight() {
		return TABBAR_HEIGHT + PADDING_TOP;
	}
}
