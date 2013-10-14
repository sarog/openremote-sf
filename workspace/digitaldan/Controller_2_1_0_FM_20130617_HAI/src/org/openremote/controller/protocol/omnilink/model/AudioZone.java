package org.openremote.controller.protocol.omnilink.model;

import org.openremote.controller.protocol.omnilink.OmniLinkCmd;
import org.openremote.controller.protocol.omnilink.OmnilinkCommandBuilder;
import org.openremote.controller.utils.Logger;

import com.digitaldan.jomnilinkII.MessageTypes.properties.AudioZoneProperties;

public class AudioZone extends OmnilinkDevice {
   private final static Logger log = Logger.getLogger(OmnilinkCommandBuilder.OMNILINK_PROTOCOL_LOG_CATEGORY);
   private AudioZoneProperties properties;
   private AudioSource audioSource;
   
   public AudioZone( AudioZoneProperties properties) {
     this.properties = properties;
   }

   public AudioZoneProperties getProperties() {
      return properties;
   }
   
   public void setProperties(AudioZoneProperties properties) {
      this.properties = properties;
   }

   public AudioSource getAudioSource() {
      return audioSource;
   }

   public void setAudioSource(AudioSource audioSource) {
      this.audioSource = audioSource;
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
         sensors.get(OmniLinkCmd.SENSOR_AUDIOZONE_TEXT).update(audioSource.formatAudioText());
      }
      if (sensors.get(OmniLinkCmd.SENSOR_AUDIOZONE_TEXT_FIELD1) != null) {
         sensors.get(OmniLinkCmd.SENSOR_AUDIOZONE_TEXT_FIELD1).update(audioSource != null ? audioSource.getAudioText(0) : "");
      }
      if (sensors.get(OmniLinkCmd.SENSOR_AUDIOZONE_TEXT_FIELD2) != null) {
         sensors.get(OmniLinkCmd.SENSOR_AUDIOZONE_TEXT_FIELD2).update(audioSource != null ? audioSource.getAudioText(1) : "");
      }
      if (sensors.get(OmniLinkCmd.SENSOR_AUDIOZONE_TEXT_FIELD3) != null) {
         sensors.get(OmniLinkCmd.SENSOR_AUDIOZONE_TEXT_FIELD3).update(audioSource != null ? audioSource.getAudioText(2) : "");
      }
   }
}
