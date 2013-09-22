package org.openremote.controller.protocol.omnilink.model;

import java.util.HashMap;
import java.util.Map;

import org.openremote.controller.model.sensor.Sensor;
import org.openremote.controller.protocol.omnilink.OmniLinkCmd;
import org.openremote.controller.protocol.omnilink.OmnilinkCommandBuilder;
import org.openremote.controller.utils.Logger;

public abstract class OmnilinkDevice {

   protected Map<OmniLinkCmd, Sensor> sensors = new HashMap<OmniLinkCmd, Sensor>();
   protected final static Logger logger = Logger.getLogger(OmnilinkCommandBuilder.OMNILINK_PROTOCOL_LOG_CATEGORY);

   
   public void addSensor(OmniLinkCmd cmd, Sensor sensor){
     this.sensors.put(cmd, sensor);
     //this.client.requestFullStatus();
   }
   
   public void removeSensor(Sensor sensor) {
     for (Map.Entry<OmniLinkCmd, Sensor> mapEntry : this.sensors.entrySet()) {
        if (mapEntry.getValue().equals(sensor)) {
           this.sensors.remove(mapEntry.getKey());
        }
     }
   }
   
   public abstract void updateSensors();
}
