package org.openremote.controller.protocol.upnp;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.util.HashMap;

import org.cybergarage.upnp.ControlPoint;
import org.cybergarage.upnp.Device;
import org.cybergarage.upnp.UPnP;
import org.cybergarage.upnp.device.DeviceChangeListener;
import org.cybergarage.upnp.device.InvalidDescriptionException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 *
 */
public class UPnPDummyLightDeviceTest  {

   private final static String TEST_DEVICE_UDN = "uuid:cybergarageLightDevice";

   private ControlPoint controlPoint;
   private UPnPDummyLightDevice device;

   private Object deviceNotyetDiscoveredLock = new Object();

   public UPnPDummyLightDeviceTest() {
      UPnP.setXMLParser(new UPnPParser());
   }

   @Before
   public synchronized void initControlPoint() {
      this.controlPoint = new ControlPoint();
      DeviceDiscoveredNotifier notifier = new DeviceDiscoveredNotifier();
      notifier.start();
      this.controlPoint.addDeviceChangeListener(notifier);
      this.controlPoint.start();
      try {
         this.device = new UPnPDummyLightDevice();
      } catch (InvalidDescriptionException e) {
         fail(e.getMessage());
      }      
      synchronized (deviceNotyetDiscoveredLock) {
         try {
            this.device.start();
            this.deviceNotyetDiscoveredLock.wait();
         } catch (InterruptedException e) {
            e.printStackTrace();
         }
      }
   }
   
   @After
   public synchronized void stopControlPoint() {
      this.device.stop();
      this.device = null;
      this.controlPoint.stop();
      this.controlPoint = null;
   }

   @Test
   public void testLightSet() {
      HashMap<String, String> args = new HashMap<String, String>();
      args.put("Power", "1");
      args.put("Result", "");
      UPnPCommand cmd = new UPnPCommand(this.controlPoint, "uuid:cybergarageLightDevice",
            "urn:upnp-org:serviceId:power:1", "SetPower", args);
      assertFalse(this.device.isOn());
      cmd.send();
      assertTrue(this.device.isOn());
   }
   
   @Test
   public void testLightGet() {
      HashMap<String, String> args = new HashMap<String, String>();
      args.put("Result", "");
      UPnPCommand cmd = new UPnPCommand(this.controlPoint, "uuid:cybergarageLightDevice",
            "urn:upnp-org:serviceId:power:1", "GetPower", args);
      cmd.send();
   }
   
   @Test
   public void testEvent() {
      HashMap<String, String> args = new HashMap<String, String>();
      args.put("Power", "1");
      args.put("Result", "");
      UPnPCommand cmd = new UPnPCommand(this.controlPoint, "uuid:cybergarageLightDevice",
            "urn:upnp-org:serviceId:power:1", "SetPower", args);
      String state = cmd.cache;
      this.device.toggle();
      assertFalse(state.equals(cmd.cache));
   }

   private class DeviceDiscoveredNotifier extends Thread implements DeviceChangeListener {

      @Override
      public synchronized void deviceAdded(Device dev) {
         System.out.println("Device discoverred with friendly name :: " + dev.getFriendlyName());
         if (dev.getUDN().equals(TEST_DEVICE_UDN)) {
            synchronized (UPnPDummyLightDeviceTest.this.deviceNotyetDiscoveredLock) {
               UPnPDummyLightDeviceTest.this.deviceNotyetDiscoveredLock.notify();
            }
         }
      }

      @Override
      public void deviceRemoved(Device dev) {
         // TODO Auto-generated method stub

      }
   }

}
