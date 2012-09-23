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
package org.openremote.web.console.widget;

import java.util.LinkedHashMap;
import java.util.Map;

import org.openremote.web.console.client.WebConsole;
import org.openremote.web.console.event.sensor.SensorChangeHandler;
import org.openremote.web.console.util.ImageContainer;
import org.openremote.web.console.util.ImageLoadedCallback;

import com.google.gwt.user.client.ui.AbsolutePanel;
/**
 * 
 *  
 * @author <a href="mailto:richard@openremote.org">Richard Turner</a>
 */
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
				} else if (initialImage == container && currentImage == null) {
					initialImage.setVisible(true);
				}
			}
		}
	};
	
	public ImageComponent() {
		super(new AbsolutePanel(), CLASS_NAME);
	}
	
	public void setInitialImage(String src) {
		String url = WebConsole.getConsoleUnit().getControllerService().getController().getUrl();
		url += src;
		initialImage = WebConsole.getConsoleUnit().getImageFromCache(url);
		initialImage.addCallback(loadedCallback);
//		initialImage.setUrl(url);
//		initialImage.getImage().addLoadHandler(new LoadHandler() {
//			@Override
//			public void onLoad(LoadEvent event) {
//				initialImage.setExists(true);
//				initialImage.setVisible(true);
//			}
//		});
		if (initialImage != null) {
			((AbsolutePanel)getWidget()).add(initialImage.getImage(), 0, 0);
		}
	}
	
	private void showInitialImage() {
		if (initialImage == null) return;
		
		if (initialImage.getLoadAttempted()) {
			if (!initialImage.getExists()) {
				return;
			}
		} else {
			initialImage.addCallback(new ImageLoadedCallback() {
				@Override
				public void onImageLoaded(ImageContainer container) {
					showInitialImage();
				}
			});
			return;
		}
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
	
	// ---------------------------------------------------------------------------------
	//			SUPER CLASS OVERRIDES BELOW
	// ---------------------------------------------------------------------------------
	
	@Override
	public void onRender(int width, int height) {
		showInitialImage();
	}
	
	@Override
	public void onUpdate(int width, int height) {
		
	}
	
	@Override
	public void sensorChanged(String value) {
		if (!value.equalsIgnoreCase(state)) {
			state = value;
			setImage(stateImageMap.get(value));
		}
	}
	
	// ---------------------------------------------------------------------------------
	//			BUILD METHOD BELOW HERE
	// ---------------------------------------------------------------------------------
	
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
