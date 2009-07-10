package org.openremote.modeler.client.model;

import java.io.Serializable;
import java.util.Map;

import org.openremote.modeler.domain.Device;

import com.extjs.gxt.ui.client.data.BaseTreeModel;

public class DeviceTreeModel extends BaseTreeModel implements Serializable {

   /**
    * 
    */
   private static final long serialVersionUID = -3212925111586567858L;
   
   public DeviceTreeModel(String name) {
      set("name", name);
   }
   
   public DeviceTreeModel(Map<String, String> device) {
      for (String key : device.keySet()) {
         set(key,device.get(key));
      }
   }
   
   public DeviceTreeModel(String name, BaseTreeModel[] children) {
      this(name);
      for (BaseTreeModel child : children) {
         add(child);
      }
   }
   
   public DeviceTreeModel(Device device) {
      set("name",device.getName());
      set("data",device);
   }
   public String getName() {
      return (String) get("name");
    }

    public String toString() {
      return getName();
    }
}
