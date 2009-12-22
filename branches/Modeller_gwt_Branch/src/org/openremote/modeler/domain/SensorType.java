package org.openremote.modeler.domain;

public enum SensorType {

   SWITCH,
   LEVEL,
   RANGE,
   COLOR,
   CUSTOM;

   @Override
   public String toString() {
      return super.toString().toLowerCase();
   }
   
}
