package org.openremote.controller.protocol.vera.model;

import org.jdom.Element;
import org.openremote.controller.protocol.vera.VeraClient;
import org.openremote.controller.protocol.vera.VeraCmd;


public class DoorLock extends VeraDevice {

   protected Boolean lockStatus;
   
   public DoorLock(VeraCategory category, int id, String name, VeraClient client) {
      super(category, id, name, client);
   }

   public void lock() {
      StringBuffer cmdUrl = new StringBuffer();
      cmdUrl.append("http://");
      cmdUrl.append(client.getAddress());
      cmdUrl.append(":3480/data_request?id=lu_action&output_format=xml&DeviceNum=");
      cmdUrl.append(id);
      cmdUrl.append("&serviceId=urn:micasaverde-com:serviceId:DoorLock1&action=SetTarget&Target=1");
      getClient().sendCommand(cmdUrl.toString());
   }

   public void unlock() {
      StringBuffer cmdUrl = new StringBuffer();
      cmdUrl.append("http://");
      cmdUrl.append(client.getAddress());
      cmdUrl.append(":3480/data_request?id=lu_action&output_format=xml&DeviceNum=");
      cmdUrl.append(id);
      cmdUrl.append("&serviceId=urn:micasaverde-com:serviceId:DoorLock1&action=SetTarget&Target=0");
      getClient().sendCommand(cmdUrl.toString());
   }

   @Override
   protected void updateDeviceSpecificSensors() {
      if ((attachedSensors.get(VeraCmd.GET_LOCK_STATUS) != null) && (lockStatus != null)) {
         attachedSensors.get(VeraCmd.GET_LOCK_STATUS).update(lockStatus?"locked":"unlocked");
      }
   }

   @Override
   protected void updateDeviceSpecificStatus(Element element) {
      if (element.getAttributeValue("status") != null) {
         this.lockStatus = (element.getAttributeValue("status").equals("1"))?true:false;
      }
   }

}
