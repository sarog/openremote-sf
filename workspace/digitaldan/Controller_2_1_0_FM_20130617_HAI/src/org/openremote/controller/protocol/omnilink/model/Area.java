package org.openremote.controller.protocol.omnilink.model;

import org.openremote.controller.protocol.omnilink.OmniLinkCmd;

import com.digitaldan.jomnilinkII.MessageTypes.properties.AreaProperties;

public class Area extends OmnilinkDevice {

   private AreaProperties properties;
   
   public Area(AreaProperties properties) {
      this.properties = properties;
   }

   public AreaProperties getProperties() {
      return properties;
   }
   
   public void setProperties(AreaProperties properties) {
      this.properties = properties;
   }

   @Override
   public void updateSensors() {
      if (sensors.get(OmniLinkCmd.SENSOR_AREA_STATUS) != null) {
         sensors.get(OmniLinkCmd.SENSOR_AREA_STATUS).update(properties.getMode() + "");
      }
   }

}
