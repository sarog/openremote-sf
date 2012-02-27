/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2011, OpenRemote Inc.
*
* See the contributors.txt file in the distribution for a
* full listing of individual contributors.
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as
* published by the Free Software Foundation, either version 3 of the
* License, or (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU Affero General Public License for more details.
*
* You should have received a copy of the GNU Affero General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*/
package org.openremote.modeler.client.widget.buildingmodeler;

import org.openremote.modeler.client.proxy.DeviceBeanModelProxy;
import org.openremote.modeler.client.rpc.AsyncSuccessCallback;
import org.openremote.modeler.client.widget.CommonForm;
import org.openremote.modeler.client.widget.CommonWindow;
import org.openremote.modeler.selenium.DebugId;
import org.openremote.modeler.shared.dto.DeviceDetailsDTO;

import com.extjs.gxt.ui.client.data.BeanModel;


/**
 * The window to creates or updates a device.
 */
public class DeviceWindow extends CommonWindow {
   
   /** The device form. */
   private CommonForm deviceForm;
   /** The device model. */
   protected BeanModel deviceModel = null;
   
   
   /**
    * Instantiates a new device window.
    * 
    * @param deviceBeanModel the device model
    */
   public DeviceWindow(BeanModel deviceBeanModel) {
      super();
      this.deviceModel = deviceBeanModel;
      initial(deviceBeanModel);
      show();
      setFocusWidget(deviceForm.getFields().get(0));
   }
   
   /**
    * Initial.
    * 
    * @param deviceBeanModel the device bean model
    */
   protected void initial(final BeanModel deviceBeanModel) {
      setSize(360, 200);
      setHeading("Loading...");
      // TODO EBR better indicate device is loading
      
      DeviceBeanModelProxy.loadDeviceDetails(deviceBeanModel, new AsyncSuccessCallback<BeanModel>() {  
        public void onSuccess(BeanModel result) {
          
          // TODO EBR : seems this is only used for edit, never new
          
          setHeading(((DeviceDetailsDTO) result.getBean()).getName() == null ? "New Device" : "Edit Device");
          deviceForm = new DeviceInfoForm(DeviceWindow.this, result);
          add(deviceForm);
          layout();
        }
      });
      
      ensureDebugId(DebugId.NEW_DEVICE_WINDOW);
   }

}
