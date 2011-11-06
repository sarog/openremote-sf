package org.openremote.web.console.widget;

import org.openremote.web.console.client.WebConsole;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.user.client.ui.Image;

public class ImageComponent extends PassiveConsoleComponent {
	public static final String CLASS_NAME = "imageComponent";
	private boolean srcExists = false;
	private String src;
	
	public ImageComponent() {
		super(new Image(), CLASS_NAME);
		((Image)getWidget()).addLoadHandler(new LoadHandler() {

			@Override
			public void onLoad(LoadEvent event) {
				srcExists = true;
				show();
			}
		});
	}
	
	public void setSrc(String src) {
		String url = WebConsole.getConsoleUnit().getControllerService().getController().getUrl();
		url += "/" + src;
		this.src = url;
		((Image)getWidget()).setUrl(url);
	}
	
	@Override
	public void onRender(int width, int height) {
		show();
	}

	public boolean srcExists() {
		return srcExists;
	}
	
	public void show() {
		if (srcExists) {
			setVisible(true);
		} else {
			hide();
		}
	}
	
	public void hide() {
		setVisible(false);
	}
	
	public static ConsoleComponent build(org.openremote.web.console.panel.entity.component.ImageComponent entity) {
		ImageComponent component = new ImageComponent();
		component.setSrc(entity.getSrc());
		return component;
	}
}
