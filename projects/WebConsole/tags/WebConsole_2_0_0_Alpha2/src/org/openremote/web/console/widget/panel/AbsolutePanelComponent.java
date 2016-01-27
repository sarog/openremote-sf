package org.openremote.web.console.widget.panel;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.openremote.web.console.panel.entity.AbsoluteLayout;
import org.openremote.web.console.panel.entity.DataValuePair;
import org.openremote.web.console.panel.entity.DataValuePairContainer;
import org.openremote.web.console.panel.entity.component.ButtonComponent;
import org.openremote.web.console.panel.entity.component.ImageComponent;
import org.openremote.web.console.panel.entity.component.LabelComponent;
import org.openremote.web.console.panel.entity.component.SliderComponent;
import org.openremote.web.console.panel.entity.component.SwitchComponent;
import org.openremote.web.console.widget.ConsoleComponent;
import org.openremote.web.console.widget.Sensor;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;

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
		component.onUpdate(width, height);
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
		absPanel.setPosition(layout.getLeft(),layout.getTop());
		
		// Create component
		LabelComponent labelComponent = layout.getLabel();
		ImageComponent imageComponent = layout.getImage();
		SliderComponent sliderComponent = layout.getSlider();
		SwitchComponent switchComponent = layout.getSwitch();
		ButtonComponent buttonComponent = layout.getButton();
		
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
		} else {
			return null;
		}
		
		if (component != null) {
			absPanel.setComponent(component);
		}
		
		return absPanel;
	}
}
