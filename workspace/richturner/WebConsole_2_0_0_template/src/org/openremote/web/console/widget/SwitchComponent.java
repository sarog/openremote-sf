package org.openremote.web.console.widget;

import org.openremote.web.console.client.WebConsole;
import org.openremote.web.console.event.sensor.SensorChangeHandler;
import org.openremote.web.console.panel.entity.ButtonDefault;
import org.openremote.web.console.panel.entity.Link;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Image;

public class SwitchComponent extends InteractiveConsoleComponent implements SensorChangeHandler {
	public static final String CLASS_NAME = "switchComponent";
	private String name;
	private int width;
	private int height;
	private Image onImage;
	private Image offImage;
	private boolean onImageExists = false;
	private boolean offImageExists = false;
	private LabelComponent label;
	
	private SwitchComponent() {
		super(new AbsolutePanel(), CLASS_NAME);
		DOM.setStyleAttribute(getElement(), "overflow", "hidden");
		DOM.setStyleAttribute(getElement(), "whiteSpace", "nowrap");
		DOM.setStyleAttribute(getElement(), "display", "inline-block");
		
		label = new LabelComponent();
		setName("OFF");
		label.setVisible(true);
		// Remove standard style name from label component
		label.removeStyleName("labelComponent");
		
		onImage = new Image();
		onImage.setVisible(false);
		
		offImage = new Image();
		offImage.setVisible(false);
		
		((AbsolutePanel)getWidget()).add(label, 0, 0);
		((AbsolutePanel)getWidget()).add(onImage, 0, 0);
		((AbsolutePanel)getWidget()).add(offImage, 0, 0);
	}
	
	/*
	 * Check images exist so we don't waste time initialising sensor later on if they don't
	 */
	public void onSensorAdd() {
		if (sensor.isValid()) {
			String url = WebConsole.getConsoleUnit().getControllerService().getController().getUrl();
			onImage.addLoadHandler(new LoadHandler() {

				@Override
				public void onLoad(LoadEvent event) {
					onImageExists = true;
				}
			});
			onImage.setUrl(url + "/" + sensor.getMappedValue("on"));
			
			offImage.addLoadHandler(new LoadHandler() {

				@Override
				public void onLoad(LoadEvent event) {
					offImageExists = true;
					onRender(0, 0);
				}
			});
			offImage.setUrl(url + "/" + sensor.getMappedValue("off"));			
		}
	}

	private void setName(String name) {
		this.name = name;
		label.setText(name);
	}
	
	@Override
	public void onRender(int width, int height) {
		if (!isInitialised) {
			label.setWidth(width + "px");
			label.setHeight(height + "px");
			label.onRender(width, height);
		}
		
		if (offImageExists) {
			offImage.setVisible(true);
			label.setVisible(false);
			DOM.setStyleAttribute(getElement(), "background", "none");
		}
		checkSensor();
	}
	
	/*
	 * Only use the sensor if either the on or off image exist
	 */
	private void checkSensor() {
		if (!onImageExists && !offImageExists) {
			sensor = null;
		}
	}
	
	@Override
	public void sensorChanged(String value) {
		if (value.equalsIgnoreCase("on")) {
			offImage.setVisible(false);
			if (onImageExists) {
				onImage.setVisible(true);
			}
		} else if (value.equalsIgnoreCase("off")) {
			onImage.setVisible(false);
			if (offImageExists) {
				offImage.setVisible(true);
			}			
		}
	}
	
	public static ConsoleComponent build(org.openremote.web.console.panel.entity.component.SwitchComponent entity) {
		SwitchComponent component = new SwitchComponent();
		if (entity == null) {
			return component;
		}
		Link link = entity.getLink();
		if (link != null) {
			component.setSensor(new Sensor(entity.getLink()));
		}
		component.setHasControlCommand(true);
		return component;
	}
}
