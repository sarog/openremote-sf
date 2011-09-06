package org.openremote.web.console.screen.view;

import org.openremote.web.console.widget.Slider;

public class TestScreenView extends ScreenView {

	public TestScreenView() {
		Slider sliderWidget = new Slider(300, 50);
		sliderWidget.initialise();
		sliderWidget.setValue(50);
		this.add(sliderWidget);
		this.setWidgetPosition(sliderWidget, 50, 50);
	}
}
