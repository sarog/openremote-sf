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

import java.util.ArrayList;
import java.util.List;

import org.openremote.web.console.event.ConsoleUnitEventManager;
import org.openremote.web.console.event.sensor.SensorChangeEvent;
import org.openremote.web.console.event.sensor.SensorChangeHandler;
import org.openremote.web.console.event.ui.BindingDataChangeEvent;
import org.openremote.web.console.event.ui.BindingDataChangeHandler;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public abstract class ConsoleComponentImpl extends Composite implements ConsoleComponent {
	protected boolean isInitialised = false;
	protected Sensor sensor;
	protected Integer id;
	protected int width;
	protected int height;
	protected List<HandlerRegistration> handlerRegistrations = new ArrayList<HandlerRegistration>();
	protected boolean handlersRegistered = false;
	protected boolean dataBindingActive = false;
	
	protected ConsoleComponentImpl(Widget container, String className) {
		initWidget(container);
		setVisible(false);
		this.setStylePrimaryName(className);
		addStyleName("consoleWidget");
		getElement().setAttribute("onselect", "return false;");
//		DOM.setStyleAttribute(container.getElement(), "cursor", "pointer");
	}
	
	public void setSensor(Sensor sensor) {
		if (sensor != null && sensor.isValid()) {
			this.sensor = sensor;
			if (this instanceof SensorChangeHandler) {
				SensorChangeHandler sensorHandler = (SensorChangeHandler) this;
				sensorHandler.onSensorAdd();
			}
		}
	}
	
	public Sensor getSensor() {
		return sensor;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public Integer getId() {
		return id;
	}
	
	protected void initHandlers() {
		// Check that handlers have been registered if interactive if not register them on the top level widget
		if (this instanceof InteractiveConsoleComponent) {
			InteractiveConsoleComponent thisWidget = (InteractiveConsoleComponent) this;
			List<Widget> interactiveChildren = thisWidget.getInteractiveChildren();
			if (interactiveChildren.size() > 0) {
				for (Widget interactiveChild : interactiveChildren) {
					thisWidget.registerHandlers(interactiveChild);
				}
			} else {
				thisWidget.registerHandlers();
			}
		}
		
		// Initialise sensor if it is defined and this is an instance of Sensor Change Handler
		if (sensor != null && this instanceof SensorChangeHandler) {
			SensorChangeHandler component = (SensorChangeHandler) this;
			HandlerManager eventBus = ConsoleUnitEventManager.getInstance().getEventBus();
			registerHandler(eventBus.addHandler(SensorChangeEvent.getType(), component));
		}
		
		// Attach Data Binding Change Handler
		if (this instanceof BindingDataChangeHandler) {
			BindingDataChangeHandler component = (BindingDataChangeHandler) this;
			HandlerManager eventBus = ConsoleUnitEventManager.getInstance().getEventBus();
			registerHandler(eventBus.addHandler(BindingDataChangeEvent.getType(), component));
		}
	}
	
	public void onSensorChange(SensorChangeEvent event) {
		SensorChangeHandler sensorHandler = (SensorChangeHandler) this;
		if (sensor != null && sensor.sensorRef == event.getSensorId()) {
			sensorHandler.sensorChanged(event.getNewValue());
		}
	}
	
	protected void unRegisterHandlers() {
		for (HandlerRegistration handler : handlerRegistrations) {
			handler.removeHandler();
		}
		handlerRegistrations.clear();
		handlersRegistered = false;
	}
	
	protected void registerHandler(HandlerRegistration registration) {
		handlerRegistrations.add(registration);
		handlersRegistered = true;
	}
	
	// ---------------------------------------------------------------------------------
	//			SUPER CLASS OVERRIDES BELOW
	// ---------------------------------------------------------------------------------
	
	@Override
	public void onAdd(int width, int height) {
		// Set container to explicit size to avoid any issues using percentages
		this.width = width;
		this.height = height;
		setWidth(width + "px");
		setHeight(height + "px");
		onRender(width, height);
		setVisible(true);
		isInitialised = true;
		
		initHandlers();
	}
	
	@Override
	public void onRefresh(int width, int height) {
		this.width = width;
		this.height = height;
		setWidth(width + "px");
		setHeight(height + "px");
		onUpdate(width, height);
	}
	
	@Override
	public void onRemove() {
		unRegisterHandlers();
	}
}
