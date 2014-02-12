package org.openremote.controller.protocol.omnilink.model;

import org.openremote.controller.protocol.omnilink.OmniLinkCmd;

import com.digitaldan.jomnilinkII.MessageTypes.properties.AudioSourceProperties;

public class AudioSource extends OmnilinkDevice {
   private AudioSourceProperties properties;
   private String[] audioText;
   
   public AudioSource(AudioSourceProperties properties) {
      this.properties = properties;
   }
   public AudioSourceProperties getProperties() {
      return properties;
   }
   
   public void setProperties(AudioSourceProperties properties) {
      this.properties = properties;
   }
   public String[] getAudioText() {
      return audioText;
   }
   public void setAudioText(String[] audioText) {
      this.audioText = audioText;
   }
   @Override
   public void updateSensors() {
      if (sensors.get(OmniLinkCmd.SENSOR_AUDIOSOURCE_TEXT) != null && audioText !=null ) {
         sensors.get(OmniLinkCmd.SENSOR_AUDIOSOURCE_TEXT).update(formatAudioText());
      }
      if (sensors.get(OmniLinkCmd.SENSOR_AUDIOSOURCE_TEXT_FIELD1) != null && audioText !=null ) {
         sensors.get(OmniLinkCmd.SENSOR_AUDIOSOURCE_TEXT_FIELD1).update(getAudioText(0));
      }
      if (sensors.get(OmniLinkCmd.SENSOR_AUDIOSOURCE_TEXT_FIELD2) != null && audioText !=null ) {
         sensors.get(OmniLinkCmd.SENSOR_AUDIOSOURCE_TEXT_FIELD2).update(getAudioText(1));
      }
      if (sensors.get(OmniLinkCmd.SENSOR_AUDIOSOURCE_TEXT_FIELD3) != null && audioText !=null ) {
         sensors.get(OmniLinkCmd.SENSOR_AUDIOSOURCE_TEXT_FIELD3).update(getAudioText(2));
      }
   }
   
   public String formatAudioText(){
      StringBuilder sb = new StringBuilder();
      for(String s : audioText)
         sb.append(s).append(" | ");
      if(sb.length() == 0)
         sb.append(" ");
      return sb.toString();
   }
   
   public String getAudioText(int fieldNum){
      return fieldNum < audioText.length ? audioText[fieldNum] : "";
   }
}
