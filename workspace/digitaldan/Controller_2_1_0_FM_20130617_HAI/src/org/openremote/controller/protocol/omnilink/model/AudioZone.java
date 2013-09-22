package org.openremote.controller.protocol.omnilink.model;

import org.openremote.controller.protocol.omnilink.OmniLinkCmd;

import com.digitaldan.jomnilinkII.MessageTypes.properties.AudioZoneProperties;

public class AudioZone extends OmnilinkDevice {

   private AudioZoneProperties properties;
   
   public AudioZone( AudioZoneProperties properties) {
     this.properties = properties;
   }

   public AudioZoneProperties getProperties() {
      return properties;
   }
   
   public void setProperties(AudioZoneProperties properties) {
      this.properties = properties;
   }

   @Override
   public void updateSensors() {
      if (sensors.get(OmniLinkCmd.SENSOR_AUDIOZONE_MUTE) != null) {
         sensors.get(OmniLinkCmd.SENSOR_AUDIOZONE_MUTE).update(properties.isMute() ? "on" : "off");
      }
      if (sensors.get(OmniLinkCmd.SENSOR_AUDIOZONE_POWER) != null) {
         sensors.get(OmniLinkCmd.SENSOR_AUDIOZONE_POWER).update(properties.isOn() ? "on" : "off");
      }
      if (sensors.get(OmniLinkCmd.SENSOR_AUDIOZONE_SOURCE) != null) {
         sensors.get(OmniLinkCmd.SENSOR_AUDIOZONE_SOURCE).update(properties.getSource() + "");
      }
      if (sensors.get(OmniLinkCmd.SENSOR_AUDIOZONE_VOLUME) != null) {
         sensors.get(OmniLinkCmd.SENSOR_AUDIOZONE_VOLUME).update(properties.getVolume() + "");
      }
   }
}
