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
package org.openremote.web.console.widget.panel;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.openremote.web.console.panel.entity.AbsoluteLayout;
import org.openremote.web.console.panel.entity.DataValuePairContainer;
import org.openremote.web.console.panel.entity.component.ButtonComponent;
import org.openremote.web.console.panel.entity.component.ImageComponent;
import org.openremote.web.console.panel.entity.component.LabelComponent;
import org.openremote.web.console.panel.entity.component.SliderComponent;
import org.openremote.web.console.panel.entity.component.SwitchComponent;
import org.openremote.web.console.panel.entity.component.WebElementComponent;
import org.openremote.web.console.widget.ConsoleComponent;
import org.openremote.web.console.widget.Sensor;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;
/**
 * 
 *  
 * @author <a href="mailto:richard@openremote.org">Richard Turner</a>
 */
public class AbsolutePanelComponent extends PanelComponent {
	private static final String CLASS_NAME = "absolutePanelComponent";
	private ConsoleComponent component;
	private HorizontalPanel componentContainer;
	
	public AbsolutePanelComponent() {
		componentContainer = new HorizontalPanel();
		componentContainer.setWidth("100%");
		componentContainer.setHeight("100%");
		componentContainer.setHorizontalAlignment(HasAlignment.ALIGN_CENTER);
		componentContainer.setVerticalAlignment(HasAlignment.ALIGN_MIDDLE);
		
		setPanelWidget(componentContainer);
	}
	
	public void setComponent(ConsoleComponent component) {
		componentContainer.add((Widget)component);
		this.component = component;
	}
	
	// ---------------------------------------------------------------------------------
	//			SUPER CLASS OVERRIDES BELOW
	// ---------------------------------------------------------------------------------
	
	@Override
	public void onRender(int width, int height, List<DataValuePairContainer> data) {
		if (component != null) {
			component.onAdd(width, height);
		}
	}	

	@Override
	public void onUpdate(int width, int height) {
		if (component != null) {
			component.onRefresh(width, height);
		}
	}
	
	@Override
	public void onRemove() {
		if (component != null) {
			component.onRemove();
		}
	}
	
	@Override
	public Set<Sensor> getSensors() {
		Set<Sensor> sensors = new HashSet<Sensor>();
		if (component != null) {
			sensors.add(component.getSensor());
		}
		return sensors;
	}
	
	@Override
	public Set<ConsoleComponent> getComponents() {
		Set<ConsoleComponent> components = new HashSet<ConsoleComponent>();
		components.add(component);
		return components;
	}
	
	@Override
	public String getClassName() {
		return CLASS_NAME;
	}
	
	// ---------------------------------------------------------------------------------
	//			BUILD METHOD BELOW HERE
	// ---------------------------------------------------------------------------------
	
	public static AbsolutePanelComponent build(AbsoluteLayout layout) throws Exception {
		AbsolutePanelComponent absPanel = new AbsolutePanelComponent();
		if (layout == null) {
			return absPanel;
		}
		absPanel.setHeight(layout.getHeight());
		absPanel.setWidth(layout.getWidth());
		absPanel.setPosition(layout.getLeft(),layout.getTop(), layout.getRight(), layout.getBottom());
		
		// Create component
		LabelComponent labelComponent = layout.getLabel();
		ImageComponent imageComponent = layout.getImage();
		SliderComponent sliderComponent = layout.getSlider();
		SwitchComponent switchComponent = layout.getSwitch();
		ButtonComponent buttonComponent = layout.getButton();
		WebElementComponent webComponent = layout.getWeb();
		
		// Create Console Component
		ConsoleComponent component = null;
		
		if (labelComponent != null) {
			component = org.openremote.web.console.widget.LabelComponent.build(labelComponent);
		} else if (imageComponent != null) {
			component = org.openremote.web.console.widget.ImageComponent.build(imageComponent);
		} else if (sliderComponent != null) {
			component = org.openremote.web.console.widget.SliderComponent.build(sliderComponent);
		} else if (switchComponent != null) {
			component = org.openremote.web.console.widget.SwitchComponent.build(switchComponent);
		} else if (buttonComponent != null) {
			component = org.openremote.web.console.widget.ButtonComponent.build(buttonComponent);
		} else if (webComponent != null) {
			component = org.openremote.web.console.widget.WebElementComponent.build(webComponent);
		} else {
			org.openremote.web.console.widget.LabelComponent lblComponent = new org.openremote.web.console.widget.LabelComponent();
			lblComponent.setText("COMPONENT TYPE NOT SUPPORTED.");
			lblComponent.setColor("#FFF");
			component = lblComponent;
		}
		
		if (component != null) {
			absPanel.setComponent(component);
		}
		
		return absPanel;
	}
}
