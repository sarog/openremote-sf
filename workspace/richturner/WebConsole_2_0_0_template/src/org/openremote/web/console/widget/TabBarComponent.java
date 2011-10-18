package org.openremote.web.console.widget;

import java.util.ArrayList;
import java.util.List;
import org.openremote.web.console.client.WebConsole;
import org.openremote.web.console.event.ConsoleUnitEventManager;
import org.openremote.web.console.event.tap.TapEvent;
import org.openremote.web.console.event.tap.TapHandler;
import org.openremote.web.console.event.ui.NavigateEvent;
import org.openremote.web.console.event.ui.ScreenViewChangeEvent;
import org.openremote.web.console.event.ui.ScreenViewChangeHandler;
import org.openremote.web.console.panel.entity.Navigate;
import org.openremote.web.console.panel.entity.TabImage;
import org.openremote.web.console.panel.entity.TabBar;
import org.openremote.web.console.panel.entity.TabBarItem;
import org.openremote.web.console.service.AutoBeanService;
import org.openremote.web.console.service.MyFactory;
import org.openremote.web.console.unit.ConsoleDisplay;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.web.bindery.autobean.shared.AutoBean;

public class TabBarComponent extends InteractiveConsoleComponent implements ScreenViewChangeHandler {
	public static final String CLASS_NAME = "tabBarComponent";
	public static final int TABBAR_HEIGHT = 47;
	public static final int PADDING_TOP = 3;
	private static List<TabBarItemComponent> items = new ArrayList<TabBarItemComponent>();
	private HorizontalPanel container;
	private int pageCount = 0;
	private int maxItemsPerPage = 0;
	private int currentPage = 0;
	private int widthPerItem = 0;
	
	private static enum EnumSystemTabItemType {
		PREVIOUS,
		NEXT;
	}
	
	public class TabBarItemComponent extends VerticalPanel implements TapHandler {
		private static final int TAB_ITEM_MIN_WIDTH = 65;
		private TabBarItem item;
		private EnumSystemTabItemType systemTabType;
		
		public TabBarItemComponent(TabBarItem item) {
			this(item, null);
		}
		
		public TabBarItemComponent(TabBarItem item, EnumSystemTabItemType systemTabType) {
			this.item = item;
			this.systemTabType = systemTabType;
			TabImage tabImage = item.getImage();
			DOM.setStyleAttribute(this.getElement(), "overflow", "hidden");
			this.setHeight("100%");
			this.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
			this.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
			DOM.setStyleAttribute(this.getElement(), "paddingTop", PADDING_TOP + "px");
			
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
			
			// Add Handlers
			registerMouseAndTouchHandlers(this);
			if (item.getNavigate() != null || systemTabType != null) {
				this.addHandler(this, TapEvent.getType());
			}
		}

		@Override
		public void onTap(TapEvent event) {
			if (systemTabType == null) {
				TabBarItem item = this.getItem();
				if (item != null) {
					Navigate navigate = item.getNavigate();
					if (navigate != null) {
						HandlerManager eventBus = ConsoleUnitEventManager.getInstance().getEventBus();
						eventBus.fireEvent(new NavigateEvent(navigate));
					}
				}
			} else {
				switch (systemTabType) {
					case PREVIOUS:
						loadPage(currentPage-1);
						break;
					case NEXT:
						loadPage(currentPage+1);
						break;
				}
			}
		}
		
		public TabBarItem getItem() {
			return this.item;
		}
	}
	
	public TabBarComponent(TabBar tabBar) {
		super(new HorizontalPanel());
		container = (HorizontalPanel)this.getWidget();
		container.setStylePrimaryName(CLASS_NAME);
		container.setHeight(TABBAR_HEIGHT + "px");
		
		// Add Elements from tab bar entity
		for (TabBarItem item : tabBar.getItem()) {
			TabBarItemComponent tabBarItem = new TabBarItemComponent(item);
			this.addItem(tabBarItem);
		}
		
		// Register screen view change handler
		HandlerManager eventBus = ConsoleUnitEventManager.getInstance().getEventBus();
		eventBus.addHandler(ScreenViewChangeEvent.getType(), this);
	}
	
	/*
	 * The position of this widget and the number of visible tab items
	 * is dependent on the display size, get that from console display
	 */
	@Override
	public void onRender() {
		ConsoleDisplay display = WebConsole.getConsoleUnit().getConsoleDisplay();		
		int displayWidth = display.getWidth();
		int itemCount = items.size();
		maxItemsPerPage = (displayWidth / TabBarItemComponent.TAB_ITEM_MIN_WIDTH);
		pageCount = (int)Math.ceil(((double)itemCount/maxItemsPerPage));
		this.setWidth(displayWidth+"px");

		// Insert Next and Prev Nav buttons if overflow
		if (itemCount > maxItemsPerPage) {
			for (int i=1; i<=pageCount; i++) {
				int insertPos = (maxItemsPerPage * i) - 1;
				// Insert next and previous if more screens
				if (i < pageCount) {
					TabBarItem nextItem = createSystemTabItem(EnumSystemTabItemType.NEXT);
					TabBarItemComponent nextComponent = new TabBarItemComponent(nextItem, EnumSystemTabItemType.NEXT);
					this.insertItem(insertPos, nextComponent);
					TabBarItem prevItem = createSystemTabItem(EnumSystemTabItemType.PREVIOUS);
					TabBarItemComponent prevComponent = new TabBarItemComponent(prevItem, EnumSystemTabItemType.PREVIOUS);
					this.insertItem(insertPos+1, prevComponent);
				}
				itemCount = items.size();
				pageCount = (int)Math.round(((double)itemCount/maxItemsPerPage));
			}
		}
		
		// Determine width per item
		int itemsOnPage = items.size() > maxItemsPerPage ? maxItemsPerPage : items.size();
		widthPerItem = (int)Math.floor((double)displayWidth / itemsOnPage);
		for (TabBarItemComponent item : items) {
			item.setWidth(widthPerItem + "px");
		}
		
		// Load first page of items
		loadPage(1);
	}
	
	private void loadPage(int pageNo) {
		pageNo = pageNo > pageCount ? pageCount : pageNo;
		currentPage = pageNo;
		
		// Remove existing items
		int count = container.getWidgetCount();
		for (int i=0; i<count; i++) {
			container.remove(0);
		}
		
		// Draw the corresponding set of items
		int startIndex = ((pageNo-1) * maxItemsPerPage);

		for (int i=startIndex; i<(startIndex+(maxItemsPerPage)); i++) {
			if (i >= items.size()) {
				break;
			}
			TabBarItemComponent item = items.get(i);
			container.add(item);
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
	
	private static TabBarItem createSystemTabItem(EnumSystemTabItemType itemType) {
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
		return TABBAR_HEIGHT;
	}

	@Override
	public void onScreenViewChange(ScreenViewChangeEvent event) {
		// Cycle through tab items and if any point to this new screen highlight it
		for (TabBarItemComponent item : items) {
			Navigate navigate = item.getItem().getNavigate();
			if (navigate != null) {
				Integer toScreen = navigate.getToScreen();
				if (toScreen != null) {
					if (toScreen == event.getNewScreenId()) {
						item.addStyleName("tabBarItemSelected");
					} else {
						item.removeStyleName("tabBarItemSelected");
					}
				}
			}
		}
	}
}
