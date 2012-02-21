package org.openremote.web.console.widget;

import org.openremote.web.console.client.WebConsole;
import org.openremote.web.console.event.press.PressCancelEvent;
import org.openremote.web.console.event.press.PressCancelHandler;
import org.openremote.web.console.event.press.PressEndEvent;
import org.openremote.web.console.event.press.PressEndHandler;
import org.openremote.web.console.event.press.PressStartEvent;
import org.openremote.web.console.event.press.PressStartHandler;
import org.openremote.web.console.event.tap.TapEvent;
import org.openremote.web.console.event.tap.TapHandler;
import org.openremote.web.console.event.ui.CommandSendEvent;
import org.openremote.web.console.event.ui.NavigateEvent;
import org.openremote.web.console.panel.entity.ButtonDefault;
import org.openremote.web.console.util.BrowserUtils;
import org.openremote.web.console.util.ImageContainer;
import org.openremote.web.console.util.ImageLoadedCallback;

import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

public class ButtonComponent extends InteractiveConsoleComponent implements PressStartHandler, PressEndHandler, PressCancelHandler, TapHandler, LoadHandler {
	public static final String CLASS_NAME = "buttonComponent";
	public static final int LABEL_FONT_SIZE = 12;
	private String name;
	private Label label; 
	private Image img;
	private boolean srcExists = false;
	
	protected ButtonComponent() {
		super(new AbsolutePanel(), CLASS_NAME);
		DOM.setStyleAttribute(getElement(), "whiteSpace", "nowrap");
		DOM.setStyleAttribute(getElement(), "display", "inline-block");
		
		label = new Label();
		DOM.setStyleAttribute(label.getElement(), "fontSize", LABEL_FONT_SIZE + "px");
		label.setWidth("100%");
		label.setHeight("100%");
		label.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		
		img = new Image();
		img.setWidth("100%");
		img.setHeight("100%");
		img.addLoadHandler(new LoadHandler() {

			@Override
			public void onLoad(LoadEvent event) {
				srcExists = true;
				showImage();
			}
		});
		
		((AbsolutePanel)getWidget()).add(label);
		((AbsolutePanel)getWidget()).add(img);
	}
	
	public void setName(String name) {
		this.name = name;		
		if (!isInitialised) {
			return;
		}
		this.name = BrowserUtils.limitStringLength(name, width, LABEL_FONT_SIZE);
		label.setText(this.name);
	}
	
	public void setImage(String src) {
		String url = WebConsole.getConsoleUnit().getControllerService().getController().getUrl();
		url += "/" + src;
		img.setUrl(url);
	}
	
	public void showImage() {
		if (srcExists) {
			img.setVisible(true);
			label.setVisible(false);			
			DOM.setStyleAttribute(getElement(), "backgroundColor", "transparent");
			DOM.setStyleAttribute(getElement(), "backgroundImage", "none");
		} else {
			hideImage();
		}
	}
	
	public void hideImage() {
		img.setVisible(false);
		label.setVisible(true);
		getElement().getStyle().clearBackgroundColor();
		getElement().getStyle().clearBackgroundImage();
	}
	
	@Override
	public void onRender(int width, int height) {
		label.setHeight(height + "px");
		isInitialised = true;
		setName(name);
		DOM.setStyleAttribute(label.getElement(), "lineHeight", height + "px");
		showImage();
	}

	@Override
	public void onPressCancel(PressCancelEvent event) {
		this.removeStyleName("pressed");
	}

	@Override
	public void onPressEnd(PressEndEvent event) {
		this.removeStyleName("pressed");
	}

	@Override
	public void onPressStart(PressStartEvent event) {
		this.addStyleName("pressed");
	}
	
	@Override
	public void onTap(TapEvent event) {
		if (navigate != null) {
			eventBus.fireEvent(new NavigateEvent(navigate));
		} else if (hasControlCommand) {
			eventBus.fireEvent(new CommandSendEvent(getId(), "click", this));
		}
	}
	
	@Override
	public void onLoad(LoadEvent event) {
		srcExists = true;
		showImage();
	}
	
	public static ConsoleComponent build(org.openremote.web.console.panel.entity.component.ButtonComponent entity) {
		ButtonComponent component = new ButtonComponent();
		
		if (entity == null) {
			return component;
		}
		component.setId(entity.getId());
		component.setName(entity.getName());
		ButtonDefault buttonDefault = entity.getDefault();
		Boolean hasControl = entity.getHasControlCommand();
		if (buttonDefault != null) {
			component.setImage(buttonDefault.getImage().getSrc());
		}
		if (hasControl != null && hasControl) {
			component.setHasControlCommand(hasControl);
		}
		component.setNavigate(entity.getNavigate());
		return component;
	}
}
