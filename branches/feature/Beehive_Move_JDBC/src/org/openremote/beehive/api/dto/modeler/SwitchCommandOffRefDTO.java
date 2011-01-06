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
package org.openremote.beehive.api.dto.modeler;

import org.openremote.beehive.domain.modeler.Switch;
import org.openremote.beehive.domain.modeler.SwitchCommandOffRef;

@SuppressWarnings("serial")
public class SwitchCommandOffRefDTO extends CommandRefItemDTO {

   private SwitchDTO  offSwitch;

   public SwitchCommandOffRefDTO() {
   }
   public SwitchCommandOffRefDTO(SwitchCommandOffRef commandOffRef, String deviceName) {
      if (commandOffRef != null) {
         setId(commandOffRef.getOid());
         setDeviceCommand(commandOffRef.getDeviceCommand().toDTO());
         setDeviceName(deviceName);
      }
   }
   
   public SwitchDTO getOffSwitch() {
      return offSwitch;
   }
   public void setOffSwitch(SwitchDTO offSwitch) {
      this.offSwitch = offSwitch;
   }
   
   public SwitchCommandOffRef toSwitchCommandOffRef(Switch switchToggle) {
      SwitchCommandOffRef commandOffRef = new SwitchCommandOffRef();
      commandOffRef.setDeviceCommand(getDeviceCommand().toDeviceCommand());
      commandOffRef.setOffSwitch(switchToggle);
      return commandOffRef;
   }
}
