package org.openremote.controller.protocol.omnilink.model;

import org.openremote.controller.protocol.omnilink.OmniLinkCmd;

import com.digitaldan.jomnilinkII.MessageUtils;
import com.digitaldan.jomnilinkII.MessageTypes.properties.AuxSensorProperties;
import com.digitaldan.jomnilinkII.MessageTypes.properties.ThermostatProperties;

public class Aux extends OmnilinkDevice {
   private AuxSensorProperties properties;
   
   public Aux(AuxSensorProperties properties) {
      this.properties = properties;
   }
   
   public AuxSensorProperties getProperties() {
      return properties;
   }
   
   public void setProperties(AuxSensorProperties properties) {
      this.properties = properties;
   }

   @Override
   public void updateSensors() {
      if (sensors.get(OmniLinkCmd.SENSOR_AUX_CURRENTC) != null) {
         sensors.get(OmniLinkCmd.SENSOR_AUX_CURRENTC).update(MessageUtils.omniToC(properties.getCurrent()) + "");
      }
      if (sensors.get(OmniLinkCmd.SENSOR_AUX_CURRENTF) != null) {
         sensors.get(OmniLinkCmd.SENSOR_AUX_CURRENTF).update(MessageUtils.omniToF(properties.getCurrent()) + "");
      }
      if (sensors.get(OmniLinkCmd.SENSOR_AUX_HIGHC) != null) {
         sensors.get(OmniLinkCmd.SENSOR_AUX_HIGHC).update(MessageUtils.omniToC(properties.getHighSetpoint()) + "");
      }
      if (sensors.get(OmniLinkCmd.SENSOR_AUX_HIGHF) != null) {
         sensors.get(OmniLinkCmd.SENSOR_AUX_HIGHF).update(MessageUtils.omniToF(properties.getHighSetpoint()) + "");
      }
      if (sensors.get(OmniLinkCmd.SENSOR_AUX_LOWC) != null) {
         sensors.get(OmniLinkCmd.SENSOR_AUX_LOWC).update(MessageUtils.omniToC(properties.getLowSetpoint()) + "");
      }
      if (sensors.get(OmniLinkCmd.SENSOR_AUX_LOWF) != null) {
         sensors.get(OmniLinkCmd.SENSOR_AUX_LOWF).update(MessageUtils.omniToF(properties.getLowSetpoint()) + "");
      }
      if (sensors.get(OmniLinkCmd.SENSOR_AUX_STATUS) != null) {
         sensors.get(OmniLinkCmd.SENSOR_AUX_STATUS).update(properties.getStatus() + "");
      }
   }

}
