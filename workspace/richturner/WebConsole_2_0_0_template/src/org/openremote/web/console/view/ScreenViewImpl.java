package org.openremote.web.console.view;

import java.util.HashSet;
import java.util.Set;
import org.openremote.web.console.client.WebConsole;
import org.openremote.web.console.panel.entity.Background;
import org.openremote.web.console.widget.ConsoleComponent;
import org.openremote.web.console.widget.ConsoleComponentImpl;
import org.openremote.web.console.widget.panel.PanelComponent;
import org.openremote.web.console.widget.Sensor;
import org.openremote.web.console.widget.panel.Positional;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

/**
 * Defines a set of widgets that form a specific
 * screen for display on the console display
 * @author rich
 *
 */
public class ScreenViewImpl extends ConsoleComponentImpl implements ScreenView {
	Set<PanelComponent> panelComponents = new HashSet<PanelComponent>();
	public static final String CLASS_NAME = "screenView";
	private Image background;
	private Set<Integer> sensorIds = new HashSet<Integer>();
	
	public ScreenViewImpl() {
		super(new AbsolutePanel(), CLASS_NAME);
		setWidth("100%");
		setHeight("100%");
	}
	
	public void addPanelComponent(PanelComponent component) {
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
		int left = 0;
		int top = 0;
		panelComponents.add(component);
		
		if (component instanceof Positional) {
			Positional positional = (Positional) component; 
			left = positional.getLeft();
			top = positional.getTop();
		}
		((AbsolutePanel)getWidget()).add((Widget) component, left, top);
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

	@Override
	public void onAdd(int width, int height) {
		setVisible(true);
		onRender(width, height);
	}
	
	@Override
	public void onRender(int width, int height) {
		for (PanelComponent component : panelComponents) {
			((ConsoleComponent)component).onAdd(width, height);
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
	
	public void setBackground(Background entity) {
		if (entity == null) {
			return;
		}
		background = new Image();
		background.setVisible(false);
		DOM.setStyleAttribute(background.getElement(), "WebkitUserSelect", "none");
		DOM.setStyleAttribute(background.getElement(), "MozUserSelect", "none");
		DOM.setStyleAttribute(background.getElement(), "KhtmlUserSelect", "none");
		DOM.setStyleAttribute(background.getElement(), "OUserSelect", "none");
		DOM.setStyleAttribute(background.getElement(), "UserSelect", "none");
		
		String url = WebConsole.getConsoleUnit().getControllerService().getController().getUrl();
		background.setUrl(url + "/" + entity.getImage().getSrc());
		background.addLoadHandler(new LoadHandler() {
			@Override
			public void onLoad(LoadEvent event) {
				background.setVisible(true);
			}
		});
		background.addDomHandler(new MouseDownHandler() {
			@Override
			public void onMouseDown(MouseDownEvent event) {
				event.preventDefault();				
			}}, MouseDownEvent.getType());
		if (entity.getFillScreen()) {
			background.setWidth("100%");
			background.setHeight("100%");
		}
		((AbsolutePanel)getWidget()).add(background, 0, 0);
	}
}
