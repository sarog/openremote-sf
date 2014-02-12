package org.openremote.controller.protocol.isy994;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.openremote.controller.Constants;
import org.openremote.controller.model.sensor.Sensor;
import org.openremote.controller.model.sensor.SwitchSensor;
import org.openremote.controller.protocol.isy994.model.InsteonDevice;
import org.openremote.controller.protocol.isy994.model.Light;
import org.openremote.controller.protocol.isy994.model.PowerSwitch;
import org.openremote.controller.utils.Logger;

import com.nanoxml.XMLElement;
import com.udi.isy.jsdk.insteon.ISYInsteonClient;
import com.universaldevices.client.NoDeviceException;
import com.universaldevices.common.properties.UDProperty;
import com.universaldevices.device.model.UDControl;
import com.universaldevices.device.model.UDFolder;
import com.universaldevices.device.model.UDGroup;
import com.universaldevices.device.model.UDNode;
import com.universaldevices.security.upnp.UPnPSecurity;
import com.universaldevices.upnp.UDProxyDevice;

public class IsyConnectionClient extends ISYInsteonClient {

   private static Logger logger = Logger.getLogger(Constants.CONTROLLER_PROTOCOL_LOG_CATEGORY + "ISY994");
   Map<InsteonDeviceAddress, List<Sensor>> mDevices = new Hashtable<InsteonDeviceAddress, List<Sensor>>();

   public void onLinkerEvent(UDProxyDevice arg0, String arg1, XMLElement arg2) {
      // TODO Auto-generated method stub
      System.out.println("method");

   }

   public void onNodeDeviceIdChanged(UDProxyDevice arg0, UDNode arg1) {
      // TODO Auto-generated method stub
      System.out.println("method");
   }

   
   public void onNodeDevicePropertiesRefreshed(UDProxyDevice arg0, UDNode arg1) {
      // TODO Auto-generated method stub
      System.out.println("method");
   }

   
   public void onNodeDevicePropertiesRefreshedComplete(UDProxyDevice arg0) {
      // TODO Auto-generated method stub
      System.out.println("method");
   }

   
   public void onNodeDevicePropertyChanged(UDProxyDevice arg0, UDNode arg1, UDProperty<?> arg2) {
      // TODO Auto-generated method stub
      System.out.println("method");
   }

   
   public void onNodeErrorCleared(UDProxyDevice arg0, UDNode arg1) {
      // TODO Auto-generated method stub
      System.out.println("method");
   }

   
   public void onNodeRevised(UDProxyDevice arg0, UDNode arg1) {
      // TODO Auto-generated method stub
      System.out.println("method");
   }

   
   public void onDeviceOffLine() {
      // TODO Auto-generated method stub
      System.out.println("method");
   }

   
   public void onDeviceOnLine() {
      System.out.println("ISY is on line ...");
      final UDProxyDevice device = getDevice();
      if (device == null) return;
      if (device.isSecurityEnabled() || device.securityLevel > UPnPSecurity.NO_SECURITY) {
         if (device.isAuthenticated && device.isOnline) return;
         try {
            // System.out.println("AUTHENICATING/SUBSCRIBING");
            if (!authenticate("craigh", "mho9tdsw")) {
               System.out.println("AUTHENICATION FAILED");
            } else {
               System.out.println("AUTHENICATION SUCCEEDED");
            }

         } catch (NoDeviceException e) {
            System.err.println("This should never happen!");
         }
      } else {
         // just subscribe to events
         System.out.println("SUBSCRIBING");
         device.subscribeToEvents(true);
         System.out.println("SUBSCRIPTION DONE");
      }

      System.out.println("Done onDeviceOnLine");
   }

   
   public void onDeviceSpecific(String arg0, String arg1, XMLElement arg2) {
      // TODO Auto-generated method stub
      System.out.println("method");
   }

   
   public void onDiscoveringNodes() {
      // TODO Auto-generated method stub
      System.out.println("method");
   }

   
   public void onFolderRemoved(String arg0) {
      // TODO Auto-generated method stub
      System.out.println("method");
   }

   
   public void onFolderRenamed(UDFolder arg0) {
      // TODO Auto-generated method stub
      System.out.println("method");
   }

   
   public void onGroupRemoved(String arg0) {
      // TODO Auto-generated method stub
      System.out.println("method");
   }

   
   public void onGroupRenamed(UDGroup arg0) {
      // TODO Auto-generated method stub
      System.out.println("method");
   }

   
   public void onInternetAccessDisabled() {
      // TODO Auto-generated method stub
      System.out.println("method");
   }

   
   public void onInternetAccessEnabled(String arg0) {
      // TODO Auto-generated method stub
      System.out.println("method");
   }

   
   public void onModelChanged(UDControl control, Object value, UDNode node) {
      if (control == null || value == null || node == null) return;
      logger.debug("Someone or something changed " + ((control.label == null) ? control.name : control.label)
            + " to " + value + " at " + node.name);

      List<Sensor> aDevice = mDevices.get(new InsteonDeviceAddress(node.address));
      if (aDevice != null) {
         for (Sensor sensor : aDevice) {
            if (sensor instanceof SwitchSensor) sensor.update(Integer.parseInt((String) value) > 0 ? "on" : "off");
            else {
               sensor.update((String) value);
            }
         }
      }

   }

   
   public void onNetworkRenamed(String arg0) {
      // TODO Auto-generated method stub
      System.out.println("method");
   }

   
   public void onNewFolder(UDFolder arg0) {
      // TODO Auto-generated method stub
      System.out.println("method");
   }

   
   public void onNewGroup(UDGroup arg0) {
      // TODO Auto-generated method stub
      System.out.println("method");
   }

   
   public void onNewNode(UDNode arg0) {
      // TODO Auto-generated method stub
      System.out.println("method");
   }

   
   public void onNodeDiscoveryStopped() {
      // TODO Auto-generated method stub
      System.out.println("method");
   }

   
   public void onNodeEnabled(UDNode arg0, boolean arg1) {
      // TODO Auto-generated method stub
      System.out.println("method");
   }

   
   public void onNodeError(UDNode arg0) {
      // TODO Auto-generated method stub
      System.out.println("method");
   }

   
   public void onNodeHasPendingDeviceWrites(UDNode arg0, boolean arg1) {
      // TODO Auto-generated method stub

   }

   
   public void onNodeIsWritingToDevice(UDNode arg0, boolean arg1) {
      // TODO Auto-generated method stub

   }

   
   public void onNodeMovedAsMaster(UDNode arg0, UDGroup arg1) {
      // TODO Auto-generated method stub

   }

   
   public void onNodeMovedAsSlave(UDNode arg0, UDGroup arg1) {
      // TODO Auto-generated method stub

   }

   
   public void onNodeParentChanged(UDNode arg0, UDNode arg1) {
      // TODO Auto-generated method stub

   }

   
   public void onNodePowerInfoChanged(UDNode arg0) {
      // TODO Auto-generated method stub

   }

   
   public void onNodeRemoved(String arg0) {
      // TODO Auto-generated method stub

   }

   
   public void onNodeRemovedFromGroup(UDNode arg0, UDGroup arg1) {
      // TODO Auto-generated method stub

   }

   
   public void onNodeRenamed(UDNode arg0) {
      // TODO Auto-generated method stub

   }

   
   public void onNodeToGroupRoleChanged(UDNode arg0, UDGroup arg1, char arg2) {
      // TODO Auto-generated method stub

   }

   
   public void onProgress(String arg0, XMLElement arg1) {
      // TODO Auto-generated method stub

   }

   
   public void onSystemConfigChanged(String arg0, XMLElement arg1) {
      // TODO Auto-generated method stub

   }

   
   public void onSystemStatus(boolean arg0) {
      // TODO Auto-generated method stub

   }

   
   public void onTriggerStatus(String arg0, XMLElement arg1) {
      logger.debug("status triggered: " + arg1);
      if (arg1.getContents() != null && arg1.getContents().startsWith("[")) {
         ISYInsteonValueParser parser = new ISYInsteonValueParser(arg1.getContents());
         InsteonDeviceAddress address = parser.address();
         String actionCodeString = parser.actionCode();
         String value = parser.value();
         if ("ST".equals(actionCodeString)) {
            List<Sensor> aDevice = mDevices.get(address);
            if (aDevice != null) {
               for (Sensor sensor : aDevice) {
                  if (sensor instanceof SwitchSensor) sensor.update(Integer.parseInt(value) > 0 ? "on" : "off");
                  else {
                     sensor.update(value);
                  }
               }
            } else {
               logger.info("No Sensors for:" + address);
            }
         }
      } else {
         logger.info("Unhandled trigger: ");
      }
   }

   
   public void onNewDeviceAnnounced(UDProxyDevice arg0) {
      // TODO Auto-generated method stub

   }

   public void registerSensor(InsteonDeviceAddress address, Sensor sensor) {
      List<Sensor> sensors = mDevices.get(address);
      if (sensors == null) {
         sensors = new ArrayList<Sensor>();
         mDevices.put(address, sensors);
      }
      sensors.add(sensor);
   }

   public void removeSensor(InsteonDeviceAddress address,Sensor sensor) {
      List<Sensor> sensors = mDevices.get(address);
      if (sensors != null) {
         sensors.remove(sensor);
      }
   }
}
