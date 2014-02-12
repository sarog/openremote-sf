package org.openremote.controller.protocol.omnilink.model;

import org.openremote.controller.protocol.omnilink.OmniLinkCmd;
import com.digitaldan.jomnilinkII.MessageTypes.properties.ZoneProperties;

public class Zone extends OmnilinkDevice {
   private static final String CURRENT_TEXT[] = {"Secure","Not ready","Trouble"};
   private static final String LATCHED_TEXT[] = {"Latch Secure","Latch Tripped","Latch Reset"};
   private static final String ARMING_TEXT[] = {"Disarmed", "Armed", "User bypass","System bypass"};
   
   private ZoneProperties properties;
   public Zone(ZoneProperties properties) {
      this.properties = properties;
   }
   
   public ZoneProperties getProperties() {
      return properties;
   }
   
   public void setProperties(ZoneProperties properties) {
      this.properties = properties;
   }

   @Override
   public void updateSensors() {
      int current = ((properties.getStatus() >> 0)& 0x03);
      int latched = ((properties.getStatus() >> 2)& 0x03);
      int arming  = ((properties.getStatus() >> 4)& 0x03);
      int trouble = ((properties.getStatus() >> 6)& 0x01);
      
      String currentText = CURRENT_TEXT[current];
      String latchedText = CURRENT_TEXT[latched];
      String armingText = CURRENT_TEXT[arming];
      
      if (sensors.get(OmniLinkCmd.SENSOR_ZONE_STATUS_CURRENT) != null) {
         sensors.get(OmniLinkCmd.SENSOR_ZONE_STATUS_CURRENT).update(currentText);
      }
      if (sensors.get(OmniLinkCmd.SENSOR_ZONE_STATUS_ARMING) != null) {
         sensors.get(OmniLinkCmd.SENSOR_ZONE_STATUS_ARMING).update(armingText);
      }
      if (sensors.get(OmniLinkCmd.SENSOR_ZONE_STATUS_LATCHED) != null) {
         sensors.get(OmniLinkCmd.SENSOR_ZONE_STATUS_LATCHED).update(latchedText);
      }
   }
}
