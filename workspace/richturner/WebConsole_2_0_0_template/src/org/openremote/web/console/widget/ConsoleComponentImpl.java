package org.openremote.web.console.widget;

import java.util.ArrayList;
import java.util.List;
import org.openremote.web.console.event.ConsoleUnitEventManager;
import org.openremote.web.console.event.sensor.SensorChangeEvent;
import org.openremote.web.console.event.sensor.SensorChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
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
	
	protected ConsoleComponentImpl(Widget container, String className) {
		initWidget(container);
		setVisible(false);
		this.setStylePrimaryName(className);
		addStyleName("consoleWidget");
		DOM.setStyleAttribute(container.getElement(), "WebkitUserSelect", "none");
		DOM.setStyleAttribute(container.getElement(), "MozUserSelect", "none");
		DOM.setStyleAttribute(container.getElement(), "KhtmlUserSelect", "none");
		DOM.setStyleAttribute(container.getElement(), "OUserSelect", "none");
		DOM.setStyleAttribute(container.getElement(), "UserSelect", "none");
		DOM.setStyleAttribute(container.getElement(), "cursor", "pointer");
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
			if (sensor != null && this instanceof SensorChangeHandler) {
				SensorChangeHandler component = (SensorChangeHandler) this;
				HandlerManager eventBus = ConsoleUnitEventManager.getInstance().getEventBus();
				registerHandler(eventBus.addHandler(SensorChangeEvent.getType(), component));
			}
		}
	}
	
	public void onRemove() {
		if (this instanceof InteractiveConsoleComponent) {
			InteractiveConsoleComponent thisWidget = (InteractiveConsoleComponent) this;
			thisWidget.unRegisterHandlers();
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
}
