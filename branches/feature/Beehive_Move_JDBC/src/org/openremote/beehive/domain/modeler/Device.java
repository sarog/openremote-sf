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
package org.openremote.beehive.domain.modeler;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.openremote.beehive.api.dto.AccountDTO;
import org.openremote.beehive.api.dto.modeler.DeviceCommandDTO;
import org.openremote.beehive.api.dto.modeler.DeviceDTO;
import org.openremote.beehive.api.dto.modeler.SensorDTO;
import org.openremote.beehive.domain.Account;
import org.openremote.beehive.domain.BusinessEntity;

import flexjson.JSON;


/**
 * Device is represent a specific controllable machine, eg: ligth, fridge, etc.
 * It includes name, vendor, model, deviceCommands. As the device is created by 
 * a account, so it associated with account.
 * 
 * In addition, the device could have sensor which include a command that can get
 * the device's status, have switch which include switch command and sensor that
 * can switch the device in two state, have slider which include slider command 
 * and sensor that can set the device's range state. 
 * 
 */
@Entity
@Table(name = "device")
public class Device extends BusinessEntity {
   
   private static final long serialVersionUID = 2591003357551228807L;

   /** The device name. */
   private String name;
   
   /** The vendor who product the device. */
   private String vendor;
   
   /** The device's model. */
   private String model;
   
   /** The device commands. */
   private List<DeviceCommand> deviceCommands = new ArrayList<DeviceCommand>();
   
   /** The device attrs. */
   private List<DeviceAttr> deviceAttrs;
   
   private List<Sensor> sensors = new ArrayList<Sensor>();
   
   /** The account who created the device in this application. */
   private Account account; 

   /**
    * Instantiates a new device.
    */
   public Device() {
      super();
   }

   /**
    * Instantiates a new device.
    * 
    * @param model
    *           the model
    * @param name
    *           the name
    * @param vendor
    *           the vendor
    */
   public Device(String name, String vendor, String model) {
      super();
      this.model = model;
      this.name = name;
      this.vendor = vendor;
   }

   /**
    * Gets the device name.
    * 
    * @return the name
    */
   @Column(nullable = false)
   public String getName() {
      return name;
   }

   /**
    * Sets the device name.
    * 
    * @param name the new name
    */
   public void setName(String name) {
      this.name = name;
   }

   /**
    * Gets the vendor.
    * 
    * @return the vendor
    */
   public String getVendor() {
      return vendor;
   }

   /**
    * Sets the vendor.
    * 
    * @param vendor the new vendor
    */
   public void setVendor(String vendor) {
      this.vendor = vendor;
   }

   /**
    * Gets the model.
    * 
    * @return the model
    */
   public String getModel() {
      return model;
   }

   /**
    * Sets the model.
    * 
    * @param model the new model
    */
   public void setModel(String model) {
      this.model = model;
   }
   

   /**
    * Gets the device commands.
    * 
    * @return the device commands
    */
   @OneToMany(mappedBy = "device", cascade = CascadeType.ALL)
   public List<DeviceCommand> getDeviceCommands() {
      return deviceCommands;
   }

   /**
    * Sets the device commands.
    * 
    * @param deviceCommands the new device commands
    */
   public void setDeviceCommands(List<DeviceCommand> deviceCommands) {
      this.deviceCommands = deviceCommands;
   }
   
   /**
    * Gets the device attrs.
    * 
    * @return the device attrs
    */
   @OneToMany(mappedBy = "device", cascade = CascadeType.REMOVE)
   public List<DeviceAttr> getDeviceAttrs() {
      return deviceAttrs;
   }

   /**
    * Sets the device attrs.
    * 
    * @param deviceAttrs the new device attrs
    */
   public void setDeviceAttrs(List<DeviceAttr> deviceAttrs) {
      this.deviceAttrs = deviceAttrs;
   }

   @OneToMany(mappedBy="device",cascade=CascadeType.ALL)
   public List<Sensor> getSensors() {
      return sensors;
   }

   public void setSensors(List<Sensor> sensors) {
      this.sensors = sensors;
   }
   
   /**
    * Gets the account.
    * 
    * @return the account
    */
   @ManyToOne
   @JSON(include = false)
   public Account getAccount() {
      return account;
   }

   /**
    * Sets the account.
    * 
    * @param account the new account
    */
   public void setAccount(Account account) {
      this.account = account;
   }
   
   public DeviceDTO toDTO() {
      DeviceDTO deviceDTO = new DeviceDTO();
      deviceDTO.setId(getOid());
      deviceDTO.setName(name);
      deviceDTO.setVendor(vendor);
      deviceDTO.setModel(model);
      deviceDTO.setAccount(new AccountDTO(account.getOid()));
      
      if (deviceCommands != null && deviceCommands.size() > 0) {
         List<DeviceCommandDTO> deviceCommandDTOs = new ArrayList<DeviceCommandDTO>();
         for (DeviceCommand deviceCommand : deviceCommands) {
            deviceCommandDTOs.add(deviceCommand.toSimpleDTO());
         }
         deviceDTO.setDeviceCommands(deviceCommandDTOs);
      }
      
      if (sensors != null && sensors.size() > 0) {
         List<SensorDTO> sensorDTOs = new ArrayList<SensorDTO>();
         for (Sensor sensor : sensors) {
            sensorDTOs.add(sensor.toDTO());
         }
         deviceDTO.setSensors(sensorDTOs);
      }
      
      return deviceDTO;
   }
   
   public DeviceDTO toSimpleDTO() {
      DeviceDTO deviceDTO = new DeviceDTO();
      deviceDTO.setId(getOid());
      deviceDTO.setName(name);
      deviceDTO.setVendor(vendor);
      deviceDTO.setModel(model);
      return deviceDTO;
   }
}
