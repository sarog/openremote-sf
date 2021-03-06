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
package org.openremote.modeler.client.event;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.EventType;

/**
 * The event for DeviceWizardListener.
 */
public class DeviceWizardEvent extends BaseEvent {

   public static final EventType ADD_CONTENT = new EventType();
   private Object data;
   
   public DeviceWizardEvent() {
      super(ADD_CONTENT);
   }
   
   public DeviceWizardEvent(Object d) {
      this();
      this.data = d;
   }
   
   @SuppressWarnings("unchecked")
   public <X> X getData() {
      return (X) data;
    }
}
