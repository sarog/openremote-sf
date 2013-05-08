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

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.openremote.web.console.event.ConsoleUnitEventManager;
import org.openremote.web.console.event.press.PressEndEvent;
import org.openremote.web.console.event.press.PressMoveEvent;
import org.openremote.web.console.event.press.PressStartEvent;
import org.openremote.web.console.event.tap.TapEvent;
import org.openremote.web.console.event.tap.TapHandler;
import org.openremote.web.console.event.ui.CommandSendEvent;
import org.openremote.web.console.panel.entity.AbsoluteLayout;
import org.openremote.web.console.panel.entity.DataValuePairContainer;
import org.openremote.web.console.panel.entity.GridLayout;
import org.openremote.web.console.panel.entity.ListItemLayout;
import org.openremote.web.console.util.BrowserUtils;
import org.openremote.web.console.widget.ConsoleComponent;
import org.openremote.web.console.widget.Interactive;
import org.openremote.web.console.widget.Sensor;
import org.openremote.web.console.widget.panel.AbsolutePanelComponent;
import org.openremote.web.console.widget.panel.GridPanelComponent;
import org.openremote.web.console.widget.panel.PanelComponent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Widget;
/**
 * 
 *  
 * @author <a href="mailto:richard@openremote.org">Richard Turner</a>
 */
public class ListItem extends PanelComponent implements Interactive, TapHandler, TouchMoveHandler, MouseMoveHandler {
	public static final String CLASS_NAME = "listItemComponent";
	private static int DEFAULT_ITEM_HEIGHT = 50;
	PressStartEvent startEvent = null;
	private String commandString = "";
	private String onTap = "";
	Set<PanelComponent> itemComponents = new HashSet<PanelComponent>();
	HandlerManager eventBus = ConsoleUnitEventManager.getInstance().getEventBus();
	ListPanelComponent parentList = null;
	AbsolutePanel component = null;
	
	static {
		int[] size;
		size = BrowserUtils.getSizeFromStyle(CLASS_NAME);
		DEFAULT_ITEM_HEIGHT = size[1] < DEFAULT_ITEM_HEIGHT ? DEFAULT_ITEM_HEIGHT : size[1];
	}
	
	public ListItem() {
		component = new AbsolutePanel();
		DOM.setStyleAttribute(component.getElement(), "overflow", "hidden");
		setPanelWidget(component);
		setHeight(DEFAULT_ITEM_HEIGHT);
	}
	
	private void setCommandString(String commandString) {
		this.commandString = commandString;
	}
	
	private String getCommandString() {
		return commandString;
	}
	
	private void setOnTap(String onTap) {
		this.onTap = onTap;
	}
	
	private String getOnTap() {
		return onTap;
	}
	
	protected void setParentList(ListPanelComponent parentList) {
		this.parentList = parentList;
	}
	
	private void addComponentToListItem(PanelComponent component) {
		// Add at 0,0 onRender will correctly position it when screen size is known
		((AbsolutePanel)getWidget()).add((Widget) component, 0, 0);
		itemComponents.add(component);
	}
	
	private void registerHandlers() {
		if(BrowserUtils.isMobile) {
			registerHandler(this.addDomHandler(this, TouchStartEvent.getType()));
			registerHandler(this.addDomHandler(this, TouchEndEvent.getType()));
			registerHandler(this.addDomHandler(this, TouchMoveEvent.getType()));
		} else {
			registerHandler(this.addDomHandler(this, MouseDownEvent.getType()));
			registerHandler(this.addDomHandler(this, MouseUpEvent.getType()));
			registerHandler(this.addDomHandler(this, MouseOutEvent.getType()));
			registerHandler(this.addDomHandler(this, MouseMoveEvent.getType()));
		}
	}
	
	protected void reset() {
		startEvent = null;
	}
	
	protected void propagateEvent(PressMoveEvent moveEvent) {
		startEvent.setSource(parentList);
		moveEvent.setSource(parentList);
		eventBus.fireEvent(startEvent);
		startEvent = null;
		eventBus.fireEvent(moveEvent);
	}
	
	// ---------------------------------------------------------------------------------
	//			SUPER CLASS OVERRIDES BELOW
	// ---------------------------------------------------------------------------------
	
	@Override
	public void onRender(int width, int height, List<DataValuePairContainer> data) {
		for (PanelComponent component : itemComponents) {
			component.onAdd((AbsolutePanel)this.getWidget(), width, height, data);
		}
		
		// If ontap defined then register on tap event
		if (!onTap.equals("")) {
			registerHandler(this.addHandler(this, TapEvent.getType()));
		}
		
		// Attach event listeners as only automatically done for interactive components
		registerHandlers();
	}

	@Override
	public void onUpdate(int width, int height) {
		setWidth("100%");
		component.setWidth(width + "px");
		for (PanelComponent component : itemComponents) {
			component.onRefresh(width, height);
		}
	}

	@Override
	public Set<Sensor> getSensors() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<ConsoleComponent> getComponents() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getClassName() {
		return CLASS_NAME;
	}
	
	@Override
	public void onTouchStart(TouchStartEvent event) {
		event.stopPropagation();
		startEvent = new PressStartEvent(event);
	}

	@Override
	public void onMouseDown(MouseDownEvent event) {
		event.stopPropagation();
		startEvent = new PressStartEvent(event);
	}

	@Override
	public void onTouchEnd(TouchEndEvent event) {
		if (startEvent != null) {
			event.stopPropagation();
			PressEndEvent endEvent = new PressEndEvent(startEvent);
			if (endEvent != null) {
				eventBus.fireEvent(startEvent);
				eventBus.fireEvent(endEvent);
			}
			reset();
		}
	}

	@Override
	public void onMouseUp(MouseUpEvent event) {
		if (startEvent != null) {
			event.stopPropagation();
			PressEndEvent endEvent = new PressEndEvent(event);
			eventBus.fireEvent(startEvent);
			eventBus.fireEvent(endEvent);
			reset();
		}
	}
	
	@Override
	public void onMouseOut(MouseOutEvent event) {
		startEvent = null;
	}
	
	@Override
	public void onTouchMove(TouchMoveEvent event) {
		if (startEvent != null) {
			event.preventDefault();
			event.stopPropagation();
			propagateEvent(new PressMoveEvent(event));
		}
	}

	@Override
	public void onMouseMove(MouseMoveEvent event) {
		if (startEvent != null) {
			event.preventDefault();
			event.stopPropagation();
			propagateEvent(new PressMoveEvent(event));
		}
	}
	
	@Override
	public void onTap(TapEvent event) {
		if (onTap.equalsIgnoreCase("command")) {
			eventBus.fireEvent(new CommandSendEvent(getId(), commandString, null));
		}
	}
	
	// ---------------------------------------------------------------------------------
	//			BUILD METHOD BELOW HERE
	// ---------------------------------------------------------------------------------
	
	public static ListItem build(ListItemLayout listLayout) throws Exception {
			ListItem item = new ListItem();
			
			// Check for height parameter
//			String itemHeight = listLayout.getItemHeight();
//			if (itemHeight != null) {
//				item.setHeight(itemHeight);
//			}
			
			// Check for id
			Integer id = listLayout.getId();
			if (id != null) {
				item.setId(id);
			}
			
			// Check for command string
			String commandString = listLayout.getCommandString();
			if (commandString != null) {
				item.setCommandString(commandString);
			}
			
			// Check for command string
			String onTap = listLayout.getOnTap();
			if (onTap != null) {
				item.setOnTap(onTap);
			}
			
			// Cycle through absolute and grid panels and create components
			List<AbsoluteLayout> absoluteElems = listLayout.getAbsolute();
			
			if (absoluteElems != null) {
				for (AbsoluteLayout layout : absoluteElems) {
					// Create Absolute Panel Component
					AbsolutePanelComponent absComponent = AbsolutePanelComponent.build(layout);
					item.addComponentToListItem(absComponent);
				}
			}
			
			List<GridLayout> gridElems = listLayout.getGrid();
			
			if (gridElems != null) {
				for (GridLayout layout : gridElems) {
					// Create Grid Panel Component
					GridPanelComponent gridComponent = GridPanelComponent.build(layout);
					item.addComponentToListItem(gridComponent);
				}
			}
			
			return item;
	}
}
