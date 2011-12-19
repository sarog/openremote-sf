package org.openremote.web.console.widget;

import java.util.LinkedHashMap;
import java.util.Map;
import org.openremote.web.console.client.WebConsole;
import org.openremote.web.console.event.sensor.SensorChangeHandler;
import org.openremote.web.console.util.ImageContainer;
import org.openremote.web.console.util.ImageLoadedCallback;

import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Image;

public class ImageComponent extends PassiveConsoleComponent implements SensorChangeHandler {
	public static final String CLASS_NAME = "imageComponent";
	private String state;
	private ImageContainer initialImage;
	private ImageContainer currentImage;
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
	
	public ImageComponent() {
		super(new AbsolutePanel(), CLASS_NAME);
		initialImage = new ImageContainer(new Image(), loadedCallback);
	}
	
	public void setInitialImage(String src) {
		String url = WebConsole.getConsoleUnit().getControllerService().getController().getUrl();
		url += "/" + src;
		initialImage.setUrl(url);
		initialImage.getImage().addLoadHandler(new LoadHandler() {
			@Override
			public void onLoad(LoadEvent event) {
				initialImage.setExists(true);
				initialImage.setVisible(true);
			}
		});
		((AbsolutePanel)getWidget()).add(initialImage.getImage(), 0, 0);
	}
	
	@Override
	public void onRender(int width, int height) {
		//initialImage.setVisible(true);
	}

	@Override
	public void onSensorAdd() {
		Map<String, String> map = sensor.getStateMap();
		for (String name : map.keySet()) {
			String value = map.get(name);
			String url = WebConsole.getConsoleUnit().getControllerService().getController().getUrl();
			url += "/" + value;
			ImageContainer container = new ImageContainer(new Image(url), loadedCallback);
			stateImageMap.put(name, container);
			((AbsolutePanel)getWidget()).add(container.getImage(), 0, 0);
		}
	}

	@Override
	public void sensorChanged(String value) {
		if (!value.equalsIgnoreCase(state)) {
			state = value;
			setImage(stateImageMap.get(value));
		}
	}
	
	private void setImage(ImageContainer container) {
		boolean showInitialImage = true;
		if (container != null) {
			if (container.getExists()) {
				if (currentImage != null) {
					currentImage.setVisible(false);
				}
				showInitialImage = false;
				container.setVisible(true);	
			}
			currentImage = container;
		} else {
			if (currentImage != null) {
				currentImage.setVisible(false);
				currentImage = null;
			}
		}
		initialImage.setVisible(showInitialImage);
	}
	
	public static ConsoleComponent build(org.openremote.web.console.panel.entity.component.ImageComponent entity) {
		ImageComponent component = new ImageComponent();
		if (entity == null) {
			return component;
		}
		component.setId(entity.getId());
		component.setInitialImage(entity.getSrc());
		component.setSensor(new Sensor(entity.getLink()));
		return component;
	}
}
