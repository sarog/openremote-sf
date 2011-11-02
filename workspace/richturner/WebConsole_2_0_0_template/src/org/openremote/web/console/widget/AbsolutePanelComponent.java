package org.openremote.web.console.widget;

import org.openremote.web.console.client.WebConsole;
import org.openremote.web.console.unit.ConsoleDisplay;

import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class AbsolutePanelComponent extends PassiveConsoleComponent implements Positional {
	private static final String CLASS_NAME = "absolutePanelComponent";
	private SimplePanel container;
	private ConsoleComponent component;
	private HorizontalPanel componentContainer;
	private int left;
	private int top;
	private int height;
	private int width;
	
	public AbsolutePanelComponent() {
		super(new SimplePanel());
		container = (SimplePanel)this.getWidget();
		container.setStylePrimaryName(CLASS_NAME);
		
		componentContainer = new HorizontalPanel();
		componentContainer.setWidth("100%");
		componentContainer.setHeight("100%");
		
		componentContainer.setHorizontalAlignment(HasAlignment.ALIGN_CENTER);
		componentContainer.setVerticalAlignment(HasAlignment.ALIGN_MIDDLE);
		
		container.add(componentContainer);
	}

	@Override
	// Pass size info to widget so explicit size can be set to avoid any cross browser rendering issues
	public void onRender(int width, int height) {
		if (component != null) {
			component.onAdd(this.width, this.height);
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
				int displayWidth = WebConsole.getConsoleUnit().getConsoleDisplay().getWidth();
				heightInt = (int)Math.round((calc / 100) * displayWidth); 
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
	}}
