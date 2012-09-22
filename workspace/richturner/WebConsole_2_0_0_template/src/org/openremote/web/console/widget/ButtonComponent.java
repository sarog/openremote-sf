/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2012, OpenRemote Inc.
*
* See the contributors.txt file in the distribution for a
* full listing of individual contributors.
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as
* published by the Free Software Foundation, either version 3 of the
* License, or (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU Affero General Public License for more details.
*
* You should have received a copy of the GNU Affero General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*/
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
import org.openremote.web.console.panel.entity.ButtonPressed;
import org.openremote.web.console.util.BrowserUtils;
import org.openremote.web.console.util.ImageContainer;
import org.openremote.web.console.util.ImageLoadedCallback;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Button;

public class ButtonComponent extends InteractiveConsoleComponent implements PressStartHandler, PressEndHandler, PressCancelHandler, TapHandler {
	public static final String CLASS_NAME = "buttonComponent";
	public static final int LABEL_FONT_SIZE = 12;
	private String name;
	private boolean pressed = false;
	private ImageContainer defaultImageContainer = null;
	private ImageContainer pressedImageContainer = null;
	
	protected ButtonComponent() {
		super(new Button(), CLASS_NAME);
	}
	
	public void setName(String name) {
		this.name = name;
		((Button)getWidget()).setHTML("<span>"+ name + "</span>");
	}
	
	public void showDefaultImage() {
		if (defaultImageContainer != null) {
			if (defaultImageContainer.getLoadAttempted()) {
				if (!defaultImageContainer.getExists()) {
					DOM.setStyleAttribute(getElement(), "backgroundImage", "");
					getElement().removeClassName("hasImage");
					return;
				}
			} else {
				defaultImageContainer.addCallback(new ImageLoadedCallback() {
					@Override
					public void onImageLoaded(ImageContainer container) {
						showDefaultImage();
					}
				});
				return;
			}
			
			if (defaultImageContainer.getNativeWidth() > this.getParent().getOffsetWidth() || defaultImageContainer.getNativeHeight() > this.getParent().getOffsetHeight()) {
				DOM.setStyleAttribute(getElement(), "backgroundSize", "contain");
			}
			DOM.setStyleAttribute(getElement(), "backgroundImage", "url(" + defaultImageContainer.getUrl() + ")");
			DOM.setStyleAttribute(getElement(), "backgroundRepeat", "no-repeat");
			getElement().addClassName("hasImage");
		} else {
			DOM.setStyleAttribute(getElement(), "backgroundImage", "");
			getElement().removeClassName("hasImage");
		}
	}
	
	// ---------------------------------------------------------------------------------
	//			SUPER CLASS OVERRIDES BELOW
	// ---------------------------------------------------------------------------------
	
	@Override
	public void onRender(int width, int height) {
		isInitialised = true;
		showDefaultImage();
	}
	
	@Override
	public void onUpdate(int width, int height) {
		showDefaultImage();
	}

	@Override
	public void onPressCancel(PressCancelEvent event) {
		onPressEnd(null);
	}

	@Override
	public void onPressEnd(PressEndEvent event) {
		if (!pressed) return;
		pressed = false;
		showDefaultImage();
		this.removeStyleName("pressed");
	}

	@Override
	public void onPressStart(PressStartEvent event) {
		pressed = true;
		if (pressedImageContainer != null && pressedImageContainer.getExists()) {
			if (pressedImageContainer.getNativeWidth() > this.getParent().getOffsetWidth() || pressedImageContainer.getNativeHeight() > this.getParent().getOffsetHeight()) {
				DOM.setStyleAttribute(getElement(), "backgroundSize", "contain");
			}
			DOM.setStyleAttribute(getElement(), "backgroundImage", "url(" + pressedImageContainer.getUrl() + ")");
			DOM.setStyleAttribute(getElement(), "backgroundRepeat", "no-repeat");
			getElement().addClassName("hasImage");
		}
//		else {
//			DOM.setStyleAttribute(getElement(), "backgroundImage", "none");
//			getElement().removeClassName("hasImage");
//		}
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
	
	// ---------------------------------------------------------------------------------
	//			BUILD METHOD BELOW HERE
	// ---------------------------------------------------------------------------------
	
	public static ConsoleComponent build(org.openremote.web.console.panel.entity.component.ButtonComponent entity) {
		ButtonComponent component = new ButtonComponent();
		
		if (entity == null) {
			return component;
		}
		component.setId(entity.getId());
		component.setName(entity.getName());
		
		ButtonDefault buttonDefault = entity.getDefault();
		if (buttonDefault != null) {
			org.openremote.web.console.panel.entity.Image img = buttonDefault.getImage();
			if (img != null) {
				String src = img.getSrc();
				if (img.getSystemImage() != null && img.getSystemImage()) {
					src = BrowserUtils.getSystemImageDir() + src;
				} else {
					src = WebConsole.getConsoleUnit().getControllerService().getController().getUrl() + src;
				}
				component.defaultImageContainer = WebConsole.getConsoleUnit().getImageFromCache(src);
			}
		}
		ButtonPressed buttonPressed = entity.getPressed();
		if (buttonPressed != null) {
			org.openremote.web.console.panel.entity.Image img = buttonPressed.getImage();
			String src = img.getSrc();
			if (img.getSystemImage() != null && img.getSystemImage()) {
				src = BrowserUtils.getSystemImageDir() + src;
			} else {
				src = WebConsole.getConsoleUnit().getControllerService().getController().getUrl() + src;
			}
			component.pressedImageContainer = WebConsole.getConsoleUnit().getImageFromCache(src);
		}
		
		Boolean hasControl = entity.getHasControlCommand();
		if (hasControl != null && hasControl) {
			component.setHasControlCommand(hasControl);
		}
		component.setNavigate(entity.getNavigate());
		return component;
	}
}
