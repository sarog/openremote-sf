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

import java.util.List;

import org.openremote.modeler.client.rpc.DeviceMacroService;
import org.openremote.modeler.domain.DeviceMacro;

// TODO: Auto-generated Javadoc
/**
 * The Class DeviceMacroController.
 */
public class DeviceMacroController extends BaseGWTSpringControllerWithHibernateSupport implements DeviceMacroService{
   
   /** The device macro service. */
   private DeviceMacroService deviceMacroService;
      
   
   /**
    * Sets the device macro service.
    * 
    * @param deviceMacroService the new device macro service
    */
   public void setDeviceMacroService(DeviceMacroService deviceMacroService) {
      this.deviceMacroService = deviceMacroService;
   }


   /* (non-Javadoc)
    * @see org.openremote.modeler.client.rpc.DeviceMacroService#loadAll()
    */
   public List<DeviceMacro> loadAll() {
      return deviceMacroService.loadAll();
   }


   /* (non-Javadoc)
    * @see org.openremote.modeler.client.rpc.DeviceMacroService#save(org.openremote.modeler.domain.DeviceMacro)
    */
   public DeviceMacro saveDeviceMacro(DeviceMacro deviceMacro) {
      return deviceMacroService.saveDeviceMacro(deviceMacro);
   }


   /* (non-Javadoc)
    * @see org.openremote.modeler.client.rpc.DeviceMacroService#edit(org.openremote.modeler.domain.DeviceMacro)
    */
   public DeviceMacro updateDeviceMacro(DeviceMacro deviceMacro) {
      return deviceMacroService.updateDeviceMacro(deviceMacro);
   }


   /* (non-Javadoc)
    * @see org.openremote.modeler.client.rpc.DeviceMacroService#deleteDeviceMacro(long)
    */
   public void deleteDeviceMacro(long id) {
      deviceMacroService.deleteDeviceMacro(id);
   }

}
