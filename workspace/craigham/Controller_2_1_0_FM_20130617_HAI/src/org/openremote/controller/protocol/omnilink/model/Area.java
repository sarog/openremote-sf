package org.openremote.controller.protocol.omnilink.model;

import org.openremote.controller.protocol.omnilink.OmniLinkCmd;

import com.digitaldan.jomnilinkII.MessageTypes.properties.AreaProperties;

public class Area extends OmnilinkDevice {
   public static final String[] omniText = {"Off", "Day", "Night", "away", 
      "Vacation", "Day Instant", "Night Delayed"};
   public static final String[] luminaText = {"Off", "Home", "Sleep", "Away", 
      "Vacation","Party", "Special"};
   public static final String[] alarms = {"Burglary", "Fire", "Gas", 
      "Auxiliary","Freeze","Water", "Duress","Temperature"};
   
   private AreaProperties properties;
   private boolean omni;
   
   public Area(AreaProperties properties, boolean omni) {
      this.properties = properties;
      this.omni = omni;
   }

   public AreaProperties getProperties() {
      return properties;
   }
   
   public void setProperties(AreaProperties properties) {
      this.properties = properties;
   }
   

   public boolean isOmni() {
      return omni;
   }

   public void setOmni(boolean omni) {
      this.omni = omni;
   }

   @Override
   public void updateSensors() {
      
      if (sensors.get(OmniLinkCmd.SENSOR_AREA_STATUS_MODE) != null) {
         String txtArray[] = omni ? omniText : luminaText;
         String text = properties.getMode() < txtArray.length ? 
               txtArray[properties.getMode()] : "Unknown";
         sensors.get(OmniLinkCmd.SENSOR_AREA_STATUS_MODE).update(text);
      }
      if (sensors.get(OmniLinkCmd.SENSOR_AREA_STATUS_ALARM) != null) {
         String text = properties.getAlarms() < alarms.length ? alarms[properties.getAlarms()] : "Unknown";
         sensors.get(OmniLinkCmd.SENSOR_AREA_STATUS_ALARM).update(text);
      }
   }

}
