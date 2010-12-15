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

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.openremote.beehive.api.dto.AccountDTO;
import org.openremote.beehive.api.dto.BusinessEntityDTO;
import org.openremote.beehive.domain.modeler.Device;

/**
 * The Class is used for transmitting device info.
 */
@SuppressWarnings("serial")
@XmlRootElement(name = "device")
public class DeviceDTO extends BusinessEntityDTO {
   private String name;
   private String vendor;
   private String model;
   
   private AccountDTO account;
   private List<DeviceCommandDTO> deviceCommands;
   private List<SensorDTO> sensors;
   private List<SwitchDTO> switchs;
   
   public String getName() {
      return name;
   }
   public String getVendor() {
      return vendor;
   }
   public String getModel() {
      return model;
   }
   public AccountDTO getAccount() {
      return account;
   }
   @XmlElement(name = "deviceCommands")
   public List<DeviceCommandDTO> getDeviceCommands() {
      return deviceCommands;
   }
   @XmlElement(name = "sensors")
   public List<SensorDTO> getSensors() {
      return sensors;
   }
   @XmlElement(name = "switchs")
   public List<SwitchDTO> getSwitchs() {
      return switchs;
   }
   
   public void setName(String name) {
      this.name = name;
   }
   public void setVendor(String vendor) {
      this.vendor = vendor;
   }
   public void setModel(String model) {
      this.model = model;
   }
   public void setAccount(AccountDTO account) {
      this.account = account;
   }
   public void setDeviceCommands(List<DeviceCommandDTO> deviceCommands) {
      this.deviceCommands = deviceCommands;
   }
   public void setSensors(List<SensorDTO> sensors) {
      this.sensors = sensors;
   }
   public void setSwitchs(List<SwitchDTO> switchs) {
      this.switchs = switchs;
   }
   
   public Device toDevice() {
      Device device = new Device();
      device.setOid(getId());
      device.setName(name);
      device.setVendor(vendor);
      device.setModel(model);
      return device;
   }
}
