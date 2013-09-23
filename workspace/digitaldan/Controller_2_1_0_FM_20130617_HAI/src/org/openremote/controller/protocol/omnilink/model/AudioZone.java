package org.openremote.controller.protocol.omnilink.model;

import org.openremote.controller.protocol.omnilink.OmniLinkCmd;
import org.openremote.controller.protocol.omnilink.OmnilinkCommandBuilder;
import org.openremote.controller.utils.Logger;

import com.digitaldan.jomnilinkII.MessageTypes.properties.AudioZoneProperties;

public class AudioZone extends OmnilinkDevice {
   private final static Logger log = Logger.getLogger(OmnilinkCommandBuilder.OMNILINK_PROTOCOL_LOG_CATEGORY);
   private AudioZoneProperties properties;
   private String audioSourceText;
   
   public AudioZone( AudioZoneProperties properties) {
     this.properties = properties;
   }

   public AudioZoneProperties getProperties() {
      return properties;
   }
   
   public void setProperties(AudioZoneProperties properties) {
      this.properties = properties;
   }

   public String getAudioSourceText() {
      return audioSourceText;
   }

   public void setAudioSourceText(String audioSourceText) {
      this.audioSourceText = audioSourceText;
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
      if (sensors.get(OmniLinkCmd.SENSOR_AUDIOZONE_TEXT) != null) {
         log.info("SENSOR_AUDIOZONE_TEXT updating zone text " + audioSourceText);
         if(audioSourceText == null || audioSourceText.length() == 0)
            audioSourceText = "...";
         sensors.get(OmniLinkCmd.SENSOR_AUDIOZONE_TEXT).update(audioSourceText);
      }
   }
}
