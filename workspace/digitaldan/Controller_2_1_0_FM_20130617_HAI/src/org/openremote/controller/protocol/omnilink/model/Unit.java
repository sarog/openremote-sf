package org.openremote.controller.protocol.omnilink.model;

import org.openremote.controller.protocol.omnilink.OmniLinkCmd;

import com.digitaldan.jomnilinkII.MessageTypes.properties.UnitProperties;

public class Unit extends OmnilinkDevice {
   public static int UNIT_OFF = 0;
   public static int UNIT_ON = 1;
   public static int UNIT_SCENE_A = 2;
   public static int UNIT_SCENE_L = 13;
   public static int UNIT_DIM_1 = 17;
   public static int UNIT_DIM_9 = 25;
   public static int UNIT_BRIGHTEN_1 = 33;
   public static int UNIT_BRIGHTEN_9 = 41;
   public static int UNIT_LEVEL_0 = 100;
   public static int UNIT_LEVEL_100 = 200;
   
   private UnitProperties properties;
   
   public Unit(UnitProperties properties) {
      this.properties = properties;
   }

   public UnitProperties getProperties() {
      return properties;
   }
   
   public void setProperties(UnitProperties properties) {
      this.properties = properties;
   }

   @Override
   public void updateSensors() {
      logger.info("Updating Unit sensor" + getProperties().getName());
      if (sensors.get(OmniLinkCmd.SENSOR_UNIT_POWER) != null) {
         sensors.get(OmniLinkCmd.SENSOR_UNIT_POWER).update(properties.getState() > 0 ?"on":"off");
         logger.info("Updating Unit SENSOR_UNIT_POWER " + getProperties().getName() + " to " + (properties.getState() > 0 ?"on":"off"));
      }
      if (sensors.get(OmniLinkCmd.SENSOR_UNIT_LEVEL) != null) {
         sensors.get(OmniLinkCmd.SENSOR_UNIT_LEVEL).update(getLevel() + "");
      }
   }
   
   private int getLevel() {
      int status = properties.getState();
      int level = 0;

      if (status == UNIT_ON) {
        level = 100;
      } else if ((status >= UNIT_SCENE_A) && (status <= UNIT_SCENE_L)){
        //display = "Scene " + (status - UNIT_SCENE_A + 'A');
      } else if ((status >= UNIT_DIM_1) && (status <= UNIT_DIM_9)) {
        //display = "Dim " + (status - UNIT_DIM_1 + 1);
        level = status - UNIT_DIM_1 + 1;
      } else if ((status >= UNIT_BRIGHTEN_1) && (status <= UNIT_BRIGHTEN_9)) {
        //display = "Brightend" + (status - UNIT_BRIGHTEN_1 + 1);
        level = status - UNIT_BRIGHTEN_1 + 1;
      } else if ((status >= UNIT_LEVEL_0) && (status <= UNIT_LEVEL_100)) {
        //display = "Level " + (status - UNIT_LEVEL_0);
        level = status - UNIT_LEVEL_0;
      } else {
        //display = "Unkown status: " + status;
      }
      return level;
    }

}
