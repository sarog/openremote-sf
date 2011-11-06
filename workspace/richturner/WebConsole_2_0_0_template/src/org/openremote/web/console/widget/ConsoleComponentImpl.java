package org.openremote.web.console.widget;


import java.util.List;

import org.openremote.web.console.client.WebConsole;
import org.openremote.web.console.event.sensor.SensorChangeEvent;
import org.openremote.web.console.event.sensor.SensorChangeHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public abstract class ConsoleComponentImpl extends Composite implements ConsoleComponent {
	protected boolean isInitialised = false;
	protected Sensor sensor;
	protected Integer id;
	protected int width;
	protected int height;
	
	protected ConsoleComponentImpl(Widget container, String className) {
		initWidget(container);
		setVisible(false);
		this.setStylePrimaryName(className);
		addStyleName("consoleWidget");
		DOM.setStyleAttribute(container.getElement(), "WebkitUserSelect", "none");
		DOM.setStyleAttribute(container.getElement(), "MozUserSelect", "none");
		DOM.setStyleAttribute(container.getElement(), "cursor", "pointer");
	}
	
	public void setSensor(Sensor sensor) {
		this.sensor = sensor;
		SensorChangeHandler sensorHandler = (SensorChangeHandler) this;
		sensorHandler.onSensorAdd();
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
	
	public void onAdd(int width, int height) {
		// Call Widgets onRender Method and then display it
		setVisible(true);
		// Set container to explicit size to avoid any issues using percentages
		this.width = width;
		this.height = height;
		setWidth(width + "px");
		setHeight(height + "px");
		onRender(width, height);
		isInitialised = true;
		
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
			WebConsole.getConsoleUnit().registerSensorHandler((SensorChangeHandler) this);
		}
	}
	
	public void onRemove() {
		if (this instanceof InteractiveConsoleComponent) {
			InteractiveConsoleComponent thisWidget = (InteractiveConsoleComponent) this;
			thisWidget.unRegisterHandlers();
		}
		if (sensor != null && this instanceof SensorChangeHandler) {
			WebConsole.getConsoleUnit().unRegisterSensorHandler((SensorChangeHandler) this);
		}
	}
	
	public void onSensorChange(SensorChangeEvent event) {
		SensorChangeHandler sensorHandler = (SensorChangeHandler) this;
		if (sensor != null && sensor.sensorRef == event.getSensorId()) {
			sensorHandler.sensorChanged(event.getSensorValue());
		}
	}
}
