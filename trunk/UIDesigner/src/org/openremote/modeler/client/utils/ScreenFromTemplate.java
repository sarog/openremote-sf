package org.openremote.modeler.client.utils;

import java.util.HashSet;
import java.util.Set;

import org.openremote.modeler.domain.BusinessEntity;
import org.openremote.modeler.domain.Device;
import org.openremote.modeler.domain.Screen;

@SuppressWarnings("serial")
public class ScreenFromTemplate extends BusinessEntity{

   private Set<Device> devices = new HashSet<Device>();
   private Screen screen = null;
   
   public ScreenFromTemplate(){}
   
   public ScreenFromTemplate(Set<Device> devices,Screen screen) {
      this.devices = devices;
      this.screen = screen;
   }

   public Set<Device> getDevices() {
      return devices;
   }

   public void setDevices(Set<Device> devices) {
      this.devices = devices;
   }

   public Screen getScreen() {
      return screen;
   }

   public void setScreen(Screen screen) {
      this.screen = screen;
   }
   
   
}
