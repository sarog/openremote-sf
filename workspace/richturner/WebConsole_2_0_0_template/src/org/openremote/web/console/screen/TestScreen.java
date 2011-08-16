package org.openremote.web.console.screen;

import org.openremote.web.console.event.ConsoleUnitEventManager;
import org.openremote.web.console.event.press.PressEndEvent;
import org.openremote.web.console.event.press.PressMoveEvent;
import org.openremote.web.console.event.press.PressStartEvent;
import org.openremote.web.console.widget.Slider;

public class TestScreen extends ConsoleScreen {

	public TestScreen(ConsoleUnitEventManager eventManager) {
		super(eventManager);

		Slider sliderWidget = new Slider();
		
		sliderWidget.addHandler(eventManager.getPressMoveReleaseHandler(), PressStartEvent.getType());
		sliderWidget.addHandler(eventManager.getPressMoveReleaseHandler(), PressMoveEvent.getType());
		sliderWidget.addHandler(eventManager.getPressMoveReleaseHandler(), PressEndEvent.getType());
		
		this.add(sliderWidget);
		this.setWidgetPosition(sliderWidget, 10, 50);
	}
}
