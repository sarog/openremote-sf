package org.openremote.controller.protocol.domintell;

public enum TemperatureMode {

   ABSENCE (1),
   AUTO (2),
   COMFORT(5),
   FROST(6);
   
   private final int value;

   TemperatureMode(int value) {
      this.value = value;
   }
   
   public int getValue() {
      return value;
   }
}
