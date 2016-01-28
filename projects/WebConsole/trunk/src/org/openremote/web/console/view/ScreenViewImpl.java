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
package org.openremote.web.console.view;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openremote.web.console.client.WebConsole;
import org.openremote.web.console.panel.entity.AbsolutePosition;
import org.openremote.web.console.panel.entity.Background;
import org.openremote.web.console.panel.entity.DataValuePairContainer;
import org.openremote.web.console.unit.ConsoleUnit;
import org.openremote.web.console.util.ImageContainer;
import org.openremote.web.console.util.ImageLoadedCallback;
import org.openremote.web.console.widget.ConsoleComponent;
import org.openremote.web.console.widget.ConsoleComponentImpl;
import org.openremote.web.console.widget.Sensor;
import org.openremote.web.console.widget.panel.PanelComponent;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Widget;
/**
 * Defines a set of widgets that form a specific
 * screen for display on the console display
 *  
 * @author <a href="mailto:richard@openremote.org">Richard Turner</a>
 */
public class ScreenViewImpl extends ConsoleComponentImpl implements ScreenView {
	Set<PanelComponent> panelComponents = new HashSet<PanelComponent>();
	public static final String CLASS_NAME = "screenView";
	private ImageContainer background;
	private Background backgroundEntity;
	private Set<Integer> sensorIds = new HashSet<Integer>();
	private Boolean isLandscape = false;
	
	public ScreenViewImpl() {
		super(new AbsolutePanel(), CLASS_NAME);
		setWidth("100%");
		setHeight("100%");
	}
	
	public void addPanelComponent(PanelComponent component) {
		if (component == null) return;
		Set<Sensor> sensors = component.getSensors();
		if (sensors != null) {
			for (Sensor sensor : sensors) {
				if (sensor != null) { 
					sensorIds.add(sensor.getSensorRef());
				}
			}
		}
		addToScreen(component);
	}

	@Override
	public Set<PanelComponent> getPanelComponents() {
		return panelComponents;
	}
	
	/**
	 * Add specified component to the Screen View Panel
	 */
	private void addToScreen(PanelComponent component) {
		panelComponents.add(component);
		
		// Just add it at 0,0 as onRender will sort actual position
		getWidget().add((Widget) component, 0, 0);
	}
	
	/**
	 * Adjust specified widgets position
	 * @param widget
	 * @param left
	 * @param top
	 */
	public void setConsoleWidgetPosition(ConsoleComponent widget, int left, int top) {
		if (((AbsolutePanel)getWidget()).getWidgetIndex((Widget)widget) >= 0) {
			((AbsolutePanel)getWidget()).setWidgetPosition((Widget)widget, left, top);
		}		
	}
	
	public AbsolutePanel getWidget() {
		return (AbsolutePanel)super.getWidget();
	}
	
	public void setBackground(Background entity) {
		if (entity == null) {
			return;
		}
		backgroundEntity = entity;
		// Get Image Container from cache
		if (entity.getImage() != null) {
			ConsoleUnit unit = WebConsole.getConsoleUnit();
			background = unit.getImageFromCache(unit.getControllerService().getController().getUrl() + entity.getImage().getSrc());
			if (background != null) {
				((AbsolutePanel)getWidget()).add(background.getImage(), 0, 0);
				DOM.setStyleAttribute(background.getImage().getElement(),"zIndex", "-1");
			}
		}
	}
	
	private void processBackgroundImage() {
		if (background == null) return;
		
		if (background.getLoadAttempted()) {
			if (!background.getExists()) {
				return;
			}
		} else {
			background.addCallback(new ImageLoadedCallback() {
				@Override
				public void onImageLoaded(ImageContainer container) {
					processBackgroundImage();
				}
			});
			return;
		}
			
		//background.setVisible(true);
		DOM.setStyleAttribute(getElement(), "backgroundImage", "url(" + background.getUrl() + ")");
		DOM.setStyleAttribute(getElement(), "backgroundRepeat", "no-repeat");
		
		if (backgroundEntity.getFillScreen() != null && backgroundEntity.getFillScreen()) {
			DOM.setStyleAttribute(getElement(), "backgroundSize", "cover");
		} else if (backgroundEntity.getRelative() != null) {
			String position = backgroundEntity.getRelative().toLowerCase();
			String[] posArr = position.split("_");
			if (posArr.length == 2) {
				position = posArr[1] + " " + posArr[0];
			} else {
				position = posArr[0];
			}
			DOM.setStyleAttribute(getElement(), "backgroundPosition", position);
		} else if (backgroundEntity.getAbsolute() != null) {
			String absPos = backgroundEntity.getAbsolute();
			if (absPos != null && !absPos.isEmpty()) {
			  String[] strArr = absPos.split(",");
			  int top = Integer.parseInt(strArr[1].trim());
			  int left = Integer.parseInt(strArr[0].trim());
			  DOM.setStyleAttribute(getElement(), "backgroundPosition", left + " " + top);
			}
		}
	}
	
	@Override
	public void onAdd(int width, int height) {
		onAdd(width, height, null);
	}
	
	@Override
	public void onAdd(int width, int height, List<DataValuePairContainer> data) {
		setVisible(true);
		onRender(width, height, data);
	}
	
	@Override
	public void onRender(int width, int height) {
		onRender(width, height, null);
	}
	
	public void onRender(int width, int height, List<DataValuePairContainer> data) {
		this.width = width;
		this.height = height;
		
		processBackgroundImage();
		
		for (PanelComponent component : panelComponents) {
			component.onAdd((AbsolutePanel)this.getWidget(), width, height, data);
		}
	}
	
	public void onRefresh(int width, int height) {
		onUpdate(width, height);
	}
	
	public void onUpdate(int width, int height) {
		this.width = width;
		this.height = height;
		
		processBackgroundImage();
		
		for (PanelComponent component : panelComponents) {
			component.onRefresh(width, height);
		}
	}
	
	@Override
	public void onRemove() {
		for (PanelComponent component : panelComponents) {
			((ConsoleComponent)component).onRemove();
		}
	}
	
	@Override
	public Set<Integer> getSensorIds() {
		return sensorIds;
	}

	@Override
	public void setIsLandscape(Boolean isLandscape) {
		this.isLandscape = isLandscape;
	}

	@Override
	public Boolean isLandscape() {
		return isLandscape;
	}
}
