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
import org.openremote.web.console.widget.panel.PanelComponent;
import org.openremote.web.console.widget.Sensor;
import org.openremote.web.console.widget.panel.Positional;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
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
		int top = 0;
		int left = 0;
	
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
			
		if (backgroundEntity.getFillScreen() != null && backgroundEntity.getFillScreen()) {
			background.getImage().setWidth("100%");
			background.getImage().setHeight("100%");
		} else if (backgroundEntity.getRelative() != null) {
			int rectLeft = 0;
			int rectTop = 0;
			String position = backgroundEntity.getRelative();
			if (position.contains("RIGHT")) {
				rectLeft = background.getNativeWidth() - width;
			} else if (position.contains("CENTER")) {
				rectLeft = (int) Math.round(((double)background.getNativeWidth() - width)/2);
				rectTop = (int) Math.round(((double)background.getNativeHeight() - height)/2);
			}
			if (position.contains("BOTTOM")) {
				rectTop = background.getNativeHeight() - height;
			}
			if (position.equals("BOTTOM")) {
				rectLeft = (int) Math.round(((double)background.getNativeWidth() - width)/2);
			}
			background.getImage().setVisibleRect(rectLeft, rectTop, width, height);
		} else if (backgroundEntity.getAbsolute() != null) {
			AbsolutePosition absPos = backgroundEntity.getAbsolute();
			top = absPos.getTop();
			left = absPos.getLeft();
		} else {
			((AbsolutePanel)getWidget()).remove(background.getImage());
			background = null;
			return;
		}
		
		((AbsolutePanel)getWidget()).add(background.getImage(), left, top);		
		
		background.setVisible(true);
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
