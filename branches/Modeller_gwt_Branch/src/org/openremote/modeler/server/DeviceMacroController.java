/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2009, OpenRemote Inc.
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

import org.openremote.modeler.client.rpc.DeviceMacroRPCService;
import org.openremote.modeler.domain.DeviceMacro;
import org.openremote.modeler.domain.DeviceMacroItem;

import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * The Class DeviceMacroController.
 */
public class DeviceMacroController extends BaseGWTSpringControllerWithHibernateSupport implements DeviceMacroRPCService {
   
   /** The device macro service. */
   private DeviceMacroRPCService deviceMacroRPCService;


    /**
    * Sets the device macro service.
    * 
    * @param deviceMacroRPCService the new device macro service
    */
   public void setDeviceMacroService(DeviceMacroRPCService deviceMacroRPCService) {
      this.deviceMacroRPCService = deviceMacroRPCService;
   }


   /* (non-Javadoc)
    * @see org.openremote.modeler.client.rpc.DeviceMacroRPCService#loadAll()
    */
   public List<DeviceMacro> loadAll() {
      return deviceMacroRPCService.loadAll();
   }


   /* (non-Javadoc)
    * @see org.openremote.modeler.client.rpc.DeviceMacroRPCService#save(org.openremote.modeler.domain.DeviceMacro)
    */
   public DeviceMacro saveDeviceMacro(DeviceMacro deviceMacro) {
      return deviceMacroRPCService.saveDeviceMacro(deviceMacro);
   }


   /* (non-Javadoc)
    * @see org.openremote.modeler.client.rpc.DeviceMacroRPCService#edit(org.openremote.modeler.domain.DeviceMacro)
    */
   public DeviceMacro updateDeviceMacro(DeviceMacro deviceMacro) {
      return deviceMacroRPCService.updateDeviceMacro(deviceMacro);
   }


   /* (non-Javadoc)
    * @see org.openremote.modeler.client.rpc.DeviceMacroRPCService#deleteDeviceMacro(long)
    */
   public void deleteDeviceMacro(long id) {
      deviceMacroRPCService.deleteDeviceMacro(id);
   }

    public List<DeviceMacroItem> loadDeviceMacroItems(DeviceMacro deviceMacro) {
        return null;
    }

    public DeviceMacro loadDeviceMacroById(long id) {
        return null;
    }


}
