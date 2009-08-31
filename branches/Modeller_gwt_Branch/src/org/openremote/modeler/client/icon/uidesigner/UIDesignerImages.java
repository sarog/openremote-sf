/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2009, OpenRemote Inc.
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
package org.openremote.modeler.client.icon.uidesigner;

import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.ImageBundle;

// TODO: Auto-generated Javadoc
/**
 * The Interface UIDesignerImages.
 */
public interface UIDesignerImages extends ImageBundle {
   
   /**
    * Iphone background.
    * 
    * @return the abstract image prototype
    */
   @Resource("iphone_background.jpg")
   AbstractImagePrototype iphoneBackground();
   
   /**
    * Iphone btn.
    * 
    * @return the abstract image prototype
    */
   @Resource("iphone_btn.jpg")
   AbstractImagePrototype iphoneBtn();
   //TODO we will store the screen background name in the database to support multi-device.
   
   /**
    * Iphone btn sprite.
    * 
    * @return the abstract image prototype
    */
   @Resource("iphone_btn_sprite.png")
   AbstractImagePrototype iphoneBtnSprite();
   
   /**
    * Iphone btn vertical.
    * 
    * @return the abstract image prototype
    */
   @Resource("iphone_btn_vertical.png")
   AbstractImagePrototype iphoneBtnVertical();
   
}
