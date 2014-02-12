package org.openremote.controller.protocol.omnilink.model;

import org.openremote.controller.component.RangeSensor;
import org.openremote.controller.model.sensor.Sensor;
import org.openremote.controller.protocol.omnilink.OmniLinkCmd;

import com.digitaldan.jomnilinkII.MessageUtils;
import com.digitaldan.jomnilinkII.MessageTypes.properties.ThermostatProperties;

public class Thermostat extends OmnilinkDevice {
   private ThermostatProperties properties;
   private static String[] MODES = {"Off", "Heat", "Cool", "Auto", "Emergency Heat"};
   public Thermostat(ThermostatProperties properties) {
      this.properties = properties;
   }
   
   public ThermostatProperties getProperties() {
      return properties;
   }
   
   public void setProperties(ThermostatProperties properties) {
      this.properties = properties;
   }

   @Override
   public void updateSensors() {
      if (sensors.get(OmniLinkCmd.SENSOR_THERMO_TEMPC) != null) {
         sensors.get(OmniLinkCmd.SENSOR_THERMO_TEMPC).update(MessageUtils.omniToC(properties.getTemperature()) + "");
      }
      if (sensors.get(OmniLinkCmd.SENSOR_THERMO_TEMPF) != null) {
         sensors.get(OmniLinkCmd.SENSOR_THERMO_TEMPF).update(MessageUtils.omniToF(properties.getTemperature()) + "");
      }
      if (sensors.get(OmniLinkCmd.SENSOR_THERMO_COOL_POINTC) != null) {
         sensors.get(OmniLinkCmd.SENSOR_THERMO_COOL_POINTC).update(MessageUtils.omniToC(properties.getCoolSetpoint()) + "");
      }
      if (sensors.get(OmniLinkCmd.SENSOR_THERMO_COOL_POINTF) != null) {
         sensors.get(OmniLinkCmd.SENSOR_THERMO_COOL_POINTF).update(MessageUtils.omniToF(properties.getCoolSetpoint()) + "");
      }
      if (sensors.get(OmniLinkCmd.SENSOR_THERMO_HEAT_POINTC) != null) {
         sensors.get(OmniLinkCmd.SENSOR_THERMO_HEAT_POINTC).update(MessageUtils.omniToC(properties.getHeatSetpoint()) + "");
      }
      if (sensors.get(OmniLinkCmd.SENSOR_THERMO_HEAT_POINTF) != null) {
         sensors.get(OmniLinkCmd.SENSOR_THERMO_HEAT_POINTF).update(MessageUtils.omniToF(properties.getHeatSetpoint()) + "");
      }
      if (sensors.get(OmniLinkCmd.SENSOR_THERMO_FAN_MODE) != null) {
         sensors.get(OmniLinkCmd.SENSOR_THERMO_FAN_MODE).update(properties.isFan() ? "on" : "off");
      }
      if (sensors.get(OmniLinkCmd.SENSOR_THERMO_HOLD_MODE) != null) {
         sensors.get(OmniLinkCmd.SENSOR_THERMO_HOLD_MODE).update(properties.isHold() ? "on" : "off");
      }
      if (sensors.get(OmniLinkCmd.SENSOR_THERMO_SYSTEM_MODE) != null) {
         Sensor sensor = sensors.get(OmniLinkCmd.SENSOR_THERMO_SYSTEM_MODE);
         if(sensor instanceof RangeSensor){
            sensor.update(properties.getMode() + "");
         } else {
            sensor.update(MODES[properties.getMode()]);
         }
      }
   }

}
