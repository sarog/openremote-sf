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
package org.openremote.modeler.client.event;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.EventType;

/**
 * The Event is fire to control the layer of the screen absolute widget.
 * 
 * @author tomsky.wang 2010-11-11
 *
 */
public class WidgetLayerControlEvent extends BaseEvent {

   public static final EventType WIDGET_LAYER_UP = new EventType();
   public static final EventType WIDGET_LAYER_DOWN = new EventType();
   
   public WidgetLayerControlEvent(EventType type) {
      super(type);
   }

}
