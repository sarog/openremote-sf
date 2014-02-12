package org.openremote.controller.protocol.isy994.model;

import org.openremote.controller.model.sensor.Sensor;
import org.openremote.controller.protocol.EventListener;
import org.openremote.controller.protocol.isy994.InsteonDeviceAddress;
import org.openremote.controller.protocol.isy994.IsyConnectionClient;

public class InsteonDeviceGetLevel implements EventListener {

   private InsteonDeviceAddress mAddress;
   private IsyConnectionClient mClient = null;

   public InsteonDeviceGetLevel(InsteonDeviceAddress address, IsyConnectionClient client) {
      mAddress = address;
      mClient = client;
   }

   @Override
   public void setSensor(Sensor sensor) {
      mClient.registerSensor(mAddress,sensor);
   }

   @Override
   public void stop(Sensor sensor) {
      mClient.removeSensor(mAddress,sensor);
   }

  

}
