package org.openremote.web.console.screen;

import org.openremote.web.console.event.ConsoleUnitEventManager;
import org.openremote.web.console.widget.Slider;

public class TestScreen extends ConsoleScreen {

	public TestScreen(ConsoleUnitEventManager eventManager) {
		super(eventManager);

		Slider sliderWidget = new Slider(40, 300);
		sliderWidget.initialise(eventManager);
		
		this.add(sliderWidget);
		this.setWidgetPosition(sliderWidget, 50, 50);
	}
}
