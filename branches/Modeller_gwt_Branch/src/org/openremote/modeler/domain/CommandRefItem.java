package org.openremote.modeler.domain;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import flexjson.JSON;

@SuppressWarnings("serial")
@Entity
@Table(name = "command_ref_item")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type")
@DiscriminatorValue("COMMAND_REF_ITEM")
public class CommandRefItem extends BusinessEntity {

   private DeviceCommand deviceCommand;

   private String deviceName;
   
   @ManyToOne
   @JoinColumn(name = "target_device_command_oid")
   @JSON(include = false)
   public DeviceCommand getDeviceCommand() {
      return deviceCommand;
   }

   public void setDeviceCommand(DeviceCommand deviceCommand) {
      this.deviceCommand = deviceCommand;
   }
   
   @Transient
   public String getDeviceName() {
      return deviceName;
   }

   public void setDeviceName(String deviceName) {
      this.deviceName = deviceName;
   }
   
   @Override
   @Transient
   public String getDisplayName() {
      this.deviceName = (this.deviceName == null || "".equals(this.deviceName)) ? getDeviceCommand().getDevice().getName() : this.deviceName;
      return getDeviceCommand().getName() + " (" + this.deviceName + ")";
   }
   
}
