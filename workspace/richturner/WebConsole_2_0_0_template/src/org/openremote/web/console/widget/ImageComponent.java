package org.openremote.web.console.widget;

import org.openremote.web.console.client.WebConsole;
import com.google.gwt.user.client.ui.Image;

public class ImageComponent extends PassiveConsoleComponent {
	public static final String CLASS_NAME = "imageComponent";
	private Image container;
	
	public ImageComponent() {
		super(new Image());
		container = (Image)this.getWidget();
		setStylePrimaryName(CLASS_NAME);
		//this.setWidth("20px");
		//this.setHeight("20px");
		//DOM.setStyleAttribute(this.getElement(), "border", "2px solid white");
	}
	
	public void setSrc(String src) {
		String controllerUrl = WebConsole.getConsoleUnit().getControllerService().getController().getUrl();
		container.setUrl(controllerUrl + "/" + src);
	}
	
	@Override
	public void onRender() {
		// TODO Auto-generated method stub
		
	}

	public static ConsoleComponent build(org.openremote.web.console.panel.entity.component.ImageComponent entity) {
		ImageComponent component = new ImageComponent();
		component.setSrc(entity.getSrc());
		return component;
	}

}
