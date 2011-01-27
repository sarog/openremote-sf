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
package org.openremote.beehive.rest;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.openremote.beehive.api.dto.modeler.DeviceMacroDTO;

/**
 * In order to let rest service to serialize list of deviceMacros
 */
@XmlRootElement(name = "deviceMacros")
public class DeviceMacroListing {

   private List<DeviceMacroDTO> deviceMacros = new ArrayList<DeviceMacroDTO>();
   
   public DeviceMacroListing() {
   }
   
   public DeviceMacroListing(List<DeviceMacroDTO> deviceMacros) {
      this.deviceMacros = deviceMacros;
   }
   
   @XmlElement(name="deviceMacro")
   public List<DeviceMacroDTO> getDeviceMacros() {
      return deviceMacros;
   }
   public void setDeviceMacros(List<DeviceMacroDTO> deviceMacros) {
      this.deviceMacros = deviceMacros;
   }
}
