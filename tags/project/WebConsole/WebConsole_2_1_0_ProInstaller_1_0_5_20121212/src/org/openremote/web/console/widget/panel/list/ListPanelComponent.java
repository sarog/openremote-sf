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
package org.openremote.web.console.widget.panel.list;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openremote.web.console.client.WebConsole;
import org.openremote.web.console.event.ConsoleUnitEventManager;
import org.openremote.web.console.event.drag.DragCancelEvent;
import org.openremote.web.console.event.drag.DragEndEvent;
import org.openremote.web.console.event.drag.DragMoveEvent;
import org.openremote.web.console.event.drag.DragStartEvent;
import org.openremote.web.console.event.drag.Draggable;
import org.openremote.web.console.event.press.PressEndEvent;
import org.openremote.web.console.event.press.PressMoveEvent;
import org.openremote.web.console.event.press.PressStartEvent;
import org.openremote.web.console.event.ui.BindingDataChangeEvent;
import org.openremote.web.console.event.ui.BindingDataChangeHandler;
import org.openremote.web.console.panel.entity.DataValuePairContainer;
import org.openremote.web.console.panel.entity.ListItemLayout;
import org.openremote.web.console.panel.entity.ListLayout;
import org.openremote.web.console.service.AutoBeanService;
import org.openremote.web.console.service.DataBindingService;
import org.openremote.web.console.unit.ConsoleUnit;
import org.openremote.web.console.util.BrowserUtils;
import org.openremote.web.console.widget.ConsoleComponent;
import org.openremote.web.console.widget.Interactive;
import org.openremote.web.console.widget.Sensor;
import org.openremote.web.console.widget.panel.PanelComponent;

import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.Splittable;
/**
 * 
 *  
 * @author <a href="mailto:richard@openremote.org">Richard Turner</a>
 */
public class ListPanelComponent extends PanelComponent implements Draggable, Interactive, BindingDataChangeHandler {
	private static final String CLASS_NAME = "listPanelComponent";
	private static final String BINDING_FIELD_REGEX = "\\$\\{(\\w+)\\}";
	private String dataSource = null;
	private String itemBindingObject = null;
	private AutoBean<?> inputObject = null;
	private Splittable dataMap = null;
	private ListItemLayout itemTemplate = null;
	private List<ListItem> items = new ArrayList<ListItem>();
	private VerticalPanel container = new VerticalPanel();
	private int initialDragYPos = 0;
	private int initialScrollYPos = 0;
	PressStartEvent startEvent = null;
	protected PressMoveEvent lastMoveEvent = null;
	HandlerManager eventBus = ConsoleUnitEventManager.getInstance().getEventBus();
	
	public ListPanelComponent() {
		ScrollPanel scroll = new ScrollPanel(container);
		DOM.setStyleAttribute(scroll.getElement(), "overflow", "hidden");
		setPanelWidget(scroll);
	}
	
	public void addItem(ListItem item) {
		if (item != null) {
			items.add(item);
		}
	}
	
	public ConsoleComponent getItem(int index) {
		if (index > items.size() - 1) {
			return null;
		} else {
			return items.get(index);
		}
	}
	
	public List<ListItem> getItems() {
		return items;
	}
	
	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}
	
	
	public String getDataSource() {
		return dataSource;
	}
	
	public void setItemBindingObject(String itemBindingObject) {
		this.itemBindingObject = itemBindingObject;
	}
	
	
	public String getItemBindingObject() {
		return itemBindingObject;
	}
	
	public void setItemTemplate(ListItemLayout itemTemplate) {
		this.itemTemplate = itemTemplate;
	}
	
	public ListItemLayout getItemTemplate() {
		return this.itemTemplate;
	}
	
	public List<ListItem> generateListItems() {
		return generateListItems(null);
	}
	
	public List<ListItem> generateListItems(List<DataValuePairContainer> data) {
		List<ListItem> listItems = new ArrayList<ListItem>();
		
		inputObject = DataBindingService.getInstance().getData(dataSource, data);
		if (inputObject != null) {
			dataMap = AutoBeanCodex.encode(inputObject);
			
			// Populate list using binding data
			if (dataMap != null && itemBindingObject != null && !itemBindingObject.equals("")) {
				dataBindingActive = true;
				// Create List Items
				try {
					Splittable listArrMap = dataMap.get(itemBindingObject);
					if (listArrMap != null) {
						String templateStr = AutoBeanService.getInstance().toJsonString(ListItemLayout.class, itemTemplate);		
						for (int i=0; i< listArrMap.size(); i++) {
							String instanceStr = templateStr;
							Splittable fieldMap = listArrMap.get(i);
							if (fieldMap != null) {
								RegExp regex = RegExp.compile(BINDING_FIELD_REGEX, "g");
								for (MatchResult result = regex.exec(templateStr); result != null; result = regex.exec(templateStr)) {
									instanceStr = instanceStr.replace(result.getGroup(0), fieldMap.get(result.getGroup(1)).asString());
								}
								AutoBean<ListItemLayout> instanceBean = AutoBeanService.getInstance().fromJsonString(ListItemLayout.class, instanceStr);
								ListItemLayout layout = instanceBean.as();
								ListItem item = ListItem.build(layout);
								item.setParentList(this);
								item.setWidth(this.width);
								listItems.add(item);
							}
						}
					}
				} catch (Exception e) {
					// TODO: Problem binding to data source do something
					//Window.alert("Failed to bind data source!");
				}
			}
		}
		
		return listItems;
	}
	
	private void refreshListItems() {
		for (ListItem item : items) {
			item.onRemove();
			container.remove(item);
		}
		items.removeAll(items);
		items = generateListItems();
		
		// Add items to container
		for (ListItem item : items) {
			if (item != null) {
				container.add(item);
				item.onAdd(width, height);
			}
		}
	}
	
	private void registerHandlers() {
		if(BrowserUtils.isMobile) {
			registerHandler(this.addDomHandler(this, TouchStartEvent.getType()));
			registerHandler(this.addDomHandler(this, TouchEndEvent.getType()));
		} else {
			registerHandler(this.addDomHandler(this, MouseDownEvent.getType()));
			registerHandler(this.addDomHandler(this, MouseUpEvent.getType()));
			registerHandler(this.addDomHandler(this, MouseOutEvent.getType()));
		}
		registerHandler(this.addHandler(this, DragStartEvent.getType()));
		registerHandler(this.addHandler(this, DragMoveEvent.getType()));
		registerHandler(this.addHandler(this, DragEndEvent.getType()));
		registerHandler(this.addHandler(this, DragCancelEvent.getType()));
	}
	
	public boolean appearsVertical() {
		ConsoleUnit consoleUnit = WebConsole.getConsoleUnit();
		return (consoleUnit.getOrientation().equals(consoleUnit.getConsoleDisplay().getOrientation()));
	}

	// ---------------------------------------------------------------------------------
	//			SUPER CLASS OVERRIDES BELOW
	// ---------------------------------------------------------------------------------
	
	@Override
	public Set<Sensor> getSensors() {
		return null;
	}

	@Override
	public Set<ConsoleComponent> getComponents() {
		return new HashSet<ConsoleComponent>(items);
	}
	
	@Override
	public String getClassName() {
		return CLASS_NAME;
	}
	
	@Override
	public void onRender(int screenWidth, int screenHeight, List<DataValuePairContainer> data) {
		// Set Scroll Panel Size and Vertical Panel Width
//		ScrollPanel panel = (ScrollPanel)getWidget();
//		setHeight(height + "px");
//		panel.setWidth(width + "px");
//		container.setWidth(width + "px");
//		container.setHeight(height + "px");
		
		if (!isInitialised) {

		}
		
		// Get data source if it is defined and generate list items
		if (dataSource != null && !dataSource.equals("")) {
			items = generateListItems(data);
		}
		
		// Add items to container
		for (ListItem item : items) {
			if (item != null) {
				container.add(item);
				item.onAdd(screenWidth, screenHeight);
			}
		}
		
		// Attach event listeners as only automatically done for interactive components
		registerHandlers();
	}
	
	@Override
	public void onUpdate(int width, int height) {
		ScrollPanel panel = (ScrollPanel)getWidget();
		panel.setHeight(height + "px");
		panel.setWidth(width + "px");
		container.setWidth("100%");
		for (ListItem item : items) {
			item.onUpdate(width, height);
		}
	}
	
	@Override
	public void onRemove() {
		for (ListItem item : items) {
			item.onRemove();
			container.remove(item);
		}
		items.removeAll(items);
		super.onRemove();
	}
	
	@Override
	public void onDragStart(DragStartEvent event) {
		//Record initial scroll position and initial YPos
		if (appearsVertical()) {
			initialDragYPos = event.getYPos();
		} else {
			initialDragYPos = event.getXPos();
		}
		initialScrollYPos = ((ScrollPanel)getWidget()).getVerticalScrollPosition();
	}

	@Override
	public void onDragMove(DragMoveEvent event) {
		
		// Calculate difference between initial Y Pos and this pos and scroll accordingly
		int scrollPos = initialScrollYPos + initialDragYPos;
		if (appearsVertical()) {
			scrollPos -= event.getYPos();
		} else {
			scrollPos -= event.getXPos();
		}
		((ScrollPanel)getWidget()).setVerticalScrollPosition(scrollPos);
	}

	@Override
	public void onDragEnd(DragEndEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDragCancel(DragCancelEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTouchStart(TouchStartEvent event) {
		event.stopPropagation();
		startEvent = new PressStartEvent(event);
		eventBus.fireEvent(startEvent);
		this.fireEvent(startEvent);
	}

	@Override
	public void onMouseDown(MouseDownEvent event) {
		event.stopPropagation();
		startEvent = new PressStartEvent(event);
		eventBus.fireEvent(startEvent);
		this.fireEvent(startEvent);
	}

	@Override
	public void onTouchEnd(TouchEndEvent event) {
		event.stopPropagation();
		PressEndEvent endEvent = null;
		if (lastMoveEvent != null) {
			endEvent = new PressEndEvent(lastMoveEvent);
		} else if (startEvent != null) {
			endEvent = new PressEndEvent(startEvent);
		}
		if (endEvent != null) {
			eventBus.fireEvent(endEvent);
			this.fireEvent(endEvent);
		}
		reset();
	}

	@Override
	public void onMouseUp(MouseUpEvent event) {
		event.stopPropagation();
		PressEndEvent endEvent = new PressEndEvent(event); 
		eventBus.fireEvent(endEvent);
		this.fireEvent(endEvent);
		reset();
	}
	
	@Override
	public void onMouseOut(MouseOutEvent event) {
		// DO NOTHING HERE
	}
	

	@Override
	public void onBindingDataChange(BindingDataChangeEvent event) {
		if (dataBindingActive) {
			refreshListItems();
			
//			List<ListItem> newItems = generateListItems();
//			
//			for (ListItem newItem : newItems) {
//				if (!items.contains(newItem)) {
//					container.add(newItem);
//					newItem.onAdd(width, height);
//					items.add(newItem);
//				}
//			}
		}
	}
	

	protected void reset() {
		startEvent = null;
		lastMoveEvent = null;
	}
	
	// ---------------------------------------------------------------------------------
	//			BUILD METHOD BELOW HERE
	// ---------------------------------------------------------------------------------
	
	public static ListPanelComponent build(ListLayout layout) throws Exception {
		
		ListPanelComponent panel = new ListPanelComponent();
		
		// Add explicitly defined list items otherwise let the onRender method create them using the data source
		if (layout == null) {
			return panel;
		}
		
		panel.setHeight(layout.getHeight());
		panel.setWidth(layout.getWidth());
		panel.setPosition(layout.getLeft(),layout.getTop(), layout.getRight(), layout.getBottom());
		
		String dataSource = layout.getDataSource();
		String itemBindingObject = layout.getItemBindingObject();
		ListItemLayout itemLayout = layout.getItemTemplate();
		
		if (dataSource != null) {
			panel.setDataSource(dataSource);
		}
		if (itemBindingObject != null) {
			panel.setItemBindingObject(itemBindingObject);
		}
		if (itemLayout != null)
		panel.setItemTemplate(itemLayout);
		return panel;
	}
}
