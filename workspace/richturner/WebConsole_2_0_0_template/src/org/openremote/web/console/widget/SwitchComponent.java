package org.openremote.web.console.widget;

import java.util.LinkedHashMap;
import java.util.Map;
import org.openremote.web.console.client.WebConsole;
import org.openremote.web.console.event.sensor.SensorChangeHandler;
import org.openremote.web.console.event.tap.TapEvent;
import org.openremote.web.console.event.tap.TapHandler;
import org.openremote.web.console.event.ui.CommandSendEvent;
import org.openremote.web.console.event.ui.NavigateEvent;
import org.openremote.web.console.util.ImageContainer;
import org.openremote.web.console.util.ImageLoadedCallback;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Image;

public class SwitchComponent extends InteractiveConsoleComponent implements SensorChangeHandler, TapHandler {
	public static final String CLASS_NAME = "switchComponent";
	public static final int LABEL_FONT_SIZE = 12;
	private LabelComponent label;
	private ImageContainer currentImage;
	private String state;
	private Map<String, ImageContainer> stateImageMap = new LinkedHashMap<String, ImageContainer>();
	private ImageLoadedCallback loadedCallback = new ImageLoadedCallback() {
		@Override
		public void onImageLoaded(ImageContainer container) {
			if (container != null) {
				if (currentImage == container) {
					setImage(container);
				}
			}
		}
	};
	
	
	private SwitchComponent() {
		super(new AbsolutePanel(), CLASS_NAME);
		DOM.setStyleAttribute(getElement(), "overflow", "hidden");
		DOM.setStyleAttribute(getElement(), "whiteSpace", "nowrap");
		DOM.setStyleAttribute(getElement(), "display", "inline-block");
		
		label = new LabelComponent();
		label.setFontSize(LABEL_FONT_SIZE);
		label.setVisible(true);
		label.removeStyleName("labelComponent");
		((AbsolutePanel)getWidget()).add(label, 0, 0);
	}
	
	private void setImage(ImageContainer container) {
		boolean showLabel = true;
		
		if (currentImage != null) {
			currentImage.setVisible(false);
			currentImage = null;
		}
		
		if (container != null) {
			if (container.getExists()) {
				showLabel = false;
				container.setVisible(true);	
			}
			currentImage = container;
		}
		
		label.setVisible(showLabel);
		if	(showLabel) {
			getElement().getStyle().clearBackgroundColor();
			getElement().getStyle().clearBackgroundImage();
		} else {
			DOM.setStyleAttribute(getElement(), "backgroundColor", "transparent");
			DOM.setStyleAttribute(getElement(), "backgroundImage", "none");
		}
	}
	
	private String getSendCommand() {
		String sendCommand = "off";
		if (state.equalsIgnoreCase("off")) {
			sendCommand = "on";
		}
		return sendCommand;
	}
	
	// ---------------------------------------------------------------------------------
	//			SUPER CLASS OVERRIDES BELOW
	// ---------------------------------------------------------------------------------
	
	@Override
	public void onRender(int width, int height) {
		if (!isInitialised) {
			label.setWidth(width + "px");
			label.setHeight(height + "px");
			DOM.setStyleAttribute(label.getElement(), "lineHeight", height + "px");
			sensorChanged("off");
		}
	}

	@Override
	public void onUpdate(int width, int height) {
		label.onUpdate(width, height);
	}
	
	@Override
	public void onSensorAdd() {
		Map<String, String> map = sensor.getStateMap();
		for (String name : map.keySet()) {
			String value = map.get(name);
			String url = WebConsole.getConsoleUnit().getControllerService().getController().getUrl();
			url += value;
			ImageContainer container = WebConsole.getConsoleUnit().getImageFromCache(url);
			container.addCallback(loadedCallback);
			stateImageMap.put(name, container);
			if (container != null) {
				((AbsolutePanel)getWidget()).add(container.getImage(), 0, 0);
			}
		}
	}
	
	@Override
	public void sensorChanged(String value) {
		if (!value.equalsIgnoreCase("off") && !value.equalsIgnoreCase("on")) {
			try {
				int numValue = Integer.parseInt(value);
				numValue = numValue > 0 ? 1 : 0;
				value = numValue == 0 ? "off" : "on";
			} catch (Exception e) {}
		}
		if (value.equalsIgnoreCase("off") || value.equalsIgnoreCase("on")) {
			state = value;
			label.setText(value.toUpperCase());
			setImage(stateImageMap.get(value));
		}
	}
	
	@Override
	public void onTap(TapEvent event) {
		if (navigate != null) {
			eventBus.fireEvent(new NavigateEvent(navigate));
		} else if (hasControlCommand) {
			eventBus.fireEvent(new CommandSendEvent(getId(), getSendCommand(), this));
		}
	}
	
	@Override
	public void onCommandSendResponse(Boolean success, String command) {
		// Update the state of the switch if send command was a success
		if (success) {
			sensorChanged(command);
		}
	}
	
	// ---------------------------------------------------------------------------------
	//			BUILD METHOD BELOW HERE
	// ---------------------------------------------------------------------------------
	
	public static ConsoleComponent build(org.openremote.web.console.panel.entity.component.SwitchComponent entity) {
		SwitchComponent component = new SwitchComponent();
		if (entity == null) {
			return component;
		}
		component.setId(entity.getId());
		component.setSensor(new Sensor(entity.getLink()));
		component.setHasControlCommand(true);
		return component;
	}
}
