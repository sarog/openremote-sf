package org.openremote.web.console.widget;

import java.util.ArrayList;
import java.util.List;
import org.openremote.web.console.client.WebConsole;
import org.openremote.web.console.event.tap.TapEvent;
import org.openremote.web.console.event.tap.TapHandler;
import org.openremote.web.console.event.ui.NavigateEvent;
import org.openremote.web.console.panel.entity.Navigate;
import org.openremote.web.console.panel.entity.TabImage;
import org.openremote.web.console.panel.entity.TabBar;
import org.openremote.web.console.panel.entity.TabBarItem;
import org.openremote.web.console.service.AutoBeanService;
import org.openremote.web.console.unit.ConsoleDisplay;
import org.openremote.web.console.util.BrowserUtils;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.web.bindery.autobean.shared.AutoBean;

public class TabBarComponent extends InteractiveConsoleComponent {
	public static final String CLASS_NAME = "tabBarComponent";
	public static final String TAB_ITEM_CLASS_NAME = "tabBarItem";
	public static final String TAB_IMAGE_CLASS_NAME = "tabBarItemImage";
	public static final String TAB_TEXT_CLASS_NAME = "tabBarItemText";
	public static final int TAB_BAR_HEIGHT = 46;
	public static final int TAB_TEXT_HEIGHT = 12;
	public static final int PADDING_TOP = 2;
	public static final int PADDING_BOTTOM = 2;
	public static final int PADDING_BETWEEN_IMAGE_AND_TEXT = 2;
	private static final int TAB_ITEM_MIN_WIDTH = 60;
	private List<TabBarItemComponent> items = new ArrayList<TabBarItemComponent>();
	private int pageCount = 0;
	private int maxItemsPerPage = 0;
	private int currentPage = 0;
	private int widthPerItem = 0;
	private List<HandlerRegistration> systemTabHandlers = new ArrayList<HandlerRegistration>();
	
	private static enum EnumSystemTabItemType {
		PREVIOUS,
		NEXT;
	}
	
	public class TabBarItemComponent extends VerticalPanel implements TapHandler {
		private TabBarItem item;
		private EnumSystemTabItemType systemTabType;
		
		public TabBarItemComponent(TabBarItem item) {
			this(item, null);
		}
		
		public TabBarItemComponent(TabBarItem item, EnumSystemTabItemType systemTabType) {
			boolean hasImage = false;
			boolean hasText = false;
			Image imageComponent = null;
			Label nameComponent = null;
			this.item = item;
			this.systemTabType = systemTabType;
			setStylePrimaryName(TAB_ITEM_CLASS_NAME);
			TabImage tabImage = item.getImage();
			setHeight("100%");
			setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
			
			// Add Image
			if (tabImage != null) {
				int tabImageSize = TAB_BAR_HEIGHT - (PADDING_TOP + PADDING_BOTTOM);
				imageComponent = new Image();
				imageComponent.setStylePrimaryName(TAB_IMAGE_CLASS_NAME);
				String controllerUrl = WebConsole.getConsoleUnit().getControllerService().getController().getUrl();
				imageComponent.setUrl(controllerUrl + "/" + tabImage.getSrc());
				imageComponent.setSize(tabImageSize + "px", tabImageSize + "px");
				imageComponent.setStylePrimaryName("tabBarItemImage");
				DOM.setStyleAttribute(imageComponent.getElement(), "padding", "0px");
				DOM.setStyleAttribute(imageComponent.getElement(), "marginTop", PADDING_TOP + "px");
				DOM.setStyleAttribute(imageComponent.getElement(), "marginBottom", PADDING_BOTTOM + "px");
				this.add(imageComponent);
				hasImage = true;
			}
			
			// Add Text
			if (item.getName() != null && !item.getName().equals("")) {
				int lineHeight = TAB_TEXT_HEIGHT;
				if (!hasImage) {
					lineHeight = TAB_BAR_HEIGHT - (PADDING_TOP + PADDING_BOTTOM); 
				}
				nameComponent = new Label();
				nameComponent.setText(item.getName());
				nameComponent.setWidth("100%");
				nameComponent.setStylePrimaryName(TAB_TEXT_CLASS_NAME);
				DOM.setStyleAttribute(nameComponent.getElement(), "lineHeight", lineHeight + "px");
				DOM.setStyleAttribute(nameComponent.getElement(), "fontSize", TAB_TEXT_HEIGHT + "px");
				DOM.setStyleAttribute(nameComponent.getElement(), "padding", "0px");
				DOM.setStyleAttribute(nameComponent.getElement(), "marginBottom", PADDING_BOTTOM + "px");
				DOM.setStyleAttribute(nameComponent.getElement(), "marginTop", PADDING_TOP + "px");
				this.add(nameComponent);
				hasText = true;
			}
			
			// If image and text add padding between them and adjust image size
			if (hasImage && hasText) {
				int tabImageSize = TAB_BAR_HEIGHT - (PADDING_TOP + PADDING_BOTTOM + TAB_TEXT_HEIGHT + PADDING_BETWEEN_IMAGE_AND_TEXT);
				DOM.setStyleAttribute(imageComponent.getElement(), "marginBottom", PADDING_BETWEEN_IMAGE_AND_TEXT/2 + "px");
				DOM.setStyleAttribute(nameComponent.getElement(), "marginTop", PADDING_BETWEEN_IMAGE_AND_TEXT/2 + "px");
				imageComponent.setSize(tabImageSize + "px", tabImageSize + "px");
			}
		}

		@Override
		public void onTap(TapEvent event) {
			if (systemTabType == null) {
				TabBarItem item = this.getItem();
				if (item != null) {
					Navigate navigate = item.getNavigate();
					if (navigate != null) {
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
		
		private TabBarItem getItem() {
			return this.item;
		}
	}
	
	public TabBarComponent(TabBar tabBar) {
		super(new HorizontalPanel(), CLASS_NAME);
		DOM.setIntStyleAttribute(getElement(), "zIndex", 1000 );
		DOM.setStyleAttribute(getElement(), "overflow", "hidden");
		if (tabBar != null) {
			// Add Elements from tab bar entity
			for (TabBarItem item : tabBar.getItem()) {
				TabBarItemComponent tabBarItem = new TabBarItemComponent(item);
				this.addItem(tabBarItem);
			}
		}
	}
	
	public int getHeight() {
		return TAB_BAR_HEIGHT;
	}
	
	/*
	 * The position of this widget and the number of visible tab items
	 * is dependent on the display size, get that from console display
	 */
	@Override
	public void onRender(int width, int height) {
		setHeight(getHeight() + "px");
		setWidth("100%");
		ConsoleDisplay display = WebConsole.getConsoleUnit().getConsoleDisplay();		
		int displayWidth = display.getWidth();
		int itemCount = items.size();
		maxItemsPerPage = (displayWidth / TAB_ITEM_MIN_WIDTH);
		pageCount = (int)Math.ceil(((double)itemCount/maxItemsPerPage));

		// Insert Next and Prev Nav buttons if there is overflow
		if (itemCount > maxItemsPerPage) {
			for (int i=1; i<=pageCount; i++) {
				int insertPos = (maxItemsPerPage * i) - 1;
				// Insert next and previous if more screens
				if (i < pageCount) {
					this.insertItem(insertPos, createSystemTabItem(EnumSystemTabItemType.NEXT));
					this.insertItem(insertPos+1, createSystemTabItem(EnumSystemTabItemType.PREVIOUS));
				}
				itemCount = items.size();
				pageCount = (int)Math.round(((double)itemCount/maxItemsPerPage));
			}
		}
		
		// Determine width per item and add handlers
		int itemsOnPage = items.size() > maxItemsPerPage ? maxItemsPerPage : items.size();
		widthPerItem = (int)Math.floor((double)displayWidth / itemsOnPage);
		for (TabBarItemComponent item : items) {
			item.setWidth(widthPerItem + "px");
			if (!isInitialised) {
				if (item.getItem().getNavigate() != null) {
					addInteractiveChild(item);
				}
			}
		}
		
		// Load first page of items
		loadPage(1);
	}
	
	/*
	 * When display dimensions change need to redraw the tab bar to
	 * account for the change
	 */
	public void refresh() {
		// Remove existing items
		hideCurrentItems();
		
		// Remove the system tab items
		List<TabBarItemComponent> removedItems = new ArrayList<TabBarItemComponent>();
		for (TabBarItemComponent item : items) {
			if (item.systemTabType != null) {
				removedItems.add(item);
			}
		}
		for (TabBarItemComponent item : removedItems) {
			items.remove(item);
		}
		
		for (HandlerRegistration handler : systemTabHandlers) {
			handler.removeHandler();
		}
		systemTabHandlers.clear();
		
		// Recall render to do the calculations
		onRender(0, 0);
	}
	
	private void loadPage(int pageNo) {
		pageNo = pageNo > pageCount ? pageCount : pageNo;
		currentPage = pageNo;
		
		// Remove existing items
		hideCurrentItems();
		
		// Draw the corresponding set of items
		int startIndex = ((pageNo-1) * maxItemsPerPage);

		for (int i=startIndex; i<(startIndex+(maxItemsPerPage)); i++) {
			if (i >= items.size()) {
				break;
			}
			TabBarItemComponent item = items.get(i);
			((HorizontalPanel)getWidget()).add(item);
		}
	}
	
	public void hideCurrentItems() {
		int count = ((HorizontalPanel)getWidget()).getWidgetCount();
		for (int i=0; i<count; i++) {
			((HorizontalPanel)getWidget()).remove(0);
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
	
	private TabBarItemComponent createSystemTabItem(EnumSystemTabItemType itemType) {
		TabBarItem tabBarItem = null;
		TabBarItemComponent component = null;
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
		component = new TabBarItemComponent(tabBarItem, itemType);
		// Add handlers as not created by usual mechanism
		if(BrowserUtils.isMobile) {
			systemTabHandlers.add(component.addDomHandler(this, TouchStartEvent.getType()));
			systemTabHandlers.add(component.addDomHandler(this, TouchEndEvent.getType()));
		} else {
			systemTabHandlers.add(component.addDomHandler(this, MouseDownEvent.getType()));
			systemTabHandlers.add(component.addDomHandler(this, MouseUpEvent.getType()));
			systemTabHandlers.add(component.addDomHandler(this, MouseOutEvent.getType()));
		}
		systemTabHandlers.add(component.addHandler(component, TapEvent.getType()));
		return component;
	}

	public void onScreenViewChange(int newScreenId) {
		if (!handlersRegistered) {
			return;
		}
		// Cycle through tab items and if any point to this new screen highlight it
		for (TabBarItemComponent item : items) {
			Navigate navigate = item.getItem().getNavigate();
			if (navigate != null) {
				Integer toScreen = navigate.getToScreen();
				if (toScreen != null) {
					if (toScreen == newScreenId) {
						item.addStyleName("selected");
					} else {
						item.removeStyleName("selected");
					}
				}
			}
		}
	}
}
