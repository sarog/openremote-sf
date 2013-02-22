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
package org.openremote.modeler.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.openremote.modeler.domain.DeviceMacroItem;

/**
 * The Class DeviceMacroItemList for storing a list of deviceMacroItems in a DeviceMacro.
 *
 * @author tomsky
 */
public class DeviceMacroItemList {

   private List<DeviceMacroItem> deviceMacroItems = new ArrayList<DeviceMacroItem>();

   public List<DeviceMacroItem> getDeviceMacroItems() {
      return deviceMacroItems;
   }

   public void setDeviceMacroItems(List<DeviceMacroItem> deviceMacroItems) {
      this.deviceMacroItems = deviceMacroItems;
   }
   
}
