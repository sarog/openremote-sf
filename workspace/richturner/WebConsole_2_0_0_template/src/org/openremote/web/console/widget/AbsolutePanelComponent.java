package org.openremote.web.console.widget;

import org.openremote.web.console.client.WebConsole;
import org.openremote.web.console.panel.entity.AbsoluteLayout;
import org.openremote.web.console.panel.entity.component.ButtonComponent;
import org.openremote.web.console.panel.entity.component.ImageComponent;
import org.openremote.web.console.panel.entity.component.LabelComponent;
import org.openremote.web.console.panel.entity.component.SliderComponent;
import org.openremote.web.console.panel.entity.component.SwitchComponent;

import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class AbsolutePanelComponent extends PassiveConsoleComponent implements Positional {
	private static final String CLASS_NAME = "absolutePanelComponent";
	private ConsoleComponent component;
	private HorizontalPanel componentContainer;
	private int left;
	private int top;
	private int height;
	private int width;
	
	public AbsolutePanelComponent() {
		super(new SimplePanel(), CLASS_NAME);
		componentContainer = new HorizontalPanel();
		componentContainer.setWidth("100%");
		componentContainer.setHeight("100%");
		
		componentContainer.setHorizontalAlignment(HasAlignment.ALIGN_CENTER);
		componentContainer.setVerticalAlignment(HasAlignment.ALIGN_MIDDLE);
		
		((SimplePanel)this.getWidget()).add(componentContainer);
	}

	@Override
	public void onAdd(int width, int height) {
		setVisible(true);
		onRender(width, height);
	}
	
	@Override
	// Pass size info to widget so explicit size can be set to avoid any cross browser rendering issues
	public void onRender(int width, int height) {
		if (component != null) {
			component.onAdd(this.width, this.height);
		}
	}
	
	@Override
	public void onRemove() {
		if (component != null) {
			component.onRemove();
		}
	}
	
	public void setComponent(ConsoleComponent component) {
		componentContainer.add((Widget)component);
		this.component = component;
	}
	
	public ConsoleComponent getComponent() {
		return (ConsoleComponent)this.componentContainer.getWidget(0);
	}

	@Override
	public void setPosition(int left, int top) {
		this.left = left;
		this.top = top;
	}

	@Override
	public int getLeft() {
		return this.left;
	}

	@Override
	public int getTop() {
		return this.top;
	}
	
	public void setHeight(int height) {
		this.height = height;
		super.setHeight(height + "px");
	}
	
	public void setWidth(int width) {
		this.width = width;
		super.setWidth(width + "px");
	}
	
	@Override
	public void setHeight(String height) {
		int heightInt = 0;
		if (height.endsWith("%")) {
			height = height.replaceAll("%", "");
			try {
				double calc = Integer.parseInt(height);
				int displayHeight = WebConsole.getConsoleUnit().getConsoleDisplay().getHeight();
				heightInt = (int)Math.round((calc / 100) * displayHeight); 
			} catch (Exception e) {}
		} else if (height.endsWith("px")) {
			height = height.replaceAll("px", "");
			try {
				heightInt = Integer.parseInt(height);
			} catch (Exception e) {}
		}
		setHeight(heightInt);
	}
	
	@Override
	public void setWidth(String width) {
		int widthInt = 0;
		if (width.endsWith("%")) {
			width = width.replaceAll("%", "");
			try {
				double calc = Integer.parseInt(width);
				int displayWidth = WebConsole.getConsoleUnit().getConsoleDisplay().getWidth();
				widthInt = (int)Math.round((calc / 100) * displayWidth); 
			} catch (Exception e) {}
		} else if (width.endsWith("px")) {
			width = width.replaceAll("px", "");
			try {
				widthInt = Integer.parseInt(width);
			} catch (Exception e) {}
		}
		setWidth(widthInt);
	}
	
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
