package org.openremote.web.console.screen;

import org.openremote.web.console.event.ConsoleUnitEventManager;
import org.openremote.web.console.widget.Slider;

public class TestScreen extends ConsoleScreen {

	public TestScreen() {
		Slider sliderWidget = new Slider(40, 300);
		sliderWidget.initialise();
		
		this.add(sliderWidget);
		this.setWidgetPosition(sliderWidget, 50, 50);
	}
}
