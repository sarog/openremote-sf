package org.openremote.web.console.widget.panel.list;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openremote.web.console.panel.entity.AbsoluteLayout;
import org.openremote.web.console.panel.entity.DataValuePair;
import org.openremote.web.console.panel.entity.FormLayout;
import org.openremote.web.console.panel.entity.GridLayout;
import org.openremote.web.console.panel.entity.ListItemLayout;
import org.openremote.web.console.widget.ConsoleComponent;
import org.openremote.web.console.widget.InteractiveConsoleComponent;
import org.openremote.web.console.widget.Sensor;
import org.openremote.web.console.widget.panel.AbsolutePanelComponent;
import org.openremote.web.console.widget.panel.GridPanelComponent;
import org.openremote.web.console.widget.panel.PanelComponent;
import org.openremote.web.console.widget.panel.Positional;
import org.openremote.web.console.widget.panel.PanelComponent.DimensionResult;
import org.openremote.web.console.widget.panel.PanelComponent.DimensionUnit;
import org.openremote.web.console.widget.panel.form.FormPanelComponent;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ListItem extends PanelComponent {
	public static final String CLASS_NAME = "listItemComponent";
	private static final int DEFAULT_ITEM_HEIGHT = 50;
	Set<PanelComponent> itemComponents = new HashSet<PanelComponent>();
	
	public ListItem() {
		AbsolutePanel panel = new AbsolutePanel();
		DOM.setStyleAttribute(panel.getElement(), "overflow", "hidden");
		setPanelWidget(panel);
		setHeight(DEFAULT_ITEM_HEIGHT);
	}
	
	public void setHtml(String html) {
		((HTML)getWidget()).setHTML(html);
	}
	
	private void addComponentToListItem(PanelComponent component) {
		int left = 0;
		int top = 0;
		
		if (component instanceof Positional) {
			Positional positional = (Positional) component; 
			left = positional.getLeft();
			top = positional.getTop();
		}
		((AbsolutePanel)getWidget()).add((Widget) component, left, top);
		itemComponents.add(component);
	}
	
	// ---------------------------------------------------------------------------------
	//			SUPER CLASS OVERRIDES BELOW
	// ---------------------------------------------------------------------------------
	
	@Override
	public void onRender(int width, int height, List<DataValuePair> data) {
		for (PanelComponent component : itemComponents) {
			component.onAdd(width, height);
		}
	}

	@Override
	public void onUpdate(int width, int height) {
		setWidth("100%");
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
	
	// ---------------------------------------------------------------------------------
	//			BUILD METHOD BELOW HERE
	// ---------------------------------------------------------------------------------
	
	public static ListItem build(ListItemLayout listLayout) throws Exception {
			ListItem item = new ListItem();
			
			// Check for height parameter
			String itemHeight = listLayout.getItemHeight();
			if (itemHeight != null) {
				item.setHeight(itemHeight);
			}
			
			// Cycle through absolute and grid panels and create components
			try {
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
			} catch (Exception e) {
				return null;
			}
			
			return item;
	}
}
