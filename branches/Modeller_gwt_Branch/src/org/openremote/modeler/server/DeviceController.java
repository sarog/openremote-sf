/* OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2009, OpenRemote Inc.
 * 
 * See the contributors.txt file in the distribution for a
 * full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 3.0 of
 * the License, or (at your option) any later version.
 * 
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.openremote.modeler.server;

import java.util.Map;

import org.openremote.modeler.client.rpc.DeviceService;
import org.openremote.modeler.domain.Device;
import org.openremote.modeler.service.DeviceServiceImpl;

// TODO: Auto-generated Javadoc
/**
 * The Class DeviceController.
 */
public class DeviceController extends BaseGWTSpringControllerWithHibernateSupport implements DeviceService {

   /** The Constant serialVersionUID. */
   private static final long serialVersionUID = -6698924847005128888L;
   
   /** The device service. */
   private DeviceService deviceService;
   
   /**
    * Sets the device service .
    * 
    * @param deviceService the new device service 
    */
   public void setDeviceService(DeviceService deviceService) {
      this.deviceService = deviceService;
   }

   /* (non-Javadoc)
    * @see org.openremote.modeler.client.rpc.DeviceService#saveDevice(java.util.Map)
    */
   public Device saveDevice(Map<String, String> map) {
      return deviceService.saveDevice(map);
   }

}
