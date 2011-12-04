/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2010, OpenRemote Inc.
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
package org.openremote.web.console.panel.entity;

import org.openremote.web.console.panel.entity.component.ButtonComponent;
import org.openremote.web.console.panel.entity.component.ImageComponent;
import org.openremote.web.console.panel.entity.component.LabelComponent;
import org.openremote.web.console.panel.entity.component.SliderComponent;
import org.openremote.web.console.panel.entity.component.SwitchComponent;


/**
 * The subclass of LayoutContainer which includes a component.
 * It parse the absolute node, contains size and position info.
 * 
 */
public interface AbsoluteLayout {
   String getHeight();
   String getWidth();
   String getLeft();
   String getTop();
	ButtonComponent getButton();
	LabelComponent getLabel();
	SliderComponent getSlider();
	ImageComponent getImage();
	SwitchComponent getSwitch();
	
   void setHeight(String height);
   void setWidth(String width);
   void setLeft(String left);
   void setTop(String top);
	void setButton(ButtonComponent component);
	void setLabel(LabelComponent component);
	void setSlider(SliderComponent component);
	void setImage(ImageComponent component);
	void setSwitch(SwitchComponent component);
}
